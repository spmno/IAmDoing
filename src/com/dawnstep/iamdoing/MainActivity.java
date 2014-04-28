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

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements OnClickListener{
    public static final long TIMEOUT = 10000L;
	
    private TextView receiveMessageView;
    private EditText doingContentView;
    private Button sendButton;

    
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
		
	}

	@Override 
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		JSONObject jsonMessage = new JSONObject();
		String doingContent = doingContentView.getText().toString();
		try {
			jsonMessage.put("message", doingContent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void notifyMessage(String content) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);  
        Builder builder = new Notification.Builder(MainActivity.this);  
        Intent intent = getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIndent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);  
        builder.setContentIntent(contentIndent)
        	   .setSmallIcon(R.drawable.ic_launcher)//����״̬�������ͼ�꣨Сͼ�꣩ ����������������������������������������.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.i5))//���������б������ͼ�꣨��ͼ�꣩ ��������������.setTicker("this is bitch!") //����״̬������ʾ����Ϣ  
               .setWhen(System.currentTimeMillis())//����ʱ�䷢��ʱ��  
               .setAutoCancel(true)//���ÿ������  
               .setContentTitle("Doing")//���������б���ı���  
               .setDefaults(Notification.DEFAULT_ALL)
               .setContentText(content);//��������������  
        Notification notification = builder.build(); 
        //��i��Ϊ����ʾ����Notification  
        notificationManager.notify(0,notification);  
	}

}
