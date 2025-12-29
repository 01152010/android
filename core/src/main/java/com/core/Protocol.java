package com.core;

import android.util.Log;

import com.core.enumeration.CmdType;
import com.core.enumeration.ProtocolType;
import com.core.model.Command;
import com.core.utils.LogUtil;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Protocol {
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    private static Protocol mProtocol;
    static {
        System.loadLibrary("core");
    }
    private native byte[] send55BB(byte address,byte[] cmd,byte[] data);
    private native byte[] send55CC(byte address,byte[] cmd,byte[] data);

    public byte[] send(ProtocolType protocolType,byte address,byte[] cmd,byte[] data){
        LogUtil.Log("Protocol-send-protocolType:"+protocolType);
        if(ProtocolType.CC == protocolType){//55CC
            return send55CC(address,cmd,data);
        }
        return send55BB(address,cmd,data);
    }
}
