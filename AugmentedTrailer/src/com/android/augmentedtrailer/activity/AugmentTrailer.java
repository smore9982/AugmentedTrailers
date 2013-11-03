package com.android.augmentedtrailer.activity;

import com.metaio.R;
import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.MetaioSDK;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.tools.io.AssetsManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class AugmentTrailer extends ARViewActivity{
	
	private IGeometry mTrailerPlaneHalo = null;
	private IGeometry mTrailerPlaneForza = null;
	private IGeometry mTrailerPlaneME = null;
	private IGeometry currentPlane = null;
	private MetaioSDKCallbackHandler mCallbackHandler;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//MetaioSDK.CreateMetaioSDKAndroid(this, R.string.metaioSDKSignature);
		mCallbackHandler = new MetaioSDKCallbackHandler();
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
	}
		
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}

	@Override
	protected int getGUILayout() {	
		return 0;
	}

	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
		return this.mCallbackHandler;
	}

	@Override
	protected void loadContents() {
		// Load desired tracking data for planar marker tracking
		try{
			final String trackingConfigFile = AssetsManager.getAssetPath("TrailerAssets/TrackingData_MarkerlessFast.xml");
			
			final boolean result = metaioSDK.setTrackingConfiguration(trackingConfigFile); 
			MetaioDebug.log("Tracking data loaded: " + result); 
	      		
			// Loading movie geometry
			final String haloMoviePath = AssetsManager.getAssetPath("TrailerAssets/HaloAssets/HaloVideo.3g2");
			final String forzaMoviePath = AssetsManager.getAssetPath("TrailerAssets/ForzaAssets/ForzaVideo.3g2");
			final String massEffectMoviePath = AssetsManager.getAssetPath("TrailerAssets/MassEffectAssets/MassEffectVideo.3g2");
			
			if (haloMoviePath != null)
			{
				mTrailerPlaneHalo = metaioSDK.createGeometryFromMovie(haloMoviePath, false);
				if (mTrailerPlaneHalo != null){
					mTrailerPlaneHalo.setScale(2.0f);
					mTrailerPlaneHalo.setRotation(new Rotation(0f, 0f, 0f));
					mTrailerPlaneHalo.setCoordinateSystemID(1);
					MetaioDebug.log("Loaded geometry "+haloMoviePath);
				}
				else {
					MetaioDebug.log(Log.ERROR, "Error loading geometry: "+haloMoviePath);
				}	
			}
			
			if(forzaMoviePath != null){
				mTrailerPlaneForza = metaioSDK.createGeometryFromMovie(forzaMoviePath, false);
				if (mTrailerPlaneForza != null){
					mTrailerPlaneForza.setScale(10.0f);
					mTrailerPlaneForza.setRotation(new Rotation(0f, 0f, 0f));
					mTrailerPlaneForza.setCoordinateSystemID(2);
					MetaioDebug.log("Loaded geometry "+forzaMoviePath);
				}
				else {
					MetaioDebug.log(Log.ERROR, "Error loading geometry: "+forzaMoviePath);
				}
			}
			
			if(massEffectMoviePath != null){
				mTrailerPlaneME = metaioSDK.createGeometryFromMovie(massEffectMoviePath, false);
				if (mTrailerPlaneME != null){
					mTrailerPlaneME.setScale(5.0f);
					mTrailerPlaneME.setRotation(new Rotation(0f, 0f, 0f));
					mTrailerPlaneME.setCoordinateSystemID(3);
					MetaioDebug.log("Loaded geometry "+forzaMoviePath);
				}
				else {
					MetaioDebug.log(Log.ERROR, "Error loading geometry: "+forzaMoviePath);
				}
			}
						
			// loading environment maps
			//final String path = AssetsManager.getAssetPath("Tutorial2/Assets2/env_map.zip");
			//if (path != null)
			//{
			//	boolean loaded = metaioSDK.loadEnvironmentMap(path);
			//	MetaioDebug.log("Environment map loaded: " + loaded);
			//}
			//else
			//{
				//MetaioDebug.log(Log.ERROR, "Environment map not found at: "+path);
			//}

			// Start by displaying metaio man (hide other models)
			//setActiveModel(0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			MetaioDebug.log(Log.ERROR, "loadContents failed, see stack trace");
		}
	}

	@Override
	protected void onGeometryTouched(IGeometry geometry) {
		// TODO Auto-generated method stub
		
	}
	
	final private class MetaioSDKCallbackHandler extends IMetaioSDKCallback{
		@Override
		public void onSDKReady() 
		{
			// show GUI after SDK is ready
			runOnUiThread(new Runnable() 
			{
				@Override
				public void run() 
				{					
				}
			});
		}
		
		@Override
		public void onTrackingEvent(TrackingValuesVector trackingValues)
		{
			super.onTrackingEvent(trackingValues);
			String cosName = trackingValues.get(0).getCosName();			
			String sensor = trackingValues.get(0).getSensor();
			int cosId = trackingValues.get(0).getCoordinateSystemID();
			String addValues = trackingValues.get(0).getAdditionalValues();
			long size = trackingValues.size();
						
			Log.i("TRACKING", "SensorName: " + sensor);
			Log.i("TRACKING", "cosName: " + cosName);
			Log.i("TRACKING", "COSID: " + cosId);
			Log.i("TRACKING","AdditonalValues: " + addValues);
			Log.i("TRACKING", "SIZE: " + size);
			
			if(currentPlane !=null){
				currentPlane.stopMovieTexture();
			}
			
			
			if(cosName.equalsIgnoreCase("HaloCOS") && mTrailerPlaneHalo !=null){
				currentPlane = mTrailerPlaneHalo;
				currentPlane.startMovieTexture();
			}
			
			if(cosName.equalsIgnoreCase("ForzaCOS") && mTrailerPlaneForza !=null){
				currentPlane = mTrailerPlaneForza;
				currentPlane.startMovieTexture();
			}
			
			if(cosName.equalsIgnoreCase("MECos") && mTrailerPlaneME !=null){
				currentPlane = mTrailerPlaneME;
				currentPlane.startMovieTexture();
			}			
		}
	}
}
