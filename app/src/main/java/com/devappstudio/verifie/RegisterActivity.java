package com.devappstudio.verifie;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cloudrail.si.services.Facebook;
import com.cloudrail.si.services.GooglePlus;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import datastore.Api;
import datastore.RealmController;
import datastore.User;
import io.realm.Realm;


public class RegisterActivity extends Activity{

    static GooglePlus googleplus;
    static Facebook facebook;
    ImageButton register,fb_register,gp_register;
    TextView registered_tv;
    EditText name,email,password,telephone,screen_name,date_of_birth;
    AutoCompleteTextView gender;
    String e_name,e_email,e_password,e_telephone,e_screen_name,e_date_of_birth,e_gender;
    String[] arr = { "Male", "Female"};
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        register = (ImageButton)findViewById(R.id.register_button);
        fb_register = (ImageButton)findViewById(R.id.fb_register_btn);
        gp_register = (ImageButton)findViewById(R.id.gplus_register_btn);

        registered_tv = (TextView)findViewById(R.id.already_registered);

        name = (EditText)findViewById(R.id.register_fullname);
        email = (EditText)findViewById(R.id.register_email);
        screen_name = (EditText)findViewById(R.id.screen_name);
        date_of_birth = (EditText)findViewById(R.id.date_of_birth);
        password = (EditText)findViewById(R.id.register_password);

        this.realm = RealmController.with(this).getRealm();

        gender = (AutoCompleteTextView)
                findViewById(R.id.gender);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item, arr);

        gender.setThreshold(2);
        gender.setAdapter(adapter);
        telephone = (EditText)findViewById(R.id.register_telephone);
        registered_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMain();
            }
        });
        setDateTimeField();


        fb_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebook_registration();
            }
        });
        gp_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gplus_registration();
            }
        });

    }

    void openLogin()
    {
        final Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();

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
            telephone.setBackgroundColor(getResources().getColor(R.color.red));
        }

        if(e_screen_name.isEmpty() || e_screen_name.equalsIgnoreCase("") || e_screen_name.equalsIgnoreCase(null))
        {
            error = 1;
            screen_name.setBackgroundColor(getResources().getColor(R.color.red));
        }

        if(e_password.isEmpty() || e_password.equalsIgnoreCase("") || e_password.equalsIgnoreCase(null))
        {
            error = 1;
            password.setBackgroundColor(getResources().getColor(R.color.red));
        }

       if(e_name.isEmpty() || e_name.equalsIgnoreCase("") || e_name.equalsIgnoreCase(null))
        {
            error = 1;
            name.setBackgroundColor(getResources().getColor(R.color.red));
        }

      if(e_gender.isEmpty() || e_gender.equalsIgnoreCase("") || e_gender.equalsIgnoreCase(null))
        {
            error = 1;
            gender.setBackgroundColor(getResources().getColor(R.color.red));
        }

      if(e_email.isEmpty() || e_email.equalsIgnoreCase("") || e_email.equalsIgnoreCase(null))
        {
            error = 1;
            email.setBackgroundColor(getResources().getColor(R.color.red));
        }


      if(e_date_of_birth.isEmpty() || e_date_of_birth.equalsIgnoreCase("") || e_date_of_birth.equalsIgnoreCase(null))
        {
            error = 1;
            date_of_birth.setBackgroundColor(getResources().getColor(R.color.red));
        }

        if(error == 1)
        {
            Toast.makeText(getApplicationContext(),"All Fields Are Required",Toast.LENGTH_LONG).show();
        }
        else
        {




            // Tag used to cancel the request

            //`users`(`id`, `fullname`, `login_type`, `security_code`, `extra_code`, `id_from_provider`, `telephone`, `file_blob`, `file_name`, `is_visible`, `visibility_code`, ``, ``, ``, ``, ``)
            final String tag = "new_user_register";
            final Dialog dialog = new Dialog(RegisterActivity.this);
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
            params.put("security_code", " --- --- --- ");
            params.put("extra_code", " --- --- --- --- ");
            params.put("id_from_provider", " ||--- --- --- ---|| ");

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    Api.getApi()+"add_user",new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.print(response.toString());
                            dialog.hide();
                            try {

                                if(response.get("status").toString().equalsIgnoreCase("1"))
                                {
//                               JSONObject jo_stock = response.getJSONObject("stock_user");
                                   // JSONObject jo_company = response.getJSONObject("company");
                                    //JSONObject jo_user = response.getJSONObject("user");
                                    //save user
                                    // save company
                                    User user = new User(e_name,e_telephone,response.get("data").toString(),"","");
                                    realm.beginTransaction();
                                    realm.copyToRealm(user);
                                    realm.commitTransaction();

                                    final Intent intent = new Intent(RegisterActivity.this, main.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
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
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag);

        }

        //send request to server
    }


    void facebook_registration()
    {
         facebook = new Facebook(getApplication(), "1016646581724640", "c88fa83f9fc4ffd2a5c708ae8ccc6675");

        new Thread() {
            public void run() {
                try {
                    Toast.makeText(getApplicationContext(),facebook.getFullName(),Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();

    }
    void gplus_registration()
    {
        googleplus = new GooglePlus(getApplication(), "959643729618-34qni9duh3f9bg1bp7of6ua7467p95up.apps.googleusercontent.com", "bISGHbAFR0paZiDT5pGZygZ8");
        new Thread() {
            public void run() {
                try {
                    Toast.makeText(getApplicationContext(),googleplus.getFullName(),Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();


    }


    private void setDateTimeField() {
        date_of_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fromDatePickerDialog.isShowing())
                    fromDatePickerDialog.show();
            }
        });

        date_of_birth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(date_of_birth.isFocused())
                {
                    if(!fromDatePickerDialog.isShowing())
                        fromDatePickerDialog.show();
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

}
