package com.upad.model;

import java.io.Serializable;

public class RoomInfo implements Serializable {
    private String floor;
    private String hotelMainID;
    private int id;
    private String roomConfigID;
    private String roomNo;
    private String roomTypeID;
    private int state;
    private String tel;
    private String updatedBy;
    private String updatedTime;
    private boolean useXCtrl;

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getHotelMainID() {
        return hotelMainID;
    }

    public void setHotelMainID(String hotelMainID) {
        this.hotelMainID = hotelMainID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomConfigID() {
        return roomConfigID;
    }

    public void setRoomConfigID(String roomConfigID) {
        this.roomConfigID = roomConfigID;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getRoomTypeID() {
        return roomTypeID;
    }

    public void setRoomTypeID(String roomTypeID) {
        this.roomTypeID = roomTypeID;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public boolean isUseXCtrl() {
        return useXCtrl;
    }

    public void setUseXCtrl(boolean useXCtrl) {
        this.useXCtrl = useXCtrl;
    }
}
