package com.example.friendsbook.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.friendsbook.Adapters.UsersAdapter;
import com.example.friendsbook.Models.Users;
import com.example.friendsbook.databinding.FragmentAddFriendBinding;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class addFriendFragment extends Fragment {



    FragmentAddFriendBinding binding;
    ArrayList<Users> list = new ArrayList<com.example.friendsbook.Models.Users>();
    FirebaseDatabase database;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddFriendBinding.inflate(inflater, container, false);

        UsersAdapter adapter = new UsersAdapter(list, getContext());





        return  binding.getRoot();
    }
}