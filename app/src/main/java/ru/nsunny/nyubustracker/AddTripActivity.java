package ru.nsunny.nyubustracker;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import ru.nsunny.nyubustracker.entities.Route;
import ru.nsunny.nyubustracker.entities.Trip;


public class AddTripActivity extends AppCompatActivity implements android.widget.AdapterView.OnItemSelectedListener{

    //DataRepository dataRepository = new DataRepository(getApplication());
    Route selectedRoute;
    String selectedSrcStop;
    String selectedDestStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        // toolbar is defined in the layout file
        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        setupNyuRouteSpinner();
        updateSrcStopSpinner();
        updateDestStopSpinner();
    }

    private void setupNyuRouteSpinner(){
        Spinner nyuRouteSpinner = (Spinner)findViewById(R.id.spinner_nyu_route);
        List<Route> routes = DataRepository.knownRoutes;
        List<String> routeNames = new ArrayList<String>(routes.size());
        for(Route route: routes)
            routeNames.add(route.routeName);
        this.selectedRoute = routes.get(0);
        ArrayAdapter<String> routeNameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,routeNames );
        nyuRouteSpinner.setAdapter(routeNameAdapter);
        nyuRouteSpinner.setOnItemSelectedListener(this);
    }
    private void updateSrcStopSpinner(){
        Spinner srcStopSpinner = (Spinner)findViewById(R.id.spinner_stop_src);
        List<String> stopNames = this.selectedRoute.getOrderedStops();
        this.selectedSrcStop = stopNames.get(0);
        ArrayAdapter<String> routeNameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,stopNames );
        srcStopSpinner.setAdapter(routeNameAdapter);
        srcStopSpinner.setOnItemSelectedListener(this);
    }
    private void updateDestStopSpinner(){
        Spinner destStopSpinner = (Spinner)findViewById(R.id.spinner_stop_dest);
        List<String> stopNames = this.selectedRoute.getOrderedStops();

        int srcStopIndex = stopNames.indexOf(selectedSrcStop);
        this.selectedDestStop = stopNames.get(srcStopIndex+1);
        ArrayAdapter<String> routeNameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                stopNames.subList(srcStopIndex+1,stopNames.size()) );
        destStopSpinner.setAdapter(routeNameAdapter);
        destStopSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if(parent.getId()==R.id.spinner_nyu_route) {
            List<Route> routes = DataRepository.knownRoutes;
            this.selectedRoute = routes.get(pos);
            updateSrcStopSpinner();
        }
        if(parent.getId()==R.id.spinner_stop_src){
            this.selectedSrcStop = this.selectedRoute.getOrderedStops().get(pos);
            updateDestStopSpinner();
        }
        if(parent.getId()==R.id.spinner_stop_dest){
            int relativePos =  this.selectedRoute.getOrderedStops().indexOf(this.selectedSrcStop)+1;
            this.selectedDestStop = this.selectedRoute.getOrderedStops().get(relativePos+pos);
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if(parent.getId()==R.id.spinner_nyu_route) {
            List<Route> routes = DataRepository.knownRoutes;
            this.selectedRoute = routes.get(0);
        }
        if(parent.getId()==R.id.spinner_nyu_route) {
            this.selectedSrcStop = this.selectedRoute.getOrderedStops().get(0);
            updateDestStopSpinner();
        }
        if(parent.getId()==R.id.spinner_nyu_route) {
            List<String> stopNames = this.selectedRoute.getOrderedStops();
            int srcStopIndex = stopNames.indexOf(selectedSrcStop);
            this.selectedDestStop = stopNames.get(srcStopIndex+1);
        }
    }

    public void addTripClick(View view){
        EditText nameInput = (EditText)findViewById(R.id.text_input_route_name);
        String tripName = nameInput.getText().toString();
        Trip trip = new Trip(selectedRoute.routeName,tripName,selectedSrcStop,selectedDestStop);
        DataRepository repo = new DataRepository(getApplication());
        repo.insertTrip(trip);
        finish();
    }
}
