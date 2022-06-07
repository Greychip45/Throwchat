package com.example.friendsbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.friendsbook.Adapters.devMessageAdapter;
import com.example.friendsbook.Models.devMessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DeveloperMessageActivity extends AppCompatActivity {

    EditText devMessageInput;
    DatabaseReference reference;
    Button devSendBtn;
    RecyclerView devRecycler;
    FirebaseUser fuser;
    ArrayList<devMessageModel> dModelArray = new ArrayList<>();
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_message);

        devMessageInput = findViewById(R.id.devMessageInput);
        devSendBtn = findViewById(R.id.devMessageSendBtn);
        devRecycler = findViewById(R.id.devMessageRecycler);


        devSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDevMessage();
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        devRecycler.setLayoutManager(layoutManager);

        reference = FirebaseDatabase.getInstance().getReference("DeveloperMessage");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dModelArray.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    devMessageModel dModel = dataSnapshot.getValue(devMessageModel.class);
                    dModelArray.add(dModel);
                }
                devMessageAdapter devAdapter = new devMessageAdapter(getApplicationContext(),dModelArray);
                devRecycler.setAdapter(devAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    private void sendDevMessage(){
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        String message = devMessageInput.getText().toString();
        reference = FirebaseDatabase.getInstance().getReference("DeveloperMessage");
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("message",message);
        reference.push().updateChildren(hashMap);

    }
}