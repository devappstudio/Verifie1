package datastore;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 9/16/16.
 */

public class ReceivedRequests extends RealmObject {

    @PrimaryKey
    private int id;


    private String id_send,time_sent,time_replied,status;
    private int reply;

    public ReceivedRequests() {
    }

    public ReceivedRequests(int id, String id_send, String time_sent, String time_replied, String status, int reply) {
        this.id = id;
        this.id_send = id_send;
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

    public String getId_send() {
        return id_send;
    }

    public void setId_send(String id_send) {
        this.id_send = id_send;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getReply() {
        return reply;
    }

    public void setReply(int reply) {
        this.reply = reply;
    }
}
