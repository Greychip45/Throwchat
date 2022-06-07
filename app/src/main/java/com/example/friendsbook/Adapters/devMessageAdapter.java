package com.example.friendsbook.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendsbook.Models.devMessageModel;
import com.example.friendsbook.R;

import java.util.ArrayList;

public class devMessageAdapter extends RecyclerView.Adapter<devMessageAdapter.ViewHolder> {
    Context context;
    ArrayList<devMessageModel> dModelArray;

    public devMessageAdapter(Context context, ArrayList<devMessageModel> dModelArray) {
        this.context = context;
        this.dModelArray = dModelArray;
    }

    @NonNull
    @Override
    public devMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_dev_message,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull devMessageAdapter.ViewHolder holder, int position) {

        devMessageModel dModel = dModelArray.get(position);
        holder.devMessage.setText(dModel.getMessage());
        holder.title.setText(dModel.getSender()+" says");
    }

    @Override
    public int getItemCount() {
        return dModelArray.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,devMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.sender);
            devMessage = itemView.findViewById(R.id.devMessage);
        }
    }
}
