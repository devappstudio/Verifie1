package datastore;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 8/8/16.
 */

public class Visibility  extends RealmObject {

    @PrimaryKey
    private int id;

    private boolean status;

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


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
