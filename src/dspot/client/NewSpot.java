package dspot.client;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class NewSpot extends Activity implements Runnable{

	private Api api;
	
	private static final int CAMERA_PIC_REQUEST = 1337;
	private int retrys = 0, max_retrys = 6;
	private double lastLatitude_network, lastLongitude_network, lastLatitude_gps, lastLongitude_gps, 
					deltaLatitude_network = 9999.9, 
					deltaLongitude_network = 9999.9 ,
					deltaLatitude_gps = 9999.9, 
					deltaLongitude_gps = 9999.9,
					finalLocation_latitude, finalLocation_longitude;
	
	LocationManager locationManager;
	LocationListener locationListener;
	List<Address> addresses;
	
	ProgressDialog dialog;
	AlertDialog alert_sports;
	AlertDialog alert_address;

	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		api = ((Api)getApplicationContext());
		
		setContentView(R.layout.new_spot);
		
		((ImageView)findViewById(R.id.new_spot_picture)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				takePicture();
				
			}
		});
				
		Toast toast = Toast.makeText(getApplicationContext(), "Tap the camera icon to take picture", Toast.LENGTH_SHORT);
		toast.show();
		
		((Button)findViewById(R.id.new_spot_setSports)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				setSports();
			}
		});
		
		EditText t = (EditText)findViewById(R.id.new_spot_locationEdit);
		t.setEnabled(false);
		
		t= (EditText)findViewById(R.id.new_spot_addressEdit);
		t.setEnabled(false);			

		
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
			    
			      Toast toast = Toast.makeText(getApplicationContext(), location.getProvider() + ": " + location.getLatitude() +" , " + location.getLongitude(), Toast.LENGTH_LONG);
				  toast.show();
				  System.out.println(location.getProvider() +": " +location.getLatitude() + " , " + location.getLongitude());
			    
				  double deltaLat, deltaLon;
				  if(location.getProvider().equals(LocationManager.GPS_PROVIDER)){
					  deltaLat = Math.abs(location.getLatitude() - lastLatitude_gps);
					  deltaLon = Math.abs(location.getLongitude() - lastLongitude_gps);
					  
					  if((deltaLat + deltaLon) < (deltaLatitude_gps + deltaLongitude_gps)){
						  lastLatitude_gps = location.getLatitude();
						  lastLongitude_gps = location.getLongitude();
						  deltaLatitude_gps = deltaLat;
						  deltaLongitude_gps = deltaLon;
				      }
				  }else{
					  deltaLat = Math.abs(location.getLatitude() - lastLatitude_network);
					  deltaLon = Math.abs(location.getLongitude() - lastLongitude_network);
					  
					  if((deltaLat + deltaLon) < (deltaLatitude_network + deltaLongitude_network)){
						  lastLatitude_network = location.getLatitude();
						  lastLongitude_network = location.getLongitude();
						  deltaLatitude_network = deltaLat;
						  deltaLongitude_network = deltaLon;
				      }
				  }
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {
		    }

		    public void onProviderDisabled(String provider) {
		    }
		  };
		
	}
		
	public void setSports(){
		final CharSequence[] items = {"Red", "Green", "Blue"};
		boolean[] checkeditems = {false, false, false};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick a color");
		builder.setNeutralButton("ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alert_sports.dismiss();
				
			}
		});
		builder.setMultiChoiceItems(items,checkeditems, new DialogInterface.OnMultiChoiceClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which,
					boolean isChecked) {
				Toast.makeText(getApplicationContext(), items[which], Toast.LENGTH_SHORT).show();
			}
		});
		alert_sports = builder.create();
		alert_sports.show();
	}
	
	
	
	
	public void takePicture(){
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);  

	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	    if (requestCode == CAMERA_PIC_REQUEST) {  
	        if(data == null)
	        	return;
	    	
	    	// do something  
	    	if(data.hasExtra("data")){
		    	Bitmap thumbnail = (Bitmap) data.getExtras().get("data");  
		    	
		    	ImageView image = (ImageView) findViewById(R.id.new_spot_picture);  
		    	image.setImageBitmap(thumbnail);
		    	
		    	
		    	// Register the listener with the Location Manager to receive location updates
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
				
				
				dialog = ProgressDialog.show(NewSpot.this, "", "Obtaining your location. Please don't move and wait...", true);
	    		Thread thread = new Thread(this);
	            thread.start();
	    	}
	    }  
	}


	@Override
	public void run() {
		while(deltaLatitude_network + deltaLongitude_network > 0.0 && deltaLatitude_gps + deltaLongitude_gps > 0.0){
			try {
				
				if(retrys > max_retrys){
					locationManager.removeUpdates(locationListener);
					System.out.println("vai sair da thread");
					Message msg = handler.obtainMessage();
					handler.sendMessage(msg);
					return;
				}	
				
				System.out.println("Sleeping. zZzZzZzzzzz");
				Thread.sleep(10000);
				retrys++;
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if(deltaLatitude_network + deltaLongitude_network == 0.0){
			finalLocation_latitude = lastLatitude_network;
			finalLocation_longitude = lastLongitude_network;
		}else{
			finalLocation_latitude = lastLatitude_gps;
			finalLocation_longitude = lastLongitude_gps;
		}
		
		locationManager.removeUpdates(locationListener);
		System.out.println("vai sair da thread");
		Message msg = handler.obtainMessage();
		handler.sendMessage(msg);
		
	}  
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		locationManager.removeUpdates(locationListener);
	}
	
	
	final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
        	
        	if(retrys> max_retrys){
        		Toast.makeText(getApplicationContext(), "Cannot obtain your position. Please try in a again in a near open space", Toast.LENGTH_SHORT).show();
				dialog.dismiss();
        		return;
        	}
        	
        	
        	Locale l = new Locale("pt-PT");
        	Geocoder gcd = new Geocoder(getApplicationContext(), l);
        	
    		try {
    			locationManager.removeUpdates(locationListener);
    			addresses = gcd.getFromLocation(finalLocation_latitude, finalLocation_longitude, 5);
 
    			
    			if (addresses.size() > 0) {
    				showAddressDialog();
    				dialog.dismiss();
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            
            dialog.dismiss();
        }
    };
    
    
	public void showAddressDialog(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose the adress");
		
		for(int i = 0; i < addresses.size();i++){
			System.out.println("localidade -> " + addresses.get(i).getLocality());
			
			for(int k= 0; k < addresses.get(i).getMaxAddressLineIndex(); k++){
				
				System.out.println("address - > " + addresses.get(i).getAddressLine(k).toString());
			}
				
		}
		/*
		builder.setSingleChoiceItems((CharSequence[]) addresses.toArray(), -1, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	
		    	System.out.println(addresses.get(0).getLocality());
				EditText t = (EditText)findViewById(R.id.new_spot_locationEdit);
				t.setText(addresses.get(0).getLocality());
				
				EditText t2 = (EditText)findViewById(R.id.new_spot_addressEdit);
				t2.setText(addresses.get(0).getAddressLine(0).toString());
				
				alert_address.dismiss();
		        
		    }
		});
		alert_address = builder.create();
		alert_address.show();
		*/
		
		
	}

}
