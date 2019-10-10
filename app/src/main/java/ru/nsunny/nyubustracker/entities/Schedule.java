package ru.nsunny.nyubustracker.entities;
import java.util.List;
import java.util.ArrayList;

public class Schedule {

    private List<String> addresses;
    public List<String> getAddresses(){
        List<String> newList = new ArrayList<String>(addresses);
        return newList;
    }

    private List<List<ScheduleTime>> times;

    private String routeName;
    public String getRouteName(){
        return routeName;
    }

    public Schedule(String routeName, List<String> addresses, List<List<ScheduleTime>> times){
        this.addresses = addresses;
        this.times = times;
        this.routeName = routeName;
    }

    public List<ScheduleTime[]> getAllTimesBetweenAddresses (String src, String dest) throws IllegalArgumentException{
        if(!addresses.contains(src)) throw new IllegalArgumentException(src+" is not a known address for route "+routeName);
        if(!addresses.contains(dest)) throw new IllegalArgumentException(dest+" is not a known address for route "+routeName);

        int srcIndex = getAddresses().indexOf(src);
        int destIndex = getAddresses().indexOf(dest);

        List<ScheduleTime[]> result = new ArrayList<ScheduleTime[]>();
        // put time at source and dest to array for each route
        for(List<ScheduleTime> row:times){
            ScheduleTime[] times=new ScheduleTime[2];
            times[0] = row.get(srcIndex);
            times[1] = row.get(destIndex);

            result.add(times);
        }

        return result;
    }
}
