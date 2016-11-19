package datastore;

import com.orm.SugarRecord;


/**
 * Created by root on 9/14/16.
 */

public class VerificationStatus  extends SugarRecord {

     String date_verified,date_to_expire,centre="",date_recommended="Now";

    public String getDate_recommended() {
        return date_recommended;
    }

    public void setDate_recommended(String date_recommended) {
        this.date_recommended = date_recommended;
    }

    public VerificationStatus() {
    }


    public VerificationStatus(String date_verified, String date_to_expire) {
        this.date_verified = date_verified;
        this.date_to_expire = date_to_expire;
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


    public VerificationStatus(String date_verified, String date_to_expire, String centre) {
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
