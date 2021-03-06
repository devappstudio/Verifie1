package datastore;

import com.orm.SugarApp;
import com.orm.SugarRecord;

/**
 * Created by root on 9/16/16.
 */

public class SentRequests extends SugarRecord {


     String id_receipent,time_sent,time_replied,status;
     int reply;

    public SentRequests() {
    }

    public SentRequests(String id_receipent, String time_sent, String time_replied, String status, int reply) {
        this.id_receipent = id_receipent;
        this.time_sent = time_sent;
        this.time_replied = time_replied;
        this.status = status;
        this.reply = reply;
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
