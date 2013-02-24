package com.luckybrews.bbf2013;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public static DatabaseHelper db;
	public static final String TAG = "MainActivity";
	
	private Context mContext = this;

	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// check external dir
		File file = new File(DetailsActivity.mDir);
		if (!file.exists()) {
			file.mkdir();
		}		
		// create db
		makeSpin(false);
		//db = new DatabaseHelper(getApplicationContext());
		//db.getReadableDatabase();
    	 
     	// list all
        TextView textListAll = (TextView) findViewById(R.id.textListAll);
        textListAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent listAll = new Intent(mContext, BeerListActivity.class);
				listAll.putExtra("sortBy", "_id");
				startActivity(listAll);	
			}
        });
   
        // wishlist
        TextView textWishlist = (TextView) findViewById(R.id.textWishlist);
        textWishlist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent wishlist = new Intent(mContext, WishlistActivity.class);
		    	startActivity(wishlist);
			}
        });
        // my drinks
        TextView textMyDrinks = (TextView) findViewById(R.id.textMyDriks); 
        textMyDrinks.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mydrinks = new Intent(mContext, MyDrinksActivity.class);
		    	startActivity(mydrinks);
			}
        });
    	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	  
	    case R.id.reloaddb:
	    	
	    	makeSpin(true);
	    	break;
	    	
	    }
	    return true;
	}

	public void makeSpin(final boolean delete) {
		
		// check network
		if (!isNetworkAvailable()) {
			Toast.makeText(mContext, "Please check your wireless connection and try again.", Toast.LENGTH_SHORT).show();
			return;
		} 
		
		final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Downloading Database");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        final Handler handler = new Handler()
        {

            @Override
            public void handleMessage(Message msg)
            {
                progress.dismiss();
                // Intent blistIntent = new Intent(mContext, BeerListActivity.class);
                // blistIntent.putExtra("sortBy", "_id");
                // startActivity(blistIntent);
                super.handleMessage(msg);
            }

        };
        progress.show();
        new Thread()
        {
            public void run()
            {
            	// database
            	if (delete) {
            		mContext.deleteDatabase(DatabaseHelper.DATABASE_NAME);
            	}
            	
    	    	db = new DatabaseHelper(getApplicationContext());
    	    	db.getReadableDatabase();
                handler.sendEmptyMessage(0);
            }

        }.start();

    }
	
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}	
	

}
