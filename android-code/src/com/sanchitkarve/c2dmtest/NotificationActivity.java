package com.sanchitkarve.c2dmtest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationActivity extends Activity
{
	public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);
        TextView lblMessage = (TextView)findViewById(R.id.lblMessage);
        // Get payload information from intent which is set up by the NotificationManager when the Notification is created.
        // If this activity is still running, the notification would show the previous pushed message when clicked.
        // To solve this issue, restart the activity before passing the intent. (NOT DONE IN THIS EXAMPLE)
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
        	String msg = extras.getString("payload");
        	if(msg != null && msg.length() > 0)
        		lblMessage.setText(msg);
        }
    }

}
