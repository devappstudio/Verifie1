package com.devappstudio.verifie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import datastore.Api;
import datastore.PwordCode;

public class ForgotPwd extends AppCompatActivity {

    TextView to_login;
    static EditText new_pword,c_pword,key_1,key_2,key_3,key_4,key_5;
    static Button send;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pwd);


        code = PwordCode.first(PwordCode.class).getCode();
        new_pword = (EditText)findViewById(R.id.new_pword);
        c_pword = (EditText)findViewById(R.id.confirm_pword);
        key_1 = (EditText)findViewById(R.id.key_1);
        key_2 = (EditText)findViewById(R.id.key_2);
        key_3 = (EditText)findViewById(R.id.key_3);
        key_4 = (EditText)findViewById(R.id.key_4);
        key_5 = (EditText)findViewById(R.id.key_5);

        to_login = (TextView)findViewById(R.id.to_login);

        to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Intent intent = new Intent(ForgotPwd.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();

            }
        });

        send = (Button)findViewById(R.id.register_button);

        send.setEnabled(false);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updte pssword in db

                //check fields re filled

                final String tag = "new_user_login";
                final ProgressDialog pd = ProgressDialog.show(ForgotPwd.this,"Contacting Server ..."," Please Wait Sending Your Code  ...", true);

                Map<String, String> params = new HashMap<String, String>();
                params.put("password", new_pword.getText().toString());
                params.put("user_id", code);
                pd.show();

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        Api.getApi()+"change_pssword",new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                System.out.print(response.toString());
                                pd.hide();
                                try {

                                    if(response.get("status").toString().equalsIgnoreCase("1"))
                                    {
                                       PwordCode.deleteAll(PwordCode.class);
                                        Toast.makeText(getApplicationContext(),"Please Log In With Your New Password",Toast.LENGTH_LONG).show();
                                        final Intent intent = new Intent(ForgotPwd.this, Login.class);
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
                        Toast.makeText(getApplicationContext(),"Sorry A Network Error Occurred Please Try Again Later",Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                        pd.hide();
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

                //send request to server


            }
        });

        //Do In Background to check if the
    }


    void all_entered_correctly()
    {
        String current = key_1.getText().toString()+key_2.getText().toString()+key_3.getText().toString()+key_4.getText().toString()+key_5.getText().toString();

        if(current.equalsIgnoreCase(code))
        {
            send.setEnabled(true);
            key_1.setEnabled(false);
            key_2.setEnabled(false);
            key_3.setEnabled(false);
            key_4.setEnabled(false);
            key_5.setEnabled(false);
        }
    }
    static void set_vals(String code)
    {
        String[] spl = code.split("(?!^)");
        key_1.setText(spl[0]);
        key_2.setText(spl[1]);
        key_3.setText(spl[1]);
        key_4.setText(spl[1]);
        key_5.setText(spl[1]);

        send.setEnabled(true);
        key_1.setEnabled(false);
        key_2.setEnabled(false);
        key_3.setEnabled(false);
        key_4.setEnabled(false);
        key_5.setEnabled(false);

    }
}
