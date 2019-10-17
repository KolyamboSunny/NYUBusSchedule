package ru.nsunny.nyubustracker;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import ru.nsunny.nyubustracker.entities.Route;


public class AddTripActivity extends AppCompatActivity implements android.widget.AdapterView.OnItemSelectedListener{

    //DataRepository dataRepository = new DataRepository(getApplication());
    Route selectedRoute;

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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if(parent.getId()==R.id.spinner_nyu_route) {
            List<Route> routes = DataRepository.knownRoutes;
            this.selectedRoute = routes.get(pos);
        }
        //if(parent.getId()==R.id.spinner_stop_src)
        //if(parent.getId()==R.id.spinner_stop_dest)

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if(parent.getId()==R.id.spinner_nyu_route) {
            List<Route> routes = DataRepository.knownRoutes;
            this.selectedRoute = routes.get(0);
        }
    }

    public void addRouteClick(View view){


        //Trip route = new Trip();
        //this.dataRepository.insertRoute();
    }
}
