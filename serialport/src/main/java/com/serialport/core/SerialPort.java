package com.serialport.core;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort {
    public static final String DEFAULT_SU_PATH = "/system/bin/su";
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    private String mSuPath = DEFAULT_SU_PATH;

    static {
        System.loadLibrary("serialport");
    }
    private native FileDescriptor open(String path, int baudrate, int stopBits, int dataBits, int parity, int flowCon, int flags); //打开串口
    public native void close(); //关闭串口

    public void setSuPath(String suPath){
        if(null == suPath || suPath.isEmpty()){
            return;
        }
        mSuPath = suPath;
    }

    public SerialPort(){}

    public void open(File device, int baudrate, int stopBits, int dataBits, int parity, int flowCon, int flags) throws SecurityException,IOException{
        /* Check access permission */  // 检查是否获取了指定串口的读写权限
        if (!device.canRead() || !device.canWrite()) {
            try {
                /* Missing read/write permission, trying to chmod the file */
                // 如果没有获取指定串口的读写权限，则通过挂在到linux的方式修改串口的权限为可读写
                Process su;
//                su = Runtime.getRuntime().exec("/system/xbin/su");
                su = Runtime.getRuntime().exec(mSuPath);
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead()
                        || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }
        mFd = open(device.getAbsolutePath(), baudrate, stopBits, dataBits, parity, flowCon, flags);
        if (mFd == null) {
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }
    // Getters and setters
    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

}
