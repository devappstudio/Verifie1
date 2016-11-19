package datastore;

import com.orm.SugarRecord;


/**
 * Created by root on 8/8/16.
 */

public class Visibility  extends SugarRecord {


     boolean status;

    public Visibility(boolean status) {
        this.status = status;
    }

    public Visibility() {
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


}
