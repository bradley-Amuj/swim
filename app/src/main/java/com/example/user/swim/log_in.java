package com.example.user.swim;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import static com.example.user.swim.MainActivity.TAG;

public class log_in extends AppCompatActivity {

    private EditText email, password;
    private Button login, register;
    private FirebaseAuth auth;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {

            startActivity(new Intent(log_in.this, MainActivity.class));
            finish();

        }


        Log.d(TAG, "Auth user current user " + auth.getCurrentUser());

        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        login = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressbar);
        register = findViewById(R.id.register_now);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(log_in.this, com.example.user.swim.register.class));
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email_txt = email.getText().toString().trim();
                String password_txt = password.getText().toString().trim();


                if (TextUtils.isEmpty(email_txt)) {
                    Toast.makeText(log_in.this, "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password_txt)) {
                    Toast.makeText(log_in.this, "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(email_txt, password_txt)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                progressBar.setVisibility(View.GONE);

                                if (!task.isSuccessful()) {

                                    Toast.makeText(log_in.this, "Error signing in ", Toast.LENGTH_LONG).show();

                                } else {
                                    Intent intent = new Intent(log_in.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            }
                        });


            }
        });
    }
}
