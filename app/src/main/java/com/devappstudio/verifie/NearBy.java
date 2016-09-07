package com.devappstudio.verifie;

/**
 * Created by root on 8/1/16.
 */

public class NearBy {

    String name,image_url,telephone_number,server_id,local_uri ="", on_verifie="",local_id="",screen_name="";

    public String getTelephone_number() {
        return telephone_number;
    }

    public void setTelephone_number(String telephone_number) {
        this.telephone_number = telephone_number;
    }

    public NearBy(String name, String image_url, String telephone_number, String server_id,String screen_name) {
        this.name = name;
        this.image_url = image_url;
        this.telephone_number = telephone_number;
        this.server_id = server_id;
        this.screen_name = screen_name;

    }

    public String getLocal_uri() {
        return local_uri;
    }

    public void setLocal_uri(String local_uri) {
        this.local_uri = local_uri;
    }

    public NearBy(String name, String image_url, String telephone_number, String server_id, String local_uri, String is_verified,String local_id,String screen_name) {
        this.name = name;
        this.image_url = image_url;
        this.telephone_number = telephone_number;
        this.server_id = server_id;
        this.local_uri = local_uri;
        this.on_verifie = is_verified;
        this.local_id = local_id;
        this.screen_name = screen_name;
    }

    public String getLocal_id() {
        return local_id;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public void setLocal_id(String local_id) {
        this.local_id = local_id;
    }

    public String getOn_verifie() {
        return on_verifie;
    }

    public void setOn_verifie(String on_verifie) {
        this.on_verifie = on_verifie;
    }

    public String getServer_id() {
        return server_id;
    }

    public void setServer_id(String server_id) {
        this.server_id = server_id;
    }

    public NearBy(String name, String image_url) {
        this.name = name;
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public NearBy() {
    }
}
