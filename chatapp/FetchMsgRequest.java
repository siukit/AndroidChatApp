package com.siukit.chatapp;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by siukit on 26/02/2017.
 */

public class FetchMsgRequest extends StringRequest{

    private static final String FM_URL = "http://194.81.104.22/~14412104/getMsg.php";
    private Map<String, String> params;

    public FetchMsgRequest(String this_username, String other_username, Response.Listener<String> listener){
        super(Request.Method.POST, FM_URL, listener, null);
        params = new HashMap<>();
        params.put("this_username", this_username);
        params.put("other_username", other_username);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
