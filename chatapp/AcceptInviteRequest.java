package com.siukit.chatapp;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by siukit on 28/02/2017.
 */

//This class is for communicating with the database whenever user accepted a chat invitation
public class AcceptInviteRequest extends StringRequest {

    //url link of the php file which talks to the database
    private static final String FM_URL = "http://194.81.104.22/~14412104/accept_invite.php";
    //map for puting on data that are needed
    private Map<String, String> params;

    //if invitation was accepted, send username of the invited user to the original user's accept_from_user table
    public AcceptInviteRequest(String accept_from_user, String accept_to_user, Response.Listener<String> listener) {
        super(Request.Method.POST, FM_URL, listener, null);
        params = new HashMap<>();
        params.put("accept_from_user", accept_from_user);
        params.put("accept_to_user", accept_to_user);
    }

    //if it is rejected then set the invite_from_user table to null
    public AcceptInviteRequest(String accept_from_user, Response.Listener<String> listener) {
        super(Request.Method.POST, FM_URL, listener, null);
        params = new HashMap<>();
        params.put("accept_from_user", accept_from_user);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}