package com.luckybrews.bbf2013;

import com.luckybrews.bbf2013.R;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

@SuppressLint("NewApi")
public class MyDrinksActivity extends ListActivity {
	
	private static final String TAG = "MyDrinksActivity";
	
	private ListView mListView;
	private Context mContext = this;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beer_list);

		// check db
		Cursor beerCursor = MainActivity.db.getReadableDatabase().rawQuery("SELECT _id, brewery, beername, type, color, abv, description, icon, rating, drunk FROM beerlist WHERE drunk=1", null);
		Log.d(TAG, "AFTER SELECT");
		
		SimpleCursorAdapter adapter;
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
			adapter = new SimpleCursorAdapter(this, R.layout.beer, beerCursor, new String[] {
				DatabaseHelper.BEERNAME,
				DatabaseHelper.BREWERY,
				DatabaseHelper.TYPE,
				DatabaseHelper.ABV,
				DatabaseHelper.COLOR,
				DatabaseHelper.ICON,
				DatabaseHelper.DESCRIPTION,
				DatabaseHelper.RATING
				},
				new int[] { R.id.beername, R.id.brewery, R.id.type, R.id.abv, R.id.color, R.id.logo, R.id.description, R.id.ratingbarSmall},
				0);
		}
		else {
			adapter=new SimpleCursorAdapter(this, R.layout.beer, beerCursor, new String[] {
					DatabaseHelper.BEERNAME,
					DatabaseHelper.BREWERY,
					DatabaseHelper.TYPE,
					DatabaseHelper.ABV,
					DatabaseHelper.COLOR,
					DatabaseHelper.ICON,
					DatabaseHelper.DESCRIPTION,
					DatabaseHelper.RATING
					},
					new int[] { R.id.beername, R.id.brewery, R.id.type, R.id.abv, R.id.color, R.id.logo, R.id.description, R.id.ratingbarSmall });
		}
		adapter.setViewBinder(new MyViewBinder());
		setListAdapter(adapter);
		
		 
	    // set click listener
	    mListView = (ListView) findViewById(android.R.id.list); 
	    mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent memo = new Intent(mContext, DetailsActivity.class);
				memo.putExtra("id", ""+id);
				//finish();
				startActivity(memo);
			}
		});
	}
}
