package datastore;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 8/8/16.
 */

public class Location_Stats  extends RealmObject {

    @PrimaryKey
    private int id;

    double longitude,latitude;

    public Location_Stats(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Location_Stats() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
