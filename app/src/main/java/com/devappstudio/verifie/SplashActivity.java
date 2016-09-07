package com.devappstudio.verifie;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.ybq.android.spinkit.style.ChasingDots;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import datastore.Api;
import datastore.RealmController;
import datastore.User;
import io.realm.Realm;

public class SplashActivity extends Activity {
    static String server_id;
    private Realm realm;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //RealmController.with(getApplication()).clearAll();
        this.realm = RealmController.with(this).getRealm();

        ProgressBar progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        ChasingDots doubleBounce = new ChasingDots();
        progressBar.setIndeterminateDrawable(doubleBounce);


        Thread background = new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void run() {

                try {



                    // Thread will sleep for 5 seconds
                    sleep(5*1000);

                    // After 5 seconds redirect to another intent

                    Intent intent=new Intent(getBaseContext(),Login.class);
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
                    sleep(5*1000);


                    Intent intent=new Intent(getBaseContext(),MyLocationRequest.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();


                } catch (Exception e) {

                }
            }
        };

            if(RealmController.with(getApplication()).hasUser())
            {
                background1.start();
            }
            else
            {
                background.start();
            }

     }

    void get_user()
    {

        //`users`(`id`, `fullname`, `login_type`, `security_code`, `extra_code`, `id_from_provider`, `telephone`, `file_blob`, `file_name`, `is_visible`, `visibility_code`, ``, ``, ``, ``, ``)
        final String tag = "new_user_logn";

        Map<String, String> params = new HashMap<String, String>();
        params.put("id", server_id);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi()+"user_details",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response.toString());
                        try {

                            if(response.get("status").toString().equalsIgnoreCase("1"))
                            {
                                JSONObject jo_stock = (JSONObject) response.get("data");
                                // JSONObject jo_company = response.getJSONObject("company");
                                //JSONObject jo_user = response.getJSONObject("user");
                                //save user
                                // save company


                                User user = new User(jo_stock.get("fullname").toString(),jo_stock.get("telephone").toString(),jo_stock.get("id").toString(),"","",jo_stock.get("file_name").toString());
                                RealmController.with(getApplication()).clearAll();
                                realm.beginTransaction();
                                realm.copyToRealm(user);
                                realm.commitTransaction();


                                final Intent intent = new Intent(SplashActivity.this, MyLocationService.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                                finish();

                            }
                            else
                            {
                                final Dialog dialog1 = new Dialog(getApplicationContext());
                                dialog1.setContentView(R.layout.dialog);
                                dialog1.setTitle("Error Connecting ...");
                                Button bb;
                                bb = (Button)dialog1.findViewById(R.id.button2);
                                bb.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        get_user();
                                    }
                                });
                            }

                        }
                        catch (Exception e)
                        {
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
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);

    }

    public void open(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Sorry Couldn't Connect To Our Server. Do You Want To Retry ?");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                get_user();
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
