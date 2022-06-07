package com.example.friendsbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;

import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;


import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.friendsbook.Adapters.VPAdapter;
import com.example.friendsbook.Fragments.callsfragment;
import com.example.friendsbook.Fragments.chatsfragment;
import com.example.friendsbook.Fragments.groupsfragment;
import com.example.friendsbook.Fragments.timelinefragment;
import com.example.friendsbook.Models.MessageModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class HomeActivity extends AppCompatActivity {

    DatabaseReference reference;
    FirebaseUser mUser;
    ImageView activityBack;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    FirebaseUser fuser;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Load_setting();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton btn = findViewById(R.id.btnAdd);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToNextActivity();
            }
        });





        activityBack = findViewById(R.id.activityBack);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewpager);


        tabLayout.setupWithViewPager(viewPager);

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

                fuser = FirebaseAuth.getInstance().getCurrentUser();
                int unread = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    if(messageModel.getReceiver().equals(fuser.getUid()) && !messageModel.isIsseen()){
                        unread++;
                    }
                }
                if(unread  == 0 ){
                    vpAdapter.addFragment(new chatsfragment(), "CHITCHAT");
                } else{
                    vpAdapter.addFragment(new chatsfragment(), "("+unread+")CHITCHAT");
                }
                
                vpAdapter.addFragment(new groupsfragment(), "GROUPS");
                vpAdapter.addFragment(new timelinefragment(), "TIMELINE");
                vpAdapter.addFragment(new callsfragment(), "CALLS");
                viewPager.setAdapter(vpAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        int id = item.getItemId();

        switch (id) {
            case R.id.logout:
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                break;
            case R.id.about:
                Toast.makeText(this, "We don't have about at the moment", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Intent intent = new Intent(getApplicationContext(), RootPreferences.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                Toast.makeText(this, "Working on it", Toast.LENGTH_SHORT).show();
                break;
            case R.id.Message:
                Toast.makeText(this, "Disabled", Toast.LENGTH_SHORT).show();
                break;
        }
        return  true;
    }




    private void sendUserToNextActivity() {


        Intent intent = new Intent(HomeActivity.this, showFriendsActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);



        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
   private void status(String status){


        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = mUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        HashMap<String,Object>hashMap = new HashMap<>();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    private void Load_setting() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean check_night = sp.getBoolean("night", false);
        boolean check_corners = sp.getBoolean("corners", false);
        activityBack = findViewById(R.id.activityBack);
        if (check_night) {
            activityBack.setImageDrawable(getDrawable(R.drawable.viewpager_dark_background));

        } else {
            activityBack.setImageDrawable(getDrawable(R.drawable.viewpager_background));
        }

        if(check_corners){
            activityBack.setBackgroundColor(Color.parseColor("#87CEEB"));
        } else{
            activityBack.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }


}