package com.example.anish.myapplication;

/**
 * Created by anish on 21/8/14.
 */
public class UserLoc {
    Double lat, lng;
    Float speed;
    int userId;

    UserLoc(int userId, Double lat, Double lng, Float speed){
        this.userId = userId;
        this.lat = lat;
        this.lng =lng;
        this.speed = speed;
    }
}
