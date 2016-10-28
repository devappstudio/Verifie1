package com.devappstudio.verifie;

/**
 * Created by root on 9/16/16.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import datastore.ApprovedRequests;
import datastore.ReceivedRequests;
import datastore.SentRequests;
import io.realm.Realm;


public class MyGcmListenerService  extends GcmListenerService  {
    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String sender = data.getString("sender");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        Log.d(TAG, "Message: " + sender);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        try {
            JSONObject jo_stock = new JSONObject(message);
            if(jo_stock.get("type").toString().equalsIgnoreCase("request"))
            {
                JSONObject user_details = jo_stock.getJSONObject("from_user");

                message = jo_stock.get("message").toString()+" from "+user_details.get("fullname").toString() ;
                //request here
                //        $res = $this->gcm->send(json_encode(array('type'=>'request','from_user'=>$this->db->get_where('users',array('id'=>$this->post('server_id')))->row(),'message'=>'Request To View Your Rotabar')),"",$tokens);
                //   public ReceivedRequests(int id, String id_send, String time_sent, String time_replied, String status, int reply)
                Long tsLong = System.currentTimeMillis()/1000;
                String tsr = tsLong.toString();
                Realm io = Realm.getDefaultInstance();

                ReceivedRequests rr = new ReceivedRequests((int)io.where(ReceivedRequests.class).maximumInt("id")+1,user_details.get("id").toString(),tsr,"","0",0);
                io.beginTransaction();
                io.copyToRealm(rr);
                io.commitTransaction();
            }

           if(jo_stock.get("type").toString().equalsIgnoreCase("reply"))
            {
                //reply here

                if(jo_stock.get("reply").toString().equalsIgnoreCase("1"))
                {
                    //Yes
                    //            $this->response(array('status'=>1,"data"=>$this->gcm->send(json_encode(array('type'=>'reply','reply'=>'1','details'=>$this->db->get_where('users',array('id'=>$this->post('server_id')))->row(),'verification'=>$user,'message'=>'Request To View Your RotaBar Bar')),"",$tokens)),200);
                    Long tsLong = System.currentTimeMillis()/1000;
                    String tsr = tsLong.toString();
                    JSONObject user_details = jo_stock.getJSONObject("details");
                    String server_id = user_details.get("id").toString();


                    Realm io = Realm.getDefaultInstance();
                    // public SentRequests(int id, String id_receipent, String time_sent, String time_replied, String status, int reply)
                    //    public ApprovedRequests(int id, String date_verified, String date_to_expire, String file_name, String server_id) {
                    JSONObject data = jo_stock.getJSONObject("verification");


                    ApprovedRequests ar = new ApprovedRequests((int)io.where(ApprovedRequests.class).maximumInt("id")+1,data.get("current").toString(),data.get("expiry").toString(),user_details.get("file_name").toString(),user_details.get("id").toString());

                    io.beginTransaction();
                    SentRequests sr = io.where(SentRequests.class).equalTo("id_receipent",server_id).findAll().first();
                    sr.setReply(1);
                    sr.setStatus("1");
                    sr.setTime_replied(tsr);
                    io.copyToRealmOrUpdate(sr);
                    io.copyToRealmOrUpdate(ar);
                    io.commitTransaction();
                    message = user_details.get("fullname").toString()+" Accepted Your Request";

                }
                else
                {
                    Long tsLong = System.currentTimeMillis()/1000;
                    String tsr = tsLong.toString();
                    JSONObject user_details = jo_stock.getJSONObject("details");
                    String server_id = user_details.get("id").toString();

                    Realm io = Realm.getDefaultInstance();
                    // public SentRequests(int id, String id_receipent, String time_sent, String time_replied, String status, int reply)
                    io.beginTransaction();
                    SentRequests sr = io.where(SentRequests.class).equalTo("id_receipent",server_id).findAll().first();
                    sr.setReply(0);
                    sr.setStatus("1");
                    sr.setTime_replied(tsr);
                    io.copyToRealmOrUpdate(sr);
                    io.commitTransaction();
                    message = user_details.get("fullname").toString()+" Denied Your Request";
                }

            }


        } catch (JSONException e) {
            Realm io = Realm.getDefaultInstance();
            io.cancelTransaction();
            e.printStackTrace();
            try {
                JSONObject jo_stock = new JSONObject(message);
                if(jo_stock.get("type").toString().equalsIgnoreCase("request"))
                {
                    JSONObject user_details = jo_stock.getJSONObject("from_user");

                    message = jo_stock.get("message").toString()+" from "+user_details.get("fullname").toString() ;
                    //request here
                    //        $res = $this->gcm->send(json_encode(array('type'=>'request','from_user'=>$this->db->get_where('users',array('id'=>$this->post('server_id')))->row(),'message'=>'Request To View Your Rotabar')),"",$tokens);
                    //   public ReceivedRequests(int id, String id_send, String time_sent, String time_replied, String status, int reply)
                    Long tsLong = System.currentTimeMillis()/1000;
                    String tsr = tsLong.toString();

                    ReceivedRequests rr = new ReceivedRequests((int)io.where(ReceivedRequests.class).maximumInt("id")+1,user_details.get("id").toString(),tsr,"","0",0);
                    io.beginTransaction();
                    io.copyToRealm(rr);
                    io.commitTransaction();
                }

                if(jo_stock.get("type").toString().equalsIgnoreCase("reply"))
                {
                    //reply here

                    if(jo_stock.get("reply").toString().equalsIgnoreCase("1"))
                    {
                        //Yes
                        //            $this->response(array('status'=>1,"data"=>$this->gcm->send(json_encode(array('type'=>'reply','reply'=>'1','details'=>$this->db->get_where('users',array('id'=>$this->post('server_id')))->row(),'verification'=>$user,'message'=>'Request To View Your RotaBar Bar')),"",$tokens)),200);
                        Long tsLong = System.currentTimeMillis()/1000;
                        String tsr = tsLong.toString();
                        JSONObject user_details = jo_stock.getJSONObject("details");
                        String server_id = user_details.get("id").toString();


                        // public SentRequests(int id, String id_receipent, String time_sent, String time_replied, String status, int reply)
                        //    public ApprovedRequests(int id, String date_verified, String date_to_expire, String file_name, String server_id) {
                        JSONObject data = jo_stock.getJSONObject("verification");


                        ApprovedRequests ar = new ApprovedRequests((int)io.where(ApprovedRequests.class).maximumInt("id")+1,data.get("current").toString(),data.get("expiry").toString(),user_details.get("file_name").toString(),user_details.get("id").toString());

                        io.beginTransaction();
                        SentRequests sr = io.where(SentRequests.class).equalTo("id_receipent",server_id).findAll().first();
                        sr.setReply(1);
                        sr.setStatus("1");
                        sr.setTime_replied(tsr);
                        io.copyToRealmOrUpdate(sr);
                        io.copyToRealmOrUpdate(ar);
                        io.commitTransaction();
                        message = user_details.get("fullname").toString()+" Accepted Your Request";

                    }
                    else
                    {
                        Long tsLong = System.currentTimeMillis()/1000;
                        String tsr = tsLong.toString();
                        JSONObject user_details = jo_stock.getJSONObject("details");
                        String server_id = user_details.get("id").toString();

                        // public SentRequests(int id, String id_receipent, String time_sent, String time_replied, String status, int reply)
                        io.beginTransaction();
                        SentRequests sr = io.where(SentRequests.class).equalTo("id_receipent",server_id).findAll().first();
                        sr.setReply(0);
                        sr.setStatus("1");
                        sr.setTime_replied(tsr);
                        io.copyToRealmOrUpdate(sr);
                        io.commitTransaction();
                        message = user_details.get("fullname").toString()+" Denied Your Request";
                    }

                }


            } catch (JSONException ee) {
                io.cancelTransaction();
                e.printStackTrace();
            }

        }

        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_cast_dark)
                .setContentTitle("Verifie")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
