package ru.nsunny.nyubustracker;

import android.app.Application;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import ru.nsunny.nyubustracker.entities.Route;
import ru.nsunny.nyubustracker.entities.RouteRoomDatabase;
import ru.nsunny.nyubustracker.entities.Trip;
import ru.nsunny.nyubustracker.entities.TripDao;

public class DataRepository {
    private TripDao tripDao;
    private LiveData<List<Trip>> allRoutes;
    LiveData<List<Trip>> getAllTrips(){
        return allRoutes;
    }

    public final static List<Route> knownRoutes = new ArrayList<Route>(){{
        add(new Route("Route A", "1jlRme7S0vBssLcbZlQTjP5QrHtV0Cj02jMydXN_7E2I"));
        add(new Route("Route B", "1RFcpF009PyBT-E-FlfidOWe0Zi5n2mVD-dk988QiSoM"));
    }};


    DataRepository(Application app){
        RouteRoomDatabase db = RouteRoomDatabase.getDatabase(app);
        tripDao = db.routeDao();
        allRoutes = tripDao.getAllRoutes();
    }

    public void insertRoute(Trip trip){
        new insertAsyncTask(tripDao).execute(trip);
    }

    private static class insertAsyncTask extends AsyncTask<Trip,Void,Void>{
        private TripDao asyncTripDao;
        insertAsyncTask(TripDao tripDao){
            asyncTripDao = tripDao;
        }

        @Override
        protected Void doInBackground(final Trip... params){
            asyncTripDao.insert(params[0]);
            return null;
        }
    }
}
