package dspot.client;


import java.util.ArrayList;

import com.facebook.android.DialogError;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

import dspot.utils.Sport;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class SearchTab  extends ListActivity{

	private Api api;
	
	MyListAdapter mAdapter;	
	
	ProgressDialog dialog;
	
	static final String[] Options = new String[] {"Search Near Me", "Search By Location", "Last Search", "Sports", "Radius"};

	
	private AlertDialog sportsDialog;
	View sportsDialogLayout;
	MySportsListAdapter sportsDialogAdapter;
	
    
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		api = ((Api)getApplicationContext());
		

		
		mAdapter = new MyListAdapter();
		setListAdapter(mAdapter);
	    	
		
		
		buildSportsDialog();
    }
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		if (Options[position].equals("Search Near Me"))
			searchNearMe();		    	
		else if (Options[position].equals("Search By Location"))
			searchByLocation();
		else if (Options[position].equals("Last Search"))
			lastSearch();
		else if (Options[position].equals("Sports"))
			showSports();
		else
			return;
	}
	
	
	public void searchByLocation(){
		Intent intent = new Intent(getApplicationContext(),SearchByLocation.class);
        startActivity(intent);
	}
	
	public void buildSportsDialog() {
		
		AlertDialog.Builder builder;
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		sportsDialogLayout = inflater.inflate(R.layout.search_tab_sports_dialog,
		                               (ViewGroup) findViewById(R.id.layout_root3));

		ListView lv = (ListView) sportsDialogLayout.findViewById(R.id.listView1);
		
		sportsDialogAdapter = new MySportsListAdapter();
		lv.setAdapter(sportsDialogAdapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				sportsDialogAdapter.setChecked(position);
				sportsDialogAdapter.notifyDataSetChanged();
				System.out.println("tlalalala");
				
			}
		});
		
		builder = new AlertDialog.Builder(this);
		builder.setView(sportsDialogLayout);
		builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();			
			}	
		});
		
		sportsDialog = builder.create();
		sportsDialog.setTitle("Choose sports to filter");
		//sportsDialog.show();
		
	}
	
	public void showSports(){
		sportsDialog.show();
	}
	
	
	public void lastSearch() {
		Toast toast = Toast.makeText(getApplicationContext(), "Last Search", Toast.LENGTH_SHORT);
		toast.show();
	}
	
	
	public void searchBySports() {
		Toast toast = Toast.makeText(getApplicationContext(), "Search By Sports", Toast.LENGTH_SHORT);
		toast.show();
	}
	
	
	public void searchNearMe(){
		Toast toast = Toast.makeText(getApplicationContext(), "Search Near Me", Toast.LENGTH_SHORT);
		toast.show();
	}

	
	
	@Override
	public void onBackPressed() {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        alertbox.setMessage("Do you want to quit the application?");
        alertbox.setPositiveButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Do nothing
            }
        });
        
        alertbox.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {            	            	
            	if (Api.user.isConnected()) {
            		if (api.logout())
            			finish();
            		else {
            			Toast toast = Toast.makeText(getApplicationContext(), "Logout failed, try again", Toast.LENGTH_SHORT);
            			toast.show();
            		}           			
            	}
            	else
            		finish();
            }       
        });
        
        alertbox.show();        
	}
	
	
	
	public void inviteFriendsFacebook(){
		api.mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = api.mPrefs.getString("access_token", null);
        long expires = api.mPrefs.getLong("access_expires", 0);
        if(access_token != null) {
            api.facebook.setAccessToken(access_token);
        }
        if(expires != 0) {
            api.facebook.setAccessExpires(expires);
        }
        
        /*
         * Only call authorize if the access_token has expired.
         */
        if(!api.facebook.isSessionValid()) {

            api.facebook.authorize(this, new String[] {"email","offline_access", "publish_checkins"}, new DialogListener() {
                @Override
                public void onComplete(Bundle values) {
                    SharedPreferences.Editor editor = api.mPrefs.edit();
                    editor.putString("access_token", api.facebook.getAccessToken());
                    editor.putLong("access_expires", api.facebook.getAccessExpires());
                    editor.commit();
                }
    
                @Override
                public void onFacebookError(FacebookError error) {}
    
                @Override
                public void onError(DialogError e) {}
    
                @Override
                public void onCancel() {}
            });
        }else{
        	        	
        	 //Send requests with no friend pre-selected and user
            //selects friends on the dialog screen.
        	Bundle params = new Bundle();
    	    params.putString("message", "Want to join DSpot?");		
            
        	api.facebook.dialog(this,"apprequests", params ,new AppRequestsListener());
    		
        }
	}
	
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        api.facebook.authorizeCallback(requestCode, resultCode, data);
        //a data nao traz nada de especial, so o access token...
      
        //Send requests with no friend pre-selected and user
        //selects friends on the dialog screen.
        
        Bundle params = new Bundle();
	    params.putString("message", "Want to join DSpot?");		
    	api.facebook.dialog(this,"apprequests", params ,new AppRequestsListener());

	  	    
    }

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.new_spot_menu, menu);
	    return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.spot_menu_new:
	    	Intent intent = new Intent(getApplicationContext(),NewSpot.class);
	        startActivity(intent);
	        return true;
	    
	    case R.id.spot_menu_refresh:
	    	Toast toast = Toast.makeText(getApplicationContext(), "Refresh to be done in a near future", Toast.LENGTH_SHORT);
    		toast.show();
	        return true;
	        
	    case R.id.spot_menu_invite:
	    	inviteFriendsFacebook();
	    	return true;
	        
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	
	
	public class AppRequestsListener extends BaseDialogListener {
        public void onComplete(Bundle values) {
            Toast toast = Toast.makeText(getApplicationContext(), "App request sent", Toast.LENGTH_SHORT);
            toast.show();
        }
        
        public void onFacebookError(FacebookError error) {
            Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
	    }
        
        public void onCancel() {
            Toast toast = Toast.makeText(getApplicationContext(), "App request cancelled", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
	
	
	public class MyListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return Options.length;
		}

		@Override
		public Object getItem(int arg0) {
			return Options[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {    
			 if(position == 4){
            	 LayoutInflater infalInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        		 convertView = infalInflater.inflate(R.layout.search_tab_child_seek, null);

				 ((TextView) convertView.findViewById(R.id.search_tab_child_seek_text)).setText(Options[position]);		
				 SeekBar sb = (SeekBar)convertView.findViewById(R.id.search_tab_child_seek_seekBar);
				 sb.setMax(100);
				 sb.setProgress(5);
				 sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						Toast toast = Toast.makeText(getApplicationContext(), "SeekBar -> " + seekBar.getProgress(), Toast.LENGTH_SHORT);
			    		toast.show();
			    		api.radious =  seekBar.getProgress();
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						//nao faz nada
						return;
						
					}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						//nao faz nada
						return;
						
					}
				});
			 }else{
	             LayoutInflater infalInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				 convertView = infalInflater.inflate(R.layout.search_tab_child_normal, null);
				 ((TextView) convertView.findViewById(R.id.search_tab_child_normal_text)).setText(Options[position]); 
				 
			 }
			 
			return convertView;
		}
	}
	
	
	
	 private class MySportsListAdapter extends BaseAdapter {


			private ArrayList<Sport> sports;
	    	
	    	public MySportsListAdapter() {
	    		sports = api.getSports();
			}

			@Override
			public int getCount() {
				return sports.size();
			}

			@Override
			public Object getItem(int arg0) {
				return sports.get(arg0);
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
	        	LayoutInflater infalInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        	convertView = infalInflater.inflate(R.layout.checklist_child, null);
	            
	            TextView name = (TextView) convertView.findViewById(R.id.checklist_child_name);
	            CheckBox check = (CheckBox) convertView.findViewById(R.id.checklist_child_check);

	            name.setText(sports.get(position).getName());
	            check.setChecked(sports.get(position).isChecked());

			
			return convertView;
			}
			
			
			public void setChecked(int position){
				if(sports.get(position).isChecked()){
					sports.get(position).setChecked(false);
					api.updateSportsCheck(sports.get(position).getId(), 0);
					
				}else{
					sports.get(position).setChecked(true);
					api.updateSportsCheck(sports.get(position).getId(), 1);
				}

			}
			
			
			@Override
			public void notifyDataSetChanged() {
				super.notifyDataSetChanged();
			}
	    	
	    }

}
