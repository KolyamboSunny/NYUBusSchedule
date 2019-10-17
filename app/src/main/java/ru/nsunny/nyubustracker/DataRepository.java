package ru.nsunny.nyubustracker;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import ru.nsunny.nyubustracker.entities.Trip;
import ru.nsunny.nyubustracker.entities.TripDao;
import ru.nsunny.nyubustracker.entities.RouteRoomDatabase;

public class DataRepository {
    private TripDao tripDao;
    private LiveData<List<Trip>> allRoutes;
    LiveData<List<Trip>> getAllRoutes(){
        return allRoutes;
    }

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
