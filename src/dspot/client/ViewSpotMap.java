package dspot.client;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class ViewSpotMap extends MapActivity {
	
	MapView mapView;
	MapController mapController;
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		setContentView(R.layout.view_spot_map);
		  
		  Bundle bundle = this.getIntent().getBundleExtra("location");
		  double lat = bundle.getDouble("lat");
		  double lon = bundle.getDouble("lon");
		  String name = bundle.getString("name");
		  String address = bundle.getString("address");

		  
		  System.out.println(lat + " " + lon);

		  
		  mapView = (MapView)findViewById(R.id.mapView); 
		  mapView.setBuiltInZoomControls(true);
		  
		  
		  List<Overlay> mapOverlays = mapView.getOverlays();
		  Drawable drawable = this.getResources().getDrawable(R.drawable.pin);
		  MyItemizedOverlay itemizedoverlay = new MyItemizedOverlay(drawable);
		  
		  GeoPoint point = new GeoPoint((int)(lat*1e6),(int)(lon*1e6));
		  OverlayItem overlayitem = new OverlayItem(point, name, address);
		  
		  itemizedoverlay.addOverlay(overlayitem);
		  mapOverlays.add(itemizedoverlay);
		  
		  mapController = mapView.getController();
		  mapController.setZoom(17);
		  mapController.setCenter(point);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.map_menu, menu);
	    return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.map_menu_satellite:
	    	mapView.setSatellite(true);
	        return true;
	        
	    case R.id.map_menu_street:
	    	mapView.setSatellite(false);
	        return true;
	        
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	
	class MyItemizedOverlay extends ItemizedOverlay<OverlayItem>{
		
		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
		
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


		@Override
		public int size() {
			return mOverlays.size();
		}
		
		
		@Override
		protected boolean onTap(int index) {
		  OverlayItem item = mOverlays.get(index);
          Toast.makeText(ViewSpotMap.this, item.getTitle() + "\n" + item.getSnippet(), Toast.LENGTH_SHORT).show();
		  return true;
		}
		
	}

}
