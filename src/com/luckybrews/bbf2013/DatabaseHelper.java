package com.luckybrews.bbf2013;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static String TAG = "DatabaseHelper";

	public static final String DATABASE_NAME = "beers.db";
	private static final int SCHEMA = 1; 

	static final String BREWERY = "brewery";
	static final String BEERNAME = "beername";
	static final String TYPE = "type";
	static final String COLOR = "color";
	static final String ABV = "abv";
	static final String DESCRIPTION = "description";
	static final String ICON = "icon";
	static final String WISHLIST = "wishlist";
	static final String DRUNK = "drunk";
	static final String RATING = "rating";
	static final String COMMENT = "comment";
	
	static final String TABLE="beerlist";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		// scrape beers online
		Scraper scraper = new Scraper();
		List<Beer> beers = scraper.scrape();
		Log.d(TAG, "Beers scraped: " + beers.size());
		 
		// fill database
		try {
			db.beginTransaction();
			db.execSQL("CREATE TABLE beerlist (_id INTEGER PRIMARY KEY AUTOINCREMENT, brewery TEXT, beername TEXT, type TEXT, color TEXT, abv TEXT, description TEXT, icon BLOB, wishlist INTEGER, drunk INTEGER, rating INTEGER, comment BLOB);");
			
			ContentValues cv = new ContentValues();
			
			for (Beer beer : beers) {
				cv.put(BREWERY, beer.brewery); 
				cv.put(BEERNAME, beer.beerName);
				cv.put(TYPE, beer.type);
				cv.put(COLOR, beer.color);
				cv.put(ABV, beer.abv);
				cv.put(DESCRIPTION, beer.desc);
				cv.put(ICON, beer.img);
				cv.put(WISHLIST, 0);
				cv.put(DRUNK, 0);
				cv.put(RATING, 0);
				//cv.put(COMMENT, );
				db.insert("beerlist", BREWERY, cv); 
			}
			
			db.setTransactionSuccessful(); 
			
		} catch (Exception e) {
			Log.e(TAG, "Error retriving beer list from network: " + e.getLocalizedMessage());
			e.printStackTrace();
			
		} finally {
			Log.d(TAG, "Closing DB.");
			db.endTransaction();
		}
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		throw new RuntimeException("Not ready to updgrade yet!");
	}

}
