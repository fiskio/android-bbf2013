package com.luckybrews.bbf2013;

import java.io.File;
import java.io.IOException;

import com.luckybrews.bbf2013.R;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends Activity {
	
	private static final String TAG = "DetailsActivity";
	private final Context mContext = this;
	private  String id;
	private MediaRecorder mRecorder = null;
	private MediaPlayer   mPlayer = null;
	private static String mFileName; 
	public static String mDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BBF2013-Notes"; 
	//private boolean hasRecord = false;
     
	boolean mStartPlaying = true;
	boolean mStartRecording = true;
	
	ImageButton speaker, record;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		
		Bundle extras = getIntent().getExtras();
		id = extras.getString("id");
		
		// check db
		Cursor result = MainActivity.db.getReadableDatabase().rawQuery("SELECT _id, brewery, beername, type, color, abv, description, icon, rating, drunk, wishlist FROM beerlist WHERE _id = ?", new String[]{id});
		Log.d(TAG, "AFTER SELECT - size " + result.getCount());
		
		//while (beerCursor.moveToNext()) {
			result.moveToNext();
			
			//int id = result.getInt(0);
			String brewery = result.getString(1);
			TextView vBrewery = (TextView) findViewById(R.id.brewery);
			vBrewery.setText(brewery);
			
			String beername = result.getString(2);
			TextView vBeer = (TextView) findViewById(R.id.beername);
			vBeer.setText(beername);
			
			// set note filename
			mFileName = mDir + "/note_" + brewery + "_" + beername + ".3gp";
			
			String type = result.getString(3);
			TextView vType = (TextView) findViewById(R.id.type);
			vType.setText(type);
			
			String abv = result.getString(5);
			TextView vAbv = (TextView) findViewById(R.id.abv);
			vAbv.setText(abv.trim());
			
			String description = result.getString(6);
			TextView vDesc = (TextView) findViewById(R.id.description);
			vDesc.setText(description);
			
			String color = result.getString(4);
			ImageView vColor = (ImageView) findViewById(R.id.color);
			vColor.setBackgroundColor(Color.parseColor(color));
			
			ImageView imgView = (ImageView) findViewById(R.id.logo);
			byte[] blob = result.getBlob(7);
			imgView.setImageBitmap(BitmapFactory.decodeByteArray(blob, 0, blob.length));

			// rate bar
			final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingbar);
			final int stelle = result.getInt(8);
			ratingBar.setRating(stelle);
			Log.d(TAG, "RATING: " + stelle);
			
			// speaker button
			speaker = (ImageButton) findViewById(R.id.speaker);
			speaker.setOnClickListener(play_clicker);
			
			// record button
			record = (ImageButton) findViewById(R.id.record);
			record.setOnClickListener(rec_clicker);
			
			// radio check
			RadioButton rwish = (RadioButton) findViewById(R.id.radio_wishlist);
			RadioButton ryes = (RadioButton) findViewById(R.id.radio_yes);
			int ydrunk = result.getInt(9);
			int wishlist = result.getInt(10);
			if (ydrunk==1 && wishlist==1)
				Log.e(TAG, "Cannot be DRUNK and WISHING!!");
			if (ydrunk==1) {
				ryes.setChecked(true);
				//ratingBar.setOnTouchListener(rating_cliker);
			}
			ratingBar.setOnTouchListener(rating_cliker);
			if (wishlist==1)
				rwish.setChecked(true);
			// do something useful with these
			Log.d(TAG, "Brewery:\t" + brewery);
			Log.d(TAG, "Name:\t" + beername);
			Log.d(TAG, "ABV:\t" + abv);
		//}
		result.close();
		
		
	}
	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_yes:
	        	if (checked) {
	        		final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingbar);
	        		ratingBar.setOnTouchListener(rating_cliker);
	        		
	        		// update in DB
					ContentValues cv = new ContentValues();
					cv.put(DatabaseHelper.RATING, 0);
					cv.put(DatabaseHelper.WISHLIST, 0);
					cv.put(DatabaseHelper.DRUNK, 1);
					
					SQLiteDatabase db = MainActivity.db.getWritableDatabase();
					db.beginTransaction();
					db.update("beerlist", cv, BaseColumns._ID + "=" + id, null);
					db.setTransactionSuccessful(); 
					db.endTransaction();
	        	}
	        	break;
	        	
	        case R.id.radio_no:
	            if (checked) {
	            	// zero ratingbar
	            	final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingbar);
	            	ratingBar.setOnTouchListener(null);
	            	ratingBar.setRating(0);
	      
	            	// update in DB
					ContentValues cv = new ContentValues();
					cv.put(DatabaseHelper.RATING, 0);
					cv.put(DatabaseHelper.WISHLIST, 0);
					cv.put(DatabaseHelper.DRUNK, 0);
					
					SQLiteDatabase db = MainActivity.db.getWritableDatabase();
					db.beginTransaction();
					db.update("beerlist", cv, BaseColumns._ID + "=" + id, null);
					db.setTransactionSuccessful(); 
					db.endTransaction();
					
					File file = new File(mFileName);
					if (file.exists()) {
						file.delete();
					}
					
	            }
	            break;
	            
	        case R.id.radio_wishlist:
	            if (checked) {
	            	final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingbar);
	            	ratingBar.setOnTouchListener(null);
	            	ratingBar.setRating(0);
	      	      
	            	// update in DB
					ContentValues cv = new ContentValues();
					cv.put(DatabaseHelper.RATING, 0);
					cv.put(DatabaseHelper.WISHLIST, 1);
					cv.put(DatabaseHelper.DRUNK, 0);
					
					SQLiteDatabase db = MainActivity.db.getWritableDatabase();
					db.beginTransaction();
					db.update("beerlist", cv, BaseColumns._ID + "=" + id, null);
					db.setTransactionSuccessful(); 
					db.endTransaction();
	            }
	            break;
	    }
	}

	
	
	 @Override
	    public void onPause() {
	        super.onPause();
	        if (mRecorder != null) {
	            mRecorder.release();
	            mRecorder = null;
	        }

	        if (mPlayer != null) {
	            mPlayer.release();
	            mPlayer = null;
	        }
	    }
	
	  private void onRecord(boolean start) {
	        if (start) {
	            startRecording();
	        } else {
	            stopRecording();
	        }
	    }
	  
	  private void onPlay(boolean start) {
	        if (start) {
	            startPlaying();
	            
	        } else {
	            stopPlaying();
	        }
	    }
	  
	  private void startPlaying() {
	        mPlayer = new MediaPlayer();
	        try {
	            mPlayer.setDataSource(mFileName);
	            mPlayer.prepare();
	            mPlayer.start();
	        } catch (IOException e) {
	            Log.e(TAG, "prepare() failed");
	        }
	    }
	  
	  private void stopPlaying() {
	        mPlayer.release();
	        mPlayer = null;
	    } 

	  private void startRecording() {
	        mRecorder = new MediaRecorder();
	        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	        mRecorder.setOutputFile(mFileName);
	        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

	        try {
	            mRecorder.prepare();
	        } catch (IOException e) {
	            Log.e(TAG, "prepare() failed");
	        }

	        mRecorder.start();
	    }

	    private void stopRecording() {
	        mRecorder.stop();
	        mRecorder.release();
	        mRecorder = null;
	    }
	    
	    OnClickListener rec_clicker = new OnClickListener() {
            public void onClick(View v) {
            	RadioButton rno = (RadioButton) findViewById(R.id.radio_no);
            	RadioButton rwish = (RadioButton) findViewById(R.id.radio_wishlist);
            	if (rno.isChecked() || rwish.isChecked()) {
            		Toast.makeText(mContext, "Must drink it first! ;-)", Toast.LENGTH_SHORT).show(); 
            		return;
            	}
                onRecord(mStartRecording);
                if (mStartRecording) {
                	Toast.makeText(mContext, "Start recording, click again to stop.", Toast.LENGTH_SHORT).show();
                } else {       	
                	Toast.makeText(mContext, "Stop recording.", Toast.LENGTH_SHORT).show();
                	speaker.setClickable(true);
                }
                mStartRecording = !mStartRecording;
            }
        };

        OnClickListener play_clicker = new OnClickListener() {
            public void onClick(View v) {
            	
            	File file = new File(mFileName);
    			if (!file.exists()) {
    				Log.e(TAG, "Audio file does not exist!");
    				Toast.makeText(mContext, "Must drink it first! ;-)", Toast.LENGTH_SHORT).show(); 
    				return;
    			}
    			
                onPlay(mStartPlaying); 	
                if (mStartPlaying) {
                	Toast.makeText(mContext, "Start playing.", Toast.LENGTH_SHORT).show();                    
                } else {
                	//Toast.makeText(mContext, "Stop playing.", Toast.LENGTH_SHORT).show();
                }
                //mStartPlaying = !mStartPlaying;
            }
        };
        
        OnTouchListener rating_cliker = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				RadioButton rno = (RadioButton) findViewById(R.id.radio_no);
            	RadioButton rwish = (RadioButton) findViewById(R.id.radio_wishlist);
            	if (rno.isChecked() || rwish.isChecked()) {
					Toast.makeText(mContext, "Must drink it first! ;-)", Toast.LENGTH_SHORT).show(); 
    				return true;
				}
				
				final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingbar);
				
				if (event.getAction() == MotionEvent.ACTION_UP) {
					float touchPositionX = event.getX();
					float width = ratingBar.getWidth();
					float starsf = (touchPositionX / width) * 5.0f;
					final int stars = (int)starsf + 1;
					ratingBar.setRating(stars);                  
					v.setPressed(false);

					// update in DB
					ContentValues cv = new ContentValues();
					cv.put(DatabaseHelper.RATING, stars);

					SQLiteDatabase db = MainActivity.db.getWritableDatabase();
					db.beginTransaction();
					db.update("beerlist", cv, BaseColumns._ID + "=" + id, null);
					db.setTransactionSuccessful(); 
					db.endTransaction();
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setPressed(true);
				}
				if (event.getAction() == MotionEvent.ACTION_CANCEL) {
					v.setPressed(false);
				}
				return true;
			}
        };
}
