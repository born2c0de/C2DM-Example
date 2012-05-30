package com.sanchitkarve.c2dmtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.c2dm.C2DMessaging;


public class C2dmtestActivity extends Activity {
	Button btnRegister;
	Button btnUnregister;
    // This is a bad idea. I've done this only to demonstrate the C2DM Example.
	// An Activity Context should be used to reference a Widget instead of
	// referencing it directly using a public member variable of the Widget.
	public static TextView txtMsg;
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(btnRegisterOnClick);
        btnUnregister = (Button)findViewById(R.id.btnUnregister);
        btnUnregister.setOnClickListener(btnUnregisterOnClick);
        txtMsg = (TextView)findViewById(R.id.txtMsg);        
    }
    
    View.OnClickListener btnRegisterOnClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// Register device to C2DM Service with this username
			C2DMessaging.register(getBaseContext(), "write2sanchit@gmail.com");			
		}
	};
	
	View.OnClickListener btnUnregisterOnClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// Unregister device from C2DM service
			C2DMessaging.unregister(getBaseContext());			
		}
	};
}