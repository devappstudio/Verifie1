package com.devappstudio.verifie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class RegisterActivity extends Activity {

    ImageButton register,fb_register,gp_register;
    TextView registered_tv;
    EditText name,email,password,telephone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register = (ImageButton)findViewById(R.id.register_button);
        fb_register = (ImageButton)findViewById(R.id.fb_register_btn);
        gp_register = (ImageButton)findViewById(R.id.gplus_register_btn);

        registered_tv = (TextView)findViewById(R.id.already_registered);

        name = (EditText)findViewById(R.id.register_fullname);
        email = (EditText)findViewById(R.id.register_email);
        password = (EditText)findViewById(R.id.register_password);
        telephone = (EditText)findViewById(R.id.register_telephone);


        registered_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        });
    }

    void openLogin()
    {
        final Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();

    }


    void fbRegister()
    {

    }

    void gpRegister()
    {

    }


    void register_new()
    {

    }

    void save_details()
    {

    }


}
