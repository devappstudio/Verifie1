package datastore;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 9/14/16.
 */

public class VerificationStatus  extends RealmObject {

    @PrimaryKey
    private int id;

    private String date_verified,date_to_expire,centre="",date_recommended="Now";

    public String getDate_recommended() {
        return date_recommended;
    }

    public void setDate_recommended(String date_recommended) {
        this.date_recommended = date_recommended;
    }

    public VerificationStatus() {
    }


    public VerificationStatus(int id, String date_verified, String date_to_expire) {
        this.id = id;
        this.date_verified = date_verified;
        this.date_to_expire = date_to_expire;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate_verified() {
        return date_verified;
    }

    public void setDate_verified(String date_verified) {
        this.date_verified = date_verified;
    }

    public String getDate_to_expire() {
        return date_to_expire;
    }

    public void setDate_to_expire(String date_to_expire) {
        this.date_to_expire = date_to_expire;
    }


    public VerificationStatus(int id, String date_verified, String date_to_expire, String centre) {
        this.id = id;
        this.date_verified = date_verified;
        this.date_to_expire = date_to_expire;
        this.centre = centre;
    }

    public String getCentre() {
        return centre;
    }

    public void setCentre(String centre) {
        this.centre = centre;
    }
}
