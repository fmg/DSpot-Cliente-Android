package dspot.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

public class SearchTab  extends Activity implements OnDrawerOpenListener, OnDrawerCloseListener{
	
	private Api api;
	private ListView lv = null;
	private ExpandableListView elv = null;
	AlertDialog alert;
	
	static final String[] Options = new String[] {"Search Near Me", "Search By Location", "Last Search", "Sports"};
	
	
	private String[] groups = { "Sports" };
    private String[][] children = {{"BasketBall", "Joggin", "Running", "Football"}};

	static final String[] Sports = new String[] {"BasketBall", "Joggin", "Running", "Football"};
    
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		api = ((Api)getApplicationContext());
		
		setContentView(R.layout.tab_search);
		
		lv = (ListView)findViewById(R.id.listView1);
		elv = (ExpandableListView)findViewById(R.id.expandableListView1);
		
		lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Options));
	    elv.setAdapter(new ExpandableListAdapter() {
			
			@Override
			public void unregisterDataSetObserver(DataSetObserver observer) {}
			
			@Override
			public void registerDataSetObserver(DataSetObserver observer) {}
			
			@Override
			public void onGroupExpanded(int groupPosition) {}
			
			@Override
			public void onGroupCollapsed(int groupPosition) {}
			
			@Override
			public boolean isEmpty() {
				return false;
			}
			
			@Override
			public boolean isChildSelectable(int groupPosition, int childPosition) {
				return true;
			}
			
			@Override
			public boolean hasStableIds() {
				return true;
			}
			
			@Override
			public View getGroupView(int groupPosition, boolean isExpanded,
					View convertView, ViewGroup parent) {
				TextView textView = getGenericView();
	            textView.setText(getGroup(groupPosition).toString());
	            //textView.setTextSize(25);
	            return textView;
			}
			
			@Override
			public long getGroupId(int groupPosition) {
				return groupPosition;
			}
			
			@Override
			public int getGroupCount() {
				return groups.length;
			}
			
			@Override
			public Object getGroup(int groupPosition) {
				return groups[groupPosition];
			}
			
			@Override
			public long getCombinedGroupId(long groupId) {
				return 0;
			}
			
			@Override
			public long getCombinedChildId(long groupId, long childId) {
				return 0;
			}
			
			@Override
			public int getChildrenCount(int groupPosition) {
				return children[groupPosition].length;
			}
			
			
			public TextView getGenericView() {
	            // Layout parameters for the ExpandableListView
	            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
	                    ViewGroup.LayoutParams.MATCH_PARENT, 64);

	            TextView textView = new TextView(SearchTab.this);
	            textView.setLayoutParams(lp);
	            // Center the text vertically
	            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
	            // Set the text starting position
	            textView.setPadding(40, 0, 0, 0);
	            return textView;
	        }
			
			
			@Override
			public View getChildView(int groupPosition, int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent) {
				TextView textView = getGenericView();
	            textView.setText(getChild(groupPosition, childPosition).toString());
	            //textView.setTextSize(15);
	            textView.setPadding(60, 0,0,0);
	            return textView;
			}
			
			@Override
			public long getChildId(int groupPosition, int childPosition) {
				return childPosition;
			}
			
			@Override
			public Object getChild(int groupPosition, int childPosition) {
				return children[groupPosition][childPosition];
			}
			
			@Override
			public boolean areAllItemsEnabled() {
				return false;
			}
		});
	    
	    lv.setOnItemClickListener(new ListView.OnItemClickListener() {
			
	    	@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {	    		
	    		
	    		if (Options[arg2].equals("Search Near Me"))
	    			searchNearMe();		    	
    			else if (Options[arg2].equals("Search By Location"))
	    			searchByLocation();
	    		else if (Options[arg2].equals("Last Search"))
	    			lastSearch();
	    		else if (Options[arg2].equals("Sports"))
	    			sports();
//	    			System.out.println("Desportos clicked");
			}
	    });
	    	    	   
	    ((SlidingDrawer)findViewById(R.id.slidingDrawer1)).setOnDrawerOpenListener(this);
	    ((SlidingDrawer)findViewById(R.id.slidingDrawer1)).setOnDrawerCloseListener(this);
	    	    
	    Toast toast = Toast.makeText(getApplicationContext(), "Click the icon below, to costumize the search", Toast.LENGTH_LONG);
	    toast.show();
    }
	
	
	public void searchByLocation(){
		Intent intent = new Intent(getApplicationContext(),SearchByLocation.class);
        startActivity(intent);
	}
	
	public void sports() {
//		final CharSequence[] items = {"Soccer", "Basketball", "Running"};
		boolean[] checkeditems = {false, false, false};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Sports");
		builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alert.dismiss();			
				}
			});
		
		builder.setMultiChoiceItems(Api.sports, checkeditems, new DialogInterface.OnMultiChoiceClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which,
					boolean isChecked) {
				Toast.makeText(getApplicationContext(), Api.sports[which], Toast.LENGTH_SHORT).show();
			}
		});
		alert = builder.create();
		alert.show();
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
            	if (Api.guestMode == true) {
            		System.out.println("guest mode is true");
            		finish();            			
            	}
            	else if (Api.guestMode == false) {
            		System.out.println("guest mode is false");
            		if (api.logout())
            			finish();
            		else {
            			Toast toast = Toast.makeText(getApplicationContext(), "Logout failed", Toast.LENGTH_SHORT);
            			toast.show();
            		}
            	}
            }       
        });
        
        alertbox.show();        
	}


	@Override
	public void onDrawerClosed() {
		lv.setVisibility(ListView.VISIBLE);
		((ImageView)findViewById(R.id.handle)).setImageResource(R.drawable.expander_up);
		
	}


	@Override
	public void onDrawerOpened() {
		lv.setVisibility(ListView.GONE);
		((ImageView)findViewById(R.id.handle)).setImageResource(R.drawable.expander_down);

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
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	
	

}
