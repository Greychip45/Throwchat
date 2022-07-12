package com.example.friendsbook.Adapters;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendsbook.Models.MessageModel;
import com.example.friendsbook.PreviewActivity;
import com.example.friendsbook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    ArrayList<MessageModel> messageModels;
    Context context;
    FirebaseUser fuser;


    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;
    boolean CONTAINS_IMAGE = false;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String chatBubbleNum = sp.getString("bubbles", "false");

        View view;
        if (viewType == SENDER_VIEW_TYPE) {
            view = LayoutInflater.from(context).inflate(R.layout.card_chat_item_sender, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.card_chat_item_receiver, parent, false);
        }
        return new ViewHolder(view);

    }


    @Override
    public int getItemViewType(int position) {

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        if (messageModels.get(position).getSender().equals(fuser.getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }


    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {



        MessageModel messageModel = messageModels.get(position);
        holder.txt_msg.setText(messageModel.getMessage());
        if(!messageModel.getImgUrl().equals("")){

            holder.textImage.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(messageModel.getImgUrl())
                    .placeholder(R.drawable.empty_image)
                    .centerCrop()
                    .into(holder.textImage);
        }

        holder.textImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, PreviewActivity.class);
                i.putExtra("imgUrl",messageModel.getImgUrl());
                context.startActivity(i);

            }
        });

        



        if(position == messageModels.size()-1){

                if(messageModel.isIsseen()){
                    holder.txt_seen.setText("Seen");
                }else {
                    holder.txt_seen.setText("Delivered");
                }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }
    }



    @Override
    public int getItemCount() {
        return messageModels.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_seen,txt_msg,txt_time;
        ImageView textImage;

        public ViewHolder(@NonNull View itemView) {
            super((itemView));

            txt_seen = itemView.findViewById(R.id.txt_seen);
            txt_msg = itemView.findViewById(R.id.txt_msg);
            txt_time = itemView.findViewById(R.id.txt_time);

            textImage = itemView.findViewById(R.id.textImage);



        }


    }



}
