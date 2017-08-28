package com.siukit.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

public class RemoveAcceptActivity extends AppCompatActivity {

    private static Response.Listener<String> resListener;

    //after both users are confirmed to start chatting, set the "accept_from_user" column to null
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_accept);

        Intent intent = getIntent();
        final String accept_from_user = intent.getStringExtra("accept_from_user");
        final String this_user = intent.getStringExtra("this_user");

//        System.out.println("accept_from_user 2 =    " + accept_from_user);

        resListener = new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            return;
                        }
                    };

        RemoveAcceptedRequest removeAcceptReq = new RemoveAcceptedRequest(this_user, resListener);
        RequestQueue queue = Volley.newRequestQueue(RemoveAcceptActivity.this);
        queue.add(removeAcceptReq);

        Intent chatIntent = new Intent(RemoveAcceptActivity.this, ChatActivity.class);
        chatIntent.putExtra("this_user", this_user);
        chatIntent.putExtra("other_user", accept_from_user);
        RemoveAcceptActivity.this.startActivities(new Intent[]{chatIntent});
        finish();
    }
}
