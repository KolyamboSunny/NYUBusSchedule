package ru.nsunny.nyubustracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import ru.nsunny.nyubustracker.entities.Trip;

public class TripsActivity extends AppCompatActivity {

    DataRepository tripRepository ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        this.tripRepository = new DataRepository(getApplication());

        // toolbar is defined in the layout file
        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


    }

    @Override
    protected void onResume(){
        super.onResume();
        List<Trip> currentTrips = tripRepository.getAllTrips();
    }

    public void onAddRouteClick(View view){
        Intent intent = new Intent(this, AddTripActivity.class);
        startActivity(intent);
    }


}
