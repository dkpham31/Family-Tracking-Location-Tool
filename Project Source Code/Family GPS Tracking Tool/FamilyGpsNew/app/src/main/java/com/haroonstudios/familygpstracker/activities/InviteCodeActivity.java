package com.haroonstudios.familygpstracker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.haroonstudios.familygpstracker.R;
import com.haroonstudios.familygpstracker.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InviteCodeActivity extends AppCompatActivity {

    @BindView(R.id.textView4) TextView textViewUsername;
    @BindView(R.id.registerButton) Button registerButton;

    String email,username,name,phone,password;
    Uri imageUri;
    boolean isUsernameFound = false;


    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    ProgressDialog dialog;
    StorageReference firebaseStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);
        ButterKnife.bind(this);
        dialog = new ProgressDialog(this);
        registerButton.setVisibility(View.GONE);
        Intent intent = getIntent();

        if(intent!=null)
        {
            email = intent.getStringExtra("email");
            username = intent.getStringExtra("username");
            name = intent.getStringExtra("name");
            password = intent.getStringExtra("password");
            phone = intent.getStringExtra("phone");
            imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));
            textViewUsername.setText(username);
        }

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        Query query = reference.orderByChild("username").equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                registerButton.setVisibility(View.VISIBLE);
                    if(dataSnapshot.exists())
                    {
                        isUsernameFound = true;
                    }
                    else
                    {
                        isUsernameFound = false;
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                registerButton.setVisibility(View.VISIBLE);
            }
        });

        firebaseStorageReference = FirebaseStorage.getInstance().getReference().child("Profile_images");
    }

    public void registerUser(View v)
    {
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Creating new Profile. Please wait");
        dialog.setCancelable(false);
        dialog.show();


        // check if username exists or not..

        // if it does not exist, then do all, if it exists, please go back
        if(isUsernameFound)
        {
            dialog.dismiss();
            Toast.makeText(InviteCodeActivity.this,"Username is already found. Please set a different username",Toast.LENGTH_SHORT).show();
        }
        else
        {
            register();
        }


    }


    public void register()
    {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser = auth.getCurrentUser();

                            User currentUser = new User(name,email,password,"na",username, firebaseUser.getUid(),
                                    "false","na","na","default_image");

                            reference.child(firebaseUser.getUid()).setValue(currentUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                StorageReference filePath =
                                                        firebaseStorageReference.child(firebaseUser.getUid() + ".jpg");

                                                filePath.putFile(imageUri)
                                                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                                                if (task.isSuccessful()) {

                                                                    Task<Uri> result = task.getResult().getStorage().getDownloadUrl();
                                                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                        @Override
                                                                        public void onSuccess(Uri uri) {


                                                                            reference.child(firebaseUser.getUid()).child("profile_image").setValue(uri.toString())
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful())
                                                                                            {
                                                                                                dialog.dismiss();
                                                                                                Intent intent = new Intent(InviteCodeActivity.this, HomeScreenActivity.class);
                                                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                                startActivity(intent);
                                                                                                finish();

                                                                                            }

                                                                                        }
                                                                                    });
                                                                        }
                                                                    });
                                                                }
                                                                else {
                                                                    dialog.dismiss();
                                                                    Toast.makeText(InviteCodeActivity.this,
                                                                            "Could not upload firebaseUser image",
                                                                            Toast.LENGTH_SHORT).show();
                                                                }


                                                            }
                                                        });


                                            }
                                        }
                                    });
                        } else {
                            dialog.dismiss();
                            Toast.makeText(InviteCodeActivity.this, "This email address/username is already registered. Please go back and change your email address", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


}
