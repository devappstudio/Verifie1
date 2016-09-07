package datastore;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 9/7/16.
 */

public class ContactsList   extends RealmObject {
    @PrimaryKey
    private int id;


    private   String name,telephone,is_on_verifie,type,file_name,server_id,screen_name;

    public String getServer_id() {
        return server_id;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public void setServer_id(String server_id) {
        this.server_id = server_id;
    }

    public ContactsList(int id, String name, String telephone, String is_on_verifie) {
        this.id = id;
        this.name = name;
        this.telephone = telephone;
        this.is_on_verifie = is_on_verifie;
    }

    public ContactsList() {
    }
    public String getType() {
        return type;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void setType(String type) {
        this.type = type;
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
