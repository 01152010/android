package com.core.model;

import java.io.Serializable;

public class Control implements Serializable {
    protected int switchStatus;
    public Control(int switchStatus){
        this.switchStatus = switchStatus;
    }
    public int getSwitchStatus() {
        return switchStatus;
    }

    public void setSwitchStatus(int switchStatus) {
        this.switchStatus = switchStatus;
    }
}
