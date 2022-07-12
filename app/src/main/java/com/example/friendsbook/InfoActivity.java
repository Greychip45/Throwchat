package com.example.friendsbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.friendsbook.Models.CreateUsers;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import retrofit2.http.Url;

public class InfoActivity extends AppCompatActivity {
    EditText inputName,inputPhone,inputBio;
    Button btn_next;
    ImageView circleImageView;
    ProgressDialog progressDialog;
    ImageView profileImage;


    FirebaseUser fuser;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseRefence;
    private Uri mImageUri;



    DatabaseReference studentDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        inputPhone=findViewById(R.id.txt_phone);
        inputName=findViewById(R.id.txt_name);
        progressDialog= new ProgressDialog(this);
        circleImageView = findViewById(R.id.circleImageView);
        profileImage = findViewById(R.id.circleImageView);
        inputBio = findViewById(R.id.txt_bio);


        btn_next = findViewById(R.id.btn_next);
        studentDbRef = FirebaseDatabase.getInstance().getReference().child("users");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        mStorageReference = FirebaseStorage.getInstance().getReference("uploads");



        btn_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(inputName.equals("")){
                    inputName.setError("Can't be empty");
                } else if(inputPhone.equals("")){
                    inputPhone.setError("Can't be empty");
                } else{
                    insertUserDetails();
                    uploadFile();

                }

            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImagePicker.Companion.with(InfoActivity.this)
                        .galleryOnly()
                        .start();

            }
        });


    }
    private void insertUserDetails() {
        progressDialog.setMessage("Please Wait...");


        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String name = inputName.getText().toString();
        String phone = inputPhone.getText().toString();
        String email = getIntent().getStringExtra("inputEmail");
        String bio = inputBio.getText().toString();




        fuser = FirebaseAuth.getInstance().getCurrentUser();
        String chatId = fuser.getUid();
        CreateUsers users = new CreateUsers(name,phone,chatId,email,bio);


        studentDbRef.child(chatId).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(InfoActivity.this,"Success", Toast.LENGTH_SHORT).show();
                    sendUserToNextActivity();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(InfoActivity.this, "Error : "+task.getException(), Toast.LENGTH_SHORT).show();
                }

            }
        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        mImageUri = data.getData();
        if(mImageUri != null){
            Picasso.get().load(mImageUri).into(profileImage);
        } else{
            Picasso.get().load(R.drawable.user12).into(profileImage);
        }

    }

    private  void sendUserToNextActivity(){
        Intent intent = new Intent(InfoActivity.this,HomeActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));

    }

    private  void uploadFile() {
        if (mImageUri != null) {

            StorageReference fileReference = mStorageReference.child(System.currentTimeMillis()
            +"."+getFileExtension(mImageUri));
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(InfoActivity.this, "Upload registered as success", Toast.LENGTH_SHORT).show();
                            mDatabaseRefence = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid());
                            fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    HashMap<String ,Object> hashMap = new HashMap<>();
                                    String url = task.getResult().toString();
                                    hashMap.put("profileUrl", url);
                                    mDatabaseRefence.updateChildren(hashMap);
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(InfoActivity.this, "ERROR: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }



}