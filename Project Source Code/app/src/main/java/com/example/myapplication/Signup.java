package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText username;
    EditText password;
    EditText confirmPassword;
    EditText mEmail;
    EditText mPhone;
    Button Register;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        username = findViewById(R.id.userRegister);
        password = findViewById(R.id.passWordRegister);
        confirmPassword = findViewById(R.id.confirmedRegister);
        mEmail = findViewById(R.id.Email1);
        mPhone = findViewById(R.id.Phone);
        Register = findViewById(R.id.RegisterButton);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim();
                final String passwords = password.getText().toString().trim();
                final String usernames = username.getText().toString();
                final String phone = mPhone.getText().toString();
                String cpwd = confirmPassword.getText().toString();

                if (TextUtils.isEmpty(cpwd)){
                    confirmPassword.setError("Confirmed password is required");
                }
                if (TextUtils.isEmpty(usernames)){
                    username.setError("Please enter your name");
                }

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(passwords)){
                    password.setError("Password is required");
                    return;
                }
                if (passwords.length() < 8){
                    password.setError("Password needs to be more than 8 characters");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email,passwords).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            fAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Signup.this, "Register successful, Please check your email for verification !", Toast.LENGTH_SHORT).show();
                                        userID = fAuth.getCurrentUser().getUid();
                                        DocumentReference documentReference = fStore.collection("users").document(userID);
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("UserName", usernames);
                                        user.put("email", email);
                                        user.put("phone", phone);
                                        user.put("password", passwords);
                                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "successful" + userID);
                                            }
                                        });
                                        startActivity(new Intent(Signup.this, MainActivity.class));

                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(Signup.this, "Error! "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
    };

}



