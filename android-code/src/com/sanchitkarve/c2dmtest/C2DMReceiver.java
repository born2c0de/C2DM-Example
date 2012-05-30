package com.sanchitkarve.c2dmtest;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;

import com.google.android.c2dm.C2DMBaseReceiver;

public class C2DMReceiver extends C2DMBaseReceiver {
	Handler mMainThreadHandler = new Handler(); 
	
	public C2DMReceiver() {
		// Each instance of the application needs a unique senderID for C2DM to work
		super("write2sanchit@gmail.com");
	}

	public C2DMReceiver(String senderId) {
		super(senderId);
	}	

	@Override
	protected void onMessage(Context context, Intent intent) {
		// Create a Notification when a push message is received		
		createNotification(getBaseContext(), intent.getStringExtra("payload"));
		
	}

	@Override
	public void onError(Context context, String errorId) {
		// Dump Error Message in LogCat
		Log.w("c2dm.onError", errorId);
	}
	
	@Override
	public void onUnregistered(Context context)
	{				
		mMainThreadHandler.post(new Runnable() { 

            @Override 

            public void run() {            	
                 
                // This is a bad idea. I've done this only to demonstrate the C2DM Example.
            	// An Activity Context should be used to reference a Widget instead of
            	// referencing it directly using a public member variable of the Widget.
            	C2dmtestActivity.txtMsg.setText("Unregistered");
            } 
        });
	}
	
	@Override
	public void onRegistrered(Context context, String registrationId)
	{
		// Get Registration ID
		final String regId = registrationId;
		// Get Unique Device ID
		final String devId = Secure.getString(context.getContentResolver(),	Secure.ANDROID_ID);
				
		// Use Handler to update the text from the UI Thread.
		mMainThreadHandler.post(new Runnable() {
			
            @Override
            public void run() {      	
                // This is a bad idea. I've done this only to demonstrate the C2DM Example.
            	// An Activity Context should be used to reference a Widget instead of
            	// referencing it directly using a public member variable of the Widget.
            	C2dmtestActivity.txtMsg.setText("Registration ID: " + regId + "\nDeviceID: " + devId);
            	// Send Registration ID to Web Server
            	new SendRegIDTask().execute("http://people.oregonstate.edu/~karves/c2dmDemo/register.php",regId,devId,"write2sanchit@gmail.com");
            }
        });	
	}
	
	@SuppressWarnings("deprecation")
	public void createNotification(Context context, String payload) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher,
				"Message received", System.currentTimeMillis());
		// Hide the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent intent = new Intent(context, NotificationActivity.class);
		// This can be anything you'd like as long as the web server sends the message with the same tag
		intent.putExtra("payload", payload);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		notification.setLatestEventInfo(context, "New Pushed Message",	"Message : " + payload, pendingIntent);
		notification.defaults = Notification.DEFAULT_ALL;
		notificationManager.notify(0, notification);
	}
	
	
	
	//Send the registration id, device id and username to web server
	private String SendDeviceIdsToServer(String urlString, String regID, String deviceID, String username) {
		 
		String response = "failure";
		DefaultHttpClient hc=new DefaultHttpClient();  
		ResponseHandler <String> res=new BasicResponseHandler();  
		HttpPost postMethod=new HttpPost(urlString);  
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);    
		nameValuePairs.add(new BasicNameValuePair("regID", regID));    
		nameValuePairs.add(new BasicNameValuePair("deviceID", deviceID));
		nameValuePairs.add(new BasicNameValuePair("username", username));  
		try {
			postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
		try {
			response=hc.execute(postMethod,res);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
}
	
	/** Class used to send device data to the web server asynchronously. */
	public class SendRegIDTask extends AsyncTask<String, Void, String> {

		/** Called when execute() is called. */
		@Override
		protected String doInBackground(String... urls)	{
			
				return SendDeviceIdsToServer(urls[0],urls[1],urls[2],urls[3]);
			
		}
		
		/** Called when the background task is complete. */
		@Override
		protected void onPostExecute(final String result) {

			mMainThreadHandler.post(new Runnable() { 

	            @Override 

	            public void run() {            	
	                 
	                // This is a bad idea. I've done this only to demonstrate the C2DM Example.
	            	// An Activity Context should be used to reference a Widget instead of
	            	// referencing it directly using a public member variable of the Widget.
	            	if(result.contentEquals("Success"))
	            		C2dmtestActivity.txtMsg.setText(C2dmtestActivity.txtMsg.getText() + "\nSent to Server");
	            	else if(result.contentEquals("Device already registered"))
	            		C2dmtestActivity.txtMsg.setText(C2dmtestActivity.txtMsg.getText() + "\nSent to Server\nDevice already registered");
	            	else
	            		C2dmtestActivity.txtMsg.setText(C2dmtestActivity.txtMsg.getText() + "\nNOT Sent to Server\n" + result);
	            }
	        });			
		}		
	}
}
