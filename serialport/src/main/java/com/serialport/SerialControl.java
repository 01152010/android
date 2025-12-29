package com.serialport;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;

import com.core.enumeration.CmdType;
import com.core.model.ComMsg;
import com.core.model.Command;
import com.core.model.KTControl;
import com.core.proto.CloudControlUpad;
import com.core.proto.CloudNodeItem;
import com.core.proto.CloudNodeStatus;
import com.core.proto.CloudQuaryNodeListStatus;
import com.core.proto.CloudRoomInfo;
import com.core.proto.CloudUpdate;
import com.core.utils.LogUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import com.serialport.core.SerialPort;
import com.serialport.interf.SerialStateInterface;
import com.serialport.utils.ByteUtil;
import com.serialport.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class SerialControl {
    private final static long TIMEOUT = 10000;
    private static final int BUFSIZ = 512;
    private LinkedBlockingQueue<Command> mBasket = new LinkedBlockingQueue<>(10);
    private final ByteBuffer mReadBuffer = ByteBuffer.allocate(BUFSIZ);
    private final ByteBuffer mRealReadBuffer = ByteBuffer.allocate(BUFSIZ);
    private Context mContext;
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
//    private String mPort = "/dev/ttyS0";
    private String mPort = "/dev/ttyS4";
    private int mBaudRate = 9600;
    private int mDataBits = 8;
    private int mStopBits = 1;
    private int mParity = 0;
    private int mFlowCon = 0;
    private int mFlags = 0;

    private ReadThread mReadThread;
    private WriteThread mWriteThread;
    private boolean mIsOpen = false;
    private List<Command> mCommandList = new ArrayList<>();
    private Command mCurrentCommand;
    private Handler mHandler = new Handler();
    private List<CloudNodeItem.NodeItem> mNodeItemList = new ArrayList<>();
    private int mAddress = 1;
    private static SerialControl mSerialControl;
    public SerialControl(Context context){
        mContext = context;
        mSerialPort = new SerialPort();
    }

    public static SerialControl getInstance(Context context){
        if(null == mSerialControl){
            mSerialControl = new SerialControl(context);
        }
        return mSerialControl;
    }

    public void setAddress(int address){
        mAddress = address;
    }

    public void setSuPath(String suPath){
        if(null == mSerialPort) {
            LogUtil.Log("SerialControl-setSuPath-mSerialPort:"+mSerialPort);
            return;
        }
        mSerialPort.setSuPath(suPath);
    }

    public void setPort(String port){
        mPort = port;
    }

    public void setBaudRate(int baudRate, int dataBits, int stopBits, int parity){
        mBaudRate = baudRate;
        mDataBits = dataBits;
        mStopBits = stopBits;
        mParity = parity;
    }

    public boolean isOpened(){
        LogUtil.Log("isOpened-mSerialPort:"+mSerialPort+"; mIsOpen:"+mIsOpen);
        if(null == mSerialPort){
            return false;
        }
        return mIsOpen;
    }

    public void open(SerialStateInterface stateInterface){
        if(isOpened()) {
            return;
        }
        try {
            mSerialPort.open(new File(mPort),mBaudRate,mStopBits,mDataBits,mParity,mFlowCon,mFlags);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            mReadThread = new ReadThread();
            mReadThread.start();
            mWriteThread = new WriteThread();
            mWriteThread.start();
            mIsOpen = true;
            stateInterface.state(State.OPEN_OK,null);
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
            stateInterface.state(State.EXCEPTION,e.getMessage());
            mIsOpen = false;
        }
    }

    public boolean isSending(CmdType cmdType){
        if(null != mCurrentCommand && cmdType == mCurrentCommand.getCmdType()){
            return true;
        }
        return false;
    }


    public boolean write(Command command){
        return mBasket.offer(command);
    }

/*    public boolean write(byte[] buffer){
        boolean success = mBasket.offer(buffer);
        return success;
    }*/

    public void close() {
        if (null != mReadThread) {
            mReadThread.interrupt();
        }
        if (null != mWriteThread) {
            mWriteThread.interrupt();
        }
        if(null != mSerialPort && mIsOpen){
            mSerialPort.close();
            mSerialPort = null;
        }
        mIsOpen = false;
        mSerialControl = null;
    }



    private void doReadContent(byte[] content){
        LogUtil.Log("SerialControl-doReadContent-content:"+ByteUtil.convertBytes2HexString(content));
        int len = content.length;
        if(6 > len){
            LogUtil.Log("SerialControl-ReadThread-content(6 > len)");
            return;
        }
        if((byte)0x55 != content[0] && ((byte)0xBB != content[1] || (byte)0xCC != content[1])){
            LogUtil.Log("SerialControl-ReadThread-content(!55BB && !55CC)");
            return;
        }
        if(mAddress != (content[2] & 0xff)){
            if((byte)0xBB == content[1] && (byte)0x5a == content[4] && (byte)0x82 == content[5]){//收到BB控制指令
                doReportBBControl(content);
                return;
            }
            if((byte)0xBB == content[1] && (byte)0xfe == content[2] && (byte)0x58 == content[4] && (byte)0x07 == content[5]){//收到空调组播状态
                doReportBBKTStatus(content);
                return;
            }
            return;
        }
        if((byte)0xBB == content[1] && (byte)0x58 == content[4] && (byte)0x11 == content[5]){//BB推送设备状态
            doBBReportDeviceListStatus(content);
            return;
        }
        if ((byte)0xCC == content[1] && (byte)0x01 == content[4] && (byte)0x01 == content[5]) {//CC推送设备列表
            doCCReportDeviceList(content);//CC推送设备列表
            return;
        }
        if ((byte)0xCC == content[1] && (byte)0x01 == content[4] && (byte)0x02 == content[5]) {//CC推送设备状态
            doCCReportDeviceStatus(content);//CC推送设备状态
            return;
        }
        if ((byte)0xCC == content[1] && (byte)0x01 == content[4] && (byte)0x0B == content[5]) {//CC推送设备更新信息
            doCCVersionUpdate(content);//CC推送设备更新信息
            return;
        }
        if ((byte)0xCC == content[1] && (byte)0x01 == content[4] && (byte)0x03 == content[5]) {//CC推送设备控制
            doCCControlUpad(content);//CC控制Upad
            return;
        }
        if ((byte)0xCC == content[1] && (byte)0x01 == content[4] && (byte)0x0D == content[5]) {//CC查询设备版本
            doCCQuaryVersion(content);//CC查询设备版本
            return;
        }
        if((byte)0xCC == content[1] && (byte)0x01 == content[4]){
            if((byte)0x04 == content[5]){//CC查询设备列表
                postCCCommandAck(CmdType.QuaryDeviceList,content);
            }else if((byte)0x05 == content[5]){//CC查询单个设备状态
                postCCCommandAck(CmdType.QuaryDeviceStatus,content);
            }else if((byte)0x06 == content[5]){//CC查询全量设备状态
                postCCCommandAck(CmdType.QuaryDeviceListStatus,content);
            }else if((byte)0x0A == content[5]){//CC查询房间信息
                postCCCommandAck(CmdType.GetRoomInfo,content);
            }
        }

    }

    private class ReadThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (!Thread.currentThread().isInterrupted()){
                try {
                    int available = mInputStream.available();
                    if(0 < available){
                        byte[] buffer = new byte[available];
                        int size = mInputStream.read(buffer);
                        step(buffer);
                    }else{
                        SystemClock.sleep(50);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.Log("SerialControl-ReadThread-IOException");
                    Thread.currentThread().interrupt();
                }
            }
            LogUtil.Log("SerialControl-ReadThread(结束)");
        }

        private int mPackLen = 0;
        private synchronized void step(byte[] content){
            LogUtil.Log("SerialControl-ReadThread-content:"+ByteUtil.convertBytes2HexString(content));
            for(int i = 0; i < content.length; i++){
                if(0 == mRealReadBuffer.position() && (byte)0x55 == content[i]){
                    mRealReadBuffer.put(content[i]);
                }else if(1 == mRealReadBuffer.position() && ((byte)0xBB == content[i] || (byte)0xCC == content[i])){
                    mRealReadBuffer.put(content[i]);
                }else if(1 < mRealReadBuffer.position()){
                    mRealReadBuffer.put(content[i]);
                    int position = mRealReadBuffer.position();
                    if(4 == position){
                        mPackLen = content[i] & 0xff;
                    }else if((4 + mPackLen) == position){
                        byte[] data = new byte[position];
                        mRealReadBuffer.rewind();
                        mRealReadBuffer.get(data,0,position);
                        mRealReadBuffer.clear();
                        doReadContent(data);
                    }
                }else{
                    LogUtil.Log("SerialControl-ReadThread-xxxxx");
                    mRealReadBuffer.clear();
                }
            }

        }
    }

    private class WriteThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (!Thread.currentThread().isInterrupted()) {
                if(null != mCurrentCommand){
                    continue;
                }
                mCurrentCommand = mBasket.poll();
                if(null != mCurrentCommand){
                    synchronized (mCurrentCommand){
                        LogUtil.Log("SerialControl-WriteThread-mCurrentCommand:"+mCurrentCommand);
                        try {
                            byte[] write = mCurrentCommand.getSends();
                            LogUtil.Log("SerialControl-WriteThread-write:"+ ByteUtil.convertBytes2HexString(write));
//                        CommonUtils.Write2File("sys/class/miscgpio/miscgpio/miscgpio","S1");
                            CommonUtils.write2File("/sys/devices/platform/misc_gpio/miscgpio","S1");
//                            CommonUtils.changeRXTX("echo s1 > /sys/devices/platform/misc_gpio/miscgpio");
                            mOutputStream.write(write);
                            if(null != mCurrentCommand && mCurrentCommand.isIgnore()){
                                mCurrentCommand = null;
                            }else{
                                mHandler.removeCallbacks(mTimeoutRunnable);
                                mHandler.postDelayed(mTimeoutRunnable,TIMEOUT);
                            }
                            SystemClock.sleep(150);
//                        CommonUtils.Write2File("sys/class/miscgpio/miscgpio/miscgpio","S0");
                            CommonUtils.write2File("/sys/devices/platform/misc_gpio/miscgpio","S0");
//                            CommonUtils.changeRXTX("echo s0 > /sys/devices/platform/misc_gpio/miscgpio");
                        } catch (IOException e) {
                            e.printStackTrace();
                            LogUtil.Log("SerialControl-WriteThread-IOException");
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
            LogUtil.Log("SerialControl-WriteThread-write(结束)");
        }
    }

    private void doCCControlUpad(byte[] content){
        int length = content[3] & 0xff;
        LogUtil.Log("SerialControl-doCCControlUpad-length:"+length);
        byte[] data = new byte[length-3];
        System.arraycopy(content,6,data,0,data.length);
        LogUtil.Log("SerialControl-doCCControlUpad-data:"+ByteUtil.convertBytes2HexString(data));
        try {
            CloudControlUpad.UPadControlRequest uPadControlRequest = CloudControlUpad.UPadControlRequest.parseFrom(data);
            ComMsg comMsg = new ComMsg(CmdType.CloudControlUpad,false,uPadControlRequest);
            report(comMsg);
        } catch (InvalidProtocolBufferException e) {
            LogUtil.Log("SerialControl-doCCControlUpad-InvalidProtocolBufferException:"+e.getMessage());
            e.printStackTrace();
        }
    }

    private void doCCQuaryVersion(byte[] content){
        LogUtil.Log("SerialControl-doCCQuaryVersion:"+content);
        ComMsg comMsg = new ComMsg(CmdType.PushVersion,false);
        report(comMsg);
    }

    private void doReportBBKTStatus(byte[] content){
        int length = content[3] & 0xff;
        LogUtil.Log("SerialControl-doBBControl-length:"+length);
        byte[] data = new byte[length-3];
        System.arraycopy(content,6,data,0,data.length);
        LogUtil.Log("SerialControl-doReportBBKTStatus-data:"+ByteUtil.convertBytes2HexString(data));
        try {
            ComMsg comMsg = new ComMsg(CmdType.ReportBBKTStatus,false,(data[0] & 0xff)+90,transKTStatusBB2CC(data));
            report(comMsg);
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.Log("SerialControl-doReportBBKTStatus-Exception:"+e.getMessage());
        }
    }

    private String transKTStatusBB2CC(byte[] datas){
/*        int switchStatusBB = datas[1] & 0xff;
        int modeBB = datas[2] & 0xff;
        int windBB = datas[3] & 0xff;
        int tempBB = datas[4] & 0xff;
        int currentBB = datas[5] & 0xff;*/
        KTControl ktControl = new KTControl(datas);
        return "AC"+ ByteUtil.convertBytes2HexString(ktControl.getCmd());
    }

    private void doReportBBControl(byte[] content){
        int length = content[3] & 0xff;
        LogUtil.Log("SerialControl-doBBControl-length:"+length);
        byte[] data = new byte[length-3];
        System.arraycopy(content,6,data,0,data.length);
        LogUtil.Log("SerialControl-doBBReportDeviceListStatus-data:"+ByteUtil.convertBytes2HexString(data));
        try {
            ComMsg comMsg = new ComMsg(CmdType.ReportBBControl,false,content[6],content[7]);
            report(comMsg);
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.Log("SerialControl-doReportBBControl-Exception:"+e.getMessage());
        }

    }

    private void doBBReportDeviceListStatus(byte[] content){
        int length = content[3] & 0xff;
        LogUtil.Log("SerialControl-doBBReportDeviceListStatus-length:"+length);
        byte[] data = new byte[length-3];
        System.arraycopy(content,6,data,0,data.length);
        LogUtil.Log("SerialControl-doBBReportDeviceListStatus-data:"+ByteUtil.convertBytes2HexString(data));
        LogUtil.Log("SerialControl-doBBReportDeviceListStatus-index:"+CommonUtils.getHBitIndexs(data));
        ComMsg comMsg = new ComMsg(CmdType.ReportBBNodeStatus,false,CommonUtils.getHBitIndexs(data));
        report(comMsg);
    }

    private void doCCVersionUpdate(byte[] content){
        int length = content[3] & 0xff;
        LogUtil.Log("SerialControl-doCCVersionUpdate-length:"+length);
        byte[] data = new byte[length-3];
        System.arraycopy(content,6,data,0,data.length);
        LogUtil.Log("SerialControl-doCCVersionUpdate-data:"+ByteUtil.convertBytes2HexString(data));
        try {
            CloudUpdate.UpdateAppRequest updateAppRequest = CloudUpdate.UpdateAppRequest.parseFrom(data);
            ComMsg comMsg = new ComMsg(CmdType.ReportCCVersionUpdate,false,updateAppRequest);
            report(comMsg);
        } catch (InvalidProtocolBufferException e) {
            LogUtil.Log("SerialControl-doCCVersionUpdate-InvalidProtocolBufferException:"+e.getMessage());
            e.printStackTrace();
        }
    }

    private void doCCReportDeviceStatus(byte[] content){
        int length = content[3] & 0xff;
        LogUtil.Log("SerialControl-doCCReportDeviceStatus-length:"+length);
        byte[] data = new byte[length-3];
        System.arraycopy(content,6,data,0,data.length);
        LogUtil.Log("SerialControl-doCCReportDeviceStatus-data:"+ByteUtil.convertBytes2HexString(data));
        try {
            CloudNodeStatus.NodeStatusModel nodeStatusModel = CloudNodeStatus.NodeStatusModel.parseFrom(data);
            ComMsg comMsg = new ComMsg(CmdType.ReportNodeStatus,false,nodeStatusModel);
            report(comMsg);
        } catch (InvalidProtocolBufferException e) {
            LogUtil.Log("SerialControl-doCCReportDeviceStatus-InvalidProtocolBufferException:"+e.getMessage());
            e.printStackTrace();
        }
    }

    private void doCCReportDeviceList(byte[] content){
        int length = content[3] & 0xff;
        LogUtil.Log("SerialControl-doCCReportDeviceList-length:"+length);
        byte[] data = new byte[length-3];
        System.arraycopy(content,6,data,0,data.length);
        LogUtil.Log("SerialControl-doCCReportDeviceList-data:"+ByteUtil.convertBytes2HexString(data));
        try {
            CloudNodeItem.TransNodePackItem transNodePackItem = CloudNodeItem.TransNodePackItem.parseFrom(data);
            int totalPageCount = transNodePackItem.getTotalPackageCount();
            int pageNum = transNodePackItem.getPackageNum();
            LogUtil.Log("SerialControl-doCCReportDeviceList-totalPageCount:"+totalPageCount+"; pageNum:"+pageNum);
            if(1 == pageNum){
                mNodeItemList.clear();
            }
            mNodeItemList.addAll(transNodePackItem.getNodeListList());
            if(totalPageCount == pageNum){
                LogUtil.Log("SerialControl-doCCReportDeviceList-mNodeItemList.size:"+mNodeItemList.size()+"; mNodeItemList:"+mNodeItemList);
                ComMsg comMsg = new ComMsg(CmdType.ReportDeviceList,false,mNodeItemList);
                report(comMsg);
            }
        } catch (InvalidProtocolBufferException e) {
            LogUtil.Log("SerialControl-step-InvalidProtocolBufferException:"+e.getMessage());
            e.printStackTrace();
        }
    }

    private Runnable mTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtil.Log("mTimeoutRunnable");
            if(null != mCurrentCommand){
                postTimeout();
                mCurrentCommand = null;
            }
        }
    };

    private void postBBCommandAck(CmdType cmdType,byte[] content){
        ComMsg comMsg = new ComMsg(cmdType,false,content[6] & 0xff,content[8] & 0xff);
        if(null != mCurrentCommand){
            comMsg = new ComMsg(mCurrentCommand.getCmdType(),false,content[6] & 0xff,content[8] & 0xff);
        }
        report(comMsg);
    }

    private void postCCCommandAck(CmdType cmdType,byte[] content){
        if(null == mCurrentCommand){
            return;
        }
        if(CmdType.QuaryDeviceList == cmdType){
            postDeviceList(content);
        }else if(CmdType.QuaryDeviceStatus == cmdType){
            postDeviceStatus(content);
        }else if(CmdType.QuaryDeviceListStatus == cmdType){
            postDeviceListStatus(content);
        }else if(CmdType.GetRoomInfo == cmdType){
            postGetRoomInfo(content);
        }
    }

    private void postDeviceList(byte[] content){
        int length = content[3] & 0xff;
        LogUtil.Log("SerialControl-postDeviceList-length:"+length);
        byte[] data = new byte[length-3];
        System.arraycopy(content,6,data,0,data.length);
        LogUtil.Log("SerialControl-postDeviceList-data:"+ByteUtil.convertBytes2HexString(data));
        try {
            CloudNodeItem.TransNodePackItem transNodePackItem = CloudNodeItem.TransNodePackItem.parseFrom(data);
            int totalPageCount = transNodePackItem.getTotalPackageCount();
            int pageNum = transNodePackItem.getPackageNum();
            LogUtil.Log("SerialControl-postDeviceList-totalPageCount:"+totalPageCount+"; pageNum:"+pageNum);
            if(1 == pageNum){
                mNodeItemList.clear();
            }
            mNodeItemList.addAll(transNodePackItem.getNodeListList());
            if(totalPageCount == pageNum){
                ComMsg comMsg = new ComMsg(CmdType.QuaryDeviceList,false,mNodeItemList);
                post(comMsg);
                mNodeItemList.clear();
            }
        } catch (InvalidProtocolBufferException e) {
            LogUtil.Log("SerialControl-step-InvalidProtocolBufferException:"+e.getMessage());
            e.printStackTrace();
        }
    }

    private void postDeviceStatus(byte[] content){
        int length = content[3] & 0xff;
        byte[] data = new byte[length-3];
        System.arraycopy(content,6,data,0,data.length);
        try {
            CloudNodeStatus.NodeStatusModel nodeStatusModel = CloudNodeStatus.NodeStatusModel.parseFrom(data);
            ComMsg comMsg = new ComMsg(CmdType.QuaryDeviceStatus,false,nodeStatusModel);
            post(comMsg);
        } catch (InvalidProtocolBufferException e) {
            LogUtil.Log("SerialControl-doCCCommandAck-InvalidProtocolBufferException:"+e.getMessage());
            e.printStackTrace();
        }
    }

    private void postGetRoomInfo(byte[] content){
        int length = content[3] & 0xff;
        byte[] data = new byte[length-3];
        System.arraycopy(content,6,data,0,data.length);
        try {
            CloudRoomInfo.RoomInfoResponse roomInfoResponse = CloudRoomInfo.RoomInfoResponse.parseFrom(data);
            ComMsg comMsg = new ComMsg(CmdType.GetRoomInfo,false,roomInfoResponse);
            post(comMsg);
        } catch (InvalidProtocolBufferException e) {
            LogUtil.Log("SerialControl-doCCCommandAck-InvalidProtocolBufferException:"+e.getMessage());
            e.printStackTrace();
        }
    }

    private void postDeviceListStatus(byte[] content){
        int length = content[3] & 0xff;
        byte[] data = new byte[length-3];
        System.arraycopy(content,6,data,0,data.length);
        try {
            CloudQuaryNodeListStatus.NodeStatusResponse nodeStatusResponse = CloudQuaryNodeListStatus.NodeStatusResponse.parseFrom(data);
            ComMsg comMsg = new ComMsg(CmdType.QuaryDeviceListStatus,false,nodeStatusResponse.getNodeStatusList());
            post(comMsg);
        } catch (InvalidProtocolBufferException e) {
            LogUtil.Log("SerialControl-doCCCommandAck-InvalidProtocolBufferException:"+e.getMessage());
            e.printStackTrace();
        }
    }

    private void postTimeout(){
        ComMsg comMsg = new ComMsg(mCurrentCommand.getCmdType(),true,mCurrentCommand);
        post(comMsg);
    }

    private void report(ComMsg comMsg){
        EventBus.getDefault().post(comMsg);
    }

    private void post(ComMsg comMsg){
        EventBus.getDefault().post(comMsg);
        mCurrentCommand = null;
    }

    private void report(List<CloudNodeItem.NodeItem> nodeItemList){
        EventBus.getDefault().post(nodeItemList);
    }

    private void report(byte[] content){
        EventBus.getDefault().post(content);
    }

}
