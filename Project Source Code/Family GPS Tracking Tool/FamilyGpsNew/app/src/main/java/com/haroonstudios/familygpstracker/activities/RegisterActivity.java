package com.haroonstudios.familygpstracker.activities;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.haroonstudios.familygpstracker.R;
import com.haroonstudios.familygpstracker.utils.Utils;
import com.theartofdev.edmodo.cropper.CropImage;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    // declare bindview for all user information such as name, email, username, and so on on Firebase
    @BindView(R.id.et_email) EditText editTextEmail;
    @BindView(R.id.et_username) EditText editTextUsername;
    @BindView(R.id.et_phone) EditText editTextPhone;
    @BindView(R.id.et_password) EditText editTextPassword;
    @BindView(R.id.et_name) EditText editTextName;
    @BindView(R.id.imagePick) ImageView imageViewPick;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.image) CircleImageView circleImageView;

    // image
    Uri resultUri;

    // set up register oncreate on login page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        });

    }

    // start adding photo/image from davice storage
    public void pickPhoto(View v)
    {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, 12);
    }


    // complete register finally
    public void register(View v)
    {
        registerFinally();

    }

    // register function
    public void registerFinally()
    {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String name = editTextName.getText().toString();
        String username = editTextUsername.getText().toString();
        String phone = editTextPhone.getText().toString();


        // check requirement for email
        if(email.isEmpty() || !Utils.isValid(email))
        {
            Toast.makeText(this,"Email is not valid",Toast.LENGTH_LONG).show();
            return;
        }

        // check requirement for password
        if(password.isEmpty() || password.length()<6)
        {
            Toast.makeText(this,"Password must be 6 characters long.",Toast.LENGTH_LONG).show();
            return;
        }

        // check requirement for name
        if(name.equals(""))
        {
            Toast.makeText(this,"Please input your name.",Toast.LENGTH_LONG).show();
            return;
        }

        // check requirement for username
        if(username.equals(""))
        {
            Toast.makeText(this,"Please input your username.",Toast.LENGTH_LONG).show();
            return;
        }

        // check requirement for phone number
        if(phone.equals(""))
        {
            Toast.makeText(this,"Please input your phone number.",Toast.LENGTH_LONG).show();
            return;
        }

        // check requirement for image
        if(resultUri==null)
        {
            Toast.makeText(this,"Please pick an image from gallery.",Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(RegisterActivity.this,InviteCodeActivity.class);
        intent.putExtra("email",email);
        intent.putExtra("password",password);
        intent.putExtra("name",name);
        intent.putExtra("username",username);
        intent.putExtra("phone",phone);
        intent.putExtra("imageUri",resultUri.toString());
        startActivity(intent);
    }


    public void openLogin()
    {
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }


    // crop image chose from photo/image storage of device
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 12 && resultCode==RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            if(uri!=null)
            {
                CropImage.activity(uri)
                        .start(this);
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                if(resultUri!=null)
                {
                    circleImageView.setImageURI(resultUri);
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                
                Toast.makeText(RegisterActivity.this,
                       "Error:"+error.getLocalizedMessage(),Toast.LENGTH_SHORT)
                        .show();

            }
        }

    }
}
