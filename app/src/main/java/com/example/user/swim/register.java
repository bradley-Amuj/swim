package com.example.user.swim;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class register extends AppCompatActivity {
    private FirebaseAuth mauth;
    private EditText email, password, confirmPass;
    private Button Register, login;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressBar = findViewById(R.id.progressbar);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        confirmPass = findViewById(R.id.et_repassword);
        login = findViewById(R.id.login_now);

        Register = findViewById(R.id.btn_register);

        mauth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(register.this, log_in.class));
                finish();
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email_txt = email.getText().toString().trim();
                String password_txt = password.getText().toString().trim();
                String confirm_pass_txt = confirmPass.getText().toString().trim();


                if (TextUtils.isEmpty(Email_txt)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password_txt)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirm_pass_txt)) {
                    Toast.makeText(getApplicationContext(), "Enter confirm password ", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!password_txt.equals(confirm_pass_txt)) {
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }


                progressBar.setVisibility(View.VISIBLE);


                mauth.createUserWithEmailAndPassword(Email_txt, password_txt)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "User successfully created ", Toast.LENGTH_SHORT).show();
                                    clearfields(email, password, confirmPass);
                                    startActivity(new Intent(register.this, log_in.class));
                                    finish();


                                } else {


                                    Toast.makeText(getApplicationContext(), "User creation failed", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });


            }
        });


    }

    private void clearfields(EditText email, EditText pass, EditText confirmpass) {

        email.setText("");
        pass.setText("");
        confirmpass.setText("");


    }
}
