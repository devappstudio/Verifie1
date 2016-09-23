package datastore;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 9/23/16.
 */

public class ApprovedRequests  extends RealmObject {

    @PrimaryKey
    private int id;

    private String date_verified,date_to_expire,file_name,server_id;

    public ApprovedRequests() {
    }

    public ApprovedRequests(int id, String date_verified, String date_to_expire, String file_name, String server_id) {
        this.id = id;
        this.date_verified = date_verified;
        this.date_to_expire = date_to_expire;
        this.file_name = file_name;
        this.server_id = server_id;
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

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getServer_id() {
        return server_id;
    }

    public void setServer_id(String server_id) {
        this.server_id = server_id;
    }
}
