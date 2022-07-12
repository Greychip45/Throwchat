package com.example.friendsbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.friendsbook.Adapters.commentsAdapter;
import com.example.friendsbook.Models.Users;
import com.example.friendsbook.Models.commentsModel;
import com.example.friendsbook.databinding.ActivityCommentsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {

    private DatabaseReference mRef;
    private static String key,posterId,postImage;
    private ActivityCommentsBinding binding;
    private ArrayList<commentsModel> cModel = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        key = getIntent().getStringExtra("key");
        posterId = getIntent().getStringExtra("posterId");
        postImage = getIntent().getStringExtra("postImage");

        LinearLayoutManager lm = new LinearLayoutManager(CommentsActivity.this);
        binding.chatRecycler.setLayoutManager(lm);
        commentsAdapter adapter = new commentsAdapter(cModel,CommentsActivity.this);
        binding.chatRecycler.setAdapter(adapter);

        Glide.with(CommentsActivity.this)
                .load(postImage)
                .centerCrop()
                .into(binding.postImage);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mRef = FirebaseDatabase.getInstance().getReference("users").child(posterId);
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users model = snapshot.getValue(Users.class);

                        binding.tvPoster.setText(model.getName());
                        Glide.with(CommentsActivity.this).load(model.getProfileUrl())
                                .placeholder(R.drawable.user12)
                                .into(binding.posterProfile);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }).start();

        mRef = FirebaseDatabase.getInstance().getReference("Comments").child(key);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cModel.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                 commentsModel model = dataSnapshot.getValue(commentsModel.class);
                 cModel.add(model);
             }
             adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}