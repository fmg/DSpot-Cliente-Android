package dspot.client;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.util.Locale;



import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import dspot.utils.SpotShortInfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.TextView;
import android.widget.Toast;

public class MapFullScreen extends MapActivity implements Runnable{
	

	 private GestureDetector gestureDetector;
	 View.OnTouchListener gestureListener;

	Api api;

	MapView mapView;
	MapController mapController;
	boolean popupActive = false;
	int selected_index = -1;

	View popupView;
	
	ProgressDialog dialog;
	 
	
	LocationManager locationManager;
	LocationListener locationListener;
    
	private double lastLatitude = 9999.9, 	
					lastLongitude= 9999.9;
	
	int location_id;
	
	ArrayList<SpotShortInfo> spots;
	List<Overlay> mapOverlays;
	MyItemizedOverlay itemizedoverlay;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		api = ((Api)getApplicationContext());
		
		setContentView(R.layout.tab_map);
		
		spots = new ArrayList<SpotShortInfo>();
		
		 mapView = (MapView) findViewById(R.id.mapView);
		 mapView.setBuiltInZoomControls(true);
		 
		 mapController = mapView.getController();
		 
		 locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			
		locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
			    
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
	      
		
		gestureDetector = new GestureDetector(new MyGestureDetector());
	      gestureListener = new View.OnTouchListener() {
	          public boolean onTouch(View v, MotionEvent event) {
	              if (gestureDetector.onTouchEvent(event)) {
	                  return true;
	              }
	              return false;
	          }
	      };
		
	      
	      List<Overlay> mapOverlays = mapView.getOverlays();
		 Drawable drawable = this.getResources().getDrawable(R.drawable.pin);
		 itemizedoverlay = new MyItemizedOverlay(drawable);
		 
		mapOverlays.add(itemizedoverlay);
		
		
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);			
		
		dialog = ProgressDialog.show(MapFullScreen.this, "", "Working. Please wait...", true);
		Thread thread = new Thread(this);
        thread.start();
       
    }
	
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
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
		
		System.out.println(lastLatitude + " " + lastLongitude);
		
		
		Locale l = new Locale("pt-PT");
    	Geocoder gcd = new Geocoder(getApplicationContext(), l);
    	
		try {
			List<Address> addresses = gcd.getFromLocation(lastLatitude, lastLongitude, 1);
			
			System.out.println(addresses.size());
			
			System.out.println(addresses.get(0).getLocality());
			
			location_id = api.getLocationID(addresses.get(0).getLocality());

			spots = api.getSpotsByLocation(location_id, false);
			
			
			System.out.println("vai sair da thread");
			Message msg = handler.obtainMessage();
			handler.sendMessage(msg);
			
		} catch (ClientProtocolException e) {
			
			dialog.dismiss();
			
			Looper.prepare();
			Toast toast = Toast.makeText(getApplicationContext(), "Error connecting to the server, try again...", Toast.LENGTH_SHORT);
    		toast.show();
    		
    		e.printStackTrace();
    		
    		Looper.loop();
    		
    		
    		
		}catch (IOException e) {
			
			dialog.dismiss();

			Looper.prepare();
			Toast toast = Toast.makeText(getApplicationContext(), "Error sending information to server, try again...", Toast.LENGTH_SHORT);
    		toast.show();
    		
    		e.printStackTrace();
    		Looper.loop();
    		
		} catch (JSONException e) {
			
			dialog.dismiss();

			Looper.prepare();
			Toast toast = Toast.makeText(getApplicationContext(), "Error parsing information from server, try again...", Toast.LENGTH_SHORT);
    		toast.show();
    		
    		e.printStackTrace();
    		Looper.loop();
		}	
		
	}  
	
	
	final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
        	
        	GeoPoint p = new GeoPoint((int)(lastLatitude*1e6), (int)(lastLongitude*1e6));
        	
        	mapController.setZoom(17);
  		  	mapController.setCenter(p);
  		  	
  		  	itemizedoverlay.removeAllOverlays();
  		  	for(int i= 0; i < spots.size(); i++){
	  		  	GeoPoint point = new GeoPoint((int)(spots.get(i).getLatitude()*1e6), (int)(spots.get(i).getLongitude()*1e6));
	  		  	OverlayItem overlayitem = new OverlayItem(point, "", "");
	  		  	itemizedoverlay.addOverlay(overlayitem);	
  		  	}
  		  	
  		  	
  		  	dialog.dismiss();
        	
        	
        }
    };
	
	
    @Override
	protected void onDestroy() {
    	locationManager.removeUpdates(locationListener);
		super.onDestroy();
	}
    
    
    @Override
  	public boolean dispatchTouchEvent(MotionEvent ev){
  		super.dispatchTouchEvent(ev);
  		return gestureDetector.onTouchEvent(ev);
  	}
    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.full_screen_map_menu, menu);
	    return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    
	    case R.id.full_screen_map_menu_refresh:
	    	
	    	lastLatitude = 9999.9; 	
			lastLongitude= 9999.9;
	    	
	    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);			
			
			dialog = ProgressDialog.show(MapFullScreen.this, "", "Working. Please wait...", true);
    		Thread thread = new Thread(this);
            thread.start();
	        return true;
	        
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
    
    
    class MyItemizedOverlay extends ItemizedOverlay<OverlayItem>{


		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

		
		public GeoPoint getSelectedGeopoint(int index){
			return mOverlays.get(index).getPoint();
		}
		
		
		public MyItemizedOverlay(Drawable defaultMarker) {
			  super(boundCenterBottom(defaultMarker));
			}


		@Override
		protected OverlayItem createItem(int i) {
			return mOverlays.get(i);		
		}
		
		
		public void addOverlay(OverlayItem overlay) {
		    mOverlays.add(overlay);
		    populate();
		}
		
		
		
		public void removeAllOverlays() {
		    mOverlays = new ArrayList<OverlayItem>();
		}


		@Override
		public int size() {
			return mOverlays.size();
		}
		
		
		@Override
		protected boolean onTap(int index) {
			
			popupActive = true;
			selected_index = index;
			
			System.out.println("selected_index-> " +selected_index);			
			
			OverlayItem item = mOverlays.get(index);
			
			mapController.animateTo(item.getPoint());
			
	        popupView = getLayoutInflater().inflate(R.layout.map_popup, mapView, false);
	        
	        ((TextView)popupView.findViewById(R.id.map_popup_name)).setText(spots.get(index).getName());
	        ((TextView)popupView.findViewById(R.id.map_popup_sports)).setText(spots.get(index).getSportsFormated());

	        
	    	  MapView.LayoutParams mapParams = new MapView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 
	                  ViewGroup.LayoutParams.WRAP_CONTENT,
	                  item.getPoint(),
	                  0,
	                  -85,
	                  MapView.LayoutParams.BOTTOM_CENTER);
	    	  mapView.addView(popupView, mapParams);
	    	  
	    	  
	        
		  return true;
		}
		
		
		@Override
		public boolean onTap(GeoPoint p, MapView mapView) {
			if(popupActive){
				mapView.removeView(popupView);
				popupActive = false;
				selected_index = -1;

			}
			selected_index = -1;
			return super.onTap(p, mapView);
		}
	}
    
    
    
    class MyGestureDetector extends SimpleOnGestureListener {
        
		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);
			
			if(selected_index != -1){
				Intent intent = new Intent(getApplicationContext(), ViewSpot.class);
	  		  	intent.putExtra("id", spots.get(selected_index).getId());
	  			intent.putExtra("index", 0);
	  			ArrayList<Integer> tmp = new ArrayList<Integer>();
	  			tmp.add(spots.get(selected_index).getId());
	  			intent.putIntegerArrayListExtra("spotList_ids", tmp);
	  	        startActivity(intent);
			}
			
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if(selected_index != -1){
				System.out.println("aki->" + selected_index);
				mapController.animateTo(itemizedoverlay.getSelectedGeopoint(selected_index));
				mapController.setZoom(17);
			}

			return super.onDoubleTap(e);
		}

		
  	}
	
}