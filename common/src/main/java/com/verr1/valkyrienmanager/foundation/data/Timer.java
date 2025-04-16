package com.verr1.valkyrienmanager.foundation.data;

public class Timer {

    private int ticks = 0;
    private int seconds = 0;
    private int minutes = 0;
    private int hours = 0;

    public Timer(){

    }

    public void reset(){
        ticks = 0;
        seconds = 0;
        minutes = 0;
        hours = 0;
    }

    public String getTime(){
        return hours + ":" + minutes + ":" + seconds;
    }

    public String ago(long tick){

        long ago = -(tick - (int)getSeconds());
        long agoSeconds = ago % 60;
        long agoMinutes = (ago / 60) % 60;
        long agoHours = ago / 3600;
        return agoHours + "h: " + agoMinutes + "m: " + agoSeconds + "s";
    }

    public double getHours(){
        return hours + (minutes / 60.0) + (seconds / 3600.0);
    }

    public double getMinutes(){
        return (hours * 60) + minutes + (seconds / 60.0);
    }

    public double getSeconds(){
        return (hours * 3600) + (minutes * 60) + seconds;
    }


    public void tick(){
        ticks++;
        if(ticks >= 20){
            ticks = 0;
            seconds++;
            if(seconds >= 60){
                seconds = 0;
                minutes++;
                if(minutes >= 60){
                    minutes = 0;
                    hours++;
                }
            }
        }
    }
}
