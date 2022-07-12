package com.example.friendsbook.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendsbook.Models.Users;
import com.example.friendsbook.Models.commentsModel;
import com.example.friendsbook.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class commentsAdapter extends RecyclerView.Adapter<commentsAdapter.CommentsHolder> {

    ArrayList<commentsModel> cModel;
    Context context;
    private DatabaseReference mRef;

    public commentsAdapter(ArrayList<commentsModel> cModel, Context context) {
        this.cModel = cModel;
        this.context = context;
    }

    @NonNull
    @Override
    public commentsAdapter.CommentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.comment_view,parent,false);
        return new CommentsHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull commentsAdapter.CommentsHolder holder, int position) {


        commentsModel model = cModel.get(position);
        holder.comment.setText(model.getComment());
        holder.time.setText(model.getTime());

        mRef = FirebaseDatabase.getInstance().getReference("users").child(model.getUserId());
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users model = snapshot.getValue(Users.class);
                holder.userName.setText(model.getName());
                Glide.with(context).load(model.getProfileUrl())
                        .placeholder(R.drawable.user12)
                        .into(holder.userProfile);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return cModel.size();
    }
    public class CommentsHolder extends RecyclerView.ViewHolder{

        private TextView userName,time,comment;
        private ImageView userProfile;

        public CommentsHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.tv_name);
            time = itemView.findViewById(R.id.tv_time);
            comment = itemView.findViewById(R.id.tv_comment);
            userProfile = itemView.findViewById(R.id.user_profile);

        }
    }

}
