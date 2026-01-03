package com.serialport.utils;

import android.util.Log;

import com.core.utils.LogUtil;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CommonUtils {

    public static void write2File(String path,String value) {
        LogUtil.Log("CommonUtils-Write2File-path:"+path);
        LogUtil.Log("CommonUtils-Write2File-value:"+value);
        File file = new File(path);
        LogUtil.Log("CommonUtils-Write2File-file.exists:"+file.exists());
        boolean exits = (file == null) || (!file.exists()) || (value == null);
        LogUtil.Log("CommonUtils-Write2File-file-exits:"+exits);
        if(exits) {
            LogUtil.Log("CommonUtils-Write2File-(file == null) || (!file.exists()) || (value == null)");
        }
        try {
            FileOutputStream fout = new FileOutputStream(file);
            PrintWriter pWriter = new PrintWriter(fout);
            pWriter.println(value);
            pWriter.flush();
            pWriter.close();
            fout.close();
        } catch(Exception e) {
            e.printStackTrace();
            LogUtil.Log("切换收发失败:"+e.getMessage());
        }finally {
            LogUtil.Log("CommonUtils-Write2File-success");
        }
    }

/*    public static void write2File(String path, String content) {
        LogUtil.Log("CommonUtils-Write2File-path:"+path);
        LogUtil.Log("CommonUtils-Write2File-value:"+content);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(path);
            if (fileWriter != null) {
                fileWriter.write(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("WriteCMD", "Write Fail Exception:"+e.getMessage());
            LogUtil.Log("切换收发失败:"+e.getMessage());
        } finally {
            if (fileWriter != null){
                try {
                    fileWriter.close();
                    Log.d("WriteCMD", "scuess");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("WriteCMD", "Write Fail IOException");
                }
            }
            LogUtil.Log("CommonUtils-Write2File-success");
        }
    }*/

    public static void changeRXTX(String cmd) {
        LogUtil.Log("CommonUtils-changeRXTX-cmd:" + cmd);
        Process p = null;
        DataOutputStream os = null;
        try {
            p = Runtime.getRuntime().exec("sh");
            os = new DataOutputStream(p.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            LogUtil.Log("CommonUtils-changeRXTX-success");
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.Log("CommonUtils-changeRXTX-IOException:" + e.getMessage());
        } finally {
            if (p != null) {
                p.destroy();
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<Integer> getHBitIndexs(byte[] content){
        List<Integer> result = new ArrayList<>();
        for(int i = 0 ; i < content.length; i++){
            for(int j = 0; j < 8; j++){
                int temp = 0x01 & ((content[i] & 0xff) >> (7 - j));
                if(1 == temp){
                    result.add(i*8+(j+1));
                }
            }
        }
        return result;
    }
}
