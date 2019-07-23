package com.summerjob.neoenergia3.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

public class Device implements Serializable {

    private String uid;
    private String token;
    private String location;
    private List<WiFi> wifiList;
    private String timestamp;

    public Device() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<WiFi> getWifiList() {
        return wifiList;
    }

    public void setWifiList(List<WiFi> wifiList) {
        this.wifiList = wifiList;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
