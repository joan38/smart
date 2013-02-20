package fr.umlv.lastproject.smart;

import java.io.IOException;

import org.osmdroid.events.MapAdapter;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.DirectedLocationOverlay;
import org.osmdroid.views.overlay.OverlayManager;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import fr.umlv.lastproject.smart.form.CreateFormActivity;
import fr.umlv.lastproject.smart.form.Form;
import android.widget.Toast;
import fr.umlv.lastproject.smart.GPSTrack.TRACK_MODE;
import fr.umlv.lastproject.smart.dialog.AlertCreateMissionDialog;
import fr.umlv.lastproject.smart.dialog.AlertExitSmartDialog;
import fr.umlv.lastproject.smart.dialog.AlertGPSSettingDialog;
import fr.umlv.lastproject.smart.dialog.AlertTrackDialog;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.layers.Geometry.GeometryType;
import fr.umlv.lastproject.smart.utils.SmartConstants;


public class MenuActivity extends Activity {

	/**
	 * 
	 * @author thibault Brun
	 * @author tanios Faddoul Description : This class contains the Menus
	 *         container
	 * 
	 */

	private SmartMapView mapView;

	private MapController mapController;
	private OverlayManager overlayManager;
	private GPS gps;
	private LocationManager locationManager;
	private InfoOverlay infoOverlay;
	private DirectedLocationOverlay directedLocationOverlay;
	private View centerMap;
	private boolean isMapTracked = true;
	private GeoPoint lastPosition = new GeoPoint(0, 0);

	private boolean missionCreated = false;

	private String missionName;

	private GPSTrack gpsTrack;

