package datastore;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 9/7/16.
 */

public class ContactsList   extends RealmObject {
    @PrimaryKey
    private int id;

    String name,telephone,is_on_verifie;

    public ContactsList(int id, String name, String telephone, String is_on_verifie) {
        this.id = id;
        this.name = name;
        this.telephone = telephone;
        this.is_on_verifie = is_on_verifie;
    }

    public ContactsList() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getIs_on_verifie() {
        return is_on_verifie;
    }

    public void setIs_on_verifie(String is_on_verifie) {
        this.is_on_verifie = is_on_verifie;
    }
}
