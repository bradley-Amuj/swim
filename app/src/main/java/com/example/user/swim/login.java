package com.example.user.swim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class login extends AppCompatActivity {

    EditText mTextusername;
    EditText mTextpassword;
    Button mbuttonlogin;
    TextView mtextviewRegister;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextusername = (EditText)findViewById(R.id.EditText_username);
        mTextpassword = (EditText)findViewById(R.id.EditText_password);
        mbuttonlogin = (Button)findViewById(R.id.button_Login);
        mtextviewRegister = (TextView)findViewById(R.id.Textview_Register);
        mtextviewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(login.this,register.class);
                startActivity(registerIntent);
            }
        });

}
