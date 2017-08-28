package com.siukit.chatapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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
import java.util.List;
import java.util.Locale;

//import android.location.LocationListener;

public class ProfileActivity extends AppCompatActivity implements LocationListener {

    ListView userList;

    private static String username;
    private static String email;

    LocationManager locationManager;
    Location getLastLocation;

    private static Double lat;
    private static Double lon;
    static String cityName;

    private Geocoder gcd;
    private static List<Address> addresses;

    private static Response.Listener<String> resListener;

    String data;

    List<String> r;
    private static String jsonString;

    ArrayAdapter<String> adapter;

    private static String test;

    private static StringBuilder jsonResult;

    //decalre user arraylist for different purposes
    private static ArrayList<User> userArrayList;
    private static ArrayList<User> tenMileArrayList;
    private static ArrayList<User> thirtyMileArrayList;
    private static ArrayList<User> ohMileArrayList;
    private static ArrayList<User> thMileArrayList;
    public static ArrayList<User> shakeArrayList;
    private static ArrayList<User> someone2ArrayList;

    String this_user;

    private static User someone;
    private static User someone2;

    private static Context context;

    static int pos;

    //this flag to indicate whether your async task completed or not
    private boolean isBusy = false;
    private boolean stop = false;
    private Handler handler = new Handler();

    private static UsersAdapter allUsersAdaptor;
    private static UsersAdapter tenMileAdaptor;
    private static UsersAdapter thirtyMileAdaptor;
    private static UsersAdapter ohMileAdaptor;
    private static UsersAdapter thMileAdaptor;

    private static Vibrator v;

    private static Boolean displayAllUsers;

    private static final int PERMISSION_REQUEST_CODE = 1;

    private static String invite_image;

    //for handling JSOn data
    private static JSONObject jsonResponse;
    private static JSONArray jsonMainNode;
    private static JSONObject jsonChildNode;

    //check if the same user try to invite you to chat again
    private static String ifInviteAgain = "test";
    private static String ifAcceptAgain = "test";


    public boolean onCreateOptionsMenu(Menu menu) {

        //use custom action bar with spinner on, for selecting users distance and other features
        getMenuInflater().inflate(R.menu.my_menu, menu);
        MenuItem my_menu = menu.findItem(R.id.my_menu);
        Spinner spinnerView = (Spinner) MenuItemCompat.getActionView(my_menu);
        //use custom menu adapter
        ArrayAdapter<CharSequence> menu_adapter = ArrayAdapter.createFromResource(this,
                R.array.distance_array, android.R.layout.simple_spinner_item);
        menu_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        //different distance options for user to select on the menu
        if (spinnerView instanceof Spinner) {
            final Spinner spinner = (Spinner) spinnerView;

            spinner.setAdapter(menu_adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String selectedItem = parent.getItemAtPosition(position).toString();

                    //Only display users within 10 miles
                    if (selectedItem.equals("Within 10 miles")) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Within 10 miles", Toast.LENGTH_LONG);
                        toast.show();
                        userList.setAdapter(tenMileAdaptor);
                        invitationHandle(tenMileArrayList);
                    }

                    //Only display users within 30 miles
                    if (selectedItem.equals("Within 30 miles")) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Within 30 miles", Toast.LENGTH_LONG);
                        toast.show();
                        userList.setAdapter(thirtyMileAdaptor);
                        invitationHandle(thirtyMileArrayList);
                    }

                    //Only display users within 100 miles
                    if (selectedItem.equals("Within 100 miles")) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Within 100 miles", Toast.LENGTH_LONG);
                        toast.show();
                        userList.setAdapter(ohMileAdaptor);
                        invitationHandle(ohMileArrayList);
                    }

