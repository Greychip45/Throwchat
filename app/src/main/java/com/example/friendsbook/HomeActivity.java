package com.example.friendsbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentPagerAdapter;

import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;


import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.friendsbook.Adapters.VPAdapter;
import com.example.friendsbook.Fragments.callsfragment;
import com.example.friendsbook.Fragments.chatsfragment;
import com.example.friendsbook.Fragments.groupsfragment;
import com.example.friendsbook.Fragments.timelinefragment;
import com.example.friendsbook.Models.MessageModel;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;


public class HomeActivity extends AppCompatActivity  {

    DatabaseReference reference;
    FirebaseUser mUser;
    CardView cardImage,cardText;
    EditText etText;
    private ProgressBar progressBar;
    private StorageReference mStorage;
    private FirebaseUser fuser;
    private DatabaseReference mRef;
    ImageView activityBack,postImage;
    ConstraintLayout constraintLayout;
    TextInputLayout inputLayout;

    private String dbkey;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Uri uri;

    FloatingActionButton fab;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Load_setting();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        inputLayout = findViewById(R.id.inputLayout);
        progressBar = findViewById(R.id.progress_bar);
        constraintLayout = findViewById(R.id.constaritLayout);
        cardText = findViewById(R.id.cardText);
        cardImage = findViewById(R.id.cardImage);
        etText = findViewById(R.id.et_text);
        postImage = findViewById(R.id.postImage);

        cardText.setVisibility(View.GONE);
        cardImage.setVisibility(View.GONE);

        fab = findViewById(R.id.btnAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewPager.getCurrentItem();
                if(position == 0){
                    sendUserToNextActivity();
                }else if (position == 1){
                    ImagePicker.Companion.with(HomeActivity.this)
                            .galleryOnly()
                            .start();
                }

            }
        });


        inputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPost();
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

                vpAdapter.addFragment(new chatsfragment(),"CHITCHAT");
                vpAdapter.addFragment(new timelinefragment(),"TIMELINE");

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
                Intent i = new Intent(getApplicationContext(),SetupProfileActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData() != null)
        {
            uri = data.getData();
            Glide.with(HomeActivity.this).load(uri).centerCrop().into(postImage);
            cardText.setVisibility(View.INVISIBLE);
            cardImage.setVisibility(View.VISIBLE);

        }
    }

    public void expand(View view) {
        int v = (cardText.getVisibility() == View.INVISIBLE)? View.VISIBLE: View.INVISIBLE;
        TransitionManager.beginDelayedTransition(constraintLayout,new AutoTransition());
        cardText.setVisibility(v);
    }
    private void uploadPost(){
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("Posts");
        dbkey = mRef.push().getKey();
        HashMap<String,Object> map = new HashMap<>();
        map.put("post",etText.getText().toString());
        map.put("posterId",fuser.getUid());
        map.put("key",dbkey);

        mStorage = FirebaseStorage.getInstance().getReference("Posts");
        StorageReference filereference = mStorage.child(System.currentTimeMillis()+".png");
        filereference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                cardImage.setVisibility(View.GONE);
                cardText.setVisibility(View.GONE);
                filereference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        map.put("postImage",task.getResult().toString());

                        mRef.child(dbkey).updateChildren(map);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                double progress = (100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
            }
        });



    }

}