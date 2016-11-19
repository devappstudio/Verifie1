package datastore;


import com.orm.SugarRecord;


/**
 * Created by root on 8/2/16.
 */

public class User extends SugarRecord{

    public String getImage_verified() {
        return image_verified;
    }

    public void setImage_verified(String image_verified) {
        this.image_verified = image_verified;
    }

     String fullname, telephone,server_id,longitude,latitude,file_name,last_verified,image_verified="0";

    public User(String fullname, String telephone, String server_id, String longitude, String latitude, String file_name) {
        this.fullname = fullname;
        this.telephone = telephone;
        this.server_id = server_id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.file_name = file_name;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getLast_verified() {
        return last_verified;
    }

    public void setLast_verified(String last_verified) {
        this.last_verified = last_verified;
    }

    public User() {
    }

    public User(String fullname, String telephone, String server_id, String longitude, String latitude) {
        this.fullname = fullname;
        this.telephone = telephone;
        this.server_id = server_id;
        this.longitude = longitude;
        this.latitude = latitude;
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
