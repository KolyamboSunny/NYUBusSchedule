package ru.nsunny.nyubustracker.entities;
import android.util.Log;
public class ScheduleTime implements Comparable<ScheduleTime>{
    public int hour=-1;
    public int minute=-1;
    public boolean isEmpty=true;

    public ScheduleTime(String rawTime){
        try {
            //time when the shuttle is not available is represented with "-"
            if (rawTime.equals("-")||rawTime.equals("")||rawTime.equals(" "))
                this.isEmpty = true;
            else {
                // time format is "HH:MM AM", trying to parse this one
                String rawHours = rawTime.split(":")[0];
                String rawMinutes = rawTime.split(":")[1].split(" ")[0];
                String amPm = rawTime.split(":")[1].split(" ")[1];

                //convert presented time to military format
                this.hour = Integer.parseInt(rawHours);
                if (amPm.toUpperCase().equals("PM")&&hour!=12)
                    this.hour += 12;
                this.minute = Integer.parseInt(rawMinutes);
                this.isEmpty = false;
            }
        }catch(Exception e){
            Log.d("FAILED.","Could not parse time: "+rawTime,e);
            this.isEmpty=true;
        }
    }
    public ScheduleTime(int hour, int minute){
        if (hour<0 || hour >24 || minute < 0 || minute>=60){
            Log.d("FAILED.","Provided time in invalid");
            this.isEmpty = true;
        }
        else{
            this.isEmpty = false;
            this.hour = hour;
            this.minute = minute;
        }

    }
    public int minuteDiff(ScheduleTime other) throws IllegalArgumentException{
        if(this.isEmpty) throw new IllegalArgumentException("Left function operand (callee) is empty");
        if(other.isEmpty) throw new IllegalArgumentException("Right function operand (argument) is empty");
        //
        return this.minute - other.minute + (this.hour-other.hour)*60;
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof ScheduleTime)) return false;
        return this.minuteDiff((ScheduleTime) other) ==0;
    }

    @Override
    public String toString(){
        if(isEmpty) return "-";
        String result="";
        if(hour<10) result+="0";
        result+=hour+":";

        if(minute<10) result+="0";
        result+=minute;

        return result;
    }

    @Override
    public int compareTo(ScheduleTime other){
        return this.minuteDiff(other);
    }
}
