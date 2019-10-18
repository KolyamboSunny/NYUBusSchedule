package ru.nsunny.nyubustracker;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import ru.nsunny.nyubustracker.entities.Route;
import ru.nsunny.nyubustracker.entities.Trip;
import ru.nsunny.nyubustracker.entities.TripDao;

public class DataRepository {

    private static TripDao tripDao;
    DatabaseHandler db;

    private List<Trip> allRoutes;
    List<Trip> getAllTrips(){
        try {
            FutureTask<List<Trip>> request = new FutureTask(new allTripsDbRetriever());
            Thread t = new Thread(request);
            t.start();

            this.allRoutes = request.get();
        }catch(Exception e){
            Log.d("FAILED","Could not retrieve trips from database",e);
        }
        return allRoutes;
    }
    private static class allTripsDbRetriever implements Callable{
        @Override
        public List<Trip> call(){
            return tripDao.getAllTrips();
        }
    }

    public void insertTrip(Trip trip){
        new insertTripAsyncTask(tripDao).execute(trip);
    }
    private static class insertTripAsyncTask extends AsyncTask<Trip,Void,Void>{
        private TripDao asyncTripDao;
        insertTripAsyncTask(TripDao tripDao){
            asyncTripDao = tripDao;
        }

        @Override
        protected Void doInBackground(final Trip... params){
            asyncTripDao.insert(params[0]);
            return null;
        }
    }


    public final static List<Route> knownRoutes = new ArrayList<Route>(){{
        add(new Route("Route A", "1jlRme7S0vBssLcbZlQTjP5QrHtV0Cj02jMydXN_7E2I"));
        add(new Route("Route B", "1RFcpF009PyBT-E-FlfidOWe0Zi5n2mVD-dk988QiSoM"));
    }};


    DataRepository(Application app){
        this.db = DatabaseHandler.getDatabase(app);
        tripDao = db.tripDao();
    }



}
