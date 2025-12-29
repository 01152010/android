package com.upad;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.upad.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class APP extends Application {
    private static Context mContext;
    private List<Activity> mActivities = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.Log("APP-onCreate()");
        mContext = this;
//        setDeviceNo();
//        initValue();
//        initDDS();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LogUtil.Log("APP-onTerminate()");
//        disableWakeup();
    }

    private void initValue(){
        Beta.autoInit = true;
        Beta.autoCheckUpgrade = true;
        Beta.upgradeCheckPeriod = 30 * 1000;
        Beta.showInterruptedStrategy = true;
        Beta.initDelay = 0;
        Bugly.init(getApplicationContext(), "b0b3fbfe1b", false);
    }

    public static Context getContext() {
        return mContext;
    }

    public void setDeviceNo(){

    }
    private void savePref(String deviceNum){
    }

    public void addActivity(Activity activity) {
        mActivities.add(activity);
    }
    public void removeActivity(Activity activity) {
        mActivities.remove(activity);
    }

    public boolean isTopActivity(Activity activity){
        if(0 == mActivities.size()){
            return false;
        }
        Activity topActivity = mActivities.get(mActivities.size()-1);
        return topActivity.getComponentName().getClassName().contains(activity.getComponentName().getClassName());
    }

    public boolean isDialogShowing(){
        if(0 == mActivities.size()){
            return false;
        }
        Activity topActivity = mActivities.get(mActivities.size()-1);
        return topActivity.getComponentName().getClassName().contains("VoiceDialogActivity");
    }
}
