package datastore;

import com.orm.SugarRecord;


/**
 * Created by root on 9/16/16.
 */

public class Facilities extends SugarRecord {

     String name,contact_person,contact_phone,location,server_id;

    public Facilities(String name, String contact_person, String contact_phone, String location, String server_id) {
        this.name = name;
        this.contact_person = contact_person;
        this.contact_phone = contact_phone;
        this.location = location;
        this.server_id = server_id;
    }

    public String getServer_id() {
        return server_id;
    }

    public void setServer_id(String server_id) {
        this.server_id = server_id;
    }

    public Facilities() {
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact_person() {
        return contact_person;
    }

    public void setContact_person(String contact_person) {
        this.contact_person = contact_person;
    }

    public String getContact_phone() {
        return contact_phone;
    }

    public void setContact_phone(String contact_phone) {
        this.contact_phone = contact_phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
