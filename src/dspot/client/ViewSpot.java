package dspot.client;

import java.util.ArrayList;

import dspot.client.ViewSpotList.MyListAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class ViewSpot extends Activity implements Runnable{
	
	Api api;
	ProgressDialog dialog;
	
	MyListAdapter mAdapter;
	ArrayList<Comment> commentList;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		api = ((Api)getApplicationContext());
		
		setContentView(R.layout.view_spot);
		
		Gallery gallery = (Gallery) findViewById(R.id.gallery1);
	    gallery.setAdapter(new ImageAdapter(this));

	    gallery.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView parent, View v, int position, long id) {
	            Toast.makeText(ViewSpot.this, "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });
	  
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
	    
	    dialog = ProgressDialog.show(ViewSpot.this, "", "Obtaining information of the Spot. Please wait...", true);
 		Thread thread = new Thread(this);
        thread.start();
        
	}
	
	
	public void navigationAction(){
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=41.178767,-8.598862"));
		startActivity(intent);
	}
	
	
	public void mapAction(){
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:41.178767,-8.598862?z=18"));
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

            dialog.dismiss();
            
            PopulateScreenComments();

        }
    };
}


	
