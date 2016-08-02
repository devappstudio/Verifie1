package com.devappstudio.verifie;

/**
 * Created by root on 8/1/16.
 */

public class NearBy {

    String name,image_url,telephone_number,server_id;

    public String getTelephone_number() {
        return telephone_number;
    }

    public void setTelephone_number(String telephone_number) {
        this.telephone_number = telephone_number;
    }

    public NearBy(String name, String image_url, String telephone_number, String server_id) {
        this.name = name;
        this.image_url = image_url;
        this.telephone_number = telephone_number;
        this.server_id = server_id;
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
