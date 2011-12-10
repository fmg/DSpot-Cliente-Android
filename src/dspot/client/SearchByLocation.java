package dspot.client;


import java.util.ArrayList;

import dspot.utils.MyLocation;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SearchByLocation extends ListActivity {

	Api api;
	MyListAdapter adapter;
	private EditText filterText = null;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		api = ((Api)getApplicationContext());
		
		
		
		setContentView(R.layout.search_by_location);
		
		adapter = new MyListAdapter(this,  android.R.layout.simple_list_item_1, api.getLocations());
		
		filterText = (EditText) findViewById(R.id.search_by_location_editText);
		filterText.addTextChangedListener(filterTextWatcher);
        
        setListAdapter(adapter);
		
        
        
		
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Toast toast = Toast.makeText(getApplicationContext(), ((MyLocation)adapter.getItem(position)).getName() + " -> " + ((MyLocation)adapter.getItem(position)).getId(), Toast.LENGTH_SHORT);
		toast.show();
		
		Intent intent = new Intent(getApplicationContext(), ViewSpotList.class);
		intent.putExtra("id", ((MyLocation)adapter.getItem(position)).getId());
        startActivity(intent);
        finish();
	}
	
	
	private TextWatcher filterTextWatcher = new TextWatcher() {

	    public void afterTextChanged(Editable s) {
	    }

	    public void beforeTextChanged(CharSequence s, int start, int count,
	            int after) {
	    }

	    public void onTextChanged(CharSequence s, int start, int before,
	            int count) {
	        adapter.getFilter().filter(s);
	    }

	};
	
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    filterText.removeTextChangedListener(filterTextWatcher);
	}
	
	
	
	private class MyListAdapter extends ArrayAdapter<MyLocation>{

		private ArrayList<MyLocation> locations;
		
		public MyListAdapter(Context context, int textViewResourceId, ArrayList<MyLocation> items) {
			super(context, textViewResourceId, items);
            this.locations = items;
		}
	}
}
