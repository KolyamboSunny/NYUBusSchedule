package ru.nsunny.nyubustracker.entities;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

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

    private List<ScheduleTime[]> addToTop(ScheduleTime[] timePair, List<ScheduleTime[]> top, int num, boolean compareByLeaveTime, boolean topEarliest){
        int elementFromPair = 0;
        if(!compareByLeaveTime) elementFromPair=1;

        if(top.size()<num) {
            top.add(timePair);
        }
        else{
            for(ScheduleTime[] pairInEarly : top)
                if(topEarliest)
                    if(timePair[elementFromPair].minuteDiff(pairInEarly[elementFromPair]) > 0){
                        top.remove(pairInEarly);
                        top.add(0,timePair);
                        break;
                    }
                else{
                    if(timePair[elementFromPair].minuteDiff(pairInEarly[elementFromPair]) < 0){
                        top.remove(pairInEarly);
                        top.add(timePair);
                        break;
                    }
                }
        }
        Collections.sort(top,new CompareTimes(elementFromPair));
        return top;
    }

    public List<List<ScheduleTime[]>> getTimesByArrivalTime (String src, String dest, ScheduleTime timeToArrive, int numEarlier, int numLater) throws IllegalArgumentException{
        List<ScheduleTime[]> allTimes = getAllTimesBetweenAddresses(src,dest);

        List<ScheduleTime[]> early = new ArrayList<>(numEarlier);
        List<ScheduleTime[]> late = new ArrayList<>(numLater);

        for(ScheduleTime[] timePair : allTimes){
            ScheduleTime departureTime = timePair[0];
            ScheduleTime arrivalTime = timePair[1];
            if(departureTime.isEmpty || arrivalTime.isEmpty)
                continue;

            // this time is earlier than arrival: minuteDiff > 0
            int timeDiff = timeToArrive.minuteDiff(timePair[1]);
            if(timeDiff>0){
                addToTop(timePair,early,numEarlier,false,true);
            }
            else{
                addToTop(timePair,late,numLater,false,false);
            }
        }
        List<List<ScheduleTime[]>> earlyAndLate = new ArrayList<List<ScheduleTime[]>>();
        earlyAndLate.add(0,early);
        earlyAndLate.add(1,late);
        return earlyAndLate;
    }

    public List<List<ScheduleTime[]>> getTimesByLeaveTime (String src, String dest, ScheduleTime timeToLeave, int numEarlier, int numLater) throws IllegalArgumentException{
        List<ScheduleTime[]> allTimes = getAllTimesBetweenAddresses(src,dest);

        List<ScheduleTime[]> early = new ArrayList<>(numEarlier);
        List<ScheduleTime[]> late = new ArrayList<>(numLater);

        for(ScheduleTime[] timePair : allTimes){
            ScheduleTime departureTime = timePair[0];
            ScheduleTime arrivalTime = timePair[1];
            if(departureTime.isEmpty || arrivalTime.isEmpty)
                continue;

            // this time is earlier than arrival: minuteDiff > 0
            int timeDiff = timeToLeave.minuteDiff(timePair[1]);
            if(timeDiff>0){
                addToTop(timePair,early,numEarlier,true,true);
            }
            else{
                addToTop(timePair,late,numLater,true,false);
            }
        }
        List<List<ScheduleTime[]>> earlyAndLate = new ArrayList<List<ScheduleTime[]>>();
        earlyAndLate.add(0,early);
        earlyAndLate.add(1,late);
        return earlyAndLate;
    }

    private class CompareTimes implements Comparator<ScheduleTime[]>{
        public int elementInPair =0;
        public CompareTimes(int elementInPair){
            this.elementInPair=elementInPair;
        }
        @Override
        public int compare(ScheduleTime[]a, ScheduleTime[] b){
            return a[elementInPair].minuteDiff(b[elementInPair]);
        }
    }
}
