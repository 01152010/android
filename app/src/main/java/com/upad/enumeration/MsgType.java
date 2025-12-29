package com.upad.enumeration;

public enum MsgType {
    Version("Version"),
    OTA("OTA");
    private String type;
    MsgType(String type){
        this.type = type;
    }
    public String getType(){
        return type;
    }
}
