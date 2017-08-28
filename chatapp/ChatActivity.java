package com.siukit.chatapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Message> messageArrayList;
    private MessageAdapter msgAdapter;

    static String this_username;
    static String other_username;

    StringBuilder jsonMessages;

    private static String jsonString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //declare button for sending message and the edittext of where the message will be written at
        final Button btSend = (Button) findViewById(R.id.btSend);
        final EditText etMessage = (EditText)findViewById(R.id.etMessage);

        //get the usernames of the two users whose will enter a chat room (from ProfileAcitivity)
        Intent intent = getIntent();
        this_username = intent.getStringExtra("this_user");
        other_username = intent.getStringExtra("other_user");
        Toast toast = Toast.makeText(getApplicationContext(), "You're talking to " + other_username, Toast.LENGTH_LONG);
        toast.show();

        //put the username (the user you're chatting to) on the action bar
        getSupportActionBar().setTitle(other_username);

        //messages will be displayed on a recyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //for handling the networking task
        startHandler();


        //when send message button is being pressed
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //change button color upon it being pressed
                int btColor = getResources().getColor(R.color.LightSeaGreen);
                btSend.setBackgroundColor(btColor);
                new CountDownTimer(150, 50) {
                    @Override
                    public void onTick(long arg0) {
                    }
                    @Override
                    public void onFinish() {
                        btSend.setBackgroundColor(Color.WHITE);
                    }
                }.start();
                String message = etMessage.getText().toString().trim();

                //clear text once the message is sent
                etMessage.getText().clear();

                //response listener for SendMsgRequest
                Response.Listener<String> sendMsgListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {

                        JSONObject jr = null;
                        try {
                            jr = new JSONObject(response);
                            boolean success = jr.getBoolean("success");

                            if(success){
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Successfully sent the message to " + other_username, Toast.LENGTH_LONG);
                                toast.show();

                            }else{
                                Toast toast = Toast.makeText(getApplicationContext(), "Failed to send messages!!", Toast.LENGTH_LONG);
                                toast.show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                //send the two usernames, message to the server which will received by a php file
                SendMsgRequest sendReq = new SendMsgRequest(this_username, message, other_username, sendMsgListener);
                RequestQueue queue = Volley.newRequestQueue(ChatActivity.this);
                queue.add(sendReq);

            }
        });

    }


    //fetch message from the server and display on the recyclerView
    //run this method on AsyncTask (onPostExecute)
    private void fetchMessages(){

        //put messages on a array list with custom Message Object
        messageArrayList = new ArrayList<Message>();
        try {
            //messages were fetched by JSON request
            JSONObject jsonResponse = new JSONObject(jsonMessages.toString());
            JSONArray jsonMainNode = jsonResponse.optJSONArray("messages");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                Message msg = new Message();

                //store all the neccesary components on each message object
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                msg.setUsername(jsonChildNode.optString("username"));
                msg.setMessage(jsonChildNode.optString("message"));
                msg.setTimeSent(jsonChildNode.optString("sent_at"));
                msg.setSendTo(jsonChildNode.optString("send_to"));


                //ensure messages only sent to the chosen correspondent
                if(msg.getSendTo().equals(this_username) && msg.getUsername().equals(other_username)){
                    messageArrayList.add(msg);
                }
                if(msg.getUsername().equals(this_username) && msg.getSendTo().equals(other_username )){
                    messageArrayList.add(msg);
                }
//                System.out.println("TEST3: " + msg.getUsername());
            }
        } catch (JSONException e) {
//            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
//                    Toast.LENGTH_SHORT).show();
//            System.out.println("ERROR:     " + e);
        }

        Intent intent = getIntent();
        this_username = intent.getStringExtra("this_user");
//        System.out.println("TEST4: " + this_username);

        //set the recyclerView to use the custom adapter
        msgAdapter = new MessageAdapter(this, messageArrayList, this_username);
        recyclerView.setAdapter(msgAdapter);
        msgAdapter.notifyDataSetChanged();

    }


    //AsyncTask for connecting to the server
    class GetMsgsJSON extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            jsonMessages = new StringBuilder();
            try {

                URL url = new URL("http://194.81.104.22/~14412104/getMsg.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(conn.getInputStream());
//                    String data = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));


                while ((jsonString = reader.readLine()) != null) {
                    jsonMessages.append(jsonString);
                }
//                System.out.println(jsonString);
                reader.close();
                in.close();
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            test = jsonMessages.toString();
//            System.out.println("TEST2:" + test);

            return null;
        }

        protected void onPostExecute(String result) {

            fetchMessages();

        }

    }

    //this flag to indicate whether asynctaks has finished or not
    private boolean isBusy = false;
    private boolean stop = false;
    private Handler handler = new Handler();


    //this handler will be ran every two seconds (after the previous AysncTask has finished)
    public void startHandler()
    {
        handler.postDelayed(new Runnable()
        {

            @Override
            public void run()
            {
                if(!isBusy) callAysncTask();

                if(!stop) startHandler();
            }
        }, 2000);
    }

    private void callAysncTask()
    {
        new GetMsgsJSON().execute();
    }


}
