package dspot.client;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Registo extends Activity {
		
	Api api;
	
	
	
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
 
        
    	//int ret = api.regist(user, pass, nome, email);
    	//TODO:
    	int ret = 0;
    	
    	if(ret ==  0){
	    	Toast toast = Toast.makeText(getApplicationContext(), "Registed with success", Toast.LENGTH_LONG);
			toast.show();
	    	
	    	Intent intent = new Intent(getApplicationContext(),DSpotActivity.class);
	        startActivity(intent);
	        finish();
	        
    	}else if(ret == -1){
    		Toast toast = Toast.makeText(getApplicationContext(), "Error registing", Toast.LENGTH_SHORT);
			toast.show();
    	}
    	else if(ret == -3){
    		Toast toast = Toast.makeText(getApplicationContext(), "Connection problems", Toast.LENGTH_SHORT);
			toast.show();
    	}
    }
    
    
    
    

}
