package com.siukit.chatapp;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by siukit on 02/03/2017.
 */

public class OnShakeRequest extends StringRequest {

    private static final String SHAKE_URL = "http://194.81.104.22/~14412104/isShake.php";
    private Map<String, String> params;

    public OnShakeRequest(String is_shake, String username, Response.Listener<String> listener){
        super(Request.Method.POST, SHAKE_URL, listener, null);
        params = new HashMap<>();
        params.put("is_shake", is_shake);
        params.put("username", username);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
