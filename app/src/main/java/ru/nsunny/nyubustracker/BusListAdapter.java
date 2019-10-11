package ru.nsunny.nyubustracker;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.nsunny.nyubustracker.entities.ScheduleTime;

public class BusListAdapter extends ArrayAdapter<ScheduleTime[]> {

    //to reference the Activity
    private final Activity context;

    //to store the list of arrival-departure times
    private final ScheduleTime[][] listOfTimePairs;

    public BusListAdapter(Activity context, List<ScheduleTime[]> listOfTimePairs){
        super(context,R.layout.bus_list_item , listOfTimePairs);
        this.context = context;
        this.listOfTimePairs = listOfTimePairs.toArray(new ScheduleTime[2][]);
    }
    @Override
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View listView=inflater.inflate(R.layout.bus_list_item, null,true);

        TextView departureTimeView = (TextView)listView.findViewById(R.id.text_departure);
        TextView arrivalTimeView = (TextView)listView.findViewById(R.id.text_arrival);

        departureTimeView.setText(listOfTimePairs[0][position].toString());
        arrivalTimeView.setText(listOfTimePairs[1][position].toString());

        return listView;
    }
}
