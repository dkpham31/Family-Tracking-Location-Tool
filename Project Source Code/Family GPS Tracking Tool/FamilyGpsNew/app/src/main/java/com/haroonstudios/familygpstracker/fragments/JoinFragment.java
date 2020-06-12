package com.haroonstudios.familygpstracker.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.haroonstudios.familygpstracker.R;
import com.haroonstudios.familygpstracker.models.CircleJoin;
import com.haroonstudios.familygpstracker.models.User;
import com.haroonstudios.familygpstracker.utils.MyGDPR;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JoinFragment extends RootFragment
{
    @BindView(R.id.username_join)
    TextInputEditText editTextUsername;
    @BindView(R.id.joinBtn) Button joinButton;

    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;

    DatabaseReference circleReference,joinedReference;
    String joinUserId,currentUserId;
    ProgressDialog progressDialog;
    String currentUsername;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  =  inflater.inflate(R.layout.fragment_join, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

               getActivity().getWindow().setSoftInputMode(

                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN

        );
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        progressDialog = new ProgressDialog(getContext());

        reference = FirebaseDatabase.getInstance().getReference().child("Users");



        reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUsername = dataSnapshot.child("username").getValue(String.class);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUserId = user.getUid();


                if(currentUsername.equals(editTextUsername.getText().toString()))
                {
                    Toast.makeText(getContext(),"You cannot add yourself",Toast.LENGTH_LONG).show();
                }
                else
                {

                        join();

                }


            }
        });
    }

    private void join()
    {
        progressDialog.setTitle("Joining");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        Query query = reference.orderByChild("username").equalTo(editTextUsername.getText().toString());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                    User createUser = null;
                    for(DataSnapshot childDss : dataSnapshot.getChildren())
                    {
                        createUser = childDss.getValue(User.class);
                    }

                  //  createUser = dataSnapshot.getValue(User.class);



                    joinUserId = createUser.userid;

                    circleReference = FirebaseDatabase.getInstance().getReference().child("Users").
                            child(joinUserId).child("CircleMembers");
                    joinedReference = FirebaseDatabase.getInstance().getReference().child("Users").
                            child(user.getUid()).child("CircleMembers");



                    CircleJoin circleJoin = new CircleJoin(currentUserId);
                    final CircleJoin circleJoin1 = new CircleJoin(joinUserId);

                    circleReference.child(user.getUid()).setValue(circleJoin)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        joinedReference.child(joinUserId).setValue(circleJoin1)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            progressDialog.dismiss();

                                                            editTextUsername.setText("");
                                                            Toast.makeText(getContext(),"You joined this circle successfully",Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });


                                    }
                                    else
                                    {
                                        progressDialog.dismiss();
                                        editTextUsername.setText("");
                                        Toast.makeText(getContext(),"Could not join, try again",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }
                else
                {
                    progressDialog.dismiss();
                    editTextUsername.setText("");
                    Toast.makeText(getContext(),"Invalid circle code entered",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getContext(),""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
