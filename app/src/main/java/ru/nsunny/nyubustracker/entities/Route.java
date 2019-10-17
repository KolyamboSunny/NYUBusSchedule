package ru.nsunny.nyubustracker.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"universityRouteName","name"}, tableName = "routes_table")
public class Route {
    @NonNull
    private String name;
    public String getName() {
        return name;
    }
    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    private String universityRouteName;
    public String getUniversityRouteName() {
        return universityRouteName;
    }
    public void setUniversityRouteName(String universityRouteName) {
        this.universityRouteName = universityRouteName;
    }

    @ColumnInfo(name="srcBusStop")
    @NonNull
    private String src;
    public String getSrc() {
        return src;
    }
    public void setSrc(String src) {
        this.src = src;
    }

    @ColumnInfo(name="destBusStop")
    @NonNull
    private String dest;
    public String getDest() {
        return dest;
    }
    public void setDest(String dest) {
        this.dest = dest;
    }

    public Route(@NonNull String universityRouteName,@NonNull String name,@NonNull String src,@NonNull String dest){
        this.universityRouteName = universityRouteName;
        this.name = name;
        this.src = src;
        this.dest = dest;
    }
}
