package ru.nsunny.nyubustracker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.nsunny.nyubustracker.entities.GoogleSheetsParser;
import ru.nsunny.nyubustracker.entities.Route;
import ru.nsunny.nyubustracker.entities.Schedule;
import ru.nsunny.nyubustracker.entities.ScheduleTime;


public class MainActivity extends AppCompatActivity implements android.widget.AdapterView.OnItemSelectedListener{
    //TextView mainOutputTextView;
    Button button_timeToArrive;
    Button button_timeToLeave;

    BusListAdapter busListAdapter;
    ScheduleTime timeToArrive;
    ScheduleTime timeToLeave;
    Schedule schedule;

    List<Route> routes;
    Route selectedRoute;
    RouteSpinnerAdapter spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScheduleTime timeToArrive = new ScheduleTime("-");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        //mainOutputTextView = (TextView)findViewById(R.id.text_output);
        button_timeToArrive = (Button)findViewById(R.id.button_timeToArrive);
        button_timeToLeave = (Button)findViewById(R.id.button_timeToLeave);


        //ListView listView = (ListView) findViewById(R.id.list_busesOutput);
        //listView.setAdapter(busListAdapter);

        routes = new ArrayList<Route>();
        routes.add(new Route("To Tandon","715 Broadway","6 Metrotech Arrival"));
        routes.add(new Route("From Tandon","6 Metrotech Departure","715 Broadway Arrival"));
        routes.get(0).setLinkedRoute(routes.get(1));
        selectedRoute = routes.get(1);

        spinnerAdapter = new RouteSpinnerAdapter(this, routes);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_route);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        GoogleSheetsParser p = new GoogleSheetsParser();
        this.schedule = p.parseBusSchedule().get(1);
    }

    @Override
    protected void onStart(){
        super.onStart();
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        ScheduleTime currentTime = new ScheduleTime(currentHour,currentMinute);

        this.timeToLeave = currentTime;
        this.timeToArrive = new ScheduleTime("-");

        updateSelectedLeaveTime(currentTime,true);
    }
    public void onLeaveNowClick(View view){

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        ScheduleTime currentTime = new ScheduleTime(currentHour,currentMinute);

        this.timeToLeave = currentTime;
        this.timeToArrive = new ScheduleTime("-");

        updateSelectedLeaveTime(currentTime,true);
    }

    //dealing with the menu on top of the screen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_routes:
                Intent intent = new Intent(this, RouteActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public synchronized void onReverseClick(View view){
        this.selectedRoute = this.selectedRoute.getLinkedRoute();
        Spinner spinner = (Spinner) findViewById(R.id.spinner_route);
        spinner.setSelection(routes.indexOf(selectedRoute));
        busListAdapter.clearTimePairs();
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        this.selectedRoute = routes.get(pos);
        if(this.timeToArrive.isEmpty && !this.timeToLeave.isEmpty){
            updateBusListByLeaveTime();
        }
        if(!this.timeToArrive.isEmpty && this.timeToLeave.isEmpty){
            updateBusListByArrivalTime();
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        this.selectedRoute = routes.get(0);
    }



    public void onTimeToArriveClick(View view){
        DialogFragment newFragment = new TimePickerFragment_ArriveBy();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
    private void updateSelectedArriveTime(ScheduleTime newTime){
        this.timeToArrive = newTime;
        button_timeToArrive.setText(timeToArrive.toString());

        this.timeToLeave = new ScheduleTime("-");
        button_timeToLeave.setText(timeToLeave.toString());
        updateBusListByArrivalTime();
    }
    private void updateBusListByArrivalTime(){
        String src = selectedRoute.getSrc();
        String dest = selectedRoute.getDest();
        List<List<ScheduleTime[]>> times = schedule.getTimesByArrivalTime(src,dest,timeToArrive,3,1);

        updateBusListView(times);
    }

    public void onTimeToLeaveClick(View view){
        DialogFragment newFragment = new TimePickerFragment_LeaveAt();
        newFragment.show(getSupportFragmentManager(), "timePicker");

    }
    private void updateSelectedLeaveTime(ScheduleTime newTime, boolean updateButtonText){
        this.timeToLeave = newTime;
        if(updateButtonText == true)
            button_timeToLeave.setText(timeToLeave.toString());

        this.timeToArrive = new ScheduleTime("-");
        button_timeToArrive.setText(timeToArrive.toString());
        updateBusListByLeaveTime();
    }
    private void updateBusListByLeaveTime(){

        String src = selectedRoute.getSrc();
        String dest = selectedRoute.getDest();
        List<List<ScheduleTime[]>> times = schedule.getTimesByLeaveTime(src,dest,timeToLeave,2,4);

        updateBusListView(times);
    }

    private void updateBusListView(List<List<ScheduleTime[]>> earlyAndLate){
        List<ScheduleTime[]> allTogether = new ArrayList<ScheduleTime[]>();
        allTogether.addAll(earlyAndLate.get(0));
        allTogether.addAll(earlyAndLate.get(1));

        if(this.busListAdapter == null)
            this.busListAdapter = new BusListAdapter(this,allTogether);

        ListView listView = (ListView) findViewById(R.id.list_busesOutput);
        listView.setAdapter(busListAdapter);
        busListAdapter.setTimePairs(allTogether);
    }

    private class RouteSpinnerAdapter extends BaseAdapter {
        private List<Route> routes;
        private Context context;
        private LayoutInflater inflater;


        public RouteSpinnerAdapter(Context context, List<Route> routes){
            this.routes = routes;
            this.context = context;
            this.inflater = (LayoutInflater.from(context));
        }
        @Override
        public int getCount(){
            return routes.size();
        }

        @Override
        public Object getItem(int position) {
            return routes.get(position);
        }

        @Override
        public long getItemId(int position){
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.route_list_item, null);
            TextView names = (TextView) convertView.findViewById(R.id.text_routeName);
            names.setText(routes.get(position).getName());
            return convertView;
        }
    }

    public static class TimePickerFragment_ArriveBy extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int currentHour = c.get(Calendar.HOUR_OF_DAY);
            currentHour++;
            if (currentHour>24) currentHour =0;
            int currentMinute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, currentHour, currentMinute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            MainActivity activity=(MainActivity)getActivity();
            activity.updateSelectedArriveTime(new ScheduleTime(hourOfDay,minute));
        }
    }
    public static class TimePickerFragment_LeaveAt extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int currentHour = c.get(Calendar.HOUR_OF_DAY);
            int currentMinute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, currentHour, currentMinute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            MainActivity activity=(MainActivity)getActivity();
            activity.updateSelectedLeaveTime(new ScheduleTime(hourOfDay,minute),true);
        }
    }
}
