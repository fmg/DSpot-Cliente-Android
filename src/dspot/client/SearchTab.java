package dspot.client;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.Toast;

public class SearchTab  extends ListActivity{
	
	Api api;
	
	 static final String[] Options = new String[] {"Search Near Me", "Search By Location", "Last Search", "Search By Sports", ""};
    
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		api = ((Api)getApplicationContext());
		
		setContentView(R.layout.tab_search);
		
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Options));
	    	  
    }
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
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

}
