package com.example.friendsbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

import com.example.friendsbook.Adapters.VPAdapter;
import com.example.friendsbook.Fragments.addFriendFragment;
import com.example.friendsbook.Fragments.friendsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class showFriendsActivity extends AppCompatActivity {


    DatabaseReference reference;
    FirebaseUser fuser;


    ImageView viewHome,friendsActivityBackground;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_friends);

        Load_setting();

        viewHome = findViewById(R.id.viewHome);
        viewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               sendUserToNextActivity();
            }
        });


        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.showFriendsViewPager);

        tabLayout.setupWithViewPager(viewPager);
        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        vpAdapter.addFragment(new friendsFragment(),"FRIENDS");
        vpAdapter.addFragment(new addFriendFragment(),"ADD FRIEND");
        viewPager.setAdapter(vpAdapter);
    }
    private void sendUserToNextActivity() {
        Intent intent = new Intent(showFriendsActivity.this,HomeActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    private void status(String status){


        fuser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = fuser.getUid();

        reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
    private void Load_setting() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean check_night = sp.getBoolean("night", false);
        boolean check_corners = sp.getBoolean("corners", false);
        friendsActivityBackground = findViewById(R.id.friendsActivityBackground);

        if (check_night) {

            friendsActivityBackground.setImageDrawable(getDrawable(R.drawable.viewpager_dark_background));

        } else {
            friendsActivityBackground.setImageDrawable(getDrawable(R.drawable.viewpager_background));
        }

        if(check_corners){
            friendsActivityBackground.setBackgroundColor(Color.parseColor("#87CEEB"));
        } else{
            friendsActivityBackground.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    }

