package ru.nsunny.nyubustracker.entities;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


@Dao
public interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(Route route);

    @Query("DELETE FROM routes_table")
    void deleteAll();

    @Query("DELETE FROM routes_table WHERE name = :nameToRemove AND universityRouteName=:universityRouteNameToRemove")
    void deleteRoute(String nameToRemove,String universityRouteNameToRemove);

    @Query("SELECT * from routes_table ORDER BY universityRouteName ASC")
    LiveData<List<Route>> getAllRoutes();

}
