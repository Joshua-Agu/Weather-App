package com.example.benjiweather;

public class WeatherBuildUp {

    private String Time;
    private String Tempterature;
    private String  Icon;
    private String GaleSpeed;

    public WeatherBuildUp(String time, String tempterature, String icon, String galeSpeed) {
        Time = time;
        Tempterature = tempterature;
        Icon = icon;
        GaleSpeed = galeSpeed;
    }

    public String getTime() {
        return Time;
    }

    public String getTempterature() {
        return Tempterature;
    }

    public String getIcon() {
        return Icon;
    }

    public String getGaleSpeed() {
        return GaleSpeed;
    }
}
