package com.luckybrews.bbf2013;

import com.luckybrews.bbf2013.R;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class MyViewBinder implements ViewBinder{

	private static final String TAG = "MyViewBinder"; 
	
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

        int viewId = view.getId();

        switch(viewId){

        case R.id.logo:
        	
        	ImageView picture = (ImageView) view;
        	byte[] blob = cursor.getBlob(columnIndex);
        	if(blob!=null){
        		picture.setImageBitmap(
        				BitmapFactory.decodeByteArray(blob, 0, blob.length)
        				);
        		Log.d(TAG, "LOGO SET!");
        	}
        	
        	else {
        		picture.setImageResource(R.drawable.ic_launcher);
        	}
        	return true;
        	
        case R.id.color:
        	
        	ImageView color = (ImageView) view;
        	String hex = cursor.getString(columnIndex);
        	color.setBackgroundColor(Color.parseColor(hex));
        	Log.d(TAG, "COLOR SET! " + hex);
        	return true;
 
        case R.id.ratingbarSmall:
        	
        	RatingBar ratingBar = (RatingBar) view;
        	int stars = cursor.getInt(columnIndex);
        	ratingBar.setRating(stars);
        	Log.d(TAG, "Rating: "+ stars);
        	return true;
        	
       
        }
        return false;
    }


}