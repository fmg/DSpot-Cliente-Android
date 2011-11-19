package dspot.client;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;

public class Inicial extends TabActivity{

	Api api;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		api = ((Api)getApplicationContext());
		
		Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    
	    if (api.user.isConnected()){
		    		    
		    // Do the same for the other tabs
		    intent = new Intent().setClass(this, SearchTab.class);
		    spec = tabHost.newTabSpec("seach").setIndicator("Search",res.getDrawable(R.layout.ic_tab_search)).setContent(intent);
		    tabHost.addTab(spec);
		    
		    
		    intent = new Intent().setClass(this, MapTab.class);
		    spec = tabHost.newTabSpec("map").setIndicator("Map",res.getDrawable(R.layout.ic_tab_map)).setContent(intent);
		    tabHost.addTab(spec);
		    
		    
		    // Create an Intent to launch an Activity for the tab (to be reused)
		    intent = new Intent().setClass(this, FavouritesTab.class);
		    // Initialize a TabSpec for each tab and add it to the TabHost
		    spec = tabHost.newTabSpec("favourites").setIndicator("Favourites",res.getDrawable(R.layout.ic_tab_favourites)).setContent(intent);
		    tabHost.addTab(spec);
		    
		    
		    // Create an Intent to launch an Activity for the tab (to be reused)
		    intent = new Intent().setClass(this, MeTab.class);
		    // Initialize a TabSpec for each tab and add it to the TabHost
		    spec = tabHost.newTabSpec("me").setIndicator("Me",res.getDrawable(R.layout.ic_tab_me)).setContent(intent);
		    tabHost.addTab(spec);
		    
	    } 
	    
	    // se utilizador é guest
	    else {
	    	
	    	// cria Search tab
		    intent = new Intent().setClass(this, SearchTab.class);
		    spec = tabHost.newTabSpec("seach").setIndicator("Search",res.getDrawable(R.layout.ic_tab_search)).setContent(intent);
		    tabHost.addTab(spec);
		    
		    // cria Map tab
		    intent = new Intent().setClass(this, MapTab.class);
		    spec = tabHost.newTabSpec("map").setIndicator("Map",res.getDrawable(R.layout.ic_tab_map)).setContent(intent);
		    tabHost.addTab(spec);
		    
	    }

	    // tab pré definida é a primeira (search)
	    tabHost.setCurrentTab(0);
		
	}

}
