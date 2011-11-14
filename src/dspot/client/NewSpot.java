package dspot.client;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class NewSpot extends Activity{
	
	private Api api;
	private static final int CAMERA_PIC_REQUEST = 1337;  

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		api = ((Api)getApplicationContext());
		
		setContentView(R.layout.new_spot);
	}
	
	
	
	public void nada(){
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);  

	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	    if (requestCode == CAMERA_PIC_REQUEST) {  
	        // do something  
	    	Bitmap thumbnail = (Bitmap) data.getExtras().get("data");  
	    	
	    	//ImageView image = (ImageView) findViewById(R.id.photoResultView);  
	    	//image.setImageBitmap(thumbnail);

	    }  
	}  
	
	

}
