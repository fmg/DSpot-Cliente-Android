package dspot.client;


import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;


import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable.Factory;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Registo extends Activity  implements Runnable{
		
	private Api api;
	String name, email, picURL = null;
	ProgressDialog dialog;
	String facebook_id;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registo);
        
        api = ((Api)getApplicationContext());

        (findViewById(R.id.regist_registButton)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				registAction();
			}
        });
        
        
        (findViewById(R.id.regist_facebookButton)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = ProgressDialog.show(Registo.this, "", "Working. Please wait...", true);
				facebookAction();
			}
        });

    }
    
    
  
    
    
    public void registAction(){
    	String user, pass, passconf, nome, email;
    	
    	
    	user = ((EditText) findViewById(R.id.register_username)).getText().toString();
    	pass = ((EditText) findViewById(R.id.register_password)).getText().toString();
    	passconf = ((EditText) findViewById(R.id.register_passwordConf)).getText().toString();
    	nome = ((EditText) findViewById(R.id.register_name)).getText().toString();
    	email = ((TextView) findViewById(R.id.register_email)).getText().toString();
    	
    	//data tem de ser com dd-mm-aaaa
    	
    	if(user.length() == 0 || pass.length() == 0 || passconf.length() == 0 || nome.length() == 0 || email.length() == 0 ){
    		Toast toast = Toast.makeText(getApplicationContext(), "Fill the all the camps in the form", Toast.LENGTH_SHORT);
    		toast.show();
    		return;
    	}

    	if(!pass.equals(passconf)){
    		Toast toast = Toast.makeText(getApplicationContext(), "The passwords must be the same", Toast.LENGTH_SHORT);
    		toast.show();
    		return;
    	}
    	
    	
    	dialog = ProgressDialog.show(Registo.this, "", "Working. Please wait...", true);
		Thread thread = new Thread(this);
        thread.start();
    }
    
    
    
    @Override
	public void run() {
    	String user, pass, passconf, nome, email;
    	
    	
    	user = ((EditText) findViewById(R.id.register_username)).getText().toString();
    	pass = ((EditText) findViewById(R.id.register_password)).getText().toString();
    	passconf = ((EditText) findViewById(R.id.register_passwordConf)).getText().toString();
    	nome = ((EditText) findViewById(R.id.register_name)).getText().toString();
    	email = ((TextView) findViewById(R.id.register_email)).getText().toString();
        
    	int ret;
		try {
			ret = api.registrate(user, pass, nome, email, picURL, facebook_id);
			if(ret ==  0){
				
				Message msg = handler.obtainMessage();
				handler2.sendMessage(msg);
						        
	    	}else if(ret == -1){
	    		dialog.dismiss();
	    		Looper.prepare();
	    		Toast toast = Toast.makeText(getApplicationContext(), "Error on regristration", Toast.LENGTH_SHORT);
				toast.show();
				Looper.loop();
	    	}
		} catch (ClientProtocolException e) {
			
			dialog.dismiss();
			Looper.prepare();
			Toast toast = Toast.makeText(getApplicationContext(), "Error connecting to the server, try again...", Toast.LENGTH_SHORT);
    		toast.show();
    		Looper.loop();
    		
    		e.printStackTrace();
    		
		} catch (IOException e) {
			dialog.dismiss();

			Looper.prepare();
			Toast toast = Toast.makeText(getApplicationContext(), "Error parsing information from server, try again...", Toast.LENGTH_SHORT);
    		toast.show();
    		Looper.loop();
			
			e.printStackTrace();
			
		} catch (URISyntaxException e) {
			dialog.dismiss();
			
			Looper.prepare();
			Toast toast = Toast.makeText(getApplicationContext(), "Error connecting to the server, try again...", Toast.LENGTH_SHORT);
    		toast.show();
    		Looper.loop();
    		
    		e.printStackTrace();
		}
	}
    
    
    public void facebookAction(){
    	
    	api.mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = api.mPrefs.getString("access_token", null);
        long expires = api.mPrefs.getLong("access_expires", 0);
        if(access_token != null) {
            api.facebook.setAccessToken(access_token);
        }
        if(expires != 0) {
            api.facebook.setAccessExpires(expires);
        }
        
        /*
         * Only call authorize if the access_token has expired.
         */
        if(!api.facebook.isSessionValid()) {

            api.facebook.authorize(this, new String[] {"email","offline_access", "publish_checkins"}, new DialogListener() {
                @Override
                public void onComplete(Bundle values) {
                    SharedPreferences.Editor editor = api.mPrefs.edit();
                    editor.putString("access_token", api.facebook.getAccessToken());
                    editor.putLong("access_expires", api.facebook.getAccessExpires());
                    editor.commit();
                }
    
                @Override
                public void onFacebookError(FacebookError error) {}
    
                @Override
                public void onError(DialogError e) {}
    
                @Override
                public void onCancel() {}
            });
        }else{
        	
        	Bundle params = new Bundle();
       		params.putString("fields", "name, email ,picture");
    		api.mAsyncRunner.request("me", params, new UserRequestListener(handler));
    		
        }
    }
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        api.facebook.authorizeCallback(requestCode, resultCode, data);
        //a data nao traz nada de especial, so o access token...
        
    	Bundle params = new Bundle();
   		params.putString("fields", "name, email ,picture");
		api.mAsyncRunner.request("me", params, new UserRequestListener(handler));
		
		Toast toast = Toast.makeText(getApplicationContext(), "Working. Please wait...", Toast.LENGTH_SHORT);
 		toast.show();
		
    }
    
    
    
    public void fillTheForm(){
    	
    	try{
    		((EditText)findViewById(R.id.register_email)).setText(email);
        	((EditText)findViewById(R.id.register_name)).setText(name);
        	
        	URL newurl;
			Bitmap mIcon_val;
			
			
			newurl = new URL(picURL);

			mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
			
			((ImageView)findViewById(R.id.register_profileImage)).setImageBitmap(mIcon_val);
			
			((EditText)findViewById(R.id.register_email)).setEnabled(false);
        	((EditText)findViewById(R.id.register_name)).setEnabled(false);
			
    	}catch (IOException e) {
			Toast toast = Toast.makeText(getApplicationContext(), "Error downloading image", Toast.LENGTH_SHORT);
     		toast.show();
		} 
    	
    }
    
    
    public class UserRequestListener extends BaseRequestListener {

    	Handler mHandler;
    	public UserRequestListener(Handler h) {
    		mHandler = h;
		}
    	
        public void onComplete(final String response, final Object state) {
        	JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(response);
				
				System.out.println(response.toString());
	        	
	        	name = jsonObject.getString("name");
	        	email = jsonObject.getString("email");
	        	picURL = jsonObject.getString("picture");
	        	facebook_id = jsonObject.getString("id");

	        	
	        	System.out.println("AKI-> " + name + " "+ email + " " + picURL);
	        	
	        	
	        					
			} catch (JSONException e) {
				e.printStackTrace();
			} 
			
			Message msg = mHandler.obtainMessage();
			mHandler.sendMessage(msg);
        }

    }
    
    
    
    // Define the Handler that receives messages from the thread and update the progress
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            fillTheForm();
            dialog.dismiss();
        }
    };
    
    
    final Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {

        	dialog.dismiss();
        	Toast toast = Toast.makeText(getApplicationContext(), "Registered with success", Toast.LENGTH_LONG);
			toast.show();
	        finish();

        }
    };
    

}
