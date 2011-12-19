package dspot.client;

import java.util.ArrayList;

import dspot.utils.SpotShortInfo;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class FavouritesTab extends ListActivity {

	Api api;
	MyListAdapter mAdapter;
	ArrayList<SpotShortInfo> favouritesList;
    
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		api = ((Api)getApplicationContext());
		
		
		favouritesList = api.getFavourites();
		
		mAdapter = new MyListAdapter();
		
		
		setListAdapter(mAdapter);
		
		
		if(favouritesList.size() == 0){
			 Toast.makeText(getApplicationContext(), "You don't have any favourites. Try adding one", Toast.LENGTH_SHORT).show();
		}

    }
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent intent = new Intent(getApplicationContext(), ViewSpot.class);
		intent.putExtra("id", favouritesList.get(position).getId());
		intent.putExtra("index", position);
		intent.putIntegerArrayListExtra("spotList_ids", mAdapter.getidsList());
        startActivity(intent);
		
	}


	@Override
	protected void onResume() {
		super.onResume();
		
		favouritesList = api.getFavourites();
		mAdapter.notifyDataSetChanged();
		
		if(favouritesList.size() == 0){
			 Toast.makeText(getApplicationContext(), "You don't have any favourites. Try adding one", Toast.LENGTH_SHORT).show();
		}
		
	}


	
	
	public class MyListAdapter extends BaseAdapter {
		
		
		public ArrayList<Integer> getidsList(){
			ArrayList<Integer> ret = new ArrayList<Integer>();
			for(SpotShortInfo s:favouritesList)
				ret.add(s.getId());
			
			return ret;
		}
		
		@Override
		public int getCount() {
			return favouritesList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return favouritesList.get(arg0);
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


	            name.setText(favouritesList.get(position).getName());
	            address.setText(favouritesList.get(position).getAddress());
	            rating.setRating(favouritesList.get(position).getRating());


			
			return convertView;
		}
		
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
	
}
