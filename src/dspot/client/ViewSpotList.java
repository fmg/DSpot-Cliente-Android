package dspot.client;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	@Override
	public void run() {
		//TODO: obter coisas da bd
		//TODO: preencher o adapter
		
		mAdapter.notifyDataSetChanged();
		
	}
	
}
