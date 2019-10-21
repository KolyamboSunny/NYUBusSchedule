package ru.nsunny.nyubustracker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import ru.nsunny.nyubustracker.entities.GoogleSheetsParser;
import ru.nsunny.nyubustracker.entities.Route;
import ru.nsunny.nyubustracker.entities.Schedule;
import ru.nsunny.nyubustracker.entities.ScheduleTime;
import ru.nsunny.nyubustracker.entities.Trip;


public class MainActivity extends AppCompatActivity implements android.widget.AdapterView.OnItemSelectedListener{
    //TextView mainOutputTextView;
    Button button_timeToArrive;
    Button button_timeToLeave;

    BusListAdapter busListAdapter;
    ScheduleTime timeToArrive;
    ScheduleTime timeToLeave;
    Map<String,Schedule> schedules;
    String selectedScheduleKey;

    List<Trip> trips;
    Trip selectedTrip;
    RouteSpinnerAdapter spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSheetsParser p = new GoogleSheetsParser();


        try {
            p.populateBusSchedule(DataRepository.knownRoutes);
        }catch(Exception e){
            Log.d("FAILED.","Could not retrieve sheet from Google Sheets");
        }

        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        button_timeToArrive = (Button)findViewById(R.id.button_timeToArrive);
        button_timeToLeave = (Button)findViewById(R.id.button_timeToLeave);

        DataRepository repository = new DataRepository(getApplication());
        this.trips = repository.getAllTrips();
        this.selectedTrip = trips.get(0);



        spinnerAdapter = new RouteSpinnerAdapter(this, trips);
        Spinner spinner_route = (Spinner) findViewById(R.id.spinner_route);
        spinner_route.setAdapter(spinnerAdapter);
        spinner_route.setOnItemSelectedListener(this);

        updateScheduleSpinner();
    }
    private void updateScheduleSpinner(){
        String selectedRouteName = this.selectedTrip.getUniversityRouteName();
        for (Route route: DataRepository.knownRoutes){
            if (route.routeName.equals(selectedRouteName))
                this.schedules = route.getAllSchedules();
        }

        Spinner spinner_day = (Spinner) findViewById(R.id.spinner_day);
        String[] scheduleKeys = this.schedules.keySet().toArray(new String[0]);
        this.selectedScheduleKey = scheduleKeys[0];
        ArrayAdapter<String> scheduleKeyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,scheduleKeys );
        spinner_day.setAdapter(scheduleKeyAdapter);
        spinner_day.setOnItemSelectedListener(this);
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
                Intent intent = new Intent(this, TripsActivity.class);
                startActivity(intent);


                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public synchronized void onReverseClick(View view){

        int routeIndex = trips.indexOf(selectedTrip);
        routeIndex++;
        if(routeIndex== trips.size())
            routeIndex=0;
        this.selectedTrip = this.trips.get(routeIndex);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_route);
        spinner.setSelection(trips.indexOf(selectedTrip));
        busListAdapter.clearTimePairs();
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if(parent.getId()==R.id.spinner_route) {
            this.selectedTrip = trips.get(pos);
            updateScheduleSpinner();
        }
        if(parent.getId()==R.id.spinner_day)
            this.selectedScheduleKey = parent.getItemAtPosition(pos).toString();
        if(this.timeToArrive.isEmpty && !this.timeToLeave.isEmpty){
            updateBusListByLeaveTime();
        }
        if(!this.timeToArrive.isEmpty && this.timeToLeave.isEmpty){
            updateBusListByArrivalTime();
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if(parent.getId()==R.id.spinner_route)
            this.selectedTrip = trips.get(0);
        if(parent.getId()==R.id.spinner_day)
            this.selectedScheduleKey = parent.getItemAtPosition(0).toString();
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
        String src = selectedTrip.getSrc();
        String dest = selectedTrip.getDest();
        List<List<ScheduleTime[]>> times = schedules.get(selectedScheduleKey).getTimesByArrivalTime(src,dest,timeToArrive,3,1);

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

        String src = selectedTrip.getSrc();
        String dest = selectedTrip.getDest();
        List<List<ScheduleTime[]>> times = schedules.get(selectedScheduleKey).getTimesByLeaveTime(src,dest,timeToLeave,2,4);

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
        private List<Trip> trips;
        private Context context;
        private LayoutInflater inflater;


        public RouteSpinnerAdapter(Context context, List<Trip> trips){
            this.trips = trips;
            this.context = context;
            this.inflater = (LayoutInflater.from(context));
        }
        @Override
        public int getCount(){
            return trips.size();
        }

        @Override
        public Object getItem(int position) {
            return trips.get(position);
        }

        @Override
        public long getItemId(int position){
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.route_list_item, null);
            TextView names = (TextView) convertView.findViewById(R.id.text_routeName);
            names.setText(trips.get(position).getName());
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
