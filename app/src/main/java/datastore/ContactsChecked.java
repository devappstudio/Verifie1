package datastore;

import com.orm.SugarRecord;

/**
 * Created by root on 11/22/16.
 */

public class ContactsChecked extends SugarRecord {
    Boolean checked = false;

    public ContactsChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
