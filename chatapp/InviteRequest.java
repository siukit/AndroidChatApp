package com.siukit.chatapp;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by siukit on 28/02/2017.
 */

public class InviteRequest extends StringRequest {

    private static final String gps_URL = "http://194.81.104.22/~14412104/invite.php";
    private Map<String, String> params;

    public InviteRequest(String invite_from_user, String invite_to_user, Response.Listener<String> listener){
        super(Request.Method.POST, gps_URL, listener, null);
        params = new HashMap<>();
        params.put("invite_from_user", invite_from_user);
        params.put("invite_to_user", invite_to_user);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
