package com.siukit.chatapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by siukit on 26/02/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    //user id
    private String username;
    private Context context;

    //Tag for tracking self message
    private int SELF = 700;

    //ArrayList of messages object containing all the messages in the thread
    private ArrayList<Message> messageArrayList;

    //Constructor
    public MessageAdapter(Context context, ArrayList<Message> messages, String username){
        this.username = username;
        this.messageArrayList = messages;
        this.context = context;
    }

    //IN this method we are tracking the self message
    @Override
    public int getItemViewType(int position) {
        //getting message object of current position
        Message message = messageArrayList.get(position);

        //If its owner  id is  equals to the logged in user id
        if (message.getUsername().equals(username)) {
            //Returning self
             return SELF;
        }else{
            return position;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Creating view 
        View itemView;
        //if view type is self 
        if (viewType == SELF) {
            //Inflating the layout self 
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_this, parent, false);
        } else {
            //else inflating the layout others 
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_other, parent, false);
        }
        //returing the view 
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Adding messages to the views 
        Message message = messageArrayList.get(position);
        holder.textViewMessage.setText(message.getMessage());
        holder.textViewTime.setText(message.getTimeSent());
    }


    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    //Initializing views 
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMessage;
        public TextView textViewTime;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewMessage = (TextView) itemView.findViewById(R.id.tvMessage);
            textViewTime = (TextView) itemView.findViewById(R.id.tvTime);
        }
    }



}