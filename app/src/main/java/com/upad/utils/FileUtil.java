package com.upad.utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FileUtil {
    private static final String XiezhuPath = "/xiezhu/";
    private static final String AES = "zT8bx%&7X2p6t$dJ%MwVl#%xu2oA9trh";
    public static String getDeviceNum() {
        RandomAccessFile raf = null;
        FileChannel channel = null;
        FileLock fileLock = null;
        String deviceNum = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ XiezhuPath, "deviceNo.txt");
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            raf = new RandomAccessFile(file, "rws");
            channel = raf.getChannel();
            while (true) {
                //true：共享锁，false：独享锁定
                fileLock = channel.tryLock(0, Long.MAX_VALUE, false);
                if (fileLock != null) {
                    long fileSize = channel.size();
                    StringBuilder sb = new StringBuilder();
                    if (0 == fileSize) {
                        deviceNum = UUID.randomUUID().toString();
                        sb.append(SecurityUtil.encrypt(AES, deviceNum));
                        channel.write(ByteBuffer.wrap(sb.toString().getBytes()));
                    } else {
                        ByteBuffer buf = ByteBuffer.allocate(48);
                        int bytesRead = channel.read(buf);
                        while (bytesRead != -1) {
                            buf.flip();
                            while (buf.hasRemaining()) {
                                sb.append((char) buf.get());
                            }
                            buf.clear();
                            bytesRead = channel.read(buf);
                        }
                        deviceNum = SecurityUtil.decrypt(AES, sb.toString());
                    }
                    break;
                } else {
                    TimeUnit.MICROSECONDS.sleep(10);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileLock != null) {
                    fileLock.release();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (channel != null)
                    channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (raf != null)
                    raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return deviceNum;
    }

    public static Bitmap convertBase64ToBitmap(String data){
        byte[] bytes = Base64.decode(data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir
     *            要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if(!dirFile.exists()){
            System.out.println("删除目录失败：" + dir + "不存在！");
            return true;
        }
        if (!dirFile.isDirectory()) {
            System.out.println("删除目录失败：" + dir + "不是一个目录！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }


    /**
     *  从assets目录中复制整个文件夹内容
     *  @param  context  Context 使用CopyFiles类的Activity
     *  @param  oldPath  String  原文件路径  如：/aa
     *  @param  newPath  String  复制后路径  如：xx:/bb/cc
     */
    public static void copyFilesFromAssets(Context context,String oldPath,String newPath) throws IOException {
        String[] fileNames = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
        if (fileNames.length > 0) {//如果是目录
            File file = new File(newPath);
            file.mkdirs();//如果文件夹不存在，则递归
            for (String fileName : fileNames) {
                copyFilesFromAssets(context,oldPath + "/" + fileName,newPath+"/"+fileName);
            }
        } else {//如果是文件
            InputStream is = context.getAssets().open(oldPath);
            FileOutputStream fos = new FileOutputStream(new File(newPath));
            byte[] buffer = new byte[1024];
            int byteCount=0;
            while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节
                fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
            }
            fos.flush();//刷新缓冲区
            is.close();
            fos.close();
        }
    }


    public static void saveFile(long length, InputStream inputStream, String fileName){
        LogUtil.Log("FileUtil-saveFile-fileName:"+fileName);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ XiezhuPath, fileName);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        if(file.exists()){
            file.delete();
        }
        FileOutputStream fos = null;
        int len = 0;
        long readSum = 0;
        byte[] buffer = new byte[1024 * 2];
        try {
            fos = new FileOutputStream(file);
            while ((len = inputStream.read(buffer)) != -1) {
                readSum += len;
                fos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != fos){
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
