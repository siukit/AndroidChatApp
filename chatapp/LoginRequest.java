package com.siukit.chatapp;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by siukit on 23/02/2017.
 */

public class LoginRequest extends StringRequest {

    private static final String LI_URL = "http://194.81.104.22/~14412104/login.php";
    private Map<String, String> params;

    public LoginRequest(String username, String password, Response.Listener<String> listener){
        super(Request.Method.POST, LI_URL, listener, null);
        params = new HashMap<>();
        //params.put("Content-Type", "application/json; charset=utf-8");
        params.put("username", username);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        //params.put("Content-Type", "application/json; charset=utf-8");
        return params;
    }
}
