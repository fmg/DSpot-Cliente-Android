package dspot.client;


import android.app.ListActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;

public class SearchByLocation extends ListActivity {
	
	Api api;
	ArrayAdapter<String> adapter;
	private EditText filterText = null;
	String[] locations = new String[]{"Vila Real", "Porto", "Amanarante", "Alijo"};
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		api = ((Api)getApplicationContext());
		
		setContentView(R.layout.search_by_location);
		
		filterText = (EditText) findViewById(R.id.search_by_location_editText);
		filterText.addTextChangedListener(filterTextWatcher);
        
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locations);
        setListAdapter(adapter);
		
		
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
