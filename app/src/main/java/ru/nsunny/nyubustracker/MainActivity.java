package ru.nsunny.nyubustracker;
import ru.nsunny.nyubustracker.entities.*;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;

import android.view.View;
import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.text.format.DateFormat;


import java.util.List;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    TextView mainOutputTextView;
    Button button_timeToArrive;

    ScheduleTime timeToArrive;
    Schedule schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainOutputTextView = (TextView)findViewById(R.id.text_output);
        button_timeToArrive = (Button)findViewById(R.id.button_timeToArrive);

        GoogleSheetsParser p = new GoogleSheetsParser();
        this.schedule = p.parseBusSchedule().get(0);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentHour++;
        if (currentHour>24) currentHour =0;
        int currentMinute = calendar.get(Calendar.MINUTE);
        ScheduleTime newTimeToArrive = new ScheduleTime(currentHour,currentMinute);

        updateSelectedTime(newTimeToArrive);
    }

    private void updateSelectedTime(ScheduleTime newTime){
        this.timeToArrive = newTime;
        button_timeToArrive.setText(timeToArrive.toString());
        updateBusListByArrivalTime();
    }
    private void updateBusListByArrivalTime(){
            String src ="715 Broadway";
            String dest = "6 Metrotech Arrival";
            List<List<ScheduleTime[]>> times = schedule.getTimesByArrivalTime(src,dest,timeToArrive,4,3);

            String toPrint ="";

            for(ScheduleTime[] timePair:times.get(0))
                toPrint+=timePair[0]+" -> "+timePair[1]+"\n";

            toPrint+="\n\n";

            for(ScheduleTime[] timePair:times.get(1))
                toPrint+=timePair[0]+" -> "+timePair[1]+"\n";
            mainOutputTextView.setText(toPrint);
    }

    public void onTimeToArriveClick(View view){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }


    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
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
            activity.updateSelectedTime(new ScheduleTime(hourOfDay,minute));
        }
    }
}
