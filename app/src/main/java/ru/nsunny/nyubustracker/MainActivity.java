package ru.nsunny.nyubustracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;


import ru.nsunny.nyubustracker.entities.GoogleSheetsParser;
import ru.nsunny.nyubustracker.entities.Schedule;
import ru.nsunny.nyubustracker.entities.ScheduleTime;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView mainOutputTextView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainOutputTextView = (TextView)findViewById(R.id.text_output);

        GoogleSheetsParser p = new GoogleSheetsParser();
        Schedule schedule = p.parseBusSchedule().get(0);

        String src ="715 Broadway";
        String dest = "6 Metrotech Arrival";
        List<ScheduleTime[]> times = schedule.getAllTimesBetweenAddresses(src,dest);

        String toPrint ="";

        for(ScheduleTime[] timePair:times)
            toPrint+=timePair[0]+" -> "+timePair[1]+"\n";

        mainOutputTextView.setText(toPrint);
    }
}
