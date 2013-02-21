package fr.umlv.lastproject.smart;

import java.io.IOException;
import java.util.List;

import org.osmdroid.events.MapAdapter;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.DirectedLocationOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayManager;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;
import fr.umlv.lastproject.smart.GPSTrack.TRACK_MODE;
import fr.umlv.lastproject.smart.browser.utils.FileUtils;
import fr.umlv.lastproject.smart.dataimport.DataImport;
import fr.umlv.lastproject.smart.dialog.AlertCreateFormDialog;
import fr.umlv.lastproject.smart.dialog.AlertCreateMissionDialog;
import fr.umlv.lastproject.smart.dialog.AlertExitSmartDialog;
import fr.umlv.lastproject.smart.dialog.AlertExportCSVDialog;
import fr.umlv.lastproject.smart.dialog.AlertGPSSettingDialog;
import fr.umlv.lastproject.smart.dialog.AlertTrackDialog;
import fr.umlv.lastproject.smart.dialog.AlertZoomDialog;
import fr.umlv.lastproject.smart.form.Form;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.form.PictureActivity;
import fr.umlv.lastproject.smart.geotiff.TMSOverlay;
import fr.umlv.lastproject.smart.layers.Geometry.GeometryType;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.layers.PolygonSymbology;
import fr.umlv.lastproject.smart.utils.SmartConstants;

public class MenuActivity extends Activity {

