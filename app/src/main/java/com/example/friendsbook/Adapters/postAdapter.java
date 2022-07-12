package com.example.friendsbook.Adapters;

import android.content.Context;
import android.content.Intent;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HeterogeneousExpandableList;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendsbook.CommentsActivity;
import com.example.friendsbook.Models.Users;
import com.example.friendsbook.Models.commentsModel;
import com.example.friendsbook.Models.postModel;
import com.example.friendsbook.PreviewActivity;
import com.example.friendsbook.R;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class postAdapter extends RecyclerView.Adapter<postAdapter.PostHolder> {

    ArrayList<postModel> pModel;
    Context context;
    private DatabaseReference mRef;
    private FirebaseUser fuser;
    private int likes = 0;
    private String postId;
    private String date,time;
    private  ArrayList<commentsModel>  cModel = new ArrayList<>();

    public postAdapter(ArrayList<postModel> pModel, Context context) {
        this.pModel = pModel;
        this.context = context;
    }

    @NonNull
    @Override
    public postAdapter.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.meme_preview,parent,false);
        return new PostHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull postAdapter.PostHolder holder, int position) {


        fuser = FirebaseAuth.getInstance().getCurrentUser();

        holder.etLayout.setVisibility(View.GONE);
        holder.commentRecyler.setVisibility(View.GONE);

        postModel model = pModel.get(position);

        holder.postText.setVisibility(View.VISIBLE);
            holder.postText.setText(model.getPostText());
            holder.postText.setVisibility(View.VISIBLE);

            holder.commentRecyler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final  Intent i = new Intent(context,CommentsActivity.class);
                    i.putExtra("postImage",model.getPostImage());
                    i.putExtra("posterId",model.getPosterId());
                    i.putExtra("key",model.getKey());
                    context.startActivity(i);
                }
            });


        Glide.with(context).load(model.getPostImage()).centerCrop().into(holder.postImage);

        mRef = FirebaseDatabase.getInstance().getReference("Likes");
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                            HashMap<String,Object> map = new HashMap<>();
                            map.put("likes",fuser.getUid());
                            map.put("key",model.getKey());
                            map.put("isLiked",true);
                            mRef.push().updateChildren(map);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int v = (holder.commentRecyler.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;


                        TransitionManager.beginDelayedTransition(holder.constraintLayout,new AutoTransition());
                        holder.commentRecyler.setVisibility(v);
                        holder.etLayout.setVisibility(v);


                        LinearLayoutManager lm = new LinearLayoutManager(context);
                        holder.commentRecyler.setLayoutManager(lm);
                        commentsAdapter adapter = new commentsAdapter(cModel,context);
                        holder.commentRecyler.setAdapter(adapter);

                        mRef = FirebaseDatabase.getInstance().getReference("Comments").child(model.getKey());
                        mRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                cModel.clear();
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    commentsModel model = dataSnapshot.getValue(commentsModel.class);
                                    cModel.add(model);
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }
        }).start();


        mRef = FirebaseDatabase.getInstance().getReference("users").child(model.getPosterId());
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                holder.posterName.setText(users.getName());
                Glide.with(context).load(users.getProfileUrl())
                        .placeholder(R.drawable.user12)
                        .into(holder.posterProfile);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mRef = FirebaseDatabase.getInstance().getReference("Likes");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    postModel model1 = dataSnapshot.getValue(postModel.class);
                    if(model1.isLiked() && model1.getKey().equals(model.getKey())){
                        likes++;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.likes.setText(likes+" like this Image");

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent i = new Intent(context, PreviewActivity.class);
                i.putExtra("imgUrl",model.getPostImage());
                context.startActivity(i);
            }
        });


        holder.etLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy hh:mma");
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma");
                date = dateFormat.format(cal.getTime());
                time = timeFormat.format(cal.getTime());
                mRef = FirebaseDatabase.getInstance().getReference("Comments").child(model.getKey());
                fuser = FirebaseAuth.getInstance().getCurrentUser();
                HashMap<String,Object> map = new HashMap<>();
                map.put("comment",holder.etComment.getText().toString());
                map.put("date",date);
                map.put("time",time);
                map.put("userId",fuser.getUid());
                map.put("postId",postId);
                postId = mRef.push().getKey();
                mRef.child(postId).updateChildren(map);


            }
        });




    }

    @Override
    public int getItemCount() {
        return pModel.size();
    }
    public class PostHolder extends RecyclerView.ViewHolder{

        private ImageButton like,comment,download;
        private ImageView postImage,posterProfile;
        private RecyclerView commentRecyler;
        private EditText etComment;
        private TextView postText,posterName,likes;
        private TextInputLayout etLayout;
        private ConstraintLayout constraintLayout;

        public PostHolder(@NonNull View itemView) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            etLayout = itemView.findViewById(R.id.et_comment_layout);
            commentRecyler = itemView.findViewById(R.id.comment_recycler);
            etComment = itemView.findViewById(R.id.et_comment);
            posterProfile = itemView.findViewById(R.id.poster_profile);
            postText = itemView.findViewById(R.id.tv_post_text);
            posterName = itemView.findViewById(R.id.tv_poster_name);
            likes = itemView.findViewById(R.id.tv_likes);
            like = itemView.findViewById(R.id.imgBtnLike);
            comment = itemView.findViewById(R.id.imgBtnComment);
            download = itemView.findViewById(R.id.imgBtnDownload);
            postImage = itemView.findViewById(R.id.postImage);

        }
    }


}
