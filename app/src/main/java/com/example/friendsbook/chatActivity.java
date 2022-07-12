package com.example.friendsbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.friendsbook.Adapters.ChatAdapter;


import com.example.friendsbook.Fragments.APIService;
import com.example.friendsbook.Models.MessageModel;
import com.example.friendsbook.Models.Users;
import com.example.friendsbook.Notifications.Client;
import com.example.friendsbook.Notifications.Data;
import com.example.friendsbook.Notifications.MyResponse;
import com.example.friendsbook.Notifications.Sender;
import com.example.friendsbook.Notifications.Token;
import com.example.friendsbook.databinding.ActivityChatBinding;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

public class chatActivity extends AppCompatActivity {



    FirebaseUser fuser;
    DatabaseReference reference;


    TextInputLayout chatTextLayout;
    TextView chatUserName,user_status,progress;
    ImageView viewBack,profilePic,textImage,cancelBtn;
    EditText inputText;
    FloatingActionButton btnSend;

    CardView imageCardView;
    ChatAdapter chatAdapter;
    ArrayList<MessageModel> mModel;
    ProgressBar progressBar;

    RecyclerView recyclerView;
    private Uri imgUrl;
    private StorageReference mStorage;
    private static  String dbKey;



    ValueEventListener seenListener;
    String receiverId;

    APIService apiService;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);








        chatTextLayout = findViewById(R.id.chatTextLayout);


        progressBar = findViewById(R.id.progressBar);
        progress  = findViewById(R.id.txt_progress);

        user_status = findViewById(R.id.user_status);
        imageCardView = findViewById(R.id.imageCardView);
        cancelBtn = findViewById(R.id.btn_cancel);
        textImage = findViewById(R.id.selectedLoadImage);
        recyclerView = findViewById(R.id.chatRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        btnSend = findViewById(R.id.btnSend);
        inputText = findViewById(R.id.inputText);
        profilePic = findViewById(R.id.profilePic);




        receiverId = getIntent().getStringExtra("userId");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        chatUserName = findViewById(R.id.chatUserName);

        String userName = getIntent().getStringExtra("userName");
        chatUserName.setText(userName);


        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);



        cancelBtn.setVisibility(View.GONE);
        setStatus();

        chatTextLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(chatActivity.this)
                        .galleryOnly()
                        .crop()
                        .start();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {




                notify = true;
                String msg = inputText.getText().toString();



                if (!msg.equals("")) {
                    sendMessage(fuser.getUid(), receiverId, msg);

                    if(imgUrl != null){

                        sendImage();
                        imgUrl = null;
                    }

                }
                typingStatus();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textImage.setVisibility(View.GONE);
                imgUrl = null;
                cancelBtn.setVisibility(View.GONE);
            }
        });


        inputText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        fuser = FirebaseAuth.getInstance().getCurrentUser();
                        String userId = fuser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
                        HashMap<String,Object>hashMap = new HashMap<>();
                        hashMap.put("status","typing...");
                        reference.updateChildren(hashMap);
                    }
                }).start();




            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });



        reference = FirebaseDatabase.getInstance().getReference("users").child(receiverId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readMessages(fuser.getUid(),receiverId);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        seenMessage(receiverId);




        viewBack = findViewById(R.id.viewBack);
        viewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToNextActivity();
            }
        });



    }


    private void sendUserToNextActivity() {
        Intent intent = new Intent(chatActivity.this, HomeActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    private void sendMessage(String sender, String receiver, String message) {
        inputText.setText("");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.keepSynced(true);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen",false);
        hashMap.put("imgUrl","");



        dbKey = reference.push().getKey();
        reference.child(dbKey).setValue(hashMap);

        final  String msg = message;


        reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if(notify){
                    sendNotification(receiver,users.getName(),msg);
                }

                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(String receiver, String name, String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Token token = snapshot1.getValue(Token.class);
                    Data data = new Data(fuser.getUid(),R.mipmap.ic_launcher,name+": "+msg,"New Message",
                            receiver,name);
                    Sender sender = new Sender(data,token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200 ){
                                        if(response.body().success != 1){
                                            Toast.makeText(chatActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void readMessages(String myId, String userId) {

        mModel = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mModel.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessageModel model = snapshot1.getValue(MessageModel.class);
                    if (model.getReceiver().equals(myId) && model.getSender().equals(userId) ||
                        model.getReceiver().equals(userId) && model.getSender().equals(myId)) {
                            mModel.add(model);
                        }

                    chatAdapter = new ChatAdapter(mModel, chatActivity.this );
                    recyclerView.setAdapter(chatAdapter);
                    }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void status(String status){


        fuser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = fuser.getUid();

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

        reference.removeEventListener(seenListener);

        status("offline");
    }
    private void setStatus(){

        String userId = getIntent().getStringExtra("userId");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                    Users users = snapshot.getValue(Users.class);
                    String status = users.getStatus();

                Glide.with(chatActivity.this)
                        .load(users.getProfileUrl())
                        .placeholder(R.drawable.user12)
                        .centerCrop()
                        .into(profilePic);

                if(!status.equalsIgnoreCase("offline")){
                    user_status.setVisibility(View.VISIBLE);
                    user_status.setText(status);
                } else{
                    user_status.setVisibility(View.GONE);
                }







            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void typingStatus(){
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        String userId = fuser.getUid();

        reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        HashMap<String,Object>hashMap = new HashMap<>();
        hashMap.put("status","online");
        reference.updateChildren(hashMap);
    }

    private void seenMessage(final String friendId){

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MessageModel model = dataSnapshot.getValue(MessageModel.class);
                    if(model.getReceiver().equals(fuser.getUid()) && model.getSender().equals(friendId)){
                        HashMap<String ,Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  String  getFileExtention(Uri url){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(url));

    }

    private void sendImage(){
        mStorage = FirebaseStorage.getInstance().getReference("chatImages");
        StorageReference fileReference = mStorage.child(System.currentTimeMillis()
        +"."+getFileExtention(imgUrl));
        fileReference.putFile(imgUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {



                fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        progress.setVisibility(View.GONE);
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.GONE);
                        imageCardView.setVisibility(View.GONE);
                        String url = task.getResult().toString();
                        reference  = FirebaseDatabase.getInstance().getReference("Chats");
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("imgUrl",url);
                        reference.child(dbKey).updateChildren(hashMap);


                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                progress.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                double imgProgress = (100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                progressBar.setProgress((int) imgProgress);
                progress.setText((int) imgProgress+"%");

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imgUrl = data.getData();
        if(imgUrl != null){
            cancelBtn.setVisibility(View.VISIBLE);
            imageCardView.setVisibility(View.VISIBLE);
            Picasso.get().load(imgUrl).into(textImage);
        }

    }
}