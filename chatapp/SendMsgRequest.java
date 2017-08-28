package com.siukit.chatapp;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by siukit on 26/02/2017.
 */

public class SendMsgRequest extends StringRequest {

    private static final String RE_URL = "http://194.81.104.22/~14412104/sendMsg.php";
    private Map<String, String> params;

    //when message being sent, it will be stored in the messages table with username of sender and the username of where it sent to,
    //and the content of the message
    public SendMsgRequest(String username, String message, String send_to, Response.Listener<String> listener){
        super(Method.POST, RE_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("message", message);
        params.put("send_to", send_to);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
