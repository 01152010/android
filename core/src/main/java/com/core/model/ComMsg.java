package com.core.model;

import com.core.enumeration.CmdType;

public class ComMsg {
    private boolean isTimeout;
    private CmdType cmdType;
    private Object[] obj;

    public ComMsg(CmdType cmdType, boolean isTimeout, Object... msg){
        this.cmdType = cmdType;
        this.isTimeout = isTimeout;
        this.obj = msg;
    }

    public boolean isTimeout() {
        return isTimeout;
    }

    public void setTimeout(boolean timeout) {
        isTimeout = timeout;
    }

    public CmdType getCmdType() {
        return cmdType;
    }

    public void setCmdType(CmdType cmdType) {
        this.cmdType = cmdType;
    }

    public Object[] getObj() {
        return obj;
    }

    public void setObj(Object[] obj) {
        this.obj = obj;
    }
}
