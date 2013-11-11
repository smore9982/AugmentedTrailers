package com.android.augmentedtrailer.activity;

import java.io.FileOutputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.augmentedtrailer.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.SensorsComponentAndroid;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.IRadar;
import com.metaio.sdk.jni.LLACoordinate;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.tools.io.AssetsManager;

public class AugmentLocation  extends ARViewActivity implements SensorsComponentAndroid.Callback, ConnectionCallbacks, OnConnectionFailedListener, LocationListener  {
	private static final String TAG = "AugmentLocation";
	Location storedGoogleLocation;
	Location currentGoogleLocation;
	LocationRequest mLocationRequest;
	
	IGeometry mLocationMarker;
	ArrayList<IGeometry> mLocationCrunbs = new ArrayList<IGeometry>();
	
	Handler handler;
	
	
	private IRadar mRadar;
	
	private MetaioSDKCallbackHandler mCallbackHandler;
	private LocationClient mLocationClient;
	private int counter = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		boolean result = metaioSDK.setTrackingConfiguration("GPS");
		mCallbackHandler = new MetaioSDKCallbackHandler();
		mLocationClient = new LocationClient(this, this, this);
		mLocationRequest = LocationRequest.create();
		
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(10);
		mLocationRequest.setFastestInterval(10);
		
