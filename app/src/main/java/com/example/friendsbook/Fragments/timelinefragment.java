package com.example.friendsbook.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.friendsbook.Adapters.postAdapter;
import com.example.friendsbook.Models.postModel;
import com.example.friendsbook.R;
import com.example.friendsbook.databinding.FragmentTimelinefragmentBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class timelinefragment extends Fragment {

    private FragmentTimelinefragmentBinding binding;
    private DatabaseReference mRef;
    private FirebaseUser fuser;
    private ArrayList<postModel> pModel = new ArrayList();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTimelinefragmentBinding.inflate(inflater,container,false);

        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        binding.timelineRecycler.setLayoutManager(lm);
        postAdapter adapter = new postAdapter(pModel,getContext());
        binding.timelineRecycler.setAdapter(adapter);
        mRef = FirebaseDatabase.getInstance().getReference("Posts");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pModel.clear();
                for(DataSnapshot datasnapshot : snapshot.getChildren() ){
                    postModel model = datasnapshot.getValue(postModel.class);
                    pModel.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }
}