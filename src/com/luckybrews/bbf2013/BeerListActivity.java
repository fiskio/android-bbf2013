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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

@SuppressLint("NewApi")
public class BeerListActivity extends ListActivity {
	
	private static final String TAG = "BeerListActivity";
	 
	private ListView mListView;
	private Context mContext = this;

	private SimpleCursorAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beer_list);
		Bundle extras = getIntent().getExtras();
		String sortBy = extras.getString("sortBy");
		loadData(sortBy);
	}
	@SuppressWarnings("deprecation")
	private void loadData(String sortBy) {
		// check db
				Cursor beerCursor = MainActivity.db.getReadableDatabase().rawQuery("SELECT _id, brewery, beername, type, color, abv, description, icon, rating FROM beerlist ORDER BY " + sortBy, null);
				Log.d(TAG, "AFTER SELECT");
				
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_beer_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Intent aux;
	    switch (item.getItemId()) {
	  
	    case R.id.sort:
	    	//MenuInflater inflater =getMenuInflater();
	    	break;
	    	
	    case R.id.beernameItem:
	    	aux = new Intent(mContext, BeerListActivity.class);
			aux.putExtra("sortBy", "beername");
			finish();
			startActivity(aux);
	        break;
	        
	    case R.id.breweryItem:
	    	aux = new Intent(mContext, BeerListActivity.class);
			aux.putExtra("sortBy", "brewery");
			finish();
			startActivity(aux);
	        break;
	        
	    case R.id.colorItem:
	    	aux = new Intent(mContext, BeerListActivity.class);
			aux.putExtra("sortBy", "color");
			finish();
			startActivity(aux);
	        break;
	
	    case R.id.abvItem:
	    	aux = new Intent(mContext, BeerListActivity.class);
			aux.putExtra("sortBy", "abv");
			finish();
			startActivity(aux);
	        break;
	        
	    case R.id.styleItem:
	    	aux = new Intent(mContext, BeerListActivity.class);
			aux.putExtra("sortBy", "type");
			finish();
			startActivity(aux);
	        break;
	        
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	    
	    return true;
	}
	
}
