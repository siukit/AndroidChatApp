package com.siukit.chatapp;

/**
 * Created by siukit on 25/02/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

//this class customize an Arayadapter which will be used for displayign users on listview
public class UsersAdapter extends ArrayAdapter<User> {
    private final Context context;
    private final ArrayList<User> data;
    private final int layoutResourceId;

    public UsersAdapter(Context context, int layoutResourceId, ArrayList<User> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            //two textviews and a image view for displaying usernames, location and profile picture
            holder = new ViewHolder();
            holder.textView1 = (TextView) row.findViewById(R.id.tvUsername);
            holder.textView2 = (TextView) row.findViewById(R.id.tvLocation);
            holder.imageView = (ImageView) row.findViewById(R.id.ivIc);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        User person = data.get(position);

        holder.textView1.setText(person.getUsername());
        holder.textView2.setText(person.getLocation());


        //if user has  profile picture, then decode the image from database and set it on image view
        if (!person.getImage().equals("null")) {
            byte[] decodedString = Base64.decode(person.getImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Bitmap icon = decodedByte;
            holder.imageView.setImageBitmap(icon);

            //otherwise just use default image
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher);
        }


        return row;
    }

    static class ViewHolder {
        TextView textView1;
        TextView textView2;
        ImageView imageView;
//
//        TextView textView3;
    }
}