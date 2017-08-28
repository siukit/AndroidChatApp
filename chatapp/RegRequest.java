package com.siukit.chatapp;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by siukit on 22/02/2017.
 */

public class RegRequest extends StringRequest {

    private static final String RE_URL = "http://194.81.104.22/~14412104/register.php";
    private Map<String, String> params;

    public RegRequest(String username, String email, String password, String image, Response.Listener<String> listener){
        super(Method.POST, RE_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("email", email);
        params.put("password", password);
        params.put("image", image);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