		handler = new Handler();
	}
	
	@Override
	protected void onPause() 
	{
		super.onPause();
		if (mSensors != null){
			mSensors.registerCallback(null);
		}
		
		
	}

	@Override
	protected void onResume(){
		super.onResume();
		if (mSensors != null){
			mSensors.registerCallback(this);
		}
		
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		Log.i(TAG, "Starting location service");
		mLocationClient.connect();
	}
	
	@Override
	protected void onStop(){
		Log.i(TAG, "Disconnecting location service");
		if(mLocationClient.isConnected()){
			mLocationClient.removeLocationUpdates(this);
		}
		mLocationClient.disconnect();
		super.onStop();
	}
	
	@Override
	public void onGravitySensorChanged(float[] arg0) {
		
	}

	@Override
	public void onHeadingSensorChanged(float[] arg0) {
				
	}

	@Override
	public void onLocationSensorChanged(LLACoordinate arg0) {
		
	}

	@Override
	protected int getGUILayout() {
		return R.layout.location_overlay;
	}

	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
		return this.mCallbackHandler;
	}

	@Override
	protected void loadContents() {
		String filepath = AssetsManager.getAssetPath("LocationAssets/POI_bg.png");
		if (filepath != null){			
			this.mLocationMarker = metaioSDK.createGeometryFromImage(createBillboardTexture("Marker"), true);
			
			for(int i=0;i<10;i++){
				IGeometry crumb = metaioSDK.createGeometryFromImage(createBillboardTexture("Marker" + i), true);
				this.mLocationCrunbs.add(crumb);
			}
			
			
			
			mRadar = metaioSDK.createRadar();
			mRadar.setBackgroundTexture(AssetsManager.getAssetPath("LocationAssets/radar.png"));
			mRadar.setObjectsDefaultTexture(AssetsManager.getAssetPath("LocationAssets/yellow.png"));
			mRadar.setRelativeToScreen(IGeometry.ANCHOR_TL);						
			mRadar.add(mLocationMarker);
		}
		
	}
	
	private String createBillboardTexture(String billBoardTitle)
    {
           try
           {
                  final String billBoardTexture = getCacheDir() + "/" + billBoardTitle + ".png";
                  Paint mPaint = new Paint();

                  // Load background image (256x128), and make a mutable copy
                  Bitmap billboard = null;
                  
                  //reading billboard background
                  String filepath = AssetsManager.getAssetPath("LocationAssets/POI_bg.png");
                  Bitmap mBackgroundImage = BitmapFactory.decodeFile(filepath);
                  
                  billboard = mBackgroundImage.copy(Bitmap.Config.ARGB_8888, true);


                  Canvas c = new Canvas(billboard);

                  mPaint.setColor(Color.WHITE);
                  mPaint.setTextSize(24);
                  mPaint.setTypeface(Typeface.DEFAULT);

                  float y = 40;
                  float x = 30;

                  // Draw POI name
                  if (billBoardTitle.length() > 0)
                  {
                        String n = billBoardTitle.trim();

                        final int maxWidth = 160;

                        int i = mPaint.breakText(n, true, maxWidth, null);
                        c.drawText(n.substring(0, i), x, y, mPaint);

                        // Draw second line if valid
                        if (i < n.length())
                        {
                               n = n.substring(i);
                               y += 20;
                               i = mPaint.breakText(n, true, maxWidth, null);

                               if (i < n.length())
                               {
                                      i = mPaint.breakText(n, true, maxWidth - 20, null);
                                      c.drawText(n.substring(0, i) + "...", x, y, mPaint);
                               } else
                               {
                                      c.drawText(n.substring(0, i), x, y, mPaint);
                               }
                        }

                  }


                  // writing file
                  try
                  {
                	  FileOutputStream out = new FileOutputStream(billBoardTexture);
                      billboard.compress(Bitmap.CompressFormat.PNG, 90, out);
                      MetaioDebug.log("Texture file is saved to "+billBoardTexture);
                      return billBoardTexture;
                  } catch (Exception e) {
                      MetaioDebug.log("Failed to save texture file");
                	  e.printStackTrace();
                   }
                 
                  billboard.recycle();
                  billboard = null;

           } catch (Exception e){
                  MetaioDebug.log("Error creating billboard texture: " + e.getMessage());
                  MetaioDebug.printStackTrace(Log.DEBUG, e);
                  return null;
           }
           return null;
    }

	@Override
	protected void onGeometryTouched(IGeometry geometry) {
		
	}
	
	
	public void onSetLocationMarker(View v){
		if(this.currentGoogleLocation !=null){
			this.storedGoogleLocation = this.currentGoogleLocation;
		}else{
			this.storedGoogleLocation = mLocationClient.getLastLocation();
		}
		
		if(mGUIView !=null){
			TextView storedLoc = (TextView) mGUIView.findViewById(R.id.location);
			TextView currentLoc = (TextView) mGUIView.findViewById(R.id.location_google);
			
			if(this.currentGoogleLocation !=null){
				currentLoc.setText("lat: " + currentGoogleLocation.getLatitude() + " log: " + currentGoogleLocation.getLongitude() + " alt: " + currentGoogleLocation.getAltitude());
			}
			storedLoc.setText("lat: " + storedGoogleLocation.getLatitude() + " log: " + storedGoogleLocation.getLongitude() + " alt: " + storedGoogleLocation.getAltitude());		
		}	
		
		isLocatePressed = true;
		
		/*if(this.mLocationMarker !=null && this.storedGoogleLocation !=null){
			LLACoordinate coord = this.mSensors.getLocation();
			coord.setLatitude(storedGoogleLocation.getLatitude());
			coord.setLongitude(storedGoogleLocation.getLongitude());
			this.mLocationMarker.setTranslationLLA(coord);
			Toast.makeText(this, "Location Saved!", Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(this, "Unable to set marker location", Toast.LENGTH_LONG).show();
		}*/
	}
	
	final private class MetaioSDKCallbackHandler extends IMetaioSDKCallback{
		@Override
		public void onSDKReady(){

			// show GUI after SDK is ready
			runOnUiThread(new Runnable(){
				@Override
				public void run() {
					mGUIView.setVisibility(View.VISIBLE);
				}
			});
		}
				
		@Override
		public void onTrackingEvent(TrackingValuesVector trackingValues){
			super.onTrackingEvent(trackingValues);		
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.i(TAG, "Connection failed. Reason=" + arg0.getErrorCode());
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.i(TAG, "Connection success");
		this.mLocationClient.requestLocationUpdates(mLocationRequest, this);
		
	}

	@Override
	public void onDisconnected() {
		Log.i(TAG,"Disconnected");		
	}

	boolean isLocatePressed = false;
	@Override
	public void onLocationChanged(Location location) {
		currentGoogleLocation = location;
		LLACoordinate currentLLACoord = this.mSensors.getLocation();
		currentLLACoord.setLatitude(currentGoogleLocation.getLatitude());
		currentLLACoord.setLongitude(currentGoogleLocation.getLongitude());		
		this.mSensors.setManualLocation(currentLLACoord);	
		if(mGUIView !=null){
			TextView currentLoc = (TextView) mGUIView.findViewById(R.id.location_google);
			
			if(this.currentGoogleLocation !=null){
				currentLoc.setText(counter + " CURRENT lat: " + currentGoogleLocation.getLatitude() + " log: " + currentGoogleLocation.getLongitude() + " alt: " + currentGoogleLocation.getAltitude());
			}
		}
		
		counter++;
		Log.i(TAG, "Counter = " + counter);
		
		if(!isLocatePressed){
			return;
		}
		
		if(counter>=20){
			Toast.makeText(this, "Adding crumb", Toast.LENGTH_LONG).show();
			counter=0;
			IGeometry geometry = null;
			if(this.mLocationCrunbs.size() > 0 ){
				geometry = this.mLocationCrunbs.remove(0);
			}
			if(AugmentLocation.this.currentGoogleLocation != null && geometry!=null){
				LLACoordinate coord = AugmentLocation.this.mSensors.getLocation();
				coord.setLatitude(currentGoogleLocation.getLatitude());
				coord.setLongitude(currentGoogleLocation.getLongitude());
				geometry.setTranslationLLA(coord);
				this.mRadar.add(geometry);
			}
		}		
	}	
}
