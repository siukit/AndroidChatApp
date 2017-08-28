package com.siukit.chatapp;

/**
 * Created by siukit on 25/02/2017.
 */

public class User {

    private String username;
    private String location;
    private String inviteFrom;
    private String inviteAcpt;
    private Double lon;
    private Double lat;
    private String isShake;
    private String image;
//    private String age;


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIsShake() {
        return isShake;
    }

    public void setIsShake(String isShake) {
        this.isShake = isShake;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getInviteFrom() {
        return inviteFrom;
    }

    public void setInviteFrom(String inviteFrom) {
        this.inviteFrom = inviteFrom;
    }

    public String getInviteAcpt() {
        return inviteAcpt;
    }

    public void setInviteAcpt(String inviteAcpt) {
        this.inviteAcpt = inviteAcpt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }




}
