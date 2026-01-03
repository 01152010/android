package com.upad.enumeration;

public enum NSQMsgType {
    Screen("Screen"),//打开灯
    Upad_Launcher("Upad_Launcher"),
    Upad_Core("Upad_Core"),
    Upad_OS("Upad_OS"),
    Welcome("welcome");
    private final String type;
    NSQMsgType(String type){
        this.type = type;
    }
    public String getType(){
        return type;
    }
}
