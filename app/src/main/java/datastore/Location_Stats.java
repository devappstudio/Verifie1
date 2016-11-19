package datastore;

import com.orm.SugarRecord;

/**
 * Created by root on 8/8/16.
 */

public class Location_Stats  extends SugarRecord {


     double longitude,latitude;

    public Location_Stats(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Location_Stats() {
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
