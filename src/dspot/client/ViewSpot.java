package dspot.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.facebook.android.DialogError;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

import dspot.utils.Comment;
import dspot.utils.SpotFullInfo;
import dspot.utils.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ViewSpot extends Activity implements Runnable{
	
	Api api;
	ProgressDialog progressDialog;
	
	AlertDialog reportDialog;
	View reportDialogLayout;
	
	AlertDialog inviteDialog;
	View inviteDialogLayout;
	MyCustomAdapter inviteDialogAdapter;
	
	
	ImageAdapter imageAdapter;
	
	
	boolean commentAreaVisible = false;
	ArrayList<Comment> commentList;
	
	
	int spot_id;
	ArrayList<Integer> spotList_ids;
	int index;
	
	
	SpotFullInfo sfi;

	private static final int SWIPE_MIN_DISTANCE = 300;
	private static final int SWIPE_MAX_OFF_PATH = 350;
	private static final int SWIPE_THRESHOLD_VELOCITY = 300;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		api = ((Api)getApplicationContext());
		
		setContentView(R.layout.view_spot);
		
		Bundle extras = getIntent().getExtras();
		spot_id = extras.getInt("id");
		spotList_ids = extras.getIntegerArrayList("spotList_ids");
		index = extras.getInt("index");
		System.out.println(spotList_ids.toString()+ " -> " + index);
		
		
		
		buildInviteDialog();
		buildReportDialog();
		
		Gallery gallery = (Gallery) findViewById(R.id.gallery1);
		imageAdapter = new ImageAdapter(this);
	    gallery.setAdapter(imageAdapter);

	    commentList = new ArrayList<Comment>();
	    
	    ((RelativeLayout)findViewById(R.id.relativeLayout2)).setVisibility(View.GONE);
	    if(!api.user.isConnected()){
	    	((RatingBar)findViewById(R.id.view_spot_rateSpot)).setVisibility(View.GONE);
	    }
	    
	    
	    ((RatingBar)findViewById(R.id.view_spot_rateSpot)).setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				((RelativeLayout)findViewById(R.id.relativeLayout2)).setVisibility(View.VISIBLE);
				commentAreaVisible = true;
			}
		});
	    
	    
	    ((RatingBar)findViewById(R.id.view_spot_favourite)).setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
				
				if(arg0.getRating() == 1){
         			//TODO: adicionar aos favoritos
					 System.out.println("Adiciona aos favoritos");
         		}else{
         			//TODO: remover dos favoritos
					 System.out.println("Remove dos favoritos");
         		}
				
			}
		});
	    
	    ((Button)findViewById(R.id.view_spot_sendComment)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendCommentAction();
			}
		}); 
	   
	    
	    ((ImageView)findViewById(R.id.view_spot_actionCall)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				callAction();				
			}
		});
	    
	    ((ImageView)findViewById(R.id.view_spot_actionNavigation)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				navigationAction();				
			}
		});
	    
	    
	    ((ImageView)findViewById(R.id.view_spot_actionMaps)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mapAction();				
			}
		});
	    
	    
	    ((ImageView)findViewById(R.id.view_spot_actionFacebook)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				facebookShareAction();				
			}
		});
	    
	    
	    ((ImageView)findViewById(R.id.view_spot_actionInvite)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				inviteAction();	
			}
		});
	    
	    
	    ((ImageView)findViewById(R.id.view_spot_actionReport)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				reportAction();				
			}
		});
	    
	    
	    
	    gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
	    
	    
	    progressDialog = ProgressDialog.show(ViewSpot.this, "", "Obtaining information of the Spot. Please wait...", true);
 		Thread thread = new Thread(this);
        thread.start();
        
	}
	
	@Override
  	public boolean dispatchTouchEvent(MotionEvent ev){
  		super.dispatchTouchEvent(ev);
  		return gestureDetector.onTouchEvent(ev);
  	}
	
	
	public void buildInviteDialog(){
		AlertDialog.Builder builder;
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		inviteDialogLayout = inflater.inflate(R.layout.view_spot_invite_dialog,
		                               (ViewGroup) findViewById(R.id.layout_root2));

		ListView lv = (ListView) inviteDialogLayout.findViewById(R.id.listView1);
		
		inviteDialogAdapter = new MyCustomAdapter();
		lv.setAdapter(inviteDialogAdapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				inviteDialogAdapter.setChecked(position);
				inviteDialogAdapter.notifyDataSetChanged();
				System.out.println("tlalalala");
				
			}
		});
		
		builder = new AlertDialog.Builder(this);
		builder.setView(inviteDialogLayout);
		builder.setNeutralButton("Send", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();			
			}	
		});
		
		inviteDialog = builder.create();
		inviteDialog.setTitle("Choose friends to send email");
		//inviteDialog.show();
	}

	
	public void inviteAction(){
		if(api.user.isConnected())
			inviteDialog.show();
		else
			Toast.makeText(ViewSpot.this, "You must login first", Toast.LENGTH_SHORT).show();
	}
	
	
	public void buildReportDialog(){
		AlertDialog.Builder builder;
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		reportDialogLayout = inflater.inflate(R.layout.view_spot_report_dialog,
		                               (ViewGroup) findViewById(R.id.layout_root));

		RadioGroup rg = (RadioGroup) reportDialogLayout.findViewById(R.id.view_spot_report_options);
		((EditText)reportDialogLayout.findViewById(R.id.view_spot_report_text)).setVisibility(View.GONE);
		
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(group.indexOfChild(group.findViewById(checkedId)) == 4){
					((EditText)reportDialogLayout.findViewById(R.id.view_spot_report_text)).setVisibility(View.VISIBLE);
				}else{
					((EditText)reportDialogLayout.findViewById(R.id.view_spot_report_text)).setVisibility(View.GONE);
				}
			}
		});

		builder = new AlertDialog.Builder(this);
		builder.setView(reportDialogLayout);
		builder.setNeutralButton("Send", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				RadioGroup rg = (RadioGroup)reportDialogLayout.findViewById(R.id.view_spot_report_options);
				if(rg.indexOfChild(rg.findViewById(rg.getCheckedRadioButtonId())) == 4){
					System.out.println(((EditText)reportDialogLayout.findViewById(R.id.view_spot_report_text)).getText().toString());
				}
				
				dialog.dismiss();			
			}	
		});
		
		reportDialog = builder.create();
		reportDialog.setTitle("What's the problem?");
		
	}
	
	public void reportAction(){
		if(api.user.isConnected())
			reportDialog.show();
		else
			Toast.makeText(ViewSpot.this, "You must login first", Toast.LENGTH_SHORT).show();
		
	}
	
	
	public void sendCommentAction(){
        Toast.makeText(ViewSpot.this, "To be done in a near future", Toast.LENGTH_SHORT).show();
        ((RelativeLayout)findViewById(R.id.relativeLayout2)).setVisibility(View.GONE);
		commentAreaVisible = false;
        
	}

	
	
	public void navigationAction(){
		if(sfi.getLatitude()== 0.0 && sfi.getLongitude() == 0.0){
			Toast toast = Toast.makeText(getApplicationContext(), "Coordenates not provided", Toast.LENGTH_SHORT);
    		toast.show();
		}else{
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
					Uri.parse("google.navigation:q="+sfi.getLatitude()+","+sfi.getLongitude()));
			startActivity(intent);
		}
		
		
	}
	
	
	public void mapAction(){
		
		if(sfi.getLatitude()== 0.0 && sfi.getLongitude() == 0.0){
			Toast toast = Toast.makeText(getApplicationContext(), "Coordenates not provided", Toast.LENGTH_SHORT);
    		toast.show();
		}else{
			Intent intent = new Intent(getApplicationContext(), ViewSpotMap.class);
			Bundle b = new Bundle();
			b.putDouble("lat", sfi.getLatitude());
			b.putDouble("lon", sfi.getLongitude());
			b.putString("name", sfi.getName());
			intent.putExtra("location",b);
	        startActivity(intent);
		}
	}
	
	
	public void callAction(){
		if(sfi.getPhoneNumber()==null){
			Toast toast = Toast.makeText(getApplicationContext(), "Phone number not provided", Toast.LENGTH_SHORT);
    		toast.show();
		}else{
			System.out.println(sfi.getPhoneNumber());
			/*
			Intent intent = new Intent(Intent.ACTION_CALL);
	        intent.setData(Uri.parse("tel:"+sfi.getPhoneNumber()));
	        startActivity(intent);
	        */
	        
		}
	}

	

	@Override
	public void run() {
		
		try {
			sfi = api.getSpotsFullInfo(spot_id);
			imageAdapter.setImages(sfi.getPhotosURL());
			commentList = sfi.getComments();
			
			handler.sendMessage(handler.obtainMessage());
			
		} catch (ClientProtocolException e) {
			
			progressDialog.dismiss();
			
			Looper.prepare();
			Toast toast = Toast.makeText(getApplicationContext(), "Error connecting to the server, try again...", Toast.LENGTH_SHORT);
    		toast.show();
    		
    		e.printStackTrace();
    		finish();
    		Looper.loop();
		
    		
		} catch (JSONException e) {
			
			progressDialog.dismiss();

			Looper.prepare();
			Toast toast = Toast.makeText(getApplicationContext(), "Error parsing information from server, try again...", Toast.LENGTH_SHORT);
    		toast.show();
    		
    		e.printStackTrace();
			finish();
    		Looper.loop();
			
			
			
		} catch (IOException e) {
			
			progressDialog.dismiss();

			Looper.prepare();
			Toast toast = Toast.makeText(getApplicationContext(), "Error sending information to server, try again...", Toast.LENGTH_SHORT);
    		toast.show();
    		
    		e.printStackTrace();
			finish();
    		Looper.loop();
			
			
		}
		
		
		
	
	}
	
	
	final Handler handler = new Handler() {
        public void handleMessage(Message msg) {

        	imageAdapter.notifyDataSetChanged();
        	
        	
        	PopulateScreenComments();
        	
        	
        	((TextView)findViewById(R.id.view_spot_spotName)).setText(sfi.getName());
        	
        	((TextView)findViewById(R.id.view_spot_spotName)).setText(sfi.getName());
        	((TextView)findViewById(R.id.view_spot_adress)).setText(sfi.getAddress());
        	((TextView)findViewById(R.id.view_spot_sports)).setText("Sports: "+(sfi.getSports().toString()).replace("[", "").replace("]", ""));
        	((TextView)findViewById(R.id.view_spot_location)).setText(sfi.getLocation());
        	
        	progressDialog.dismiss();
            
            api.last_visited_spot = sfi.getId();

        }
    };
    
    
    
    public void PopulateScreenComments(){
		
		LinearLayout list = (LinearLayout)this.findViewById(R.id.view_spot_commentHolder);
		
		for(int i = 0; i < commentList.size(); i++){
			LayoutInflater infalInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View convertView = infalInflater.inflate(R.layout.comments_child, null);
				
		    TextView name = (TextView)convertView.findViewById(R.id.comments_child_userName);
		    TextView comment = (TextView) convertView.findViewById(R.id.comments_child_userComment);
		    RatingBar rating = (RatingBar) convertView.findViewById(R.id.comments_child_userRating);
		    
		    name.setText(commentList.get(i).getUsername());
		    comment.setText(commentList.get(i).getComment());
		    rating.setRating(commentList.get(i).getRating());
		    
		    
		    list.addView(convertView);
		    
		}
		
		
	}
    
    
    
    @Override
	public void onBackPressed() {
    	
    	if(commentAreaVisible){
    		 ((RelativeLayout)findViewById(R.id.relativeLayout2)).setVisibility(View.GONE);
    		 commentAreaVisible = false;
    	}else
    		super.onBackPressed();
	}
    
    
       
    public void facebookShareAction(){
    	
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
        	
        	 //post on user's wall.
    	    api.facebook.dialog(this, "feed", new PostDialogListener());
    		
        }
    }
    
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        api.facebook.authorizeCallback(requestCode, resultCode, data);
        //a data nao traz nada de especial, so o access token...
      
        //post on user's wall.
		api.facebook.dialog(this, "feed", new PostDialogListener());
	  	    
    }
    
    
      
    public class PostDialogListener extends BaseDialogListener {
        public void onComplete(Bundle values) {
        	final String postId = values.getString("post_id");     
        	if (postId != null) {                                                                                   
        		Toast.makeText(getApplicationContext(), "Message posted on the wall.", Toast.LENGTH_SHORT).show();           
        	} else {             
        		Toast.makeText(getApplicationContext(), "No message posted on the wall.", Toast.LENGTH_SHORT).show();                                                                  
        	}   
        }
    }
    
    
    
    private class MyCustomAdapter extends BaseAdapter {


		private ArrayList<User> friends;
    	
    	public MyCustomAdapter() {
			friends = api.getFriends();
		}

		@Override
		public int getCount() {
			return friends.size();
		}

		@Override
		public Object getItem(int arg0) {
			return friends.get(arg0);
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

            name.setText(friends.get(position).getName());
            check.setChecked(friends.get(position).isSelected());

		
		return convertView;
		}
		
		
		public void setChecked(int position){
			if(friends.get(position).isSelected()){
				friends.get(position).setSelected(false);
				api.updateFrienState(friends.get(position).getId(), 0);
				
			}else{
				friends.get(position).setSelected(true);
				api.updateFrienState(friends.get(position).getId(), 1);
			}

		}
		
		
		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}
    	
    }
    
    
    
    public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
	    private Context mContext;

	    private ArrayList<Bitmap> photos; 
		
	    public void setImages(ArrayList<String> photosURL){
	    	
			
			for(int i = 0; i < photosURL.size(); i++){
				try {
					System.out.println(photosURL.size()+" VAI FAZER DOWNLOAD DA IMAGEM -> " + photosURL.get(i));
					
					URL newurl = new URL(photosURL.get(i));
					Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());	
					photos.add(mIcon_val.copy(mIcon_val.getConfig(), false));
					
					System.out.println(i +" Download Feito -> " + photosURL.get(i));
				} catch (Exception e) {
					e.printStackTrace();
					
				}
			}	
			
	    }
	    
		
		public ImageAdapter(Context c) {
	        mContext = c;
	        photos = new ArrayList<Bitmap>();
	        TypedArray attr = mContext.obtainStyledAttributes(R.styleable.HelloGallery);
	        mGalleryItemBackground = attr.getResourceId(
	                R.styleable.HelloGallery_android_galleryItemBackground, 0);
	        attr.recycle();
	    }
		
		 public int getCount() {
		        return photos.size();
	    }

	    public Object getItem(int position) {
	        return position;
	    }

	    public long getItemId(int position) {
	        return position;
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	    	
			ImageView imageView = new ImageView(mContext);   	
			imageView.setImageBitmap(photos.get(position));
		    imageView.setLayoutParams(new Gallery.LayoutParams(350, 250));
		    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		    imageView.setBackgroundResource(mGalleryItemBackground);
	
	        return imageView;
	    }
	}
    
    
    class MyGestureDetector extends SimpleOnGestureListener {
        
		@Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	System.out.println("right to left");                	
                	
                	if(index +1 < spotList_ids.size()){
	                	Intent intent = new Intent(getApplicationContext(),ViewSpot.class);
	                	intent.putExtra("id", spotList_ids.get(index+1));
	            		intent.putExtra("index", index+1);
	            		intent.putIntegerArrayListExtra("spotList_ids", spotList_ids);
	                    startActivity(intent);                   
	                    overridePendingTransition  (R.anim.right_slide_in, R.anim.left_slide_out);
	                    finish();
                	}else{
                		Toast toast = Toast.makeText(getApplicationContext(), "No more spots to show", Toast.LENGTH_SHORT);
    	        		toast.show();
                	}
                    
          
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	System.out.println("left to right");  
                	
                	if(index -1 >= 0){
	                	Intent intent = new Intent(getApplicationContext(),ViewSpot.class);
	                	intent.putExtra("id", spotList_ids.get(index-1));
	            		intent.putExtra("index", index-1);
	            		intent.putIntegerArrayListExtra("spotList_ids", spotList_ids);
	                    startActivity(intent);
	                    overridePendingTransition  (R.anim.left_slide_in, R.anim.right_slide_out);
	                    
	                    finish();
                	}else{
                		Toast toast = Toast.makeText(getApplicationContext(), "No more spots to show", Toast.LENGTH_SHORT);
    	        		toast.show();
                	}
                    
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }
    
}


	
