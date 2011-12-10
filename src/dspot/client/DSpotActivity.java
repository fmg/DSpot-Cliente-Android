package dspot.client;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import dspot.client.database.DatabaseAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class DSpotActivity extends Activity implements Runnable {
	
	ProgressDialog dialog;
	Api api;
	boolean guest_mode;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        api = new Api();
        
        api.dbAdapter = new DatabaseAdapter(getApplicationContext());
        
        
        //TODO: apagar
        api.resetDefinitions();

        
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
				guestAction();
			}
        });
    }
    
    public void guestAction(){
    	guest_mode = true;
		dialog = ProgressDialog.show(DSpotActivity.this, "", "Loading. Please wait...", true);
		Thread thread = new Thread(this);
        thread.start();
    }
    
        
    public void connectAction(){
    	guest_mode = false;
		dialog = ProgressDialog.show(DSpotActivity.this, "", "Loading. Please wait...", true);
		Thread thread = new Thread(this);
        thread.start();

    }
    
    
    @Override
	public void run() {
    	
    	api.updateAplicationDefinitions();
    	
		
    	if(!guest_mode){
			EditText user = (EditText) findViewById(R.id.login_username); 
			EditText pass = (EditText) findViewById(R.id.login_password); 
			
			int success;
			try {
				
				success = api.login(user.getText().toString(), pass.getText().toString());
				
				if(success == -2){
					
					dialog.dismiss();
					
					Looper.prepare();
	        		Toast toast = Toast.makeText(getApplicationContext(), "Invalid user/password combination", Toast.LENGTH_SHORT);
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
				Toast toast = Toast.makeText(getApplicationContext(), "Error sending information to server", Toast.LENGTH_SHORT);
	    		toast.show();
	    		Looper.loop();
				
				e.printStackTrace();
			}
    	}else{
    		api.user.setConnected(false);
    		Intent intent = new Intent(getApplicationContext(), Inicial.class);
            startActivity(intent);
            finish();
            dialog.dismiss();	
    	}
		
	}
    
    
    
    @Override
	public void onBackPressed() {
		
		// prepare the alert box
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);

        // set the message to display
        alertbox.setMessage("Do you want to quit the application?");

        // set a positive/yes button and create a listener
        alertbox.setPositiveButton("No", new DialogInterface.OnClickListener() {

            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                //Do nothing
            }
        });

        // set a negative/no button and create a listener
        alertbox.setNegativeButton("Yes", new DialogInterface.OnClickListener() {

            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
            	
        		finish();
      
            }
        });

        // display box
        alertbox.show();

	}      
    
}