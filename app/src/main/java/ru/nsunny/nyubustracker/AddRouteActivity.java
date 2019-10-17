package ru.nsunny.nyubustracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class AddRouteActivity extends AppCompatActivity {

    DataRepository dataRepository;
    Spinner nyuRouteSpinner;

    List<String> nyuRoutes;

    private void setDefaultData(){
        nyuRoutes.add("Route A");
        nyuRoutes.add("Route B");
        nyuRoutes.add("Route C");
        nyuRoutes.add("Route E");
        nyuRoutes.add("Route F");
        nyuRoutes.add("Route G");
        nyuRoutes.add("Route W");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route);

        // toolbar is defined in the layout file
        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        dataRepository = new DataRepository(getApplication());

        this.nyuRouteSpinner = (Spinner)findViewById(R.id.spinner_nyu_route);

    }

    public void addRouteClick(View view){


        //Route route = new Route();
        //this.dataRepository.insertRoute();
    }
}
