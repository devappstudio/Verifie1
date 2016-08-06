package datastore;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 8/2/16.
 */

public class User extends RealmObject{

    @PrimaryKey
    private int id;

    private String fullname, telephone,server_id,longitude,latitude;

    public User() {
    }

    public User(String fullname, String telephone, String server_id, String longitude, String latitude) {
        this.fullname = fullname;
        this.telephone = telephone;
        this.server_id = server_id;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getServer_id() {
        return server_id;
    }

    public void setServer_id(String server_id) {
        this.server_id = server_id;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
