package com.siukit.chatapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

public class OnAcceptInviteActivity extends AppCompatActivity {

    private static String icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_accept_invite);

        Intent intent = getIntent();
        final String invite_from_user = intent.getStringExtra("invite_from_user");
        final String this_user = intent.getStringExtra("this_user");
        icon = intent.getStringExtra("icon");

        System.out.println("icon:           " + icon);

        final Button bAccept = (Button) findViewById(R.id.bAccept);
        final Button bReject = (Button) findViewById(R.id.bReject);
        final TextView tvInvite = (TextView) findViewById(R.id.tvInvite);
        final ImageView ivUserIcon = (ImageView) findViewById(R.id.ivUserIcon);

        if (icon != null) {
            byte[] decodedString = Base64.decode(icon, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Bitmap iconBitmap = decodedByte;
            ivUserIcon.setImageBitmap(iconBitmap);
        }

        tvInvite.setText(invite_from_user + " has invited you to chat.");


        final Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                return;
            }
        };

        //when user accept the invitation
        bAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                System.out.println("THIS FUCKIN BUTTON IS BEING CLICKED");
//                Toast.makeText(OnAcceptInviteActivity.this, "You clicked the button", Toast.LENGTH_LONG).show();


                AcceptInviteRequest sendReq = new AcceptInviteRequest(this_user, invite_from_user, listener);

                RequestQueue queue = Volley.newRequestQueue(OnAcceptInviteActivity.this);
                queue.add(sendReq);

                Intent chatIntent = new Intent(OnAcceptInviteActivity.this, ChatActivity.class);
                chatIntent.putExtra("this_user", this_user);
                chatIntent.putExtra("other_user", invite_from_user);
                OnAcceptInviteActivity.this.startActivities(new Intent[]{chatIntent});
                finish();


            }
        });

        //when user reject the invitation
        bReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AcceptInviteRequest sendReq2 = new AcceptInviteRequest(this_user, listener);
                RequestQueue queue = Volley.newRequestQueue(OnAcceptInviteActivity.this);
                queue.add(sendReq2);
                finish();
            }
        });


    }
}
