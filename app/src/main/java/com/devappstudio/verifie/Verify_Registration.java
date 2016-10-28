package com.devappstudio.verifie;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import datastore.Api;
import datastore.RealmController;
import datastore.User;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

public class Verify_Registration extends AppCompatActivity {

    CircleImageView circleImageView;
    Intent intent;
    String e_name,e_email,e_password,e_telephone,e_screen_name,e_date_of_birth,e_gender,e_pic,e_secrete,e_link;
    EditText name,email,password,telephone,screen_name,date_of_birth;
    String[] arr = { "Male", "Female"};
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    AutoCompleteTextView gender;
    private Realm realm;
    Button register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify__registration);
        /**
         *  intent.putExtra("e_name", e_name);
         intent.putExtra("e_date_of_birth", e_date_of_birth);
         intent.putExtra("e_gender", e_gender);
         intent.putExtra("e_email", e_email);
         intent.putExtra("e_pic", e_pic);
         intent.putExtra("e_secrete", e_secrete);
         */

        try{
            //savedInstanceState = new Intent().getExtras();
            intent = getIntent();
            e_name = intent.getStringExtra("e_name");
            e_email = intent.getStringExtra("e_email");
            e_telephone = intent.getStringExtra("e_telephone");
            e_date_of_birth = intent.getStringExtra("e_date_of_birth");
            e_gender = intent.getStringExtra("e_gender");
            e_pic = intent.getStringExtra("e_pic");
            e_secrete = intent.getStringExtra("e_secrete");
            e_link = intent.getStringExtra("e_link");
        }catch(Exception e){

        }


        circleImageView = (CircleImageView)findViewById(R.id.profile_image);
        Picasso.with(getApplicationContext()).load(e_pic).into(circleImageView);




        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        name = (EditText)findViewById(R.id.register_fullname);
        name.setText(e_name);
        email = (EditText)findViewById(R.id.register_email);
        email.setText(e_email);
        screen_name = (EditText)findViewById(R.id.screen_name);
        date_of_birth = (EditText)findViewById(R.id.date_of_birth);
        date_of_birth.setText(e_date_of_birth);
        password = (EditText)findViewById(R.id.register_password);

        this.realm = RealmController.with(this).getRealm();

        gender = (AutoCompleteTextView)
                findViewById(R.id.gender);
        gender.setText(e_gender);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item, arr);

        gender.setThreshold(2);
        gender.setAdapter(adapter);
        telephone = (EditText)findViewById(R.id.register_telephone);
        telephone.setText(e_telephone);
        register = (Button)findViewById(R.id.register_button);


        setDateTimeField();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMain();
            }
        });



    }
    private void setDateTimeField() {
        date_of_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(!fromDatePickerDialog.isShowing())
                    fromDatePickerDialog.show();
                */
            }
        });

        date_of_birth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(date_of_birth.isFocused())
                {
                   /* if(!fromDatePickerDialog.isShowing())
                        fromDatePickerDialog.show();
                        */
                }
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                date_of_birth.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }


    void openMain()
    {

        int error = 0;
        //check if all fields are filled
        e_date_of_birth = date_of_birth.getText().toString();
        e_email = email.getText().toString();
        e_gender = gender.getText().toString();
        e_name = name.getText().toString();
        e_password = password.getText().toString();
        e_screen_name = screen_name.getText().toString();
        e_telephone = telephone.getText().toString();

        if(e_telephone.isEmpty() || e_telephone.equalsIgnoreCase("") || e_telephone.equalsIgnoreCase(null))
        {
            error = 1;
        }

        if(e_screen_name.isEmpty() || e_screen_name.equalsIgnoreCase("") || e_screen_name.equalsIgnoreCase(null))
        {
            error = 1;
        }

        if(e_password.isEmpty() || e_password.equalsIgnoreCase("") || e_password.equalsIgnoreCase(null))
        {
            error = 1;
        }

        if(e_name.isEmpty() || e_name.equalsIgnoreCase("") || e_name.equalsIgnoreCase(null))
        {
            error = 1;
        }

        if(e_gender.isEmpty() || e_gender.equalsIgnoreCase("") || e_gender.equalsIgnoreCase(null))
        {
            error = 1;
        }

        if(e_email.isEmpty() || e_email.equalsIgnoreCase("") || e_email.equalsIgnoreCase(null))
        {
            error = 1;
        }


        if(e_date_of_birth.isEmpty() || e_date_of_birth.equalsIgnoreCase("") || e_date_of_birth.equalsIgnoreCase(null))
        {
            error = 1;
        }

        if(error == 1)
        {
            Toast.makeText(getApplicationContext(),"All Fields Are Required",Toast.LENGTH_LONG).show();
        }
        else
        {

            final String tag = "new_user_register";
            final Dialog dialog = new Dialog(Verify_Registration.this);
            dialog.setContentView(R.layout.loading_dialog_layout);
            dialog.setTitle("Contacting Our Servers ...");
            TextView text = (TextView) dialog.findViewById(R.id.message);
            text.setText("Please wait ...");

            dialog.show();

            Map<String, String> params = new HashMap<String, String>();
            params.put("fullname", e_name);
            params.put("telephone", e_telephone);
            params.put("screen_name", e_screen_name);
            params.put("password", e_password);
            params.put("gender", e_gender);
            params.put("date_of_birth", e_date_of_birth);
            params.put("email", e_email);
            params.put("security_code", e_secrete);
            params.put("extra_code", " --- --- --- --- ");
            params.put("id_from_provider",e_secrete);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    Api.getApi()+e_link,new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.print(response.toString());
                            dialog.hide();
                            Realm realm = Realm.getDefaultInstance();
                            try {
                                if(response.get("status").toString().equalsIgnoreCase("1"))
                                {
//                               JSONObject jo_stock = response.getJSONObject("stock_user");
                                    // JSONObject jo_company = response.getJSONObject("company");
                                    //JSONObject jo_user = response.getJSONObject("user");
                                    //save user
                                    // save company
                                    RealmController.with(getApplication()).clearAll();

                                    User user = new User(e_name,e_telephone,response.get("data").toString(),"","");
                                    realm.beginTransaction();
                                    realm.copyToRealm(user);
                                    realm.commitTransaction();
                                    final Intent intent = new Intent(Verify_Registration.this, MyLocationRequest.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    //Toast.makeText(getApplicationContext(),response.get("error").toString(),Toast.LENGTH_LONG).show();
                                }

                            }
                            catch (Exception e)
                            {
                                realm.cancelTransaction();
                                e.printStackTrace();
                                try {
                                    if(response.get("status").toString().equalsIgnoreCase("1"))
                                    {
//                               JSONObject jo_stock = response.getJSONObject("stock_user");
                                        // JSONObject jo_company = response.getJSONObject("company");
                                        //JSONObject jo_user = response.getJSONObject("user");
                                        //save user
                                        // save company
                                        RealmController.with(getApplication()).clearAll();

                                        User user = new User(e_name,e_telephone,response.get("data").toString(),"","");
                                        realm.beginTransaction();
                                        realm.copyToRealm(user);
                                        realm.commitTransaction();
                                        final Intent intent = new Intent(Verify_Registration.this, MyLocationRequest.class);
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
                                catch (Exception ee)
                                {
                                    //realm.cancelTransaction();
                                    ee.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Sorry An Error Occurred "+response.toString(), Toast.LENGTH_LONG).show();
                                }
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

}
