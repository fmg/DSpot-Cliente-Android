package dspot.client;

import android.app.Activity;
import android.os.Bundle;

public class ViewSpot extends Activity{
	
	Api api;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		api = ((Api)getApplicationContext());
		
	}

	
	
}
