package dspot.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import dspot.utils.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MeTab extends Activity implements Runnable{
	
	Api api;
	User u;
	MyListAdapter adapter;
	
	
	Bitmap mIcon_val;
	
	ProgressDialog dialog;
	private boolean firstStart = true;
    
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		api = ((Api)getApplicationContext());
		
		setContentView(R.layout.tab_me);
		
		u = api.getUserInfo();
		
		((TextView)findViewById(R.id.tab_me_email)).setText(u.getEmail());
		((TextView)findViewById(R.id.tab_me_name)).setText(u.getName());
		((TextView)findViewById(R.id.tab_me_username)).setText(u.getUsername());
		
		
		adapter = new MyListAdapter(this,  android.R.layout.simple_list_item_1, api.getFriends());
		((ListView)findViewById(R.id.tab_me_friendsList)).setAdapter(adapter);
		

		
    }
	
	
	@Override
	public void onBackPressed() {
		
		// prepare the alert box
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);

        // set the message to display
        alertbox.setMessage("Do you want to quit the application?");

        // set a positive/yes button and create a listener
        alertbox.setPositiveButton("No", new DialogInterface.OnClickListener() {

            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                //Do nothing
            }
        });

        // set a negative/no button and create a listener
        alertbox.setNegativeButton("Yes", new DialogInterface.OnClickListener() {

            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
   	
            	if(api.logout()){       	
            		finish();
            	}else{
            		Toast toast = Toast.makeText(getApplicationContext(), "Logout failed", Toast.LENGTH_SHORT);
            		toast.show();
            	}
 
            }
        });

        // display box
        alertbox.show();

	}
	
	
	
	private class MyListAdapter extends ArrayAdapter<User>{

		private ArrayList<User> friends;
		
		public MyListAdapter(Context context, int textViewResourceId, ArrayList<User> items) {
			super(context, textViewResourceId, items);
            this.friends = items;
		}
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(firstStart){		
			firstStart = false;
			
			dialog = ProgressDialog.show(MeTab.this, "", "Working. Please wait...", true);
    		Thread thread = new Thread(this);
            thread.start();
		}
	}



	@Override
	public void run() {
		URL newurl;
		try {
			System.out.println("aki->"+ u.getPhoto());
			newurl = new URL(u.getPhoto());
			mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
			
			//((ImageView)findViewById(R.id.tab_me_image)).setImageBitmap(mIcon_val);
			
		} catch (MalformedURLException e) {
			//((ImageView)findViewById(R.id.tab_me_image)).setImageResource(R.drawable.profile_picture);
			e.printStackTrace();
		} catch (IOException e) {
			//((ImageView)findViewById(R.id.tab_me_image)).setImageResource(R.drawable.profile_picture);
			e.printStackTrace();
		}
		
		Message msg = handler.obtainMessage();
		handler.sendMessage(msg);
		
	}
	
	
	final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
        	
        	
        	if(mIcon_val == null)
        		((ImageView)findViewById(R.id.tab_me_image)).setImageResource(R.drawable.profile_picture);
        	else
        		((ImageView)findViewById(R.id.tab_me_image)).setImageBitmap(mIcon_val);
        		
        	dialog.dismiss();
        }
	};

}
