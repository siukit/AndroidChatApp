package com.siukit.chatapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.HashMap;

import static com.siukit.chatapp.R.id.map;


public class MapActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static String username;
    private static Double lon;
    private static Double lat;

    private static StringBuilder gpsJsons;

    private static ArrayList<User> gpsArrayList;
    private static ArrayList<User> userArrayList;

    private static String jsonString;

    private SupportMapFragment fm;

    private static ArrayList<Marker> markers;

    private static HashMap<Marker, Integer> mHashMap;

    private static User someone, someone2;

    private static Response.Listener<String> resListener;

    Context context;

    static String other_user;

    private static String invite_image;

    private static String ifInviteAgain = "test";

    private static String ifAcceptAgain = "test";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        context = this;

        // Getting reference to the SupportMapFragment of activity_main.xml
        fm = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(map);

        mHashMap = new HashMap<Marker, Integer>();

        //get the username and his current latitude and longitude
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        lon = intent.getDoubleExtra("lon", 00.00);
        lat = intent.getDoubleExtra("lat", 00.00);

        gpsArrayList = new ArrayList<User>();

        //perform the network tasks for fetching GPS data of users
        new GetGPSJson().execute();

    }

    //when the marker on map being clicked
    @Override
    public boolean onMarkerClick(Marker marker) {

        //get users data from an araylist
        //display username on marker
        for (int i = 0; i < gpsArrayList.size(); i++) {
            if (marker.getTitle().equals(gpsArrayList.get(i).getUsername())) {
                Intent intent = new Intent(MapActivity.this, ChatActivity.class);
                intent.putExtra("this_user", username);
                intent.putExtra("other_user", marker.getTitle());
                MapActivity.this.startActivity(intent);
            }
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap myMap) {
        //check if neccesary permission are granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
//            myMap.setMyLocationEnabled(true);
            markers = new ArrayList<Marker>();

            //
            for (int i = 0; i < gpsArrayList.size(); i++) {

                Marker marker = myMap.addMarker(new MarkerOptions()
                        .position(new LatLng(gpsArrayList.get(i).getLat(), gpsArrayList
                                .get(i).getLon()))
                        .title(gpsArrayList.get(i).getUsername()));
                marker.showInfoWindow();
                //for zooming to the markers later on
                markers.add(marker);
                //for use of the marker listener
                mHashMap.put(marker, i);
                System.out.println("MARKERS TEST:    " + marker.getTitle() + "    " + i);

            }

            myMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lon))
                    .title("You are here"))
                    .showInfoWindow();
            System.out.println("MY POSITION IS:    " + lon + "      " + lat);
        } else {
            // Show rationale and request permission.
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 150; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        myMap.animateCamera(cu);

        myMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                for (int i = 0; i < gpsArrayList.size(); i++) {
                    final int index = i;
                    other_user = marker.getTitle();

                    //when user click on a marker, a dialog appears which ask if he wants to invite the user for chat
                    if (marker.getTitle().equals(gpsArrayList.get(i).getUsername())) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Invite " + gpsArrayList.get(i).getUsername() + " to chat?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                resListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                    }
                                };

                                InviteRequest inviteReq = new InviteRequest(username, gpsArrayList.get(index).getUsername(), resListener);
                                System.out.println("TEST:   userArrayList.get(pos).getUsername() =   " + gpsArrayList.get(index).getUsername());
                                RequestQueue queue = Volley.newRequestQueue(MapActivity.this);
                                queue.add(inviteReq);
                            }
                        }).setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        final AlertDialog dialog = builder.create();
                        LayoutInflater inflater = getLayoutInflater();

                        //set custom layout for the dialog
                        View dialogLayout = inflater.inflate(R.layout.my_dialog_layout, null);
                        ImageView image = (ImageView) dialogLayout.findViewById(R.id.userIcon);

                        //display user profile picture in the dialog
                        if (!gpsArrayList.get(index).getImage().equals("null")) {

                            byte[] decodedString = Base64.decode(gpsArrayList.get(index).getImage(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            image.setImageBitmap(decodedByte);
                        }
                        dialog.setView(dialogLayout);
                        dialog.show();

                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface d) {

                            }
                        });


                    }


                }


            }
        });
    }

    public void getUsersGPS() {
        userArrayList = new ArrayList<User>();

        try {
            JSONObject jsonResponse = new JSONObject(gpsJsons.toString());
            JSONArray jsonMainNode = jsonResponse.optJSONArray("users");

            //someone2 for checking if user has profile image
            for (int i = 0; i < jsonMainNode.length(); i++) {
                someone2 = new User();
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                someone2.setUsername(jsonChildNode.optString("username"));
                someone2.setLocation(jsonChildNode.optString("cityname"));
                someone2.setInviteFrom(jsonChildNode.optString("invite_from_user"));
                someone2.setInviteAcpt(jsonChildNode.optString("accept_from_user"));
                someone2.setLon(jsonChildNode.optDouble("lon"));
                someone2.setLat(jsonChildNode.optDouble("lat"));
                someone2.setImage(jsonChildNode.optString("image"));
                userArrayList.add(someone2);
            }

            //put users(data) on arraylist
            for (int i = 0; i < jsonMainNode.length(); i++) {
                someone = new User();

                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                someone.setUsername(jsonChildNode.optString("username"));
                someone.setLocation(jsonChildNode.optString("cityname"));
                someone.setInviteFrom(jsonChildNode.optString("invite_from_user"));
                someone.setInviteAcpt(jsonChildNode.optString("accept_from_user"));
                someone.setLon(jsonChildNode.optDouble("lon"));
                someone.setLat(jsonChildNode.optDouble("lat"));
                someone.setImage(jsonChildNode.optString("image"));


                //ensure messages only sent to the chosen correspondent
                if (!someone.getUsername().equals(username) && !someone.getLocation().equals("null")) {
                    gpsArrayList.add(someone);
                }

                System.out.println("TEST ARRAYLIST:    " + someone.getUsername());

                if (someone.getUsername().equals(username) && !someone.getInviteFrom().equals("null")) {
                    //trigger a dialog to ask if accept chat
                    //getInvite will be the person who invited you
                    //once accept remove that field on database
                    //call accept_invite.php
                    //enter the chat room once accepted the chat
                    for (int f = 0; f < userArrayList.size(); f++) {
                        if (userArrayList.get(f).getUsername().equals(someone.getInviteFrom())) {
                            invite_image = userArrayList.get(f).getImage();
//                                invite_image = someone2ArrayList.get(f).getUsername();
                        }
                    }

                    if (!ifInviteAgain.equals(someone.getInviteFrom())) {
                        Intent intent = new Intent(MapActivity.this, OnAcceptInviteActivity.class);
                        intent.putExtra("invite_from_user", someone.getInviteFrom());
                        intent.putExtra("this_user", username);
                        intent.putExtra("icon", invite_image);
                        startActivity(intent);
                        ifInviteAgain = someone.getInviteFrom();
                    }
                }

                if (!someone.getInviteAcpt().equals("null") && someone.getUsername().equals(username)) {
                    //your invitation has been accepted
                    //start the chat immediately
                    //getAcpinvite will be the username of who you invited to chat with
                    //remove the field immediately

                    if (!ifAcceptAgain.equals(someone.getInviteFrom())) {
                        Intent intent2 = new Intent(MapActivity.this, RemoveAcceptActivity.class);
                        intent2.putExtra("this_user", username);
                        intent2.putExtra("accept_from_user", someone.getInviteAcpt());
                        System.out.println("accept_from_user =    " + someone.getInviteAcpt());
                        startActivity(intent2);
                        ifAcceptAgain = someone.getInviteAcpt();
                    }

                }
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
            System.out.println("ERROR:     " + e);
        }
        fm.getMapAsync(this);
    }


    //perform network tasks for getting users data
    class GetGPSJson extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            gpsJsons = new StringBuilder();
            try {
                //use httpurl connection to connect to getUser.php on server
                URL url = new URL("http://194.81.104.22/~14412104/getUsers.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                while ((jsonString = reader.readLine()) != null) {
                    gpsJsons.append(jsonString);
                }
                System.out.println("TEST THIS: " + gpsJsons.toString());
                reader.close();
                in.close();
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            getUsersGPS();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

}
