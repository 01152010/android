package com.upad.model;

import java.io.Serializable;

public class UpdateVersion implements Serializable {
    private String appType;
    private String versionName;
    private String versionDesc;
    private String url;

    public UpdateVersion(String appType,String url,String versionName){
        this.appType = appType;
        this.url = url;
        this.versionName = versionName;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionDesc() {
        return versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
