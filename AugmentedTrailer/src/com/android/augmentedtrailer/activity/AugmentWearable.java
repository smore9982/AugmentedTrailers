package com.android.augmentedtrailer.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.augmentedtrailer.R;
import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.ILight;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.tools.io.AssetsManager;

public class AugmentWearable  extends ARViewActivity {
	
	private IGeometry mCapShieldModel;
	private IGeometry mZeldaShieldModel;
	private IGeometry mRiotShieldModel;
	
	private IGeometry mIronmanModel;
	
	private MetaioSDKCallbackHandler mCallbackHandler;
	
	private final static int SHIELD_CAP = 0;
	private final static int SHIELD_ZELDA = 1;
	private final static int SHIELD_POLICE = 2;
	
	private boolean ironmanTorso = true;
	
	private int currentModel = SHIELD_CAP;
	private IGeometry currentGeometry = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		mCallbackHandler = new MetaioSDKCallbackHandler();
	}
	
	@Override
	protected int getGUILayout() {
		return R.layout.overlay; 
	}

	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
		return mCallbackHandler;
	}
	
	public void onToggleButtonBar(View v){
		if(mGUIView == null){
			return;
		}
		View buttonBar = this.mGUIView.findViewById(R.id.buttonBar);
		if(buttonBar == null){
			return;
		}
		if(buttonBar.getVisibility() ==View.VISIBLE){
			buttonBar.setVisibility(View.GONE);
		}
		if(buttonBar.getVisibility() ==View.GONE){
			buttonBar.setVisibility(View.VISIBLE);
		}
		
	}
	
	public void onShowCapShield(View v){
		showGeometry(this.SHIELD_CAP);
	}
	
	public void onShowZeldaShield(View v){
		showGeometry(this.SHIELD_ZELDA);
	}

	public void onShowRiotShield(View v){
		showGeometry(this.SHIELD_POLICE);
	}
	
	public void onToggleIronManSwitch(View v){
		if(this.mIronmanModel == null){
			return;
		}
		
		if(this.ironmanTorso){
			this.mIronmanModel.setVisible(false);
			this.ironmanTorso = false;
		}else{
			this.mIronmanModel.setVisible(true);
			this.ironmanTorso = true;
		}
		
	}
	
	private void showGeometry(int currentModel){
		if(mGUIView == null){
			return;
		}
		View buttonBar = this.mGUIView.findViewById(R.id.buttonBar);
		this.currentModel = currentModel;
		if(this.currentGeometry !=null){
			this.currentGeometry.setVisible(false);
		}
		if(this.currentModel == SHIELD_ZELDA){
			if(mZeldaShieldModel!=null){
				mZeldaShieldModel.setVisible(true);
				currentGeometry = mZeldaShieldModel;
			}
		}else if(this.currentModel == SHIELD_CAP){
			if(mCapShieldModel!=null){
				mCapShieldModel.setVisible(true);
				currentGeometry = mCapShieldModel;
			}
		}else if(this.currentModel == SHIELD_POLICE){
			if(mRiotShieldModel!=null){
				mRiotShieldModel.setVisible(true);
				currentGeometry = mRiotShieldModel;
			}
		}
		buttonBar.setVisibility(View.GONE);
	}

	@Override
	protected void loadContents() {
		try{
			final String trackingConfigFile = AssetsManager.getAssetPath("WearableAssets/TrackingData_MarkerlessFast.xml");
			final boolean result = metaioSDK.setTrackingConfiguration(trackingConfigFile); 
			MetaioDebug.log("Tracking data loaded: " + result); 
			
			String capShielModel = AssetsManager.getAssetPath("WearableAssets/ShieldAssets/CapShield/a9707b8984a4ae2cd462bfac4378d1a5.obj");
			String zeldaShieldModel = AssetsManager.getAssetPath("WearableAssets/ShieldAssets/ZeldaShield/9d907565264f551bfa08882d08cfe9ad.obj");	
			String riotShieldModel = AssetsManager.getAssetPath("WearableAssets/ShieldAssets/RiotShield/e6426e91e7cc9d640cff1f9402d57d6d.obj");
			String ironManModel = AssetsManager.getAssetPath("WearableAssets/IronmanAssets/d9ad31f50070f88afdfb8ddd1486d46f.obj");
			
			if(ironManModel != null){
				mIronmanModel = metaioSDK.createGeometry(ironManModel);
				mIronmanModel.setTranslation(new Vector3d(3.8f,-169.7f,-171.1f));
				mIronmanModel.setScale(new Vector3d(0.2729332745f,0.2729332745f,0.2729332745f));
				mIronmanModel.setCoordinateSystemID(2);
			}

			if (capShielModel != null){
				// Loading 3D geometry
				mCapShieldModel = metaioSDK.createGeometry(capShielModel);
				mCapShieldModel.setScale(new Vector3d(18.6080665588f,18.6080665588f,18.6080665588f));
				mCapShieldModel.setTranslation(new Vector3d(8.3834152222f,-268.6576538086f,17.1997070312f));
				mCapShieldModel.setCoordinateSystemID(1);
				mCapShieldModel.setVisible(false);
			}		
			if (zeldaShieldModel != null){
				// Loading 3D geometry
				mZeldaShieldModel = metaioSDK.createGeometry(zeldaShieldModel);
				mZeldaShieldModel.setScale(new Vector3d(24.8294963837f,24.8294963837f,24.8294963837f));
				mZeldaShieldModel.setTranslation(new Vector3d(-4.0915489197f,-261.6435241699f,-66.2747497559f));
				mZeldaShieldModel.setCoordinateSystemID(1);
				mZeldaShieldModel.setVisible(false);
			}		
			
			if (riotShieldModel != null){
				// Loading 3D geometry
				mRiotShieldModel = metaioSDK.createGeometry(riotShieldModel);
				mRiotShieldModel.setRotation(new Rotation(new Vector3d(0f,0f,0f)));
				mRiotShieldModel.setScale(new Vector3d(0.11f,0.16f,0.08f));
				mRiotShieldModel.setTranslation(new Vector3d(-39.6f,-955.7f,-85.4f));
				mRiotShieldModel.setCoordinateSystemID(1);
				mRiotShieldModel.setVisible(false);
			}		
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
			
			if(true){
				if(currentGeometry != null){
					currentGeometry.setVisible(false);
				}
			
				if(currentModel == SHIELD_CAP && mCapShieldModel !=null){
					mCapShieldModel.setVisible(true);
					currentGeometry = mCapShieldModel;
				}
		
				if(currentModel == SHIELD_ZELDA && mZeldaShieldModel !=null){
					mZeldaShieldModel.setVisible(true);
					currentGeometry = mZeldaShieldModel;
				}
		
				if(currentModel == SHIELD_POLICE){
					mRiotShieldModel.setVisible(true);
					currentGeometry = mRiotShieldModel;
				}
			}
		
		}
	}


	@Override
	protected void onGeometryTouched(IGeometry geometry) {
		// TODO Auto-generated method stub
		
	}
}
