package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    EditText mEmail;
    EditText password;
    TextView SignUp;
    TextView Forget;
    private Button login;
    SharedPreferences sharedPreferences;
    CheckBox checkBox;
    ProgressBar progressBar;
    SharedPreferences.Editor editor;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEmail = findViewById(R.id.E_Mail);
        password = findViewById(R.id.passWord);
        SignUp = findViewById(R.id.SignUp);
        login = findViewById(R.id.button);
        checkBox = findViewById(R.id.checkBox2);
        Forget = findViewById(R.id.Forget);
        progressBar = findViewById(R.id.progressBar2);
        sharedPreferences = getSharedPreferences("Login",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        checkPreferences();
        fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference usersRef = rootRef.collection("users");

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email = mEmail.getText().toString().trim();
                String passwords = password.getText().toString().trim();

                if (checkBox.isChecked()) {
                    editor.putString("email",mEmail.getText().toString());
                    editor.putString("password",password.getText().toString());
                    editor.commit();
                }
                else {
                    editor.putString("email","");
                    editor.putString("password","");
                    editor.commit();
                }
                editor.putBoolean("checkbox",checkBox.isChecked());
                editor.commit();

                if (TextUtils.isEmpty(email)) {
                    progressBar.setVisibility(View.GONE);
                    mEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(passwords)) {
                    progressBar.setVisibility(View.GONE);
                    password.setError("Password is required");
                    return;
                }
                fAuth.signInWithEmailAndPassword(email, passwords).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {


                            if(fAuth.getCurrentUser().isEmailVerified()){
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                usersRef.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                String username = document.getString("UserName");
                                                if (username.equals("Khai")) {
                                                    startActivity(new Intent(getApplicationContext(), Contact.class));
                                                }
                                                else {
                                                    startActivity(new Intent(getApplicationContext(), HomePage.class));
                                                }
                                            }
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(MainActivity.this, "Please verify your email address " , Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Signup.class));
            }
        });
        Forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ForgetPassword.class));
            }
        });


    }
    public void checkPreferences() {
        String mail = sharedPreferences.getString("email","");
        String pass = sharedPreferences.getString("password","");
        mEmail.setText(mail);
        password.setText(pass);
        boolean valueChecked = sharedPreferences.getBoolean("checkbox",false);
        checkBox.setChecked(valueChecked);
    }

}



