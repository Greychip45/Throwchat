package com.example.friendsbook.Adapters;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.friendsbook.Models.Users;
import com.example.friendsbook.R;
import com.example.friendsbook.chatActivity;
import com.google.android.gms.common.api.Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {


    ArrayList<Users> list;
    Context context;
    private FirebaseUser fuser;
    private DatabaseReference mRef;





    public UsersAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_advanced_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        Users users = list.get(position);
        Picasso.get().load(users.getProfileUrl())
                .placeholder(R.drawable.user12)
                .into(holder.image);
        holder.userName.setText(users.getName());
        holder.lastMessage.setText(users.getBio());

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                View dialogView = LayoutInflater.from(view.getRootView().getContext()).inflate(R.layout.popup_dialog,null);
                ImageView bigProfile = dialogView.findViewById(R.id.big_profile);


                Glide.with(context)
                        .load(users.getProfileUrl())
                        .placeholder(R.drawable.user12)
                        .centerCrop()
                        .into(bigProfile);

                builder.setView(dialogView);
                builder.setCancelable(true);
                builder.show();


                
            }
        });

        if(users.getStatus().equals("online")){
           holder.status.setVisibility(View.VISIBLE);
            holder.offStatus.setVisibility(View.GONE);
        } else {
            holder.offStatus.setVisibility(View.VISIBLE);
            holder.status.setVisibility(View.GONE);
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(context, chatActivity.class);
               intent.putExtra("userId", users.getChatId());
               intent.putExtra("userName",users.getName());
               context.startActivity(intent);

            }
        });
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fuser = FirebaseAuth.getInstance().getCurrentUser();
                mRef = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid()).child("Friends");
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        HashMap<String,Object> map = new HashMap<>();
                        map.put("chatId",users.getChatId());
                        mRef.push().updateChildren(map);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
       private ImageView image,status,offStatus;
       private TextView userName,lastMessage,lastConnected;
       private Button addButton;
       public ViewHolder(@NonNull View itemView) {
           super((itemView));

           addButton = itemView.findViewById(R.id.btn_add);
           status = itemView.findViewById(R.id.status);
           offStatus = itemView.findViewById(R.id.offStatus);
           image = itemView.findViewById(R.id.st_icon);
           userName = itemView.findViewById(R.id.userName);

           lastMessage = itemView.findViewById(R.id.lastMessage);
           lastConnected = itemView.findViewById(R.id.lastConnected);
       }

   }
}