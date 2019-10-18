package ru.nsunny.nyubustracker.entities;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Route {
    public final String routeName;

    private final List<String> orderedStops = new ArrayList<String>();
    public void addStop(String stop){
        orderedStops.add(stop);
    }
    public void addAllStops(Collection<String> stops){
        orderedStops.addAll(stops);
    }
    public List<String> getOrderedStops(){
        return new ArrayList<String>(orderedStops);
    }

    public final String googleSheetsLink;

    private final Map<String, Schedule> scheduleForDays = new HashMap<>();
    public void addScheduleForDay(String day, Schedule schedule){
        scheduleForDays.put(day,schedule);
        if (this.orderedStops.size()==0)
            this.orderedStops.addAll(schedule.getAddresses());
        else {
            // compare if the schedule has exactly the same stops in the same order as the previous ones
            Iterator stopInRouteIter = this.orderedStops.iterator();
            for(String stopInSchedule: schedule.getAddresses()){
                String stopInRoute = stopInRouteIter.next().toString();
                boolean stopsMatch = stopInSchedule.equals(stopInRoute);
                if(!stopsMatch)
                    Log.d("FAILURE", this.routeName+"has stops not like in the schedule: schedule - "+stopInSchedule+" but route - "+stopInRoute);
            }
        }
    }
    public Schedule getScheduleForDay(String day){
        return scheduleForDays.get(day);
    }
    public Map<String, Schedule> getAllSchedules(){
        return new HashMap<String, Schedule>(scheduleForDays);
    }

    public Route(String name, String googleLink){
        this.routeName = name;
        this.googleSheetsLink = googleLink;
    }
}
