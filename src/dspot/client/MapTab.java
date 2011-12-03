package dspot.client;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

public class MapTab  extends MapActivity {

	private int radious;
	Api api;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		api = ((Api)getApplicationContext());
		
		setContentView(R.layout.tab_map);
		
		 MapView mapView = (MapView) findViewById(R.id.mapView);
		 mapView.setBuiltInZoomControls(true);
       
    }
	
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	

	@Override
	protected void onResume() {
		super.onResume();
		
		Toast toast = Toast.makeText(getApplicationContext(), "SeekBar -> " + api.radious, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	
	@Override
	public void onBackPressed() {
		
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        alertbox.setMessage("Do you want to quit the application?");
        alertbox.setPositiveButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Do nothing
            }
        });
        
        alertbox.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {            	            	
            	if (Api.user.isConnected()) {
            		finish();            			
            	}
            	else if (!Api.user.isConnected()) {
            		if (api.logout())
            			finish();
            		else {
            			Toast toast = Toast.makeText(getApplicationContext(), "Logout failed", Toast.LENGTH_SHORT);
            			toast.show();
            		}
            	}
            }       
        });
        
        alertbox.show();
	}
}
