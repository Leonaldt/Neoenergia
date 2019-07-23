package com.summerjob.neoenergia3.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

public class WiFi implements Serializable {

    private String name;
    private String frequency;
    private String strength;

    public WiFi() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    @Override
    public String toString() {
        return this.name+", "+this.frequency+", "+this.strength;
    }
}
