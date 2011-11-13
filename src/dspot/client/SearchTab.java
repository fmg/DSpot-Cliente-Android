package dspot.client;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
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
	
	static final String[] Options = new String[] {"Search Near Me", "Search By Location", "Last Search"};
	
	
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
			public void unregisterDataSetObserver(DataSetObserver observer) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void registerDataSetObserver(DataSetObserver observer) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGroupExpanded(int groupPosition) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGroupCollapsed(int groupPosition) {
				// TODO Auto-generated method stub
				
			}
			
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
	            textView.setTextSize(25);
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
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public long getCombinedChildId(long groupId, long childId) {
				// TODO Auto-generated method stub
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
	            textView.setPadding(36, 0, 0, 0);
	            return textView;
	        }
			
			
			@Override
			public View getChildView(int groupPosition, int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent) {
				TextView textView = getGenericView();
	            textView.setText(getChild(groupPosition, childPosition).toString());
	            textView.setTextSize(15);
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
				// TODO Auto-generated method stub
				return false;
			}
		});
	    
	    lv.setOnItemClickListener(new ListView.OnItemClickListener() {
			
	    	@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
	    		Toast toast = Toast.makeText(getApplicationContext(), "AKI", Toast.LENGTH_SHORT);
	    		toast.show();
				
			}
	    });
	    
	    
	    ((SlidingDrawer)findViewById(R.id.slidingDrawer1)).setOnDrawerOpenListener(this);
	    ((SlidingDrawer)findViewById(R.id.slidingDrawer1)).setOnDrawerCloseListener(this);
	    
	    
	    
	    Toast toast = Toast.makeText(getApplicationContext(), "Click the icon below, to costumize the search", Toast.LENGTH_LONG);
	    toast.show();
    }
	
	
	public void searchByLocation(){
		Toast toast = Toast.makeText(getApplicationContext(), "Search By Location", Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public void lastSearch(){
		Toast toast = Toast.makeText(getApplicationContext(), "Last Search", Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public void searchBySports(){
		Toast toast = Toast.makeText(getApplicationContext(), "Search By Sports", Toast.LENGTH_SHORT);
		toast.show();
	}
	
	
	public void searchNearMe(){
		Toast toast = Toast.makeText(getApplicationContext(), "Search Near Me", Toast.LENGTH_SHORT);
		toast.show();
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


	@Override
	public void onDrawerClosed() {
		// TODO Auto-generated method stub
		lv.setVisibility(ListView.VISIBLE);
		((ImageView)findViewById(R.id.handle)).setImageResource(R.drawable.expander_up);
		
	}


	@Override
	public void onDrawerOpened() {
		// TODO Auto-generated method stub
		lv.setVisibility(ListView.GONE);
		((ImageView)findViewById(R.id.handle)).setImageResource(R.drawable.expander_down);

	}

}
