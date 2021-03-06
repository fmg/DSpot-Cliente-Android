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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class SearchByName extends ListActivity implements Runnable{

	Api api;
	
	ProgressDialog dialog;
	String keyword;

	
	MyListAdapter mAdapter;
	ArrayList<SpotShortInfo> spotList;
	ArrayList<Integer> spotList_ids;
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		api = ((Api)getApplicationContext());

		setContentView(R.layout.search_by_name);
		
		mAdapter = new MyListAdapter();
		spotList = new ArrayList<SpotShortInfo>();
		spotList_ids = new ArrayList<Integer>();

        setListAdapter(mAdapter);
		
        ((Button)findViewById(R.id.search_by_name_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				keyword = ((EditText)findViewById(R.id.search_by_name_editText)).getText().toString();
				submitSearch();
			}
		});
		
	}
	
	
	public void submitSearch(){
		dialog = ProgressDialog.show(SearchByName.this, "", "Obtaining Spot list. Please wait...", true);
 		Thread thread = new Thread(this);
        thread.start();
	}
	
	
	@Override
	public void run() {
		try {
			
			String tmp = keyword.replace(" ", "%20");
			
			
			spotList = api.getSpotsByName(tmp);
			for(SpotShortInfo ssi: spotList){
				spotList_ids.add(ssi.getId());
			}
			
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
		intent.putIntegerArrayListExtra("spotList_ids", spotList_ids);
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
		
	}
}
