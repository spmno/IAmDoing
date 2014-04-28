package com.dawnstep.iamdoing.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.JSONCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;
import com.koushikdutta.async.http.socketio.SocketIORequest;
import com.koushikdutta.async.http.socketio.StringCallback;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class DoingService extends Service {
	
    SocketIOClient socketIOClient;
    
	public DoingService() {
	}	

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
    public void connectServer() throws Exception {

        SocketIORequest req = new SocketIORequest("http://115.29.139.76:5080");
        //req.setLogging("Socket.IO", Log.VERBOSE);
        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), req, new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, SocketIOClient client) {
            	socketIOClient = client;
                client.setStringCallback(new StringCallback() {
                    @Override
                    public void onString(String string, Acknowledge acknowledge) {
                        //trigger1.trigger("hello".equals(string));
                        Message message=new Message();  
                        message.what=1;  
                        Bundle bundle = new Bundle();    
                        bundle.putString("text", string);
                        message.setData(bundle);
                    }
                });
                client.on("message", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray arguments, Acknowledge acknowledge) {
                        //trigger2.trigger(arguments.length() == 3);
                    	String aa = "";  
                    	try {  
                    		int length = arguments.length();  
                    		
                    		for(int i = 0; i < length; i++){//±éÀúJSONArray  
                    			Log.d("debugTest",Integer.toString(i));  
                    			JSONObject oj = arguments.getJSONObject(i);  
                    			aa = aa + oj.getString("content");  
                    		}  
                    	} catch (JSONException e) {  
                            throw new RuntimeException(e);  
                        }  

                    }
                });
                client.setJSONCallback(new JSONCallback() {
                    @Override
                    public void onJSON(JSONObject json, Acknowledge acknowledge) {
                        //trigger3.trigger("world".equals(json.optString("hello")));
                    }
                });
                
                //try {
                //    client.emit("hello");
                //    client.emit(new JSONObject("{\"hello\":\"world\"}"));
                //    client.emit("ping", new JSONArray("[2,3,4]"));
                //}
                //catch (JSONException e) {
                //}
            }
        });

    }
}
