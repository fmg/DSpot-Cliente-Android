package dspot.client;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class MapTab  extends MapActivity implements Runnable{

	Api api;
	
	MapView mapView;
	MapController mapController;
	
	
	private boolean fistStart = true;
	LocationManager locationManager;
	LocationListener locationListener;
    
	private double lastLatitude = 9999.9, 	
					lastLongitude= 9999.9;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		api = ((Api)getApplicationContext());
		
		setContentView(R.layout.tab_map);
		
		 mapView = (MapView) findViewById(R.id.mapView);
		 mapView.setBuiltInZoomControls(true);
		 
		 mapController = mapView.getController();
		 
		 locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			
			locationListener = new LocationListener() {
			    public void onLocationChanged(Location location) {
				    
				      Toast toast = Toast.makeText(getApplicationContext(), location.getProvider() + ": " + location.getLatitude() +" , " + location.getLongitude(), Toast.LENGTH_LONG);
					  toast.show();
					  System.out.println(location.getProvider() +": " +location.getLatitude() + " , " + location.getLongitude());
				    
					 lastLatitude = location.getLatitude();
					 lastLongitude = location.getLongitude();
			    }

			    public void onStatusChanged(String provider, int status, Bundle extras) {}

			    public void onProviderEnabled(String provider) {
			    }

			    public void onProviderDisabled(String provider) {
			    }
			  };
			
       
    }
	
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	

	@Override
	protected void onResume() {
		super.onResume();
		
		if(fistStart){
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);			
			
			fistStart = false;
    		Thread thread = new Thread(this);
            thread.start();
			
		}else{
			
		}
		
	}
	
	
	
	@Override
	public void run() {
		while(lastLatitude > 900.0 && lastLongitude > 900.0){
			try {					
				
				System.out.println("Sleeping. zZzZzZzzzzz");
				Thread.sleep(5000);
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		locationManager.removeUpdates(locationListener);
		System.out.println("vai sair da thread");
		Message msg = handler.obtainMessage();
		handler.sendMessage(msg);
		return;
		
	}  
	
	
	final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
        	
        	GeoPoint p = new GeoPoint((int)(lastLatitude*1e6), (int)(lastLongitude*1e6));
        	
        	mapController.setZoom(17);
  		  	mapController.setCenter(p);
        	/*
        	Locale l = new Locale("pt-PT");
        	Geocoder gcd = new Geocoder(getApplicationContext(), l);
        	
    		try {
    			locationManager.removeUpdates(locationListener);
    			List<Address> addresses = gcd.getFromLocation(finalLocation_latitude, finalLocation_longitude, 1);
 
    			
    			if (addresses.size() > 0) {
    				showAddressDialog();
    				dialog.dismiss();
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            */
        }
    };
	
	
    @Override
	protected void onDestroy() {
		super.onDestroy();
		locationManager.removeUpdates(locationListener);
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
