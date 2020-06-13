package com.haroonstudios.familygpstracker.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haroonstudios.familygpstracker.R;
import com.haroonstudios.familygpstracker.activities.LiveMapActivity;
import com.haroonstudios.familygpstracker.adapters.MembersAdapter;
import com.haroonstudios.familygpstracker.models.User;
import com.haroonstudios.familygpstracker.utils.MyGDPR;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyCircleFragment extends RootFragment
{
    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    @BindView(R.id.textViewNoFound) TextView textViewNoFound;

    DatabaseReference reference,usersReference;
    FirebaseAuth auth;
    FirebaseUser user;
    User createUser;
    ArrayList<User> nameList;
    ArrayList<String> circleuser_idList;
    String memberUserId;
    MembersAdapter adapter;

    InterstitialAd interstitialAd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  =  inflater.inflate(R.layout.fragment_mycircle, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        setHasOptionsMenu(true);


        interstitialAd = new InterstitialAd(getContext());
        interstitialAd.setAdUnitId(getActivity().getResources().getString(R.string.ADS_INTERSTITIAL_UNIT_ID));

        AdRequest adRequestInterstitial = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, MyGDPR.getBundleAd(getActivity())).build();
        interstitialAd.loadAd(adRequestInterstitial);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        nameList = new ArrayList<>();
        circleuser_idList = new ArrayList<>();
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("CircleMembers");

        adapter = new MembersAdapter(nameList,MyCircleFragment.this,getContext());
        recyclerView.setAdapter(adapter);


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nameList.clear();
                if(dataSnapshot.exists())
                {
                    textViewNoFound.setVisibility(View.GONE);

                    for(DataSnapshot dss: dataSnapshot.getChildren())
                    {
                        memberUserId = dss.child("circlememberid").getValue(String.class);

                        usersReference.child(memberUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                createUser = dataSnapshot.getValue(User.class);
                                nameList.add(createUser);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }

                else
                {
                    textViewNoFound.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(getContext(),""+databaseError,Toast.LENGTH_LONG).show();
            }
        });
    }


    public void openLiveActivity(final int pos, final ArrayList<User> nameArrayList)
    {
        final User addCircle = nameArrayList.get(pos);

        if(interstitialAd.isLoaded())
        {
            interstitialAd.show();
            interstitialAd.setAdListener(new AdListener()
            {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    AdRequest adRequestInterstitial = new AdRequest.Builder()
                            .addNetworkExtrasBundle(AdMobAdapter.class, MyGDPR.getBundleAd(getActivity())).build();
                    interstitialAd.loadAd(adRequestInterstitial);
                    openAct(addCircle);
                }
            });

        }
        else
        {
            openAct(addCircle);
            AdRequest adRequestInterstitial = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, MyGDPR.getBundleAd(getActivity())).build();
            interstitialAd.loadAd(adRequestInterstitial);
        }




    }

    private void openAct(User addCircle)
    {
        String latitude_user = addCircle.lat;
        String longitude_user = addCircle.lng;

        if(latitude_user.equals("na") && longitude_user.equals("na"))
        {
            Toast.makeText(getContext(),"This circle member is not sharing location.",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent mYIntent = new Intent(getContext(), LiveMapActivity.class);
            mYIntent.putExtra("latitude",latitude_user);
            mYIntent.putExtra("longitude",longitude_user);
            mYIntent.putExtra("name",addCircle.name);
            mYIntent.putExtra("userid",addCircle.userid);
            mYIntent.putExtra("date",addCircle.date);
            mYIntent.putExtra("image",addCircle.profile_image);
            mYIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mYIntent);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.navRefresh)
        {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            if (Build.VERSION.SDK_INT >= 26) {
                transaction.setReorderingAllowed(false);
            }
            transaction.detach(this).attach
                    (this).commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_refresh_mycircle, menu);
    }
}
