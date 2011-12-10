package dspot.client;

import java.util.ArrayList;

import dspot.client.ViewSpotList.MyListAdapter;
import dspot.utils.MyLocation;
import dspot.utils.SpotShortInfo;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchByName extends ListActivity {

	Api api;
	
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
				submitSearch();
			}
		});
		
	}
	
	
	public void submitSearch(){
		Toast toast = Toast.makeText(getApplicationContext(), "To be done", Toast.LENGTH_SHORT);
		toast.show();
	}
	
	
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

	            name.setText(spotList.get(position).getName());
	            address.setText(spotList.get(position).getAddress());

			
			return convertView;
		}
		
	}
}
