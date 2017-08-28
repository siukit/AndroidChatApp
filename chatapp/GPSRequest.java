package com.siukit.chatapp;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by siukit on 23/02/2017.
 */

public class GPSRequest extends StringRequest{

    private static final String gps_URL = "http://194.81.104.22/~14412104/store_gps.php";
    private Map<String, String> params;

    public GPSRequest(String username, String lon, String lat, String city, Response.Listener<String> listener){
        super(Request.Method.POST, gps_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("lon", lon);
        params.put("lat", lat);
        params.put("city", city);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
