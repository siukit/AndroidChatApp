package com.siukit.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);

        final Button bLogin = (Button) findViewById(R.id.bLogin);
        final TextView tvReg = (TextView) findViewById(R.id.tvReg);

        //when the register button is clicked
        tvReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goes to the register page
                Intent regIntent = new Intent(LoginActivity.this, RegActivity.class);
                LoginActivity.this.startActivities(new Intent[]{regIntent});
            }
        });

        //when login button is clicked
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();

                Response.Listener<String> rl = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jr = null;
                        try {
                            jr = new JSONObject(response);
                            boolean success = jr.getBoolean("success");

                            //if login successful, goes to ProfileAcitivty
                            if (success) {
                                String name = jr.getString("username");
                                String email = jr.getString("email");
                                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                intent.putExtra("username", name);
                                intent.putExtra("email", email);
                                LoginActivity.this.startActivity(intent);
                                // if login was unsuccessfull, display failure message
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Login Failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                };
                //send data to LoginRequest which will then send data to the php file on server
                LoginRequest loginRequest = new LoginRequest(username, password, rl);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });

    }

}
