package com.upad.enumeration;

public enum VoiceLineCmd {
    LightOpen("usay.ctrl.light.open"),//打开灯
    LightClose("usay.ctrl.light.close"),//关闭灯
    SceneOpen("usay.ctrl.scene.open"),//打开场景
    SceneClose("usay.ctrl.scene.close"),//关闭场景
    SceneSwitch("usay.ctrl.scene.switch"),//切换场景
    CLOpen("usay.ctrl.blind.open"),//打开窗帘
    CLClose("usay.ctrl.blind.close"),//关闭窗帘
    CLPause("usay.ctrl.blind.pause"),//暂停窗帘
    AirOpen("usay.ctrl.air.open"),//打开空调
    AirClose("usay.ctrl.air.close"),//关闭空调
    AirMode("usay.ctrl.air.mode"),//设置空调模式
    AirTemp("usay.ctrl.air.temperature"),//设置空调温度
    AirWindSpeed("usay.ctrl.air.windspeed"),//设置空调风速
    TVOpen("upad.ctrl.tv.open"),//打开电视
    TVClose("upad.ctrl.tv.close"),//关闭电视
    TVVolume("upad.ctrl.tv.volume"),//音量设置
    VOL_UP("upad.volumeController.up"),//音量加
    VOL_DN("upad.volumeController.dn"),//音量减
    VOL_MUTE("upad.volumeController.mute"),//静音
    TVChanel("upad.ctrl.tv.chanel"),//切换频道
    TVChanel2("upad.ctrl.tv.chanel2"),//切换频道

    MUSICPLAY("music.play"),//QQ音乐
    MUSICCTRL("media.ctrl"),//QQ音乐控制
    MUSICNEXT("DUI.MediaController.Next");//QQ音乐控制

    private String cmd;
    VoiceLineCmd(String cmd){
        this.cmd = cmd;
    }
    public String getCmd(){
        return cmd;
    }
}
