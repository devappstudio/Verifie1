package com.devappstudio.verifie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cloudrail.si.services.Facebook;
import com.cloudrail.si.services.GooglePlus;

public class Login extends Activity {
   static GooglePlus googleplus;
    static Facebook facebook;

    ImageButton fb_login,gplus_login,login;
    TextView not_registered;
    EditText email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//         googleplus = new GooglePlus(getApplication(), "959643729618-34qni9duh3f9bg1bp7of6ua7467p95up.apps.googleusercontent.com", "bISGHbAFR0paZiDT5pGZygZ8");
//         facebook = new Facebook(getApplication(), "1016646581724640", "c88fa83f9fc4ffd2a5c708ae8ccc6675");

        //dont_have_account
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


    }

    void open_register()
    {
        final Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }



    void fbLogin()
    {

    }

    void gpLogin()
    {

    }


    void loginNow()
    {

    }

    void save_details()
    {

    }

    void openMain()
    {
        final Intent intent = new Intent(this, main.class);
        startActivity(intent);
        finish();

    }



}
