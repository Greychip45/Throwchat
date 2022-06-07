package com.example.friendsbook.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.friendsbook.Adapters.UsersAdapter;
import com.example.friendsbook.Models.Users;
import com.example.friendsbook.databinding.FragmentChatsfragmentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class friendsFragment extends Fragment {




  FragmentChatsfragmentBinding binding;
  ArrayList<Users> list = new ArrayList<>();
  FirebaseDatabase database;
    FirebaseUser fuser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatsfragmentBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        database.getReference().keepSynced(true);
        UsersAdapter adapter = new UsersAdapter(list, getContext());
        binding.chatRecyclerViewer.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.chatRecyclerViewer.setLayoutManager(layoutManager);


        database.getReference().child("users").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                fuser = FirebaseAuth.getInstance().getCurrentUser();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);

                    if(!users.getChatId().equals(fuser.getUid())){
                        list.add(users);
                    }
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