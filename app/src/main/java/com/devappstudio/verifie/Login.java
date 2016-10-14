package com.devappstudio.verifie;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cloudrail.si.services.Facebook;
import com.cloudrail.si.services.GooglePlus;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import datastore.Api;
import datastore.RealmController;
import datastore.User;
import io.realm.Realm;

public class Login extends Activity {
   static GooglePlus googleplus;
    static Facebook facebook;
    String e_email,e_password,e_secrete,e_name,e_pic;

    ImageButton fb_login,gplus_login,login;
    TextView not_registered;
    EditText email,password;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//         googleplus = new GooglePlus(getApplication(), "959643729618-34qni9duh3f9bg1bp7of6ua7467p95up.apps.googleusercontent.com", "bISGHbAFR0paZiDT5pGZygZ8");
//         facebook = new Facebook(getApplication(), "1016646581724640", "c88fa83f9fc4ffd2a5c708ae8ccc6675");

        //dont_have_account
        this.realm = RealmController.with(this).getRealm();

        fb_login = (ImageButton)findViewById(R.id.fb_login_btn);
        gplus_login = (ImageButton)findViewById(R.id.gplus_login_btn);
        login = (ImageButton)findViewById(R.id.login_btn);
        not_registered = (TextView)findViewById(R.id.dont_have_account);
        email = (EditText)findViewById(R.id.login_email);
        password = (EditText)findViewById(R.id.login_password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMain();
            }
        });


        not_registered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_register();
            }
        });

        fb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login.fb_login task = new Login.fb_login();
                task.execute("");
            }
        });
        gplus_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login.gp_login task = new Login.gp_login();
                task.execute("");
            }
        });
     
    }

    void open_register()
    {
        final Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }



    void fbLogin()
    {

        //`users`(`id`, `fullname`, `login_type`, `security_code`, `extra_code`, `id_from_provider`, `telephone`, `file_blob`, `file_name`, `is_visible`, `visibility_code`, ``, ``, ``, ``, ``)
        final String tag = "new_user_logn";

        Map<String, String> params = new HashMap<String, String>();
        params.put("security_code", e_secrete);
        params.put("id_from_provider",e_secrete);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi()+"login_fb",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response.toString());
                        try {

                            if(response.get("status").toString().equalsIgnoreCase("1"))
                            {
                                JSONObject jo_stock = response.getJSONObject("data");
                                // JSONObject jo_company = response.getJSONObject("company");
                                //JSONObject jo_user = response.getJSONObject("user");
                                //save user
                                // save company

                                RealmController.with(getApplication()).clearAll();

                                    User user = new User(jo_stock.get("fullname").toString(),jo_stock.get("telephone").toString(),jo_stock.get("id").toString(),"","");
                                    realm.beginTransaction();
                                    realm.copyToRealm(user);
                                    realm.commitTransaction();


                                final Intent intent = new Intent(Login.this, MyLocationRequest.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),response.get("error").toString(),Toast.LENGTH_LONG).show();
                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Sorry An Error Occurred "+response.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Sorry A Network Error Occurred",Toast.LENGTH_LONG).show();
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);

    }

    void gpLogin()
    {

        //`users`(`id`, `fullname`, `login_type`, `security_code`, `extra_code`, `id_from_provider`, `telephone`, `file_blob`, `file_name`, `is_visible`, `visibility_code`, ``, ``, ``, ``, ``)
        final String tag = "new_user_logn";
        Map<String, String> params = new HashMap<String, String>();
        params.put("security_code", e_secrete);
        params.put("id_from_provider",e_secrete);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi()+"login_gp",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response.toString());
                        try {

                            if(response.get("status").toString().equalsIgnoreCase("1"))
                            {
                                JSONObject jo_stock = response.getJSONObject("data");
                                // JSONObject jo_company = response.getJSONObject("company");
                                //JSONObject jo_user = response.getJSONObject("user");
                                //save user
                                // save company

                                RealmController.with(getApplication()).clearAll();

                                    User user = new User(jo_stock.get("fullname").toString(),jo_stock.get("telephone").toString(),jo_stock.get("id").toString(),"","");
                                    realm.beginTransaction();
                                    realm.copyToRealm(user);
                                    realm.commitTransaction();


                                final Intent intent = new Intent(Login.this, MyLocationRequest.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),response.get("error").toString(),Toast.LENGTH_LONG).show();
                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Sorry An Error Occurred "+response.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Sorry A Network Error Occurred",Toast.LENGTH_LONG).show();
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);

    }

    void loginNow()
    {

    }

    void save_details()
    {

    }


    void openMain()
    {

        int error = 0;
        //check if all fields are filled
        e_email = email.getText().toString();
        e_password = password.getText().toString();


        if(e_password.isEmpty() || e_password.equalsIgnoreCase("") || e_password.equalsIgnoreCase(null))
        {
            error = 1;
        }



        if(e_email.isEmpty() || e_email.equalsIgnoreCase("") || e_email.equalsIgnoreCase(null))
        {
            error = 1;
        }
        if(error == 1)
        {
            Toast.makeText(getApplicationContext(),"All Fields Are Required",Toast.LENGTH_LONG).show();
        }
        else
        {




            // Tag used to cancel the request

            //`users`(`id`, `fullname`, `login_type`, `security_code`, `extra_code`, `id_from_provider`, `telephone`, `file_blob`, `file_name`, `is_visible`, `visibility_code`, ``, ``, ``, ``, ``)
            final String tag = "new_user_login";
            final Dialog dialog = new Dialog(Login.this);
            dialog.setContentView(R.layout.loading_dialog_layout);
            dialog.setTitle("Contacting Our Servers ...");
            TextView text = (TextView) dialog.findViewById(R.id.message);
            text.setText("Please wait ...");

            dialog.show();

            Map<String, String> params = new HashMap<String, String>();
            params.put("password", e_password);
            params.put("email", e_email);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    Api.getApi()+"login",new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.print(response.toString());
                            dialog.hide();
                            try {

                                if(response.get("status").toString().equalsIgnoreCase("1"))
                                {
                               JSONObject jo_stock = response.getJSONObject("data");
                                    // JSONObject jo_company = response.getJSONObject("company");
                                    //JSONObject jo_user = response.getJSONObject("user");
                                    //save user
                                    // save company

                                    RealmController.with(getApplication()).clearAll();

                                    User user = new User(jo_stock.get("fullname").toString(),jo_stock.get("telephone").toString(),jo_stock.get("id").toString(),"","");
                                    user.setFile_name(jo_stock.get("file_name").toString());
                                    realm.beginTransaction();
                                    realm.copyToRealm(user);
                                    realm.commitTransaction();


                                    final Intent intent = new Intent(Login.this, MyLocationRequest.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),response.get("error").toString(),Toast.LENGTH_LONG).show();
                                }

                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Sorry An Error Occurred "+response.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),"Sorry A Network Error Occurred",Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                    dialog.hide();
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
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag);

        }

        //send request to server
    }



    private class gp_login extends AsyncTask<String, Void, String> {

        /**
         * Runs on the UI thread before {@link #doInBackground}.
         *
         * @see #onPostExecute
         * @see #doInBackground
         */
        final Dialog dialog = new Dialog(Login.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            googleplus = new GooglePlus(getApplication(), "959643729618-34qni9duh3f9bg1bp7of6ua7467p95up.apps.googleusercontent.com", "bISGHbAFR0paZiDT5pGZygZ8");
            dialog.setContentView(R.layout.loading_dialog_layout);
            dialog.setTitle("Retrieving your information ...");
            TextView text = (TextView) dialog.findViewById(R.id.message);
            text.setText("Please wait ...");
            dialog.show();

        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */

        @Override
        protected String doInBackground(String... params) {
            e_email = googleplus.getEmail();
            e_secrete = googleplus.getIdentifier();
            e_name = googleplus.getFullName();
            e_pic = googleplus.getPictureURL();
            return null;
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param s The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.hide();
            final Intent intent = new Intent(Login.this, verifyLogin.class);
            intent.putExtra("val", "shown");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("e_name", e_name);
            intent.putExtra("e_email", e_email);
            intent.putExtra("e_pic", e_pic);
            intent.putExtra("e_secrete", e_secrete);
            intent.putExtra("e_link", "login_gp");
            startActivity(intent);
            finish();        }
    }


    private class fb_login extends AsyncTask<String, Void, String> {

        /**
         * Runs on the UI thread before {@link #doInBackground}.
         *
         * @see #onPostExecute
         * @see #doInBackground
         */
        final Dialog dialog = new Dialog(Login.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
         facebook = new Facebook(getApplication(), "1016646581724640", "c88fa83f9fc4ffd2a5c708ae8ccc6675");
            dialog.setContentView(R.layout.loading_dialog_layout);
            dialog.setTitle("Retrieving your information ...");
            TextView text = (TextView) dialog.findViewById(R.id.message);
            text.setText("Please wait ...");
            dialog.show();

        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */

        @Override
        protected String doInBackground(String... params) {
            e_email = facebook.getEmail();
            e_secrete = facebook.getIdentifier();
            e_name = facebook.getFullName();
            e_pic = facebook.getPictureURL();
            return null;
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param s The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.hide();
            final Intent intent = new Intent(Login.this, verifyLogin.class);
            intent.putExtra("val", "shown");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("e_name", e_name);
            intent.putExtra("e_email", e_email);
            intent.putExtra("e_pic", e_pic);
            intent.putExtra("e_secrete", e_secrete);
            intent.putExtra("e_link", "login_fb");
            startActivity(intent);
            finish();
        }


    }




}
