package ru.nsunny.nyubustracker.entities;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Route.class}, version = 1)
public abstract class RouteRoomDatabase extends RoomDatabase {
    public abstract RouteDao routeDao();

    //used as a singleton
    private static volatile RouteRoomDatabase INSTANCE;
    public static RouteRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RouteRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RouteRoomDatabase.class,"NYUBusTracker_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
