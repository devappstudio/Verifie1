package datastore;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 9/16/16.
 */

public class SentRequests extends RealmObject {

    @PrimaryKey
    private int id;

    private String id_receipent,time_sent,time_replied;
    private Boolean status;
    private int reply;

    public SentRequests() {
    }

    public SentRequests(int id, String id_receipent, String time_sent, String time_replied, Boolean status, int reply) {
        this.id = id;
        this.id_receipent = id_receipent;
        this.time_sent = time_sent;
        this.time_replied = time_replied;
        this.status = status;
        this.reply = reply;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getId_receipent() {
        return id_receipent;
    }

    public void setId_receipent(String id_receipent) {
        this.id_receipent = id_receipent;
    }

    public String getTime_sent() {
        return time_sent;
    }

    public void setTime_sent(String time_sent) {
        this.time_sent = time_sent;
    }

    public String getTime_replied() {
        return time_replied;
    }

    public void setTime_replied(String time_replied) {
        this.time_replied = time_replied;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public int getReply() {
        return reply;
    }

    public void setReply(int reply) {
        this.reply = reply;
    }
}
