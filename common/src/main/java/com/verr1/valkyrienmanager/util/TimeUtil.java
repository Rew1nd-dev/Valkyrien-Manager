package com.verr1.valkyrienmanager.util;

import java.text.SimpleDateFormat;

public class TimeUtil {

    public static final SimpleDateFormat Date_Formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String parse(long timestamp){
        return Date_Formatter.format(timestamp);
    }

    public static String parseAgo(long timestamp){
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - timestamp;

        long seconds = (diff / 1000) % 60;
        long minutes = (diff / (1000 * 60)) % 60;
        long hours = (diff / (1000 * 60 * 60)) % 24;
        long days = diff / (1000 * 60 * 60 * 24);

        return String.format("%02d %02d:%02d:%02d ago", days, hours, minutes, seconds);

    }

}
