package ru.nsunny.nyubustracker;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import ru.nsunny.nyubustracker.entities.Trip;
import ru.nsunny.nyubustracker.entities.TripDao;

@Database(entities = {Trip.class}, version = 1)
public abstract class DatabaseHandler extends RoomDatabase {
    public abstract TripDao tripDao();

    public final static String DB_NAME = "NYUBusTracker_database";

    //used as a singleton
    private static volatile DatabaseHandler INSTANCE;
    public static DatabaseHandler getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DatabaseHandler.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DatabaseHandler.class,DB_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
