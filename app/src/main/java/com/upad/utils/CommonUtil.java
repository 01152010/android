package com.upad.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.upad.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommonUtil {
    private static final File RECOVERY_DIR = new File("/cache/recovery");
    private static final File UPDATE_FLAG_FILE = new File(RECOVERY_DIR, "last_flag");
    private static final String COMMAND_FLAG_SUCCESS = "success";
    private static final String COMMAND_FLAG_UPDATING = "updating";

    public static String getMacFromHardware() {
        StringBuffer stringBuffer = new StringBuffer(20);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader("/sys/class/net/wlan0/address"));
            char[] buf = new char[20];
            int numRead = 0;
            while ((numRead = bufferedReader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                stringBuffer.append(readData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuffer.toString().trim();
    }


    public static boolean compareVersion(String remoteVersion, String localVersion) {
        try {
            String[] remote = remoteVersion.split("\\.");
            String[] local = localVersion.split("\\.");
            for (int i = 0; i < 3; i++) {
                if (Integer.valueOf(remote[i]) > Integer.valueOf(local[i])) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.Log("版本格式有问题：" + e.getMessage());
        }
        return false;
    }

    public static boolean checkAdmin(String pwd) {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmm");
        Date time = new Date(System.currentTimeMillis());// 获取当前时间
        String strTime = formatter.format(time);
        return strTime.equalsIgnoreCase(pwd);
    }

    public static PackageInfo getPackageInfo(Context context) {
        try {
            PackageInfo packageInfo = context.getApplicationContext().getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getProperty(String key, String defaultValue) {
        //ro.product.version
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, "unknown"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.Log("Key : " + key + " Value : " + value);
        return value;
    }

    public static void writeFlagCommand(String path) throws IOException {
        RECOVERY_DIR.mkdirs();
        UPDATE_FLAG_FILE.delete();
        FileWriter writer = new FileWriter(UPDATE_FLAG_FILE);
        LogUtil.Log("writeFlagCommand:" + path);
        try {
            writer.write("updating$path=" + path);
        } finally {
            writer.close();
        }
    }

    public static void deletePackage(String path) {
        LogUtil.Log("try to deletePackage...");
        if (path.startsWith("@")) {
            String fileName = "/data/media/0/xiezhu/update.zip";
            String fileName1 = "/data/media/0/update.zip";
            LogUtil.Log("ota was maped, so try to delete path = " + path);
            File f_ota = new File(fileName);
            File f_ota1 = new File(fileName1);
            if (f_ota.exists()) {
                f_ota.delete();
                LogUtil.Log("delete complete! path=" + fileName);
            }
            if (f_ota1.exists()) {
                f_ota1.delete();
                LogUtil.Log("delete complete! path1=" + fileName1);
            }/*else{
                fileName = "/data/media/0/update.img";
                f_ota = new File(fileName);
                if(f_ota.exists()){
                    f_ota.delete();
                    LogUtil.Log("delete complete! path=" + fileName);
                }else{
                    LogUtil.Log("path = " + fileName + ", file not exists!");
                }
            }*/
        }

        File f = new File(path);
        if (f.exists()) {
            f.delete();
            LogUtil.Log("delete complete! path=" + path);
        } else {
            LogUtil.Log("path=" + path + " ,file not exists!");
        }
    }

    public static boolean deletePackage() {
        String fileName = "/data/media/0/xiezhu/update.zip";
//        String fileName1 = "/data/media/0/update.zip";
        File f_ota = new File(fileName);
//        File f_ota1 = new File(fileName1);
        if (f_ota.exists()) {
            f_ota.delete();
            LogUtil.Log("delete complete! path=" + fileName);
        }
//        if(f_ota1.exists()){
//            f_ota1.delete();
//            LogUtil.Log("delete complete! path1=" + fileName1);
//        }
        return true;
    }

    public static boolean checkDeletePackage() {
        String command = readFlagCommand();
        LogUtil.Log("checkDeletePackage-command=" + command);
        if (null == command) {
            return false;
        }
        if (command.contains("$path")) {
            String path = command.substring(command.indexOf('=') + 1);
            LogUtil.Log("last_flag: path = " + path);
            if (command.startsWith(COMMAND_FLAG_SUCCESS)) {
                deletePackage(path);
                return true;
            } else if (command.startsWith(COMMAND_FLAG_UPDATING)) {
                return false;
            }
        }
        return false;
    }

    public static String readFlagCommand() {
        if (UPDATE_FLAG_FILE.exists()) {
            LogUtil.Log("UPDATE_FLAG_FILE is exists");
            char[] buf = new char[128];
            int readCount = 0;
            try {
                FileReader reader = new FileReader(UPDATE_FLAG_FILE);
                readCount = reader.read(buf, 0, buf.length);
                LogUtil.Log("readCount = " + readCount + " buf.length = " + buf.length);
            } catch (IOException e) {
                LogUtil.Log("can not read /cache/recovery/last_flag!");
            } finally {
                UPDATE_FLAG_FILE.delete();
            }
            StringBuilder sBuilder = new StringBuilder();
            for (int i = 0; i < readCount; i++) {
                if (buf[i] == 0) {
                    break;
                }
                sBuilder.append(buf[i]);
            }
            return sBuilder.toString();
        } else {
            return null;
        }
    }

    public static Notification pupNotification(Context mcontext, PendingIntent pi, String state) {
        Notification notification = null;
        NotificationManager mNotificationManager;
        String id = "channel_service";
        CharSequence name = "upad recorder";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager = (NotificationManager) mcontext.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.enableVibration(false);
            channel.setVibrationPattern(new long[]{0});
            channel.setSound(null, null);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
                notification = new Notification.Builder(mcontext, id)
                        .setContentTitle("Upad")
                        .setContentText(state)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(mcontext.getResources(), R.mipmap.ic_launcher))
                        .setContentIntent(pi)
                        .build();
            }
        } else {
            notification = new Notification.Builder(mcontext)
                    .setContentTitle("Upad")
                    .setContentText(state)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(mcontext.getResources(), R.mipmap.ic_launcher))
                    .setContentIntent(pi)
                    .build();
        }
        return notification;
    }

    public static int[] getPixels(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point realSize = new Point();
        display.getRealSize(realSize);
        return new int[]{realSize.x, realSize.y};
    }


    public static String getDeviceId(Context context) {
        TelephonyManager telephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = null;
        try {
            imei = telephonyMgr.getDeviceId();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        String serial = Build.SERIAL;
        String uuid;
        if (TextUtils.isEmpty(imei)) {
            imei = "unkown";
        } else if (TextUtils.isEmpty(serial)) {
            serial = "unkown";
        }
        uuid = UUID.nameUUIDFromBytes((imei + serial).getBytes()).toString();
        return uuid;
    }

    public static String getTopicName(String env, String hotelID, String roomNo) {
        String tempRoomNo = roomNo.replaceAll(" ", "");
        for (int i = 0; i < tempRoomNo.length() - 1; i++) {
            char c = tempRoomNo.charAt(i);
            //数字
            Pattern p = Pattern.compile("[0-9]*");
            Matcher m = p.matcher(String.valueOf(c));
            boolean checkNumber = m.matches();
            //字母
            p = Pattern.compile("[a-zA-Z]");
            m = p.matcher(String.valueOf(c));
            boolean checkLetter = m.matches();
            //空格
            boolean checkSpace = c == '_';
            if (!checkNumber && !checkLetter && !checkSpace) {
                tempRoomNo = String.valueOf(tempRoomNo.hashCode());
                break;
            }
        }
        String topicName = String.format(Locale.CHINA, "upad.%s.%s.%s",
                env,
                hotelID,
                tempRoomNo).toLowerCase();
        if (topicName.length() > 64) {
            topicName = topicName.replace(env + ".", "");
            if (topicName.length() > 64)
                topicName = topicName.replace("-", "");
        }
        return topicName;
    }

    /**
     * 转换中央电视台的频道
     * 临时方案
     */
    public static String convertChannel(String chanel) {
        if(chanel.contains("中央")) {
            if (chanel.contains("综合")) {
                chanel = "CCTV-1";
            } else if (chanel.contains("新闻")) {
                chanel = "CCTV-13";
            } else if (chanel.contains("国防军事")) {
                chanel = "CCTV-7";
            } else if (chanel.contains("电影")) {
                chanel = "CCTV-6";
            } else if (chanel.contains("记录")) {
                chanel = "CCTV-9";
            } else if (chanel.contains("电视剧")) {
                chanel = "CCTV-8";
            } else if (chanel.contains("社会与法")) {
                chanel = "CCTV-12";
            } else if (chanel.contains("综艺")) {
                chanel = "CCTV-3";
            } else if (chanel.contains("音乐")) {
                chanel = "CCTV-15";
            } else if (chanel.contains("戏曲")) {
                chanel = "CCTV-11";
            } else if (chanel.contains("财经")) {
                chanel = "CCTV-2";
            } else if (chanel.contains("少儿")) {
                chanel = "CCTV-14";
            } else if (chanel.contains("科教")) {
                chanel = "CCTV-10";
            } else if (chanel.contains("体育")) {
                chanel = "CCTV-5";
            } else if (chanel.contains("中文国际")) {
                chanel = "CCTV-4";
            } else if (chanel.contains("奥运赛事")) {
                chanel = "CCTV-16";
            } else if (chanel.contains("农业农村")) {
                chanel = "CCTV-17";
            }
        }
        return chanel;
    }
}
