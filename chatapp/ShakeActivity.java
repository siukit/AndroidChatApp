package com.siukit.chatapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import static com.siukit.chatapp.ProfileActivity.shakeArrayList;

public class ShakeActivity extends AppCompatActivity {


    private static Response.Listener<String> shakeResponseListener;
    private static String username;
    private static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        context = this;

        //get the name of current user from ProfileActivity
        Intent intent = getIntent();
        username = intent.getStringExtra("username");


        shakeResponseListener = new Response.Listener<String>() {
            public void onResponse(String response) {
            }
        };

        //declare a shake listener for detecting shake movement
        ShakeListener mShaker = new ShakeListener(this);
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {
                //display message shaking on screen while user is shaking his phone
                Toast toast = Toast.makeText(getApplicationContext(), "SHAKING", Toast.LENGTH_SHORT);
                toast.show();
                //update database shake column to yes
                OnShakeRequest sendReq = new OnShakeRequest("yes", username, shakeResponseListener);
                RequestQueue queue = Volley.newRequestQueue(ShakeActivity.this);
                queue.add(sendReq);

                //check if someone else is also shaking at the same time
                if (!shakeArrayList.isEmpty()) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    Intent chatIntent = new Intent(ShakeActivity.this, ChatActivity.class);
                                    chatIntent.putExtra("this_user", username);
                                    chatIntent.putExtra("other_user", shakeArrayList.get(0).getUsername());
                                    ShakeActivity.this.startActivities(new Intent[]{chatIntent});

                                    break;

                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Found user " + shakeArrayList.get(0).getUsername() + " shaking his/her phone as well!")
                            .setPositiveButton("Let's chat!", dialogClickListener).show();


                    //remove the "yes" on shake field on user table once user stop shaking
                    OnShakeRequest sendNoReq = new OnShakeRequest("no", username, shakeResponseListener);
                    RequestQueue queue2 = Volley.newRequestQueue(ShakeActivity.this);
                    queue2.add(sendNoReq);

                    //clear shake arraylist after users quited shake activity and joined chat room
                    shakeArrayList.clear();
                }
            }

            public void onPause() {
                Toast toast = Toast.makeText(getApplicationContext(), "STOPPED SHAKING", Toast.LENGTH_SHORT);
                toast.show();
                OnShakeRequest sendNoReq = new OnShakeRequest("no", username, shakeResponseListener);
                RequestQueue queue = Volley.newRequestQueue(ShakeActivity.this);
                queue.add(sendNoReq);
            }
        });


        //when return button being pressed, set the shake field on the user table to no
    }

    //if back button pressed, update Shake column to no (in case of bugs where system doesnt update table on its own)
    @Override
    public void onBackPressed() {
        Toast toast = Toast.makeText(getApplicationContext(), "BACK BUTTON PRESSED AND SETTING SHAKE FIELD TO \"no\"", Toast.LENGTH_SHORT);
        toast.show();
        OnShakeRequest sendReq = new OnShakeRequest("no", username, shakeResponseListener);
        RequestQueue queue = Volley.newRequestQueue(ShakeActivity.this);
        queue.add(sendReq);
        finish();
    }
}
