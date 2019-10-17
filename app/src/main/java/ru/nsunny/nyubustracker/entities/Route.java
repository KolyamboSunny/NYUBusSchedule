package ru.nsunny.nyubustracker.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
