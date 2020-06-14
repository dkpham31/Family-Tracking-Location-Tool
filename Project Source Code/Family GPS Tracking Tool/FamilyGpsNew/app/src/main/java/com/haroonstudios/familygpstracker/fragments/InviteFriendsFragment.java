package com.haroonstudios.familygpstracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haroonstudios.familygpstracker.R;


import butterknife.BindView;
import butterknife.ButterKnife;

public class InviteFriendsFragment extends RootFragment
{
    // declare text view and button as bind view
    @BindView(R.id.textView4) TextView textViewCode;
    @BindView(R.id.shareButton) Button shareButton;

    // database reference for authentity and user on Firebase
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  =  inflater.inflate(R.layout.fragment_invite_friends, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String user_code = dataSnapshot.child(firebaseUser.getUid()).child("username").getValue().toString();
                textViewCode.setText(user_code);
            }

            // error message when database has an error
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(),"Could not fetch circle code",Toast.LENGTH_LONG).show();
            }
        });


        // message for clicking on share button
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, "Hello, I'm using Family Locator App. Join my circle. My username is:" + textViewCode.getText().toString());
                startActivity(i.createChooser(i, "Share using:"));
            }
        });
    }
}
