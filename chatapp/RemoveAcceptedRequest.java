package com.siukit.chatapp;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
/**

 * Created by siukit on 28/02/2017.
 */

//after the user accepted the chat, set the accept_from_user field back to null
public class RemoveAcceptedRequest extends StringRequest {

    private static final String gps_URL = "http://194.81.104.22/~14412104/remove_accepted.php";
    private Map<String, String> params;

    public RemoveAcceptedRequest(String username, Response.Listener<String> listener){
        super(Request.Method.POST, gps_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}