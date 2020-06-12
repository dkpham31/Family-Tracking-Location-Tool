package com.haroonstudios.familygpstracker.models;



public class User
{
    public String name;
    public String email;
    public String password;
    public String date;
    public String username;
    public String userid;
    public String issharing;
    public String lat;
    public String lng;
    public String profile_image;


    public User()
    {}

    public User(String name, String email, String password, String date,
                String username, String userid, String issharing, String lat,
                String lng, String profile_image) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.date = date;
        this.username = username;
        this.userid = userid;
        this.issharing = issharing;
        this.lat = lat;
        this.lng = lng;
        this.profile_image = profile_image;
    }


}
