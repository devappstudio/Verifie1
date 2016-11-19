package com.devappstudio.verifie;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import datastore.Api;
import datastore.User;
import de.hdodenhof.circleimageview.CircleImageView;


public class verifyLogin extends AppCompatActivity {

    TextView username;
    String e_secrete,e_link,e_name,e_pic,e_email;
    Button back, login;
    CircleImageView circleImageView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_login);

        try{
            //savedInstanceState = new Intent().getExtras();
            intent = getIntent();
            e_name = intent.getStringExtra("e_name");
            e_email = intent.getStringExtra("e_email");
            e_pic = intent.getStringExtra("e_pic");
            e_secrete = intent.getStringExtra("e_secrete");
            e_link = intent.getStringExtra("e_link");
        }catch(Exception e){

        }
        username = (TextView)findViewById(R.id.user_name);
        back = (Button) findViewById(R.id.deny_button);
        login = (Button) findViewById(R.id.confirm_button);
        circleImageView = (CircleImageView)findViewById(R.id.profile_image);

        Picasso.with(getApplicationContext()).load(e_pic).into(circleImageView);
        username.setText("Welcome "+e_name);
        login.setText("LOGIN AS "+e_name +" ? ");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent intent = new Intent(verifyLogin.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(e_link.equalsIgnoreCase("login_gp"))
                {
                    gpLogin();
                }
                if(e_link.equalsIgnoreCase("login_fb"))
                {
                    fbLogin();
                }
            }
        });
    }




    void fbLogin()
    {

        //`users`(`id`, `fullname`, `login_type`, `security_code`, `extra_code`, `id_from_provider`, `telephone`, `file_blob`, `file_name`, `is_visible`, `visibility_code`, ``, ``, ``, ``, ``)
        final String tag = "new_user_logn";
        final Dialog dialog = new Dialog(verifyLogin.this);
        dialog.setContentView(R.layout.loading_dialog_layout);
        dialog.setTitle("Contacting Our Servers ...");
        TextView text = (TextView) dialog.findViewById(R.id.message);
        text.setText("Please wait ...");

        dialog.show();

        Map<String, String> params = new HashMap<String, String>();
        params.put("security_code", e_secrete);
        params.put("id_from_provider",e_secrete);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi()+"login_fb",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response.toString());
                        dialog.hide();
                        try {

                            if(response.get("status").toString().equalsIgnoreCase("1"))
                            {
                                JSONObject jo_stock = (JSONObject) response.get("data");
                                // JSONObject jo_company = response.getJSONObject("company");
                                //JSONObject jo_user = response.getJSONObject("user");
                                //save user
                                // save company

                                User.deleteAll(User.class);

                                    User user = new User(jo_stock.get("fullname").toString(),jo_stock.get("telephone").toString(),jo_stock.get("id").toString(),"","");
                                    user.save();


                                final Intent intent = new Intent(verifyLogin.this, MyLocationRequest.class);
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
                            Toast.makeText(getApplicationContext(), "Sorry An Error Occurred ", Toast.LENGTH_LONG).show();
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

    void gpLogin()
    {

        //`users`(`id`, `fullname`, `login_type`, `security_code`, `extra_code`, `id_from_provider`, `telephone`, `file_blob`, `file_name`, `is_visible`, `visibility_code`, ``, ``, ``, ``, ``)
        final String tag = "new_user_logn";

        final Dialog dialog = new Dialog(verifyLogin.this);
        dialog.setContentView(R.layout.loading_dialog_layout);
        dialog.setTitle("Contacting Our Servers ...");
        TextView text = (TextView) dialog.findViewById(R.id.message);
        text.setText("Please wait ...");

        dialog.show();

        Map<String, String> params = new HashMap<String, String>();
        params.put("security_code", e_secrete);
        params.put("id_from_provider",e_secrete);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi()+"login_gp",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response.toString());
                        dialog.hide();
                        try {

                            if(response.get("status").toString().equalsIgnoreCase("1"))
                            {
                                JSONObject jo_stock = (JSONObject) response.get("data");
                                // JSONObject jo_company = response.getJSONObject("company");
                                //JSONObject jo_user = response.getJSONObject("user");
                                //save user
                                // save company
                                User.deleteAll(User.class);
                                    User user = new User(jo_stock.get("fullname").toString(),jo_stock.get("telephone").toString(),jo_stock.get("id").toString(),"","");
                          user.save();

                                final Intent intent = new Intent(verifyLogin.this, MyLocationRequest.class);
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
                            Toast.makeText(getApplicationContext(), "Sorry An Error Occurred ", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
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

}
