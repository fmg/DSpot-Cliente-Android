package dspot.client;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class Inicial extends TabActivity{

	Api api;
	TabHost tabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		api = ((Api)getApplicationContext());
		
		setContentView(R.layout.tabs);
		
		tabHost = getTabHost();

	    
	    if (api.user.isConnected()){
	    	
	    	this.addTab(tabHost,  SearchTab.class, "Search",R.drawable.ic_tab_search_selected);  	
	    	this.addTab(tabHost,  MapTab.class, "Map",R.drawable.ic_tab_map_selected);
	    	this.addTab(tabHost,  FavouritesTab.class, "Starred",R.drawable.ic_tab_favourites_selected);
	    	this.addTab(tabHost,  MeTab.class, "Me",R.drawable.ic_tab_me_selected);

	    } 
	    
	    // se utilizador for guest
	    else {
	    	
	    	
	    	this.addTab(tabHost,  SearchTab.class, "Search",R.drawable.ic_tab_search_selected);  	
	    	this.addTab(tabHost,  MapTab.class, "Map",R.drawable.ic_tab_map_selected);
 
	    }

	    // tab predefinida e a primeira (search)
	    tabHost.setCurrentTab(0);
		
	}

    
	private static View createTabView(final Context context, final String text, Drawable dIcon) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		
		ImageView icon= (ImageView) view.findViewById(R.id.icon);
		icon.setImageDrawable(dIcon);
		
		return view;
	}
	
	private  void addTab(TabHost tabHost,Class<?> destClass, final String text, int icon){
		
		Intent intent = new Intent().setClass(this, destClass);
		View tabview = createTabView(tabHost.getContext(), text, this.getResources().getDrawable(icon));
		
		//tabview.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.listwhite));
		
		TabHost.TabSpec spec=tabHost.newTabSpec(text).setIndicator(tabview).setContent(intent);
		
		tabHost.addTab(spec);
		
		
	}

}
