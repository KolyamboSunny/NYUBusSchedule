package ru.nsunny.nyubustracker;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.nsunny.nyubustracker.entities.ScheduleTime;

public class BusListAdapter extends BaseAdapter {

    //to reference the Activity
    private Activity context;

    //to store the list of arrival-departure times
    private List<ScheduleTime[]> timePairs = new ArrayList<ScheduleTime[]>();
    public void clearTimePairs(){
        timePairs.clear();
        this.notifyDataSetChanged();
    }
    public void setTimePairs(List<ScheduleTime[]> listOfTimePairs){
        this.timePairs = listOfTimePairs;
        this.notifyDataSetChanged();
    }

    public BusListAdapter(Activity context){
        this.context = context;
    }
    @Override
    public int getCount() {
        return timePairs.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return timePairs.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View listView=inflater.inflate(R.layout.bus_list_item, null,true);


        TextView departureTimeView = (TextView)listView.findViewById(R.id.text_departure);
        TextView arrivalTimeView = (TextView)listView.findViewById(R.id.text_arrival);


        departureTimeView.setText(timePairs.get(position)[0].toString());
        arrivalTimeView.setText(timePairs.get(position)[1].toString());

        return listView;
    }
}
