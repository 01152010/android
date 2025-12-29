package com.core.model;

import com.core.Protocol;
import com.core.enumeration.CmdType;
import com.core.enumeration.ProtocolType;
import com.core.proto.CloudControl;
import com.core.proto.CloudNodeItem;
import com.core.proto.CloudUpdateApp;
import com.core.proto.CloudVersionInfo;
import com.core.utils.ByteUtil;
import com.core.utils.Constants;
import com.core.utils.LogUtil;

import java.io.Serializable;

public class Command implements Serializable {
    private int address;
    private int nodeNo;
    private CloudNodeItem.NodeType nodeType;
    private Control control;
    private String msg;
    private CloudUpdateApp.UpdateStatus updateStatus;
    private CmdType cmdType;
    private int cmdId;
    private byte[] sends;
    private byte[] acks;
    private byte[] datas;
    private Object obj;
    private boolean ignore = true;

    public ProtocolType getProtoType(){
        if(CmdType.ControlCurtain == cmdType || CmdType.ControlLock == cmdType || CmdType.ControlTV == cmdType || CmdType.ControlHWAC == cmdType ||
                CmdType.QuaryDeviceList == cmdType || CmdType.QuaryDeviceStatus == cmdType || CmdType.QuaryDeviceListStatus == cmdType ||
                CmdType.GetRoomInfo == cmdType || CmdType.PushVersion == cmdType  || CmdType.PushVersionUpdateStatus == cmdType){
            return ProtocolType.CC;
        }
        return ProtocolType.BB;
    }

    public byte[] getHeadCmd() {
        if(CmdType.QuaryDeviceList == cmdType){
            return new byte[]{(byte)0x02,(byte)0x04};
        }
        if(CmdType.QuaryDeviceStatus == cmdType){
            return new byte[]{(byte)0x02,(byte)0x05};
        }
        if(CmdType.QuaryDeviceListStatus == cmdType){
            return new byte[]{(byte)0x02,(byte)0x06};
        }
        if(CmdType.GetRoomInfo == cmdType){
            return new byte[]{(byte)0x02,(byte)0x0A};
        }
        if(CmdType.PushVersion == cmdType){
            return new byte[]{(byte)0x02,(byte)0x0D};
        }
        if(CmdType.PushVersionUpdateStatus == cmdType){
            return new byte[]{(byte)0x02,(byte)0x0B};
        }
        if(CmdType.ControlCurtain == cmdType || CmdType.ControlLock == cmdType ||
                CmdType.ControlTV == cmdType || CmdType.ControlHWAC == cmdType){
            return new byte[]{(byte)0x02,(byte)0x07};
        }
        return new byte[]{(byte)0x5a,(byte)0x82};
    }

    public byte[] toDataBytes(){
        if(CmdType.QuaryDeviceList == cmdType || CmdType.QuaryDeviceStatus == cmdType ||
            CmdType.QuaryDeviceListStatus == cmdType || CmdType.GetRoomInfo == cmdType){
            return new byte[]{};
        }
        if(CmdType.ControlCurtain == cmdType || CmdType.ControlLock == cmdType ||
                CmdType.ControlTV == cmdType || CmdType.ControlHWAC == cmdType ||
                CmdType.PushVersion == cmdType || CmdType.PushVersionUpdateStatus == cmdType){
            return getProtoDataBytes();
        }
        if(CmdType.Control485AC == cmdType){
            KTControl ktControl = (KTControl)control;
            byte[] head = new byte[]{(byte)nodeNo,getBBNodeType(cmdType),(byte)(nodeNo-90)};
            byte[] temp = ktControl.get485Cmd();
            return ByteUtil.copyArray(head,temp);
        }
        return getDataBytes();
    }

    private byte[] getDataBytes(){
        LogUtil.Log("Command-getDataBytes-cmdType:"+cmdType);
        return new byte[]{(byte)nodeNo,getBBNodeType(cmdType),(byte)control.getSwitchStatus()};
    }

    private byte getBBNodeType(CmdType cmdType){
        byte result = (byte)0x01;//继电器(灯光)
        if(CmdType.ControlScene == cmdType){
            result = (byte)0x02;
        }else if(CmdType.Control485AC == cmdType){
            result = (byte)0x03;
        }else if(CmdType.ControlCurtain == cmdType){
            result = (byte)0x04;
        }
        return result;
    }

    private byte[] getProtoDataBytes(){
        if(CmdType.ControlHWAC == cmdType){
            KTControl ktControl = (KTControl)control;
            byte[] cmd = ktControl.getCmd();
            LogUtil.Log("Command-getProtoDataBytes-ControlAC:"+ByteUtil.convertBytes2HexString(cmd));
            CloudControl.NodeControlRequest nodeControlRequest = CloudControl.NodeControlRequest.newBuilder().clear()
                    .setNodeNo(nodeNo)
                    .setAction("AC"+ ByteUtil.convertBytes2HexString(cmd)).build();
            return nodeControlRequest.toByteArray();
        }else if(CmdType.ControlTV == cmdType){
            TVControl tvControl = (TVControl)control;
            LogUtil.Log("Command-getProtoDataBytes-ControlTV:"+tvControl.getCmd());
            CloudControl.NodeControlRequest nodeControlRequest = CloudControl.NodeControlRequest.newBuilder().clear()
                    .setNodeNo(nodeNo)
                    .setAction(tvControl.getCmd()).build();
            return nodeControlRequest.toByteArray();
        }else if(CmdType.PushVersion == cmdType){
            CloudVersionInfo.VersionInfo versionInfo = CloudVersionInfo.VersionInfo.newBuilder().clear()
                    .setVersionDes(msg).build();
            return versionInfo.toByteArray();
        }else if(CmdType.PushVersionUpdateStatus == cmdType){
            CloudUpdateApp.UpdateAppResponse updateAppResponse = CloudUpdateApp.UpdateAppResponse.newBuilder().clear()
                    .setStatus(updateStatus).build();
            return updateAppResponse.toByteArray();
        }
        LogUtil.Log("Command-getProtoDataBytes-Control:"+control.getSwitchStatus());
        CloudControl.NodeControlRequest nodeControlRequest = CloudControl.NodeControlRequest.newBuilder().clear()
                .setNodeNo(nodeNo)
                .setAction(""+control.getSwitchStatus()).build();
        return nodeControlRequest.toByteArray();
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getNodeNo() {
        return nodeNo;
    }

    public void setNodeNo(int nodeNo) {
        this.nodeNo = nodeNo;
    }

    public CloudNodeItem.NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(CloudNodeItem.NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public Control getControl() {
        return control;
    }

    public void setControl(Control control) {
        this.control = control;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public CloudUpdateApp.UpdateStatus getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(CloudUpdateApp.UpdateStatus updateStatus) {
        this.updateStatus = updateStatus;
    }

    public CmdType getCmdType() {
        return cmdType;
    }

    public void setCmdType(CmdType cmdType) {
        this.cmdType = cmdType;
    }

    public int getCmdId() {
        return cmdId;
    }

    public void setCmdId(int cmdId) {
        this.cmdId = cmdId;
    }

    public byte[] getSends() {
        return sends;
    }

    public void setSends(byte[] sends) {
        this.sends = sends;
    }

    public byte[] getAcks() {
        return acks;
    }

    public void setAcks(byte[] acks) {
        this.acks = acks;
    }

    public byte[] getDatas() {
        return datas;
    }

    public void setDatas(byte[] datas) {
        this.datas = datas;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }
}