                    //Only display users within 300 miles
                    if (selectedItem.equals("Within 300 miles")) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Within 300 miles", Toast.LENGTH_LONG);
                        toast.show();
                        userList.setAdapter(thMileAdaptor);
                        invitationHandle(thMileArrayList);
                    }

                    //Display all the users
                    if (selectedItem.equals("Display all users")) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Display all users", Toast.LENGTH_LONG);
                        toast.show();
                        userList = (ListView) findViewById(R.id.lvUsers);
                        userList.setAdapter(allUsersAdaptor);
                        invitationHandle(userArrayList);
                    }

                    //Go to MapActivity
                    if (selectedItem.equals("Find users on map")) {
                        Intent mapIntent = new Intent(ProfileActivity.this, MapActivity.class);
                        mapIntent.putExtra("username", username);
                        mapIntent.putExtra("lon", lon);
                        mapIntent.putExtra("lat", lat);
                        ProfileActivity.this.startActivity(mapIntent);
                    }

                    //Go to shakeActivity
                    if (selectedItem.equals("Shake")) {
                        Intent shakeIntent = new Intent(ProfileActivity.this, ShakeActivity.class);
                        shakeIntent.putExtra("username", username);
                        ProfileActivity.this.startActivity(shakeIntent);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });

        }

        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        shakeArrayList = new ArrayList<User>();

        //if it is false then the asyntask will display all user on list the first time user open the app
        displayAllUsers = true;

        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        startHandler();

        context = this;

        //get the user info from LoginAcitivity
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        this_user = intent.getStringExtra("username");
        email = intent.getStringExtra("email");

        //decalre Location Manager for tracking user GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //method for requesting permission and start tracking user locaiton
        requestRunTimePermissionAndStartTracking();

    }


    //fetch user data from the database
    class GetUsersJSON extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            jsonResult = new StringBuilder();
            try {
                //connect to getUsers php file
                URL url = new URL("http://194.81.104.22/~14412104/getUsers.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(conn.getInputStream());
//                    String data = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                while ((jsonString = reader.readLine()) != null) {
                    jsonResult.append(jsonString);
                }
//                System.out.println(jsonString);
                reader.close();
                in.close();
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            test = jsonResult.toString();
//            System.out.println("TEST:" + test);

            return null;
        }

        protected void onPostExecute(String result) {

            showUserList();
        }

    }

    //show users on list view
    protected void showUserList() {

        //each arraylist represent a group of users that needed to be display (depends on the chosen distance option on menu)
        userArrayList = new ArrayList<User>();
        tenMileArrayList = new ArrayList<User>();
        thirtyMileArrayList = new ArrayList<User>();
        ohMileArrayList = new ArrayList<User>();
        thMileArrayList = new ArrayList<User>();
        someone2ArrayList = new ArrayList<User>();

        try {
            jsonResponse = new JSONObject(jsonResult.toString());
            jsonMainNode = jsonResponse.optJSONArray("users");

//            System.out.println("Response from JSON:     " + jsonResult.toString());

            //someone2ArrayList is for checking if the user who invited you to chat has a profile picture
            for (int i = 0; i < jsonMainNode.length(); i++) {
                someone2 = new User();
                jsonChildNode = jsonMainNode.getJSONObject(i);
                someone2.setUsername(jsonChildNode.optString("username"));
                someone2.setLocation(jsonChildNode.optString("cityname"));
                someone2.setInviteFrom(jsonChildNode.optString("invite_from_user"));
                someone2.setInviteAcpt(jsonChildNode.optString("accept_from_user"));
                someone2.setLon(jsonChildNode.optDouble("lon"));
                someone2.setLat(jsonChildNode.optDouble("lat"));
                someone2.setIsShake(jsonChildNode.optString("shake"));
                someone2.setIsShake(jsonChildNode.optString("shake"));
                someone2.setImage(jsonChildNode.optString("image"));
                someone2ArrayList.add(someone2);
            }

            for (int i = 0; i < jsonMainNode.length(); i++) {
                someone = new User();

                jsonChildNode = jsonMainNode.getJSONObject(i);

                someone.setUsername(jsonChildNode.optString("username"));
                someone.setLocation(jsonChildNode.optString("cityname"));
                someone.setInviteFrom(jsonChildNode.optString("invite_from_user"));
                someone.setInviteAcpt(jsonChildNode.optString("accept_from_user"));
                someone.setLon(jsonChildNode.optDouble("lon"));
                someone.setLat(jsonChildNode.optDouble("lat"));
                someone.setIsShake(jsonChildNode.optString("shake"));
                someone.setIsShake(jsonChildNode.optString("shake"));
                someone.setImage(jsonChildNode.optString("image"));


                //To NOT show THIS user on the list
                if (!someone.getUsername().equals(username) && !someone.getLocation().equals("null")) {
                    userArrayList.add(someone);
                }

                if (getLastLocation != null) {
                    Location myLocation = new Location("My location");
                    myLocation.setLatitude(lat);
                    myLocation.setLongitude(lon);

                    Location location = new Location("Someone's location");
                    location.setLatitude(someone.getLat());
                    location.setLongitude(someone.getLon());


                    if (!someone.getUsername().equals(username) && (myLocation.distanceTo(location) <= 16093)) {
                        tenMileArrayList.add(someone);
                    }

                    if (!someone.getUsername().equals(username) && (myLocation.distanceTo(location) <= 48280)) {
                        thirtyMileArrayList.add(someone);
                    }

                    if (!someone.getUsername().equals(username) && (myLocation.distanceTo(location) <= 160934)) {
                        ohMileArrayList.add(someone);
                    }

                    if (!someone.getUsername().equals(username) && (myLocation.distanceTo(location) <= 482803)) {
                        thMileArrayList.add(someone);
                    }

                }

                if (someone.getUsername().equals(username) && !someone.getInviteFrom().equals("null")) {
                    //trigger a dialog to ask if accept chat
                    //getInvite will be the person who invited you
                    //once accept remove that field on database
                    //call accept_invite.php
                    //enter the chat room once accepted the chat

                    for (int f = 0; f < someone2ArrayList.size(); f++) {
                        if (someone2ArrayList.get(f).getUsername().equals(someone.getInviteFrom())) {
                            invite_image = someone2ArrayList.get(f).getImage();
//                                invite_image = someone2ArrayList.get(f).getUsername();
                        }
                    }

                    if (!ifInviteAgain.equals(someone.getInviteFrom())) {
                        v.vibrate(300);
                        Intent intent = new Intent(ProfileActivity.this, OnAcceptInviteActivity.class);
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
                        v.vibrate(300);
                        Intent intent2 = new Intent(ProfileActivity.this, RemoveAcceptActivity.class);
                        intent2.putExtra("this_user", username);
                        intent2.putExtra("accept_from_user", someone.getInviteAcpt());
                        System.out.println("accept_from_user =    " + someone.getInviteAcpt());
                        startActivity(intent2);
                        ifAcceptAgain = someone.getInviteAcpt();
                    }


                }

                if (!someone.getUsername().equals(username) && someone.getIsShake().equals("yes")) {
                    shakeArrayList.add(someone);
                }

            }
        } catch (JSONException e) {
//            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
//                    Toast.LENGTH_SHORT).show();
//            System.out.println("ERROR:     " + e);
        }

        allUsersAdaptor = new UsersAdapter(this, R.layout.list_items, userArrayList);
        tenMileAdaptor = new UsersAdapter(this, R.layout.list_items, tenMileArrayList);
        thirtyMileAdaptor = new UsersAdapter(this, R.layout.list_items, thirtyMileArrayList);
        ohMileAdaptor = new UsersAdapter(this, R.layout.list_items, ohMileArrayList);
        thMileAdaptor = new UsersAdapter(this, R.layout.list_items, thMileArrayList);

        userList = (ListView) findViewById(R.id.lvUsers);

        //set it to true so that it wont run again
        if (displayAllUsers) {
            userList.setAdapter(allUsersAdaptor);
            invitationHandle(userArrayList);
            displayAllUsers = false;
        }
//        userList.setAdapter(allUsersAdaptor);
//        invitationHandle(userArrayList);

    }


    //run AysncTask every three second (after previous one has finished)
    public void startHandler() {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!isBusy) callAysncTask();

                if (!stop) startHandler();
            }
        }, 3000);
    }

    private void callAysncTask() {
        new ProfileActivity.GetUsersJSON().execute();
    }


    //for handling invitation
    private void invitationHandle(final ArrayList<User> userArray) {

        //When the user click on a user profile, a dialog will be display and ask if want to invite that user to chat
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                pos = position;
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                resListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                    }
                                };

                                InviteRequest inviteReq = new InviteRequest(username, userArray.get(pos).getUsername(), resListener);
