package dspot.client;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import dspot.utils.SpotShortInfo;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class SearchNearMe extends ListActivity implements Runnable{
	
	Api api;
	ProgressDialog dialog;
	
	MyListAdapter mAdapter;

	ArrayList<SpotShortInfo> spotList;
	
	double radius;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		api = ((Api)getApplicationContext());

		setContentView(R.layout.search_near_me);
		
		mAdapter = new MyListAdapter();
		spotList = new ArrayList<SpotShortInfo>();

        setListAdapter(mAdapter);
		
        ((Button)findViewById(R.id.search_near_me_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				submitSearch();
			}
		});
        
        
        ((SeekBar)findViewById(R.id.search_near_me_seekbar)).setMax(10);
        ((SeekBar)findViewById(R.id.search_near_me_seekbar)).setProgress(1);



        
        ((SeekBar)findViewById(R.id.search_near_me_seekbar)).setOnSeekBarChangeListener( new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				radius = arg0.getProgress();
				if(radius == 0){
					arg0.setProgress(1);
					radius=1.0;
				}
					
				Toast.makeText(getApplicationContext(), radius+ " KM", Toast.LENGTH_SHORT).show(); 
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {				
			}
		});  
		
	}
	
	
	public void submitSearch(){
		dialog = ProgressDialog.show(SearchNearMe.this, "", "Obtaining Spot list. Please wait...", true);
 		Thread thread = new Thread(this);
        thread.start();
	}
	
	
	
	
	@Override
	public void run() {
		/*
		try {
			
			
			
			//TODO:
			//spotList = api.getSpotsByName(tmp);
			
			
			handler.sendMessage(handler.obtainMessage());
			
			
		
		} catch (ClientProtocolException e) {
			
			dialog.dismiss();
			
			Looper.prepare();
			Toast toast = Toast.makeText(getApplicationContext(), "Error connecting to the server, try again...", Toast.LENGTH_SHORT);
    		toast.show();
    		
    		e.printStackTrace();
    		finish();
    		
    		Looper.loop();
    		
    		
    		
		} catch (JSONException e) {
			
			dialog.dismiss();

			Looper.prepare();
			Toast toast = Toast.makeText(getApplicationContext(), "Error parsing information from server, try again...", Toast.LENGTH_SHORT);
    		toast.show();
    		
    		e.printStackTrace();
			finish();
    		Looper.loop();
			
			
			
		} catch (IOException e) {
			
			dialog.dismiss();

			Looper.prepare();
			Toast toast = Toast.makeText(getApplicationContext(), "Error sending information to server, try again...", Toast.LENGTH_SHORT);
    		toast.show();
    		
    		e.printStackTrace();
			finish();	
    		Looper.loop();
			
			
		}
*/
	}
	
	
	
	final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
        	
        	mAdapter.notifyDataSetChanged();
            dialog.dismiss();
            
            if(spotList.size() == 0){
            	Toast toast = Toast.makeText(getApplicationContext(), "No spots in your area, sry... If you find a good spot to practice any sport, feel free to add", Toast.LENGTH_LONG);
        		toast.show();	
            }
        }
    };
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent intent = new Intent(getApplicationContext(), ViewSpot.class);
		intent.putExtra("id", spotList.get(position).getId());
		intent.putExtra("index", position);
		intent.putIntegerArrayListExtra("spotList_ids", mAdapter.getIdsList());
        startActivity(intent);
	}
	

	
	
	public class MyListAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return spotList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return spotList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			 if (convertView == null) {
	                
	            	LayoutInflater infalInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	                convertView = infalInflater.inflate(R.layout.spot_list_child, null);
	            }
	            TextView name = (TextView) convertView.findViewById(R.id.spotList_name_text);
	            TextView address = (TextView) convertView.findViewById(R.id.spotList_address_text);
	            RatingBar rating = (RatingBar) convertView.findViewById(R.id.spotList_ratingBar);


	            name.setText(spotList.get(position).getName());
	            address.setText(spotList.get(position).getAddress());
	            rating.setRating(spotList.get(position).getRating());


			
			return convertView;
		}
		
		
		
		ArrayList<Integer> getIdsList(){
			ArrayList<Integer> ret = new ArrayList<Integer>();
			
			for(SpotShortInfo s:spotList)
				ret.add(s.getId());
			
			return ret;
		}
		
	}

}
