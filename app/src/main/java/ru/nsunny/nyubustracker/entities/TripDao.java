package ru.nsunny.nyubustracker.entities;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


@Dao
public interface TripDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(Trip trip);

    @Query("DELETE FROM trips_table")
    void deleteAll();

    @Query("DELETE FROM trips_table WHERE name = :nameToRemove AND universityRouteName=:universityRouteNameToRemove")
    void deleteRoute(String nameToRemove,String universityRouteNameToRemove);

    @Query("SELECT * from trips_table ORDER BY universityRouteName ASC")
    LiveData<List<Trip>> getAllRoutes();

}
