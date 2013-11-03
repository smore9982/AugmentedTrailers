package com.android.augmentedtrailer.activity;


import java.io.IOException;

import com.android.augmentedtrailer.R;
import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.Toast;

public class SplashScreenActivity extends Activity {
	Handler _handler; 
	AssetsExtracter mTask;
	
	private Runnable delayRunnable = new Runnable(){
		public void run() {
			SplashScreenActivity.this.loadNextScreen();
		}
	};
	
	private void loadNextScreen(){
		Intent nextScreen = new Intent(this,MainActivity.class);
		this.startActivity(nextScreen);
		this.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_splashscreen);
		super.onCreate(savedInstanceState);
		mTask = new AssetsExtracter();
		mTask.execute(0);
	}
		
	@Override
	protected void onPostCreate(Bundle savedInstanceState){
	       super.onPostCreate(savedInstanceState);
	}
		
		
	@Override
	protected void onStart(){
		super.onStart();
	}
	    
	@Override
	protected void onRestart(){
		super.onRestart();
	}

	@Override
	protected void onResume(){
		super.onResume();
	}
		
	@Override
	protected void onPause(){
		super.onPause();
	}
		
	@Override
	protected void onStop(){
		super.onStop();
    	if (this._handler != null){
    		this._handler.removeCallbacks(this.delayRunnable);
    	}
	}
		
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	
	private class AssetsExtracter extends AsyncTask<Integer, Integer, Boolean>
	{

		@Override
		protected void onPreExecute() 
		{			
		}
		
		@Override
		protected Boolean doInBackground(Integer... params) 
		{
			try 
			{
				AssetsManager.extractAllAssets(getApplicationContext(), true);
			} 
			catch (IOException e) 
			{
				MetaioDebug.printStackTrace(Log.ERROR, e);
				return false;
			}

			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) 
		{			
			
			if (result)
			{
				Toast.makeText(SplashScreenActivity.this, "Metaio Assets Loaded", Toast.LENGTH_SHORT).show();
				_handler = new Handler();
				_handler.postDelayed(delayRunnable, 10);
			}
			else
			{
				MetaioDebug.log(Log.ERROR, "Error extracting assets, closing the application...");
				finish();
			}
	    }
	}
}
