package dspot.client;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class NewSpot extends Activity{
	
	private Api api;
	private static final int CAMERA_PIC_REQUEST = 1337;
	AlertDialog alert;  

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
	}
		
	public void setSports(){
		final CharSequence[] items = {"Red", "Green", "Blue"};
		boolean[] checkeditems = {false, false, false};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick a color");
		builder.setNeutralButton("ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alert.dismiss();
				
			}
		});
		builder.setMultiChoiceItems(items,checkeditems, new DialogInterface.OnMultiChoiceClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which,
					boolean isChecked) {
				Toast.makeText(getApplicationContext(), items[which], Toast.LENGTH_SHORT).show();
			}
		});
		alert = builder.create();
		alert.show();
	}
	
	
	
	
	public void takePicture(){
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);  

	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	    if (requestCode == CAMERA_PIC_REQUEST) {  
	        // do something  
	    	if(data.hasExtra("data")){
		    	Bitmap thumbnail = (Bitmap) data.getExtras().get("data");  
		    	
		    	ImageView image = (ImageView) findViewById(R.id.new_spot_picture);  
		    	image.setImageBitmap(thumbnail);
		    	
		    	
		    	Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
		    	List<Address> addresses;
				try {
					addresses = gcd.getFromLocation(41.173396, -8.591801, 1);
					if (addresses.size() > 0) 
			    	    System.out.println(addresses.get(0).getLocality());
						EditText t = (EditText)findViewById(R.id.new_spot_locationEdit);
						t.setText(addresses.get(0).getLocality());
						
						EditText t2 = (EditText)findViewById(R.id.new_spot_addressEdit);
						t2.setText(addresses.get(0).getAddressLine(0).toString());
						
						
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
		    	

	    	}
	    }  
	}  
	
	

}
