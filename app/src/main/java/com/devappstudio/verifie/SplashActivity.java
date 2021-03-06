package com.devappstudio.verifie;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import datastore.Api;
import datastore.User;

public class SplashActivity extends Activity {
    static String server_id;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //RealmController.with(getApplication()).clearContacts();

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.spin_kit);
        ChasingDots doubleBounce = new ChasingDots();
        progressBar.setIndeterminateDrawable(doubleBounce);


/*
        rr.beginTransaction();
        rr.clear(ContactsList.class);
        rr.clear(ApprovedRequests.class);
        rr.clear(Facilities.class);
        rr.clear(Location_Stats.class);
        rr.clear(ReceivedRequests.class);
        rr.clear(SentRequests.class);
        rr.clear(User.class);
        rr.clear(VerificationStatus.class);
        rr.clear(Visibility.class);
        rr.commitTransaction();
*/


        Thread background = new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void run() {

                try {


                    // Thread will sleep for 5 seconds
                    sleep(5 * 1000);

                    // After 5 seconds redirect to another intent

                    Intent intent = new Intent(SplashActivity.this, Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                    startActivity(intent);

                    //Remove activity
                    finish();

                } catch (Exception e) {

                }
            }
        };
        Thread background1 = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 5 seconds
                    sleep(2 * 1000);


                    Intent intent = new Intent(SplashActivity.this, MyLocationRequest.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {

                }
            }
        };

        if (!User.listAll(User.class).isEmpty()) {
            background1.start();
        } else {
            get_user();
            background.start();

        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    void get_user() {

        //`users`(`id`, `fullname`, `login_type`, `security_code`, `extra_code`, `id_from_provider`, `telephone`, `file_blob`, `file_name`, `is_visible`, `visibility_code`, ``, ``, ``, ``, ``)
        final String tag = "new_user_logn";

        Map<String, String> params = new HashMap<String, String>();
        params.put("id", server_id);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi() + "user_details", new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response.toString());
                        try {

                            if (response.get("status").toString().equalsIgnoreCase("1")) {
                                JSONObject jo_stock = (JSONObject) response.get("data");
                                User.deleteAll(User.class);
                                User user = new User(jo_stock.get("fullname").toString(), jo_stock.get("telephone").toString(), jo_stock.get("id").toString(), "", "", jo_stock.get("file_name").toString());
                                user.setImage_verified(jo_stock.get("image_verified").toString());
                                user.setId((long)1);
                                user.save();


                                final Intent intent = new Intent(SplashActivity.this, MyLocationService.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                                finish();

                            } else {
                                final Dialog dialog1 = new Dialog(getApplicationContext());
                                dialog1.setContentView(R.layout.dialog);
                                dialog1.setTitle("Error Connecting ...");
                                Button bb;
                                bb = (Button) dialog1.findViewById(R.id.button2);
                                bb.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        get_user();
                                    }
                                });
                            }

                        } catch (Exception e) {
                           e.printStackTrace();
                            open();

                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                open();
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
// Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);

    }

    public void open() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Sorry Couldn't Connect To Our Server. Do You Want To Retry ?");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                get_user();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Splash Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
