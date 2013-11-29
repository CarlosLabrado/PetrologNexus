package com.petrologautomation.petrolognexus.database;

/**
 * Created by Carlos Labrado on 8/28/13.
 */
public class PetrologMarker {
    private long id;
    private int serial;
    private String comment;
    private double lat;
    private double lng;
    private String bluetooth;
    private String wifiAddress;
    private String wifiPassword;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return comment;
    }

    public String getBluetooth() {
        return bluetooth;
    }

    public void setBluetooth(String bluetooth) {
        this.bluetooth = bluetooth;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getWifiAddress() {
        return wifiAddress;
    }

    public void setWifiAddress(String wifiAddress) {
        this.wifiAddress = wifiAddress;
    }

    public String getWifiPassword() {
        return wifiPassword;
    }

    public void setWifiPassword(String wifiPassword) {
        this.wifiPassword = wifiPassword;
    }
}
