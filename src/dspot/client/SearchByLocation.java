package dspot.client;


import android.app.ListActivity;
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
	ArrayAdapter<String> adapter;
	private EditText filterText = null;
	String[] locations = new String[]{"Vila Real", "Porto", "Amanarante", "Alijo"};
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		api = ((Api)getApplicationContext());
		
		
		//TODO: falta receber os desportos
		
		setContentView(R.layout.search_by_location);
		
		filterText = (EditText) findViewById(R.id.search_by_location_editText);
		filterText.addTextChangedListener(filterTextWatcher);
        
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locations);
        setListAdapter(adapter);
		
		
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Toast toast = Toast.makeText(getApplicationContext(), locations[position], Toast.LENGTH_SHORT);
		toast.show();
		
		Intent intent = new Intent(getApplicationContext(), ViewSpotList.class);
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
}
