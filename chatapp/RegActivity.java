package com.siukit.chatapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class RegActivity extends AppCompatActivity {

    private static Context context;

//    private ImageView imageView;

    private static Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private static ImageView ivIcon;
    private static Path imagepath;
    private static Uri selectedImage;
    private static String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);


        context = this;

        final EditText etEmail = (EditText) findViewById(R.id.etUsername);
        final EditText etName = (EditText) findViewById(R.id.etName);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);

        final Button bPicture = (Button) findViewById(R.id.bPicture);
        final Button bRegister = (Button) findViewById(R.id.bRegister);

        ivIcon = (ImageView) findViewById(R.id.ivIc);

        //when register button being clicked
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the texts from all fields and store them in variables
                final String username = etName.getText().toString();
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();
                final String image = getImageString(bitmap);

                //response listener for register
                Response.Listener<String> resListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean success = jsonResponse.getBoolean("success");

                            //if the register was successful, goes to LoginActivity
                            if (success) {
                                Intent intent = new Intent(RegActivity.this, LoginActivity.class);
                                RegActivity.this.startActivities(new Intent[]{intent});
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegActivity.this);
                                builder.setMessage("Fail to register, please try again!")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                //send all the register data to server php file
                RegRequest regReq = new RegRequest(username, email, password, image, resListener);
                RequestQueue queue = Volley.newRequestQueue(RegActivity.this);
                queue.add(regReq);


            }
        });


        //when "CHOOSE A PROFILE PICTURE" button is clicked
        bPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //declare the dialog click listener
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                //get to the camera
                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(takePicture, 0);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                //get to the gallery
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto, 1);

                                break;
                        }
                    }
                };

                //build the dialog
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                builder.setMessage("Take a picture from:").setPositiveButton("Camera", dialogClickListener)
                        .setNegativeButton("Gallery", dialogClickListener).show();

            }
        });


    }

    //respond to after user has picked a picture
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    //get the image data from imageReturnedIntent
                    selectedImage = imageReturnedIntent.getData();
//                    String result = selectedImage.getPath();

                    try {
                        //set the image to the image view so user can view the chosen picture before registering
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        ivIcon.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    //get the image data from imageReturnedIntent
                    selectedImage = imageReturnedIntent.getData();
//                    String result = selectedImage.getPath();

                    try {
                        //set the image to the image view so user can view the chosen picture before registering
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        ivIcon.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    //encod the bitmap image to base64 string for sending it to database later
    public String getImageString(Bitmap bmp) {
        //declare byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //crop the image to 300x300 resolution
        Bitmap scaled = Bitmap.createScaledBitmap(bmp, 300, 300, true);
        //compress as JPEG format
        scaled.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        scaled.recycle();
        byte[] imageBytes = baos.toByteArray();
        String encodedImg = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImg;
    }


}
