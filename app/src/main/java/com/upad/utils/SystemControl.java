package com.upad.utils;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.PowerManager;

public class SystemControl {
    private static SystemControl mSystemControl;
    private PowerManager.WakeLock mWakeLock;
    private DevicePolicyManager mPolicyManager;
    private PowerManager mPowerManager;

    public SystemControl(Context context) {
        mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mPolicyManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, this.getClass().getName());
    }

    public static SystemControl getInstance(Context context) {
        if (null == mSystemControl) {
            mSystemControl = new SystemControl(context);
        }
        return mSystemControl;
    }

    public void reboot(){
        mPowerManager.reboot(null);
    }

    public void acquireWakeLock() {//亮屏
        if(!mWakeLock.isHeld()) {
            LogUtil.Log("acquireWakeLock");
            mWakeLock.acquire();
        }
    }

    public boolean isHeld(){
        return mWakeLock.isHeld();
    }

    public boolean isScreenOn(){
        return mPowerManager.isScreenOn();
    }

    public void releaseWakeLock() {//息屏
        try{
            mPolicyManager.lockNow();
        }catch (SecurityException e){
            e.printStackTrace();
            LogUtil.Log("WakeLockUtil-SecurityException");
        }
        if (null != mWakeLock && mWakeLock.isHeld()) {
            LogUtil.Log("releaseWakeLock");
            mWakeLock.release();
        }
    }
}