	private AlertCreateMissionDialog missionDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_smart);
		initMap();
		initGps();

		ImageButton home = (ImageButton) findViewById(R.id.home);
		home.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent homeActivity = new Intent(MenuActivity.this,
						HomeActivity.class);
				homeActivity.putExtra("missionCreated", missionCreated);
				startActivityForResult(homeActivity, SmartConstants.HOME_VIEW);
			}
		});

		ImageButton layers = (ImageButton) findViewById(R.id.layers);
		layers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent layersActivity = new Intent(MenuActivity.this,
						LayersActivity.class);
				startActivityForResult(layersActivity,
						SmartConstants.LAYERS_VIEW);
			}
		});
	}

	@Override
	public void onBackPressed() {
		final AlertExitSmartDialog exitDialog = new AlertExitSmartDialog(this);
		exitDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_smart, menu);
		menu.add(0, 1, 0, R.string.hideInfoZone);
		menu.add(0, 2, 0, R.string.gpsSettings);
		return true;
	}

	/**
	 * This method is use to init the map
	 */
	public void initMap() {
		mapView = (SmartMapView) findViewById(R.id.mapview);
		mapController = mapView.getController();
		overlayManager = mapView.getOverlayManager();
		mapView.setTileSource(TileSourceFactory.MAPNIK);
		mapView.setClickable(true);
		mapView.setMultiTouchControls(true);
		mapController.setZoom(SmartConstants.DEFAULT_ZOOM);
		overlayManager.add(new ScaleBarOverlay(this));

		directedLocationOverlay = new DirectedLocationOverlay(this);
		directedLocationOverlay.setShowAccuracy(true);
		overlayManager.add(directedLocationOverlay);

		mapView.setMapListener(new MapAdapter() {
			@Override
			public boolean onScroll(ScrollEvent event) {
				
				isMapTracked = false;
				centerMap.setVisibility(View.VISIBLE);
				return super.onScroll(event);
			}
		});

		infoOverlay = new InfoOverlay(findViewById(R.id.table));
		// centerOverlay = new CenterOverlay(findViewById(R.id.centermap));
		centerMap = findViewById(R.id.centermap);

		centerMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isMapTracked = true;
				mapController.setCenter(lastPosition);
				centerMap.setVisibility(View.INVISIBLE);
			}
		});

		/**
		 * Exemple d'utilisation d'un shapefile
		 */
		/*
		 * GeometryLayer gltest = DataImport.importShapeFile(this,
		 * "/storage/sdcard0/Download/shp/TestPolygon.shp");
		 * 
		 * Log.d("layer retourne", "Layer retourne "+gltest.toString());
		 * 
		 * gltest.setSymbology(new PolygonSymbology(30, Color.BLACK));
		 * 
		 * overlayManager.add(gltest) ;
		 */

		/**
		 * Exemple d'utilisation d'une mission
		 */
		/*
		 * Mission.createMission("ma mission thibault yoyo",
		 * getApplicationContext(), mapView);
		 * Mission.getInstance().startMission();
		 * overlayManager.add(Mission.getInstance().getPolygonLayer() ) ;
		 * overlayManager.add(Mission.getInstance().getLineLayer() ) ;
		 * overlayManager.add(Mission.getInstance().getPointLayer() ) ;
		 * Mission.getInstance().startSurvey(GeometryType.POLYGON );
		 * Mission.getInstance().stopMission();
		 */

	}

	/**
	 * This method is use to connect the GPS to the positionOverlay
	 */
	public void initGps() {

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		gps = new GPS(locationManager);

		if (!gps.isEnabled(locationManager)) {
			final AlertGPSSettingDialog gpsSettingDialog = new AlertGPSSettingDialog(
					this);
			gpsSettingDialog.show();
		}

		gps.start(SmartConstants.GPS_REFRESH_TIME,
				SmartConstants.GPS_REFRESH_DISTANCE);
		gps.addGPSListener(new IGPSListener() {

			@Override
			public void actionPerformed(GPSEvent event) {
				/* Init Position Overlay */

				lastPosition = new GeoPoint(event.getLatitude(), event
						.getLongitude());

				if (isMapTracked) {
					mapController.setCenter(lastPosition);
				}
				/* Init Informations zone */
				infoOverlay.updateInfo(event);

				/* change position marker */
				directedLocationOverlay.setLocation(new GeoPoint(event
						.getLatitude(), event.getLongitude()));
				directedLocationOverlay.setAccuracy((int) event.getAccuracy());
				directedLocationOverlay.setBearing(event.getBearing());
				mapView.invalidate();

			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			if (findViewById(R.id.table).getVisibility() == View.INVISIBLE) {
				item.setTitle(R.string.hideInfoZone);
				findViewById(R.id.table).setVisibility(View.VISIBLE);
			} else {
				item.setTitle(R.string.showInfoZone);
				findViewById(R.id.table).setVisibility(View.INVISIBLE);

			}
			break;
		case 2:
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case SmartConstants.HOME_VIEW:
				Integer index = (Integer) data.getSerializableExtra("position");

				switch (index) {
				case SmartConstants.CREATE_MISSION:
					if (missionCreated) {
						missionCreated = Mission.getInstance().stopMission();

					} else {
						missionDialog = new AlertCreateMissionDialog(this);
						missionDialog.show();
					}
//					Form f = new Form("MonForm");
//					f.addField(new TextField("titi"));
//					f.addField(new NumericField("toto", 5, 10));
//					f.addField(new BooleanField("alors"));
//					List<String> l = new LinkedList<String>();
//					l.add("pouet");
//					l.add("pouet2");

					//f.addField(new ListField("liste", l));


					
					
//					Mission.createMission("ma mission thibault yoyo",
//							this, mapView, f);
//					Mission.getInstance().startMission();
//					overlayManager.add(Mission.getInstance().getPolygonLayer());
//					overlayManager.add(Mission.getInstance().getLineLayer());
//					overlayManager.add(Mission.getInstance().getPointLayer());

					break;
				case SmartConstants.CREATE_FORM:
					LayoutInflater factory = LayoutInflater.from(this);
					final View alertDialogView = factory.inflate(
							fr.umlv.lastproject.smart.R.layout.name_form,
							null);
					final AlertDialog.Builder adb = new AlertDialog.Builder(this);
				
					adb.setView(alertDialogView);
					adb.setTitle(getResources().getString(R.string.CreateForm));
					
					adb.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
							EditText et = (EditText) alertDialogView
									.findViewById(fr.umlv.lastproject.smart.R.id.nameForm);
					
							
							Intent intent = new Intent(MenuActivity.this, CreateFormActivity.class);
							intent.putExtra("nameForm", et.getText().toString());
							
							startActivity(intent);	

						}
					});

					adb.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					adb.show();
					
					break;
				case SmartConstants.POINT_SURVEY:
					Mission.getInstance().startSurvey(GeometryType.POINT);
					break;

				case SmartConstants.LINE_SURVEY:
					Mission.getInstance().startSurvey(GeometryType.LINE);
					break;

				case SmartConstants.POLYGON_SURVEY:
					Mission.getInstance().startSurvey(GeometryType.POLYGON);
					break;
				case SmartConstants.GPS_TRACK:
					if(gpsTrack==null){
						final AlertTrackDialog trackDialog=new AlertTrackDialog(this);
						trackDialog.show();
						break;
					}
					else{
						try {
							gpsTrack.stopTrack();
							gpsTrack=null;
						} catch (IOException e) {
							gpsTrack=null;
							Toast.makeText(this, R.string.track_error, Toast.LENGTH_LONG).show();
						}
						
					}
					break;
				default:
					// Mission.getInstance().stopMission();
					break;
				}

			}
		}
	}
	public void createGPSTrack(final String name, final TRACK_MODE trackMode) {
		gpsTrack=new GPSTrack(trackMode, name, locationManager, mapView);
		gpsTrack.startTrack();
		
	}

	public void startMission(final String missionName) {
		this.setMissionName(missionName);
		Mission.createMission(missionName, this, mapView, new Form());
		missionCreated = Mission.getInstance().startMission();
		overlayManager.add(Mission.getInstance().getPolygonLayer());
		overlayManager.add(Mission.getInstance().getLineLayer());
		overlayManager.add(Mission.getInstance().getPointLayer());
	}
	
	public String getMissionName() {
		return missionName;
	}

	public void setMissionName(String missionName) {
		this.missionName = missionName;
	}
}
