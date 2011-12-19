package dspot.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;

import dspot.utils.Sport;

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
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

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
	View sportsDialogLayout;
	MySportsListAdapter sportsDialogAdapter;
	
	
	AlertDialog alert_address;
	
	Bitmap photo;
	
	
	int operation =1; //1- obter localizacao, 2-criarspot

	
	
	
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
		
		((Button)findViewById(R.id.new_spot_createButton)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				createSpot();
			}
		});
		
		EditText t = (EditText)findViewById(R.id.new_spot_locationEdit);
		t.setEnabled(false);
		t.setFocusable(false);
		
		t= (EditText)findViewById(R.id.new_spot_addressEdit);
		t.setEnabled(false);
		t.setFocusable(false);


		
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
			    
			      //Toast toast = Toast.makeText(getApplicationContext(), location.getProvider() + ": " + location.getLatitude() +" , " + location.getLongitude(), Toast.LENGTH_LONG);
				  //toast.show();
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
		  
		  buildSportsDialog();
		
	}
		
	public void setSports(){
		
		alert_sports.show();
	}
	
	
	
	public void buildSportsDialog() {
		
		AlertDialog.Builder builder;
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		sportsDialogLayout = inflater.inflate(R.layout.search_tab_sports_dialog,
		                               (ViewGroup) findViewById(R.id.layout_root3));

		ListView lv = (ListView) sportsDialogLayout.findViewById(R.id.listView1);
		
		sportsDialogAdapter = new MySportsListAdapter();
		lv.setAdapter(sportsDialogAdapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				sportsDialogAdapter.setChecked(position);
				sportsDialogAdapter.notifyDataSetChanged();
				
			}
		});
		
		builder = new AlertDialog.Builder(this);
		builder.setView(sportsDialogLayout);
		builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();			
			}	
		});
		
		alert_sports = builder.create();
		alert_sports.setTitle("Choose sports");
		
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
		    	photo = (Bitmap) data.getExtras().get("data");  
		    	
		    	ImageView image = (ImageView) findViewById(R.id.new_spot_picture);  
		    	image.setImageBitmap(photo);
		    	
		    	
		    	// Register the listener with the Location Manager to receive location updates
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
				
				
				dialog = ProgressDialog.show(NewSpot.this, "", "Obtaining your location. Please don't move and wait...", true);
				operation=1;
	    		Thread thread = new Thread(this);
	            thread.start();
	    	}
	    }  
	}


	@Override
	public void run() {
		if(operation == 1){
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
		}else if(operation == 2){
			
			
			try {
				if(api.createSpot(photo, sportsDialogAdapter.getIDs(), finalLocation_latitude, finalLocation_longitude,
						((EditText)findViewById(R.id.new_spot_nameEdit)).getText().toString(),
						((EditText)findViewById(R.id.new_spot_locationEdit)).getText().toString(),
						((EditText)findViewById(R.id.new_spot_addressEdit)).getText().toString(), 
						((EditText)findViewById(R.id.new_spot_descriptionEdit)).getText().toString()) == 0){
					
					
					Message msg = handler2.obtainMessage();
					handler2.sendMessage(msg);
					
				}else{
					dialog.dismiss();
		    		Looper.prepare();
		    		Toast toast = Toast.makeText(getApplicationContext(), "Error on creation", Toast.LENGTH_SHORT);
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
        		retrys =0;
				dialog.dismiss();
        		return;
        	}
        	
        	
        	Locale l = new Locale("pt-PT");
        	Geocoder gcd = new Geocoder(getApplicationContext(), l);
        	
    		try {
    			locationManager.removeUpdates(locationListener);
    			addresses = gcd.getFromLocation(finalLocation_latitude, finalLocation_longitude, 1);
 
    			
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
		
		String[] ruas = new String[addresses.get(0).getMaxAddressLineIndex()];
		
		for(int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++)
			ruas[i] = addresses.get(0).getAddressLine(i).toString();
		
		builder.setSingleChoiceItems((CharSequence[])ruas , -1, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	
				EditText t = (EditText)findViewById(R.id.new_spot_locationEdit);
				t.setText(addresses.get(0).getLocality());
				
				EditText t2 = (EditText)findViewById(R.id.new_spot_addressEdit);
				t2.setText(addresses.get(0).getAddressLine(item).toString());
				
				alert_address.dismiss();
		        
		    }
		});
		alert_address = builder.create();
		alert_address.show();
		
		
		
	}
	
	
	
	
	public void createSpot(){
		String name, address, location;
		name = ((EditText)findViewById(R.id.new_spot_nameEdit)).getText().toString();
		location = ((EditText)findViewById(R.id.new_spot_locationEdit)).getText().toString();
		address = ((EditText)findViewById(R.id.new_spot_addressEdit)).getText().toString();

		if(name.equalsIgnoreCase("") || location.equalsIgnoreCase("") || address.equalsIgnoreCase("")){
			Toast.makeText(getApplicationContext(), "Fill the form before creating. Phone and description not mandatory", Toast.LENGTH_SHORT).show();
		}else{
			dialog = ProgressDialog.show(NewSpot.this, "", "Creating. Please wait...", true);
			operation=2;
    		Thread thread = new Thread(this);
            thread.start();	
		}
		
		
	}

	final Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
        	
        	Toast.makeText(getApplicationContext(), "Created with success", Toast.LENGTH_SHORT).show();
			dialog.dismiss();
			
			finish();

        }
    };
	
	
	
	 private class MySportsListAdapter extends BaseAdapter {


			private ArrayList<Sport> sports;
	    	
	    	public MySportsListAdapter() {
	    		sports = api.getSports();
			}

			@Override
			public int getCount() {
				return sports.size();
			}

			@Override
			public Object getItem(int arg0) {
				return sports.get(arg0);
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
	        	LayoutInflater infalInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        	convertView = infalInflater.inflate(R.layout.checklist_child, null);
	            
	            TextView name = (TextView) convertView.findViewById(R.id.checklist_child_name);
	            CheckBox check = (CheckBox) convertView.findViewById(R.id.checklist_child_check);

	            name.setText(sports.get(position).getName());
	            check.setChecked(sports.get(position).isChecked());

			
			return convertView;
			}
			
			
			public void setChecked(int position){
				if(sports.get(position).isChecked()){
					sports.get(position).setChecked(false);
					
				}else{
					sports.get(position).setChecked(true);
				}

			}
			
			
			public ArrayList<Integer> getIDs(){
				ArrayList<Integer> ret = new ArrayList<Integer>();
				
				for(Sport s: sports)
					ret.add(s.getId());
				
				return ret;
			}
			
			
			@Override
			public void notifyDataSetChanged() {
				super.notifyDataSetChanged();
			}
	    	
	    }

}
