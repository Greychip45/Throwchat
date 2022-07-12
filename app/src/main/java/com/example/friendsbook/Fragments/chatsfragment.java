package com.example.friendsbook.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.friendsbook.Adapters.UsersAdapter;
import com.example.friendsbook.Models.MessageModel;
import com.example.friendsbook.Models.Users;
import com.example.friendsbook.Notifications.Token;
import com.example.friendsbook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Iterator;


public class chatsfragment extends Fragment {




    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;

    private ArrayList<Users> mUsers;
    FirebaseUser fuser;
    DatabaseReference reference;
    private ArrayList<String> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chatsfragment, container, false);

        recyclerView = view.findViewById(R.id.chatRecyclerViewer);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        usersList = new ArrayList<>();

        updateToken(FirebaseInstanceId.getInstance().getToken());
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             usersList.clear();
             for(DataSnapshot snapshot1 : snapshot.getChildren()){
                 MessageModel model = snapshot1.getValue(MessageModel.class);


                 if(!model.getSender().equals(fuser.getUid())) {
                     usersList.add(model.getReceiver());
                 }
                 if(model.getReceiver().equals(fuser.getUid())) {
                     usersList.add(model.getSender());
                 }
             }
             readChats();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return  view;




    }



    private void readChats() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fuser = FirebaseAuth.getInstance().getCurrentUser();
                mUsers.clear();

                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Users users =  snapshot1.getValue(Users.class);

                    for (String id : usersList) {
                        if (users.getChatId().equals(id)) {
                            if (mUsers.size() != 0) {
                                Iterator<Users> itr = mUsers.iterator();
                                while (itr.hasNext()) {
                                    Users users1 = itr.next();
                                    if (users.getChatId().equals(users1.getChatId())) {
                                        itr.remove();
                                        mUsers.add(users);
                                    }
                                }


                            } else {

                                mUsers.add(users);
                            }
                        }
                    }

                }
                usersAdapter = new UsersAdapter(mUsers,getContext());
                recyclerView.setAdapter(usersAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }));
    }
    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }
}