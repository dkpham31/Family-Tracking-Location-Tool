package com.haroonstudios.familygpstracker.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.haroonstudios.familygpstracker.R;
import com.haroonstudios.familygpstracker.utils.MyGDPR;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    // declare for edit text of email and password
    @BindView(R.id.et_email) EditText editTextEmail;
    @BindView(R.id.et_password) EditText editTextPassword;

    // declare for Firebase data and dialog for notification
    FirebaseAuth auth;
    FirebaseUser user;
    ProgressDialog dialog;



    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // check permission of user to access to the homepage
        if(user == null)
        {
            setContentView(R.layout.activity_main);
            ButterKnife.bind(this);
            MyGDPR.updateConsentStatus(MainActivity.this);
            dialog = new ProgressDialog(this);
            checkPermissions();

        }
        else
        {
            Intent myIntent = new Intent(MainActivity.this, HomeScreenActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(myIntent);
            finish();

        }
    }

    // start register activity
    public void getStarted_click(View v)
    {
            Intent myintent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(myintent);
    }

    public void login(View v)
    {
            loginFinally();
    }

    // login function finally complete when all requirement of email, password, username, and so on are true
    private void loginFinally()
    {
        if(!editTextEmail.equals("") && !editTextPassword.equals("") && editTextPassword.length()>=6)
        {
            dialog.setMessage("Please wait!");
            dialog.show();


            auth.signInWithEmailAndPassword(editTextEmail.getText().toString(),editTextPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                dialog.dismiss();

                                Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }
                            // login fail notification
                            else
                            {

                                Toast.makeText(MainActivity.this,"Incorrect email/password combination.",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this,"Error: "+task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        // requirement of password
        else
        {
            dialog.dismiss();
            Toast.makeText(this,"Password must be 6 characters long",Toast.LENGTH_LONG).show();
        }
    }


    // forgot password function, reset password through email
    public void forgotPassword(View v)
    {
        final EditText taskEditText = new EditText(MainActivity.this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Forgot Password?")
                .setMessage("Enter your email address?")
                .setView(taskEditText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());

                        if(task.equals(""))
                        {
                            Toast.makeText(getApplicationContext(),"Email cannot be empty",Toast.LENGTH_LONG).show();
                        }
                        else {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(task)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(MainActivity.this,"Please check your email.",Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }


                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }


    // check permission of user for login
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            for (int i = 0; i < permissions.length; i++) {

                int grantResult = grantResults[i];

                if (grantResult == PackageManager.PERMISSION_GRANTED) {

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission
                                .READ_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                    }
                }

            }

        }

    }

}
