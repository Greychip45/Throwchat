package com.example.friendsbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class PreviewActivity extends AppCompatActivity {

    private static int REQUEST_CODE= 100;
    private ImageView bigImagePreview,btnDownload;
    private String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        bigImagePreview = findViewById(R.id.bigImagePreview);
        btnDownload = findViewById(R.id.btn_download);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(PreviewActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    bigImagePreview.buildDrawingCache();
                    Bitmap bMap = bigImagePreview.getDrawingCache();
                    saveImage(bMap);
                } else{
                    askPermission();
                }




            }
        });

        uri = getIntent().getStringExtra("imgUrl");

        loadBigImage(uri);

        ActivityCompat.requestPermissions(PreviewActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(PreviewActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);

    }

    private void askPermission() {
        ActivityCompat.requestPermissions(PreviewActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
            bigImagePreview.buildDrawingCache();
            Bitmap bMap = bigImagePreview.getDrawingCache();
            saveImage(bMap);
        } else {
            Toast.makeText(this, "Please grant permission", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void loadBigImage(String uri){
        Glide.with(getApplicationContext())
                .load(uri)
                .placeholder(R.drawable.empty_image)
                .centerCrop()
                .into(bigImagePreview);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
    private void saveImage(Bitmap bitmap) {
        OutputStream fos;
        try {
           if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
               ContentResolver cR = getContentResolver();
               ContentValues cV = new ContentValues();
               cV.put(MediaStore.MediaColumns.DISPLAY_NAME,"Image_"+".jpg");
               cV.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpg");
               cV.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES + File.separator + "Throwchat");
               Uri imageUri = cR.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cV);
               fos = cR.openOutputStream((Objects.requireNonNull(imageUri)));
               bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
               Objects.requireNonNull(fos);
           }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}