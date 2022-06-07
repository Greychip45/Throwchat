package com.example.friendsbook.Adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;


import com.example.friendsbook.Models.Users;
import com.example.friendsbook.R;
import com.example.friendsbook.chatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {


    ArrayList<Users> list;
    Context context;
    FirebaseUser fuser;




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
        holder.lastMessage.setText(users.getPhone());







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


    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
       ImageView image,status,offStatus;
       TextView userName,lastMessage,lastConnected;
       public ViewHolder(@NonNull View itemView) {
           super((itemView));

           status = itemView.findViewById(R.id.status);
           offStatus = itemView.findViewById(R.id.offStatus);
           image = itemView.findViewById(R.id.st_icon);
           userName = itemView.findViewById(R.id.userName);
           lastMessage = itemView.findViewById(R.id.lastMessage);
           lastConnected = itemView.findViewById(R.id.lastConnected);



       }

   }
}