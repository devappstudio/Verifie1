package datastore;

import com.orm.SugarRecord;


/**
 * Created by root on 11/16/16.
 */

public class PwordCode extends SugarRecord {

     String code;
     int is_verified;

    public void setIs_verified(int is_verified) {
        this.is_verified = is_verified;
    }

    public int getIs_verified() {
        return is_verified;
    }


    public PwordCode(String code, int is_verified) {
        this.code = code;
        this.is_verified = is_verified;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