	/**
	 * 
	 * @author thibault Brun
	 * @author tanios Faddoul
	 * 
	 * @Description : This class contains the Menus container
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

	private String formPath;

	private boolean missionCreated = false;

	private String missionName;

	private GPSTrack gpsTrack;

	private AlertCreateMissionDialog missionDialog;

	private int zoomLevel;

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
				layersActivity.putExtra("overlays", mapView.getListOverlay());
				startActivityForResult(layersActivity,
						SmartConstants.LAYERS_VIEW);
			}
		});

		// importKML();
	}

	private void importKML() {
		// import kml
		try {
			List<GeometryLayer> kmls = DataImport.importKml(this, Environment
					.getExternalStorageDirectory().getPath()
					+ "/SMART/test.kml");
			Log.d("TEST2", "" + kmls.size());
			mapView.addGeometryLayers(kmls);

			GeometryLayer kml = DataImport.importKml(this, Environment
					.getExternalStorageDirectory().getPath()
					+ "/SMART/poly.kml", GeometryType.POLYGON);
			kml.setSymbology(new PolygonSymbology(3, 0xffff0000));

			mapView.addGeometryLayer(kml);

		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Couche pr�sentes
		String s = "getOverlays() size = "
				+ String.valueOf(mapView.getOverlays().size()) + "\ndata = ";
		for (int i = 0; i < mapView.getOverlays().size(); i++) {
			s += ((Overlay) mapView.getOverlays().get(i)).toString() + " ";
		}
		Log.d("debug", s);
		Log.d("debug", "getListOverlay() size = "
				+ mapView.getListOverlay().size() + "\ndata = "
				+ mapView.getListOverlay().toString());
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
		zoomLevel = mapView.getZoomLevel();
		mapController = mapView.getController();
		overlayManager = mapView.getOverlayManager();
		mapView.setTileSource(TileSourceFactory.MAPNIK);
		mapView.setClickable(true);
		mapView.setMultiTouchControls(true);
		mapController.setZoom(SmartConstants.DEFAULT_ZOOM);
		overlayManager.add(new ScaleBarOverlay(this));

		// mapView.addGeoTIFFOverlay(new TMSOverlay(
		// new MapTileProviderBasic(this), this, 10, 16, "geo1"));
		//
		// mapView.addGeoTIFFOverlay(new TMSOverlay(
		// new MapTileProviderBasic(this), this, 10, 16, "geo2"));

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

			@Override
			public boolean onZoom(ZoomEvent event) {
				final int oldZoom = zoomLevel;
				final int newZoom = mapView.getZoomLevel();

				Log.d("TEST", "" + mapView.getGeoTIFFOverlays().size());

				for (TMSOverlay o : mapView.getGeoTIFFOverlays()) {
					Log.d("TEST",
							"" + o.getZoomLevelMin() + " / "
									+ o.getZoomLevelMax());
				}

				int zoomEvent = event.getZoomLevel();
				Log.d("TEST", "" + zoomEvent);
				for (TMSOverlay overlay : mapView.getGeoTIFFOverlays()) {
					if (zoomEvent - overlay.getZoomLevelMax() == 1
							&& oldZoom < newZoom) {
						final AlertZoomDialog dialog = new AlertZoomDialog(
								MenuActivity.this, true, mapView);
						dialog.show();
						return true;

					} else if (overlay.getZoomLevelMin() - zoomEvent == 1
							&& newZoom < oldZoom) {
						final AlertZoomDialog dialog = new AlertZoomDialog(
								MenuActivity.this, false, mapView);
						dialog.show();
						return true;
					}

				}
				zoomLevel = newZoom;
				return true;

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
					break;
				case SmartConstants.CREATE_FORM:
					AlertCreateFormDialog createFormDialog = new AlertCreateFormDialog(
							this);
					createFormDialog.show();

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
					if (gpsTrack == null) {
						final AlertTrackDialog trackDialog = new AlertTrackDialog(
								this);
						trackDialog.show();
						break;
						
					} else {
						try {
							gpsTrack.stopTrack();
							gpsTrack = null;
						} catch (IOException e) {
							gpsTrack = null;
							Toast.makeText(this, R.string.track_error,
									Toast.LENGTH_LONG).show();
						}

					}
					break;
					
				case SmartConstants.EXPORT_CSV:
					AlertExportCSVDialog exportCSVDialog = new AlertExportCSVDialog(
							this);
					exportCSVDialog.show();
					break;

				case SmartConstants.EXPORT_KML:
					Intent intent = new Intent(MenuActivity.this,
							PictureActivity.class);
					startActivityForResult(intent, 10);
					break;

				case SmartConstants.EXPORT_FORM:
					Intent intentForm = FileUtils.createGetContentIntent(
							FileUtils.XML_TYPE,
							Environment.getExternalStorageDirectory() + "");
					startActivityForResult(intentForm,
							SmartConstants.FORM_BROWSER_ACTIVITY);
					break;

				default:
					// Mission.getInstance().stopMission();
					break;
				}
				break;

			case SmartConstants.LAYERS_VIEW:
				ListOverlay listOverlay = (ListOverlay) data
						.getSerializableExtra("layers");
				mapView.setReorderedLayers(listOverlay);
				break;

			case SmartConstants.MISSION_BROWSER_ACTIVITY:
				Uri fileForm = data.getData();
				formPath = fileForm.toString().split("file:///")[1];
				missionDialog.setPathForm(formPath);

				break;
			case SmartConstants.FORM_BROWSER_ACTIVITY:
				Uri file = data.getData();
				
				Intent sendIntent = new Intent(Intent.ACTION_SEND);
		        sendIntent.setType("application/formulaire");
		        sendIntent.putExtra(Intent.EXTRA_STREAM, file);
		        startActivity(Intent.createChooser(sendIntent, "Select E-Mail Application"));

				break;
			}

		}
	}

	public void createGPSTrack(final String name, final TRACK_MODE trackMode) {
		gpsTrack = new GPSTrack(trackMode, name, locationManager, mapView);
		gpsTrack.startTrack();

	}

	public void startMission(final String missionName) {
		this.setMissionName(missionName);
		Form form = new Form();
		if (formPath != null) {
			try {
				form.read(formPath);
			} catch (IOException e) {
				Log.d("TEST2", e.getMessage());
			} catch (XmlPullParserException e) {
				Log.d("TEST2", e.getMessage());
			}
		}

		Mission.createMission(missionName, MenuActivity.this, mapView, form);
		missionCreated = Mission.getInstance().startMission();
		overlayManager.add(Mission.getInstance().getPolygonLayer());
		overlayManager.add(Mission.getInstance().getLineLayer());
		overlayManager.add(Mission.getInstance().getPointLayer());
	}

	public String getMissionName() {
		return missionName;
	}

	public void setMissionName(final String missionName) {
		this.missionName = missionName;
	}
}
