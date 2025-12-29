package com.core.model;

import com.core.utils.ByteUtil;
import com.core.utils.LogUtil;

public class KTControl extends Control {
    private int mode;//模式
    private int windSpeed;//风速
    private int windDirection;//风向
    private int setTemp;//设置当前温度
    private int currentTemp;//当前温度

    public KTControl(int switchStatus) {
        super(switchStatus);
    }

    public KTControl(byte[] datas) {
        super(datas[1] & 0xff);
        int modeBB = datas[2] & 0xff;
        int windBB = datas[3] & 0xff;
        int tempBB = datas[4] & 0xff;
        int currentBB = datas[5] & 0xff;
        this.mode = transHWMode(modeBB);
        this.windSpeed = transHWWind(windBB);
        this.setTemp = tempBB;
        this.currentTemp = currentBB;
    }


    public KTControl(int switchStatus, int mode, int windSpeed, int windDirection, int setTemp, int currentTemp) {
        super(switchStatus);
        this.mode = mode;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.setTemp = setTemp;
        this.currentTemp = currentTemp;
    }

    public byte[] getCmd(){
        return new byte[]{(byte)setTemp,(byte)windSpeed,(byte)0x02,(byte)0x01,(byte)switchStatus,(byte)0x01,(byte)mode};
    }

    public byte[] get485Cmd(){
        return new byte[]{(byte)switchStatus,trans485Mode(mode),trans485Wind(windSpeed),(byte)setTemp,(byte)currentTemp};
    }

    private int transHWWind(int wind485){
        int wind = 3;
        switch (wind485){
            case 0://低速
                wind = 2;
                break;
            case 1://中速
                wind = 3;
                break;
            case 2://高速
                wind = 4;
                break;
            case 3://自动
                wind = 1;
                break;
            default:break;
        }
        return wind;
    }

    private int transHWMode(int mode485){
        int mode = 4;
        switch (mode485){
            case 0://通风
                mode = 4;
                break;
            case 1://制冷
                mode = 2;
                break;
            case 2://制热
                mode = 5;
                break;
            case 3://除湿
                mode = 3;
                break;
            default:break;
        }
        return mode;
    }

    private byte trans485Wind(int hwWind){
        byte wind = (byte)0x01;
        switch (hwWind){
            case 1://自动
                wind = (byte)0x03;
                break;
            case 2://低速
                wind = (byte)0x00;
                break;
            case 3://中速
                wind = (byte)0x01;
                break;
            case 4://高速
                wind = (byte)0x02;
                break;
            default:break;
        }
        return wind;
    }

    private byte trans485Mode(int hwMode){
        byte mode = (byte)0x00;
        switch (hwMode){
            case 2://制冷
                mode = (byte)0x01;
                break;
            case 3://除湿
                mode = (byte)0x03;
                break;
            case 4://通风
                mode = (byte)0x00;
                break;
            case 5://制热
                mode = (byte)0x02;
                break;
            default:break;
        }
        return mode;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(int windDirection) {
        this.windDirection = windDirection;
    }

    public int getSetTemp() {
        return setTemp;
    }

    public void setSetTemp(int setTemp) {
        this.setTemp = setTemp;
    }

    public int getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(int currentTemp) {
        this.currentTemp = currentTemp;
    }
}
