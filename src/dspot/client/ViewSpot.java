package dspot.client;

import java.util.ArrayList;

import com.facebook.android.DialogError;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

import dspot.client.ViewSpotList.MyListAdapter;
import dspot.utils.Comment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	
	//AlertDialog inviteDialog;
	//View inviteDialogLayout;
	
	MyListAdapter mAdapter;
	ArrayList<Comment> commentList;
	
	boolean commentAreaVisible = false;
	
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		api = ((Api)getApplicationContext());
		
		setContentView(R.layout.view_spot);
		
		Gallery gallery = (Gallery) findViewById(R.id.gallery1);
	    gallery.setAdapter(new ImageAdapter(this));

	    
	    ((RelativeLayout)findViewById(R.id.relativeLayout2)).setVisibility(View.GONE);
	    
	    
	    ((RatingBar)findViewById(R.id.view_spot_rateSpot)).setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				((RelativeLayout)findViewById(R.id.relativeLayout2)).setVisibility(View.VISIBLE);
				commentAreaVisible = true;
			}
		});
	    
	    ((Button)findViewById(R.id.view_spot_sendComment)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendCommentAction();
			}
		});
	    /*
	    gallery.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView parent, View v, int position, long id) {
	            Toast.makeText(ViewSpot.this, "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });
	  	*/
	    
	    
	    commentList = new ArrayList<Comment>();
	    
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
				//facebookInviteAction();	
				//TODO:
			}
		});
	    
	    
	    ((ImageView)findViewById(R.id.view_spot_actionReport)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				reportAction();				
			}
		});
	    
	    progressDialog = ProgressDialog.show(ViewSpot.this, "", "Obtaining information of the Spot. Please wait...", true);
 		Thread thread = new Thread(this);
        thread.start();
        
	}
	
	
	
	
	
	
	public void reportAction(){
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
		reportDialog.show();
	}
	
	
	public void sendCommentAction(){
        Toast.makeText(ViewSpot.this, "To be done in a near future", Toast.LENGTH_SHORT).show();
	}

	
	
	public void navigationAction(){
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=41.178767,-8.598862"));
		startActivity(intent);
	}
	
	
	public void mapAction(){
		Intent intent = new Intent(getApplicationContext(), ViewSpotMap.class);
		Bundle b = new Bundle();
		b.putDouble("lat", 41.178767);
		b.putDouble("lon", -8.598862);
		b.putString("name", "lalalaalla");
		b.putString("address", "rua lalal");
		intent.putExtra("location",b);
        startActivity(intent);
	}
	
	
	public void callAction(){
		Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:*#100#"));
        startActivity(intent);
	}

	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
	    private Context mContext;

	    private Integer[] mImageIds = {
	            R.drawable.sample_1,
	            R.drawable.sample_2,
	            R.drawable.sample_3,
	            R.drawable.sample_4,
	            R.drawable.sample_5,
	            R.drawable.sample_6,
	            R.drawable.sample_7
	    };
		
		
		public ImageAdapter(Context c) {
	        mContext = c;
	        TypedArray attr = mContext.obtainStyledAttributes(R.styleable.HelloGallery);
	        mGalleryItemBackground = attr.getResourceId(
	                R.styleable.HelloGallery_android_galleryItemBackground, 0);
	        attr.recycle();
	    }
		
		 public int getCount() {
		        return mImageIds.length;
		    }

		    public Object getItem(int position) {
		        return position;
		    }

		    public long getItemId(int position) {
		        return position;
		    }

		    public View getView(int position, View convertView, ViewGroup parent) {
		        ImageView imageView = new ImageView(mContext);

		        imageView.setImageResource(mImageIds[position]);
		        imageView.setLayoutParams(new Gallery.LayoutParams(350, 250));
		        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		        imageView.setBackgroundResource(mGalleryItemBackground);

		        return imageView;
		    }
	}



	@Override
	public void run() {
		
		//TODO:buscar as coisas ao site
		
		Comment c1 = new Comment("Eu", "A tua prima de 4!!!!", 5);
		Comment c2 = new Comment("Eu", "A tua prima!!!!", 3);
		Comment c3 = new Comment("Eu", "A tua prima de costas!!!!", 5);
		Comment c4 = new Comment("Eu", "A prima do xpto!!!!", 4);
		
		commentList.add(c1);
		commentList.add(c2);
		commentList.add(c3);
		commentList.add(c4);
		
		
		handler.sendMessage(handler.obtainMessage());
	
	}
	
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
	
	
	final Handler handler = new Handler() {
        public void handleMessage(Message msg) {

        	progressDialog.dismiss();
            
            PopulateScreenComments();

        }
    };
    
    
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
    
}


	
