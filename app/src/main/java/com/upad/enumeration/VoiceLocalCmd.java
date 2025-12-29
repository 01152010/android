package com.upad.enumeration;

public enum VoiceLocalCmd {
    LightOpen("xz.deviceController.openLight"),//打开灯
    LightClose("xz.deviceController.closeLight"),//关闭灯
    CLOpen("xz.deviceController.openBlind"),//打开窗帘
    CLClose("xz.deviceController.closeBlind"),//关闭窗帘
    CLPause("xz.deviceController.pauseBlind"),//暂停窗帘
    AirOpen("xz.deviceController.openAir"),//打开空调
    AirClose("xz.deviceController.closeAir"),//关闭空调
    AirMode("xz.deviceController.airMode"),//设置空调模式
    AirTemp("xz.acController.absoluteTemp"),//设置空调绝对温度
    AirWindSpeed("xz.deviceController.airWindspeed"),//设置空调风速

    TVOpen("xz.tvController.open"),//打开电视
    TVClose("xz.tvController.close"),//关闭电视
    TVVolume("xz.tvController.volume"),//音量设置
    TVChanel("xz.tvController.chanel"),//切换频道
    TVChanel2("xz.tvController.chanel2"),//切换频道

    VOL_UP("xz.volumeController.up"),//音量加
    VOL_DN("xz.volumeController.dn"),//音量减
    VOL_MUTE("xz.volumeController.mute"),//静音


    SceneOpen("xz.deviceController.openScene"),//打开场景
    SceneSwitch("xz.deviceController.switchScene"),//切换场景
    SceneClose("xz.deviceController.closeScene"),//关闭场景

    MUSICPlay("DUI.MediaController.Play"),//播放
    MUSICPause("DUI.MediaController.Pause"),//暂停
    MUSICPrev("DUI.MediaController.Prev"),//上一首
    MUSICStop("DUI.MediaController.Stop"),//停止播放
    MUSICClose("DUI.MediaController.CloseFullScreen"),//停止播放
    MUSICCForward("DUI.MediaController.Forward"),//快进
    MUSICCBackward("DUI.MediaController.Backward"),//快退
    MUSICNext("DUI.MediaController.Next"),//下一首
    SetVolume("DUI.MediaController.SetVolume"),//设置音量
    OpenMode("DUI.System.Sounds.OpenMode"),//设置音量
    Exit("DUI.System.Exit");//退出

    private String cmd;
    VoiceLocalCmd(String cmd){
        this.cmd = cmd;
    }
    public String getCmd(){
        return cmd;
    }
}
