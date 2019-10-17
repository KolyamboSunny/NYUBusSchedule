package ru.nsunny.nyubustracker;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import ru.nsunny.nyubustracker.entities.Route;
import ru.nsunny.nyubustracker.entities.RouteDao;
import ru.nsunny.nyubustracker.entities.RouteRoomDatabase;

public class DataRepository {
    private RouteDao routeDao;
    private LiveData<List<Route>> allRoutes;
    LiveData<List<Route>> getAllRoutes(){
        return allRoutes;
    }

    DataRepository(Application app){
        RouteRoomDatabase db = RouteRoomDatabase.getDatabase(app);
        routeDao = db.routeDao();
        allRoutes = routeDao.getAllRoutes();
    }

    public void insertRoute(Route route){
        new insertAsyncTask(routeDao).execute(route);
    }

    private static class insertAsyncTask extends AsyncTask<Route,Void,Void>{
        private RouteDao asyncRouteDao;
        insertAsyncTask(RouteDao routeDao){
            asyncRouteDao = routeDao;
        }

        @Override
        protected Void doInBackground(final Route... params){
            asyncRouteDao.insert(params[0]);
            return null;
        }
    }
}
