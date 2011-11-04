package dspot.client;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DSpotActivity extends Activity implements Runnable {
	
	ProgressDialog dialog;
	Api api;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        api = new Api();
        
        (findViewById(R.id.login_loginButton)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				connectAction();
			}
        });
        
        
        (findViewById(R.id.login_registerButton)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),Registo.class);
	            startActivity(intent);
			}
        });
        
        (findViewById(R.id.login_guestButton)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				api.user.setConnected(false);
				Intent intent = new Intent(getApplicationContext(),Inicial.class);
	            startActivity(intent);
	            finish();
			}
        });
    }
    
        
        public void connectAction(){
        	
			
    		dialog = ProgressDialog.show(DSpotActivity.this, "", "Loading. Please wait...", true);
    		Thread thread = new Thread(this);
            thread.start();

        }
        
        
        @Override
    	public void run() {
    		
    		EditText user = (EditText) findViewById(R.id.login_username); 
    		EditText pass = (EditText) findViewById(R.id.login_password); 
    		
    		int success;
			try {
				
				success = api.login(user.getText().toString(), pass.getText().toString());
				
				if(success == -2){
					
					dialog.dismiss();
					
					Looper.prepare();
	        		Toast toast = Toast.makeText(getApplicationContext(), "Autentication failed", Toast.LENGTH_SHORT);
	        		toast.show();
	        		Looper.loop();
				}else if (success == -1){
	    			
					dialog.dismiss();

					Looper.prepare();
	    			Toast toast = Toast.makeText(getApplicationContext(), "Insert your credentials or register", Toast.LENGTH_SHORT);
	        		toast.show();
	    			Looper.loop();
	    		}else if (success == 0){
	    			
	    			boolean retsuccess = api.getProfile();
	    			
	    			if(retsuccess){
	    				
	    				api.user.setConnected(true);
	    				
	    				dialog.dismiss();
	    				
	    				Intent intent = new Intent(getApplicationContext(),Inicial.class);
	    	            startActivity(intent);
	    	            finish();
	    	            
	    			}else{
	    				
	    				dialog.dismiss();

	    				
	    				Looper.prepare();
	    				Toast toast = Toast.makeText(getApplicationContext(), "Error obtaining profile, try logging in again", Toast.LENGTH_SHORT);
	    	    		toast.show();
	    				Looper.loop();

	    			}
	    		}
				
			} catch (ClientProtocolException e) {
				
				dialog.dismiss();
				
				Looper.prepare();
    			Toast toast = Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_SHORT);
        		toast.show();
        		Looper.loop();
        		
        		e.printStackTrace();
        		
			} catch (JSONException e) {
				
				dialog.dismiss();

				Looper.prepare();
    			Toast toast = Toast.makeText(getApplicationContext(), "Error parsing information from server", Toast.LENGTH_SHORT);
        		toast.show();
        		Looper.loop();
				
				e.printStackTrace();
				
			} catch (IOException e) {
				
				dialog.dismiss();

				Looper.prepare();
    			Toast toast = Toast.makeText(getApplicationContext(), "Error sending information from server", Toast.LENGTH_SHORT);
        		toast.show();
        		Looper.loop();
				
				e.printStackTrace();
			}
    		
    	}
        
        
        
}
