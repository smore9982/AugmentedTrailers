package com.android.augmentedtrailer.activity;

import com.android.augmentedtrailer.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends BaseActivity {
	Button augmentedTrailers;
	Button augmentedWearables;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		
		augmentedTrailers = (Button) this.findViewById(R.id.augmentedTrailer);
		augmentedWearables = (Button) this.findViewById(R.id.augmentedWearables);
	}
		
	@Override
	protected void onPostCreate(Bundle savedInstanceState){
	       super.onPostCreate(savedInstanceState);
	       
	       augmentedTrailers.setOnClickListener(new OnClickListener(){
	    	   @Override
	    	   public void onClick(View v) {
	    		   Intent i = new Intent(MainActivity.this,AugmentTrailer.class);
	    		   startActivity(i);
	    	   }
	       });
	       
	       augmentedWearables.setOnClickListener(new OnClickListener(){
	    	   @Override
	    	   public void onClick(View v) {
	    		   Intent i = new Intent(MainActivity.this,AugmentWearable.class);
	    		   startActivity(i);
	    	   }
	       });
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
	}
		
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
}
