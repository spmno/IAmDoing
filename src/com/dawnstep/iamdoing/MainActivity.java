package com.dawnstep.iamdoing;

import org.json.JSONArray;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.future.SimpleFuture;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.DisconnectCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.JSONCallback;
import com.koushikdutta.async.http.socketio.ReconnectCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;
import com.koushikdutta.async.http.socketio.SocketIORequest;
import com.koushikdutta.async.http.socketio.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements OnClickListener{
    public static final long TIMEOUT = 10000L;
	
    private TextView receiveMessageView;
    private EditText doingContentView;
    private Button sendButton;
    SocketIOClient socketIOClient;
    
	private Handler displayHandle = new Handler(){  
        public void handleMessage(Message msg) {  
            switch(msg.what)  
            {  
            case 1:  
            	String doing = msg.getData().getString("text");
            	receiveMessageView.setText(doing);  
                break;  
            default:  
                break;        
            }  
            super.handleMessage(msg);  
        }  
    };  
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		receiveMessageView = (TextView)findViewById(R.id.message);
		doingContentView = (EditText)findViewById(R.id.doing);
		sendButton = (Button)findViewById(R.id.sendButton);
		sendButton.setOnClickListener(this);
		
		try {
			connectServer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
    public void connectServer() throws Exception {

        SocketIORequest req = new SocketIORequest("http://koush.clockworkmod.com:8080");
        req.setLogging("Socket.IO", Log.VERBOSE);
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
                        displayHandle.sendMessage(message); 
                        notifyMessage(string);
                    }
                });
                client.on("pong", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray arguments, Acknowledge acknowledge) {
                        //trigger2.trigger(arguments.length() == 3);
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

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		socketIOClient.emit(doingContentView.getText().toString());
	}
	
	private void notifyMessage(String content) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);  
        Builder builder = new Notification.Builder(MainActivity.this);  
        Intent intent = getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIndent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);  
        builder.setContentIntent(contentIndent)
        	   .setSmallIcon(R.drawable.ic_launcher)//设置状态栏里面的图标（小图标） 　　　　　　　　　　　　　　　　　　　　.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.i5))//下拉下拉列表里面的图标（大图标） 　　　　　　　.setTicker("this is bitch!") //设置状态栏的显示的信息  
               .setWhen(System.currentTimeMillis())//设置时间发生时间  
               .setAutoCancel(true)//设置可以清除  
               .setContentTitle("Doing")//设置下拉列表里的标题  
               .setContentText(content);//设置上下文内容  
        Notification notification = builder.build(); 
        //加i是为了显示多条Notification  
        notificationManager.notify(0,notification);  
	}

}
