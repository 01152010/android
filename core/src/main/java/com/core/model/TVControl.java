package com.core.model;

public class TVControl extends Control{
    private String cmd;

    public TVControl(int switchStatus, String cmd) {
        super(switchStatus);
        this.cmd = cmd;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
}
