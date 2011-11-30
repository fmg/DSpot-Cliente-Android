package dspot.client;

import java.util.ArrayList;

import dspot.utils.SpotShortInfo;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewSpotList extends ListActivity implements Runnable{

	Api api;
	ProgressDialog dialog;

	
	MyListAdapter mAdapter;
	ArrayList<SpotShortInfo> spotList;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		api = ((Api)getApplicationContext());
		
		mAdapter = new MyListAdapter();
		spotList = new ArrayList<SpotShortInfo>();
				
		setListAdapter(mAdapter);
		
		
		
		dialog = ProgressDialog.show(ViewSpotList.this, "", "Obtaining Spot list. Please wait...", true);
 		Thread thread = new Thread(this);
        thread.start();
        
        		
	}
	
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Toast toast = Toast.makeText(getApplicationContext(), spotList.get(position).getName(), Toast.LENGTH_SHORT);
		toast.show();
		
		Intent intent = new Intent(getApplicationContext(), ViewSpot.class);
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

	            name.setText(spotList.get(position).getName());
	            address.setText(spotList.get(position).getAddress());

			
			return convertView;
		}
		
	}

	@Override
	public void run() {
		//TODO: obter coisas da bd
				
		SpotShortInfo s1 = new SpotShortInfo("gym 1", "algures numa ruma", 1);
		SpotShortInfo s2 = new SpotShortInfo("gym 2", "algures numa ruma 2", 2);
		spotList.add(s1);
		spotList.add(s2);
		
		mAdapter.notifyDataSetChanged();
		handler.sendMessage(handler.obtainMessage());
		
	}
	
	final Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            dialog.dismiss();
        }
    };
	
}