//                                System.out.println("TEST:   userArray.get(pos).getUsername() =   " + userArray.get(pos).getUsername());
                                RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
                                queue.add(inviteReq);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Invite " + userArray.get(position).getUsername() + " to chat?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });
    }

    // check whether we are having location permission for marshmellow
    public static boolean checkPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, android.Manifest.permission
                .ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;

        }
    }

    // used to request for location permission.
    public static void requestPermission(Activity activity, int code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission
                .ACCESS_FINE_LOCATION)) {
            Toast.makeText(activity, "GPS permission allows us to access location data. Please allow " +
                    "in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission
                    .ACCESS_FINE_LOCATION, android.Manifest.permission
                    .ACCESS_COARSE_LOCATION}, code);
        }
    }

    //track location if able to grant permission
    private void requestRunTimePermissionAndStartTracking() {
        if (Build.VERSION.SDK_INT >= 23) {

//            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));

            // Marshmallow+
            if (!checkPermission(getBaseContext())) {
                requestPermission(ProfileActivity.this, PERMISSION_REQUEST_CODE);
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                // get location here
                getLastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (getLastLocation != null) {
                    lat = getLastLocation.getLatitude();
                    lon = getLastLocation.getLongitude();

                    gcd = new Geocoder(this, Locale.getDefault());
                    addresses = null;

                    try {
                        addresses = gcd.getFromLocation(getLastLocation.getLatitude(),
                                getLastLocation.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    cityName = addresses.get(0).getLocality();


                    resListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                        }
                    };

                    GPSRequest gpsReq = new GPSRequest(username, lon.toString(), lat.toString(), cityName, resListener);
                    RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
                    queue.add(gpsReq);
                }


            }
        } else {
            // get location here

        }
    }

//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Util.showToast(getBaseContext(), getBaseContext().getString(R.string
//                            .loc_permission_granted));
//                    Util.startActivityAndLocationTracking(getApplicationContext());
//                } else {
//                    Util.showToast(getBaseContext(), getBaseContext().getString(R.string
//                            .loc_permission_denied));
//                }
//                break;
//            default:
//                break;
//        }
//    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


}
