package fr.umlv.lastproject.smart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osmdroid.events.MapAdapter;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapController.AnimationType;
import org.osmdroid.views.overlay.DirectedLocationOverlay;
import org.osmdroid.views.overlay.OverlayManager;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import fr.umlv.lastproject.smart.GPSTrack.TRACK_MODE;
import fr.umlv.lastproject.smart.browser.utils.FileUtils;
import fr.umlv.lastproject.smart.data.DataImport;
import fr.umlv.lastproject.smart.data.TMSOverlay;
import fr.umlv.lastproject.smart.dialog.AlertCreateFormDialog;
import fr.umlv.lastproject.smart.dialog.AlertCreateMissionDialog;
import fr.umlv.lastproject.smart.dialog.AlertExitSmartDialog;
import fr.umlv.lastproject.smart.dialog.AlertGPSSettingDialog;
import fr.umlv.lastproject.smart.dialog.AlertGPSTrackDialog;
import fr.umlv.lastproject.smart.dialog.AlertHelpDialog;
import fr.umlv.lastproject.smart.dialog.AlertMeasureRequestDialog;
import fr.umlv.lastproject.smart.dialog.AlertMeasureResultDialog;
import fr.umlv.lastproject.smart.dialog.AlertModifFormDialog;
import fr.umlv.lastproject.smart.dialog.AlertSettingInfoDialog;
import fr.umlv.lastproject.smart.dialog.AlertSymbologyDialog;
import fr.umlv.lastproject.smart.dialog.AlertThemeDialog;
import fr.umlv.lastproject.smart.dialog.AlertTrackDialog;
import fr.umlv.lastproject.smart.dialog.FormDialog;
import fr.umlv.lastproject.smart.dialog.MissionDialogUtils;
import fr.umlv.lastproject.smart.dialog.WMSDialog;
import fr.umlv.lastproject.smart.form.Form;
import fr.umlv.lastproject.smart.form.FormEditedListener;
import fr.umlv.lastproject.smart.form.FormIOException;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.form.MissionListener;
import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.layers.GeometryType;
import fr.umlv.lastproject.smart.layers.LineSymbology;
import fr.umlv.lastproject.smart.layers.PointGeometry;
import fr.umlv.lastproject.smart.layers.PointSymbology;
import fr.umlv.lastproject.smart.layers.PolygonSymbology;
import fr.umlv.lastproject.smart.layers.Symbology;
import fr.umlv.lastproject.smart.survey.MeasureStopListener;
import fr.umlv.lastproject.smart.survey.Measures;
import fr.umlv.lastproject.smart.utils.SmartConstants;
import fr.umlv.lastproject.smart.utils.SmartException;

public class MenuActivity extends Activity {

	// static final int sensor = Sensor;

	/**
	 * 
	 * @author thibault Brun
	 * @author tanios Faddoul
	 * @author EVERYBODY !
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
	private boolean trackStarted = false;
	private String missionName;
	private GPSTrack gpsTrack;
	private AlertCreateMissionDialog missionDialog;
	private Preferences pref;
	private Map<Integer, Boolean> shorcutsMap;
	private List<MissionListener> missionListeners = new ArrayList<MissionListener>();
	private List<GPSTrackListener> gpsTrackListeners = new ArrayList<GPSTrackListener>();
	private Mission mission;
	private Form form;
	private Geometry geom;
	private Object[] valuesList;
	private static final int FORM_DIALOG_ID = 1;
	private static final int FORM_FILLED_DIALOG_ID = 2;
	private static final int FORM_MODIFY_DIALOG = 3;
	private int heightIndex;
	private Dialog dialog;
	private GeometryLayer geometryLayer;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		BundleCreator.savePosition(outState, mapView) ;
		BundleCreator.saveMission(outState, missionCreated);
		BundleCreator.saveGeomtryLayers(outState, mapView.getGeometryOberlays()) ;
	
		
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pref = Preferences.getInstance(this);
		setTheme(pref.theme);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_smart);
		initMap();
		if(savedInstanceState != null){
			BundleCreator.loadPosition(savedInstanceState, mapView) ;
			missionCreated = BundleCreator.loadMission(savedInstanceState, mapView, this) ;
			BundleCreator.loadGeometryLayers(savedInstanceState, this, mapView);
		}
		initGps();
		ImageView home = (ImageView) findViewById(R.id.home);
		home.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent homeActivity = new Intent(MenuActivity.this,
						HomeActivity.class);
				homeActivity.putExtra("missionCreated", missionCreated);
				homeActivity.putExtra("trackStarted", trackStarted);
				startActivityForResult(homeActivity, SmartConstants.HOME_VIEW);
			}
		});

		ImageView layers = (ImageView) findViewById(R.id.layers);
		layers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent layersActivity = new Intent(MenuActivity.this,
						LayersActivity.class);

				layersActivity.putExtra("overlays", mapView.getListOverlay());
				if (Mission.getInstance() != null
						&& Mission.getInstance().isStatus()) {
					layersActivity.putExtra("mission", Mission.getInstance()
							.getTitle());
				} else {
					String mission = null;
					layersActivity.putExtra("mission", mission);
				}

				if (gpsTrack != null && gpsTrack.isStarted()) {
					layersActivity.putExtra("mission", gpsTrack.getName());
				} else {
					String track = null;
					layersActivity.putExtra("track", track);
				}
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
		menu.add(0, 0, 0, R.string.infoSettings);
		menu.add(0, 1, 0, R.string.hideInfoZone);
		menu.add(0, 2, 0, R.string.gpsSettings);
		menu.add(0, 3, 0, R.string.help);
		menu.add(0, 4, 0, R.string.theme);

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
		// mapView.setUseDataConnection(false);

		mapView.getOverlayManager().getTilesOverlay().setEnabled(false);
		mapController.setZoom(15);
		mapController.setCenter(new GeoPoint(48.85, 2.35));

		// overlayManager.add(new ScaleBarOverlay(this));

		// final WMSMapTileProviderBasic tileProvider = new
		// WMSMapTileProviderBasic(
		// getApplicationContext());
		// final ITileSource tileSource = new WMSTileSource(
		// "WMS",
		// null,
		// 0,
		// 15,
		// 256,
		// ".png",
		// "http://sampleserver1.arcgisonline.com/arcgis/services/Specialty/ESRI_StatesCitiesRivers_USA/MapServer/WMSServer"
		// +
		// "?REQUEST=GetMap&SERVICE=WMS&VERSION=1.1.1&LAYERS=0&STYLES=default&FORMAT=image/png&BGCOLOR=0xFFFFFF&TRANSPARENT="
		// + "TRUE&SRS=EPSG:4326&WIDTH=256&HEIGHT=256&QUERY_LAYERS=0&BBOX=");
		// tileProvider.setTileSource(tileSource);
		// final TilesOverlay tilesOverlay = new TilesOverlay(tileProvider,
		// this.getBaseContext());
		//
		// tilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
		//
		// // mapView.getOverlays().add(tilesOverlay);
		//
		// mapView.getOverlays().clear();
		// // mapView.getOverlayManager().getTilesOverlay().setEnabled(false);
		// mapView.getOverlays().add(0, tilesOverlay);
		// mapView.invalidate();
		// if (true)
		// return;

		mapView.addGeoTIFFOverlay(new TMSOverlay(
				new MapTileProviderBasic(this), this, 10, 16, "geo1",
				new BoundingBoxE6(90, -180, -90, 180)));
		//
		// mapView.addGeoTIFFOverlay(new TMSOverlay(
		// new MapTileProviderBasic(this), this, 10, 16, "geo2"));

		directedLocationOverlay = new DirectedLocationOverlay(getApplicationContext());
		directedLocationOverlay.setShowAccuracy(true);
		overlayManager.add(directedLocationOverlay);

		mapView.setMapListener(new MapAdapter() {
			@Override
			public boolean onScroll(ScrollEvent event) {

				isMapTracked = false;
				centerMap.setVisibility(View.VISIBLE);
				return super.onScroll(event);
			}

			// @Override
			// public boolean onZoom(ZoomEvent event) {
			// final int oldZoom = zoomLevel;
			// final int newZoom = mapView.getZoomLevel();
			//
			// Log.d("TEST", "" + mapView.getGeoTIFFOverlays().size());
			//
			// for (TMSOverlay o : mapView.getGeoTIFFOverlays()) {
			// Log.d("TEST",
			// "" + o.getZoomLevelMin() + " / "
			// + o.getZoomLevelMax());
			// }
			//
			// int zoomEvent = event.getZoomLevel();
			// Log.d("TEST", "" + zoomEvent);
			// for (TMSOverlay overlay : mapView.getGeoTIFFOverlays()) {
			// if (zoomEvent - overlay.getZoomLevelMax() == 1
			// && oldZoom < newZoom) {
			// final AlertZoomDialog dialog = new AlertZoomDialog(
			// MenuActivity.this, true, mapView);
			// dialog.show();
			// return true;
			//
			// } else if (overlay.getZoomLevelMin() - zoomEvent == 1
			// && newZoom < oldZoom) {
			// final AlertZoomDialog dialog = new AlertZoomDialog(
			// MenuActivity.this, false, mapView);
			// dialog.show();
			// return true;
			// }
			//
			// }
			// zoomLevel = newZoom;
			// return true;
			//
			// }
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

	private void checkGPSEnabled() {
		if (!gps.isEnabled(locationManager)) {
			final AlertGPSSettingDialog gpsSettingDialog = new AlertGPSSettingDialog(
					this);
			gpsSettingDialog.show();
		}
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
				infoOverlay.updateInfos(event);

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
		case 0:
			new AlertSettingInfoDialog(this, findViewById(R.id.table),
					infoOverlay);
			break;

		case 1:
			infoOverlay.hideInfoZone(findViewById(R.id.table), item);
			break;

		case 2:
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
			break;

		case 3:
			final AlertHelpDialog helpDialog = new AlertHelpDialog(this,
					R.string.helpMap);
			helpDialog.show();
			break;

		case 4:
			if (!missionCreated) {
				new AlertThemeDialog(this, getApplication());
			} else {
				Toast.makeText(this, "Stop the mission before",
						Toast.LENGTH_LONG).show();
			}
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Object[] shortcuts = (Object[]) data
					.getSerializableExtra("shortcut");
			Integer index = (Integer) data.getSerializableExtra("position");
			createShortcut(shortcuts);

			switch (requestCode) {
			case SmartConstants.HOME_VIEW:
				doAction(index);
				break;

			case SmartConstants.LAYERS_VIEW:
				ListOverlay listOverlay = (ListOverlay) data
				.getSerializableExtra("overlays");

				// ListOverlay listOverlay = (ListOverlay) data.getExtras().get(
				// "layers");

				if ((Boolean) data.getSerializableExtra("editSymbo")) {
					// mapView.setReorderedLayers(listOverlay);
					// GeometryLayer layer = (GeometryLayer)
					// mapView.getOverlays()
					// .get((String) data
					// .getSerializableExtra("symboToEdit"));
					GeometryLayer layer = (GeometryLayer) mapView
							.getOverlay(listOverlay
									.get((Integer) data
											.getSerializableExtra("symboToEdit"))
											.getName());
					new AlertSymbologyDialog(this, layer,
							listOverlay.get((Integer) data
									.getSerializableExtra("symboToEdit")));

				}
				Collections.reverse(listOverlay.toList());
				mapView.setReorderedLayers(listOverlay);
				break;

			case SmartConstants.MISSION_BROWSER_ACTIVITY:
				formPath = data.getData().getPath();
				missionDialog.setPathForm(formPath);
				break;

			case SmartConstants.FORM_BROWSER_ACTIVITY:
				Uri file = data.getData();

				Intent sendIntent = new Intent(Intent.ACTION_SEND);
				sendIntent.setType("application/formulaire");
				sendIntent.putExtra(Intent.EXTRA_STREAM, file);
				startActivity(Intent.createChooser(sendIntent,
						"Select E-Mail Application"));

				break;

			case SmartConstants.IMPORT_KML_BROWSER_ACTIVITY:
				String kmlPath = data.getData().getPath();
				try {
					mapView.addGeometryLayers(DataImport.importKml(this,
							kmlPath));
					Toast.makeText(this, R.string.kmlImport, Toast.LENGTH_SHORT)
					.show();
				} catch (XmlPullParserException e) {
					Toast.makeText(this, R.string.kmlParseError,
							Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					Toast.makeText(this, R.string.kmlReadError,
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				break;

			case SmartConstants.IMPORT_SHP_BROWSER_ACTIVITY:
				String shpPath = data.getData().getPath();
				GeometryLayer gl = DataImport.importShapeFile(this,
						shpPath) ;
				mapView.addGeometryLayer(gl);
				mapView.getController().setCenter(gl.getExtent().getCenter()) ;
				mapView.invalidate();

				Toast.makeText(this, R.string.shpImport, Toast.LENGTH_SHORT)
				.show();
				break;

			case SmartConstants.IMPORT_TIFF_BROWSER_ACTIVITY:
				final String tiffPath = data.getData().getPath();
				final ProgressDialog progressDialog = ProgressDialog.show(this,
						getString(R.string.tiff_progress_title),
						getString(R.string.tiff_progress));
				final Thread tiffThread = new Thread(new Runnable() {

					@Override
					public void run() {
						TMSOverlay tms = null;
						try {

							tms = DataImport.importGeoTIFFFileFolder(tiffPath,
									MenuActivity.this);

						} catch (IOException e) {
							//
						}
						final TMSOverlay overlay = tms;
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								if (overlay == null) {
									Toast.makeText(MenuActivity.this,
											R.string.geotiffReadError,
											Toast.LENGTH_SHORT).show();
								} else {
									mapView.addGeoTIFFOverlay(overlay);
									Toast.makeText(MenuActivity.this,
											R.string.geotiffImport,
											Toast.LENGTH_SHORT).show();
								}

								progressDialog.dismiss();

							}
						});

					}
				});
				tiffThread.start();

				break;

			case SmartConstants.HEIGHT_ACTIVITY:
				Log.d("TESTX", "ACTIVITY RESULT");
				final Bundle bundle = data.getExtras();
				final Object oResult = bundle.get(HeightActivity.HEIGHT_RESULT);
				if (oResult == null) {
					final String error = (bundle
							.get(HeightActivity.ERROR_RESULT)) == null ? bundle
									.get(HeightActivity.ERROR_RESULT).toString()
									: getString(R.string.height_error);
									Toast.makeText(this, error, Toast.LENGTH_LONG);
									createDialog(FORM_FILLED_DIALOG_ID, new Bundle());
									return;
				}
				final double heightValue = Double.parseDouble(oResult
						.toString());
				final Bundle b = new Bundle();
				b.putDouble("height", heightValue);
				createDialog(FORM_FILLED_DIALOG_ID, b);

				break;
			case SmartConstants.HEIGHT_MODIFY_ACTIVITY:
				Log.d("TESTX", "ACTIVITY RESULT");
				final Bundle bundle2 = data.getExtras();
				final Object oResult2 = bundle2
						.get(HeightActivity.HEIGHT_RESULT);
				if (oResult2 == null) {
					final String error = (bundle2
							.get(HeightActivity.ERROR_RESULT)) == null ? bundle2
									.get(HeightActivity.ERROR_RESULT).toString()
									: getString(R.string.height_error);
									Toast.makeText(this, error, Toast.LENGTH_LONG);
									createDialog(FORM_MODIFY_DIALOG, new Bundle());
									return;
				}
				final double heightValue2 = Double.parseDouble(oResult2
						.toString());
				final Bundle b2 = new Bundle();
				b2.putDouble("height", heightValue2);
				createDialog(FORM_MODIFY_DIALOG, b2);

				break;

			}
		}
		if (resultCode == RESULT_CANCELED) {
			if (requestCode == SmartConstants.HOME_VIEW) {
				Object[] shortcuts = (Object[]) data
						.getSerializableExtra("shortcut");

				createShortcut(shortcuts);
			} else if (requestCode == SmartConstants.GPS_ACTIVITY) {
				if (!gps.isEnabled(locationManager)) {
					Toast.makeText(this, R.string.track_notstarted,
							Toast.LENGTH_LONG).show();
					return;
				}
				if (gpsTrack == null) {
					// final AlertTrackDialog trackDialog = new
					// AlertTrackDialog(
					// this);
					// trackDialog.show();
					new AlertTrackDialog(this, mapView.getListOverlay());

				} else {
					try {
						gpsTrack.stopTrack();
						gpsTrack = null;

						for (GPSTrackListener l : this.gpsTrackListeners) {
							l.actionPerformed(false);
						}

						trackStarted = false;
						Toast.makeText(this, R.string.track_stop,
								Toast.LENGTH_LONG).show();

					} catch (IOException e) {
						gpsTrack = null;
						Toast.makeText(this, R.string.track_error,
								Toast.LENGTH_LONG).show();
						trackStarted = false;

					}
				}
			} else if (requestCode == SmartConstants.HEIGHT_ACTIVITY) {
				Log.d("TESTX", "ACTIVITY RESULT CANCELED");
				createDialog(FORM_FILLED_DIALOG_ID, new Bundle());
				return;

			} else if (requestCode == SmartConstants.HEIGHT_MODIFY_ACTIVITY) {
				Log.d("TESTX", "ACTIVITY RESULT CANCELED");
				createDialog(FORM_MODIFY_DIALOG, new Bundle());
				return;

			}
		}
	}

	private void createShortcut(Object[] shortcuts) {
		if (shortcuts != null) {
			final LinearLayout shortcutsView = (LinearLayout) findViewById(R.id.shortcuts);

			for (final Object o : shortcuts) {
				final int i = ((Integer) o).intValue();
				final ImageView shortcut = new ImageView(this) {
					@Override
					public boolean showContextMenu() {
						return true;
					}
				};
				shortcut.setImageResource(SmartConstants.icons[i]);
				shortcut.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						doAction(i);
					}
				});
				shortcut.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						shortcutsView.removeView(shortcut);
						Toast.makeText(MenuActivity.this,
								R.string.shortcut_remove, Toast.LENGTH_SHORT)
								.show();
						shortcutsView.invalidate();
						return false;
					}
				});
				if (i == SmartConstants.CREATE_MISSION) {
					if (Mission.getInstance() != null
							&& Mission.getInstance().isStatus()) {
						Log.d("debug", (Mission.getInstance() + " " + Mission
								.getInstance().isStatus()));
						shortcut.setImageResource(SmartConstants.icons[SmartConstants.STOP_MISSION]);
					}
					this.addMissionListener(new MissionListener() {

						@Override
						public void actionPerformed(boolean status) {
							if (status) {
								shortcut.setImageResource(SmartConstants.icons[SmartConstants.STOP_MISSION]);
								shortcutsView.invalidate();
							} else {
								shortcut.setImageResource(SmartConstants.icons[SmartConstants.CREATE_MISSION]);
								shortcutsView.invalidate();
							}

						}
					});
				} else if (i == SmartConstants.GPS_TRACK) {
					if (gpsTrack != null && gpsTrack.isStarted()) {
						Log.d("debug", gpsTrack + " " + gpsTrack.isStarted());
						shortcut.setImageResource(SmartConstants.icons[SmartConstants.STOP_GPS_TRACK]);
					}
					this.addGPSTrackListener(new GPSTrackListener() {

						@Override
						public void actionPerformed(boolean status) {
							if (status) {
								shortcut.setImageResource(SmartConstants.icons[SmartConstants.STOP_GPS_TRACK]);
								shortcutsView.invalidate();
							} else {
								shortcut.setImageResource(SmartConstants.icons[SmartConstants.GPS_TRACK]);
								shortcutsView.invalidate();
							}

						}
					});
				}
				shortcutsView.addView(shortcut);
			}
			shortcutsView.invalidate();
		}
	}

	private void doAction(int index) {
		try {
			switch (index) {
			case SmartConstants.CREATE_MISSION:
				if (missionCreated) {
					missionCreated = Mission.getInstance().stopMission();

					for (MissionListener l : this.missionListeners) {
						l.actionPerformed(false);
					}

					Toast.makeText(this,
							getResources().getText(R.string.missionStop),
							Toast.LENGTH_LONG).show();
				} else {
					missionDialog = new AlertCreateMissionDialog(this,
							mapView.getListOverlay());
				}
				break;

			case SmartConstants.CREATE_FORM:
				new AlertCreateFormDialog(this);
				break;

			case SmartConstants.POINT_SURVEY:
				if (Mission.getInstance() == null) {
					Toast.makeText(
							this,
							getResources()
									.getText(R.string.noMissionInProgress),
							Toast.LENGTH_LONG).show();
				} else {
					Mission.getInstance().startSurvey(GeometryType.POINT);
					Toast.makeText(this,
							getResources().getText(R.string.point_survey),
							Toast.LENGTH_LONG).show();
				}
				break;

			case SmartConstants.POINT_SURVEY_POSITION:
				if (Mission.getInstance() == null) {
					Toast.makeText(
							this,
							getResources()
									.getText(R.string.noMissionInProgress),
							Toast.LENGTH_LONG).show();
				} else {
					Mission.getInstance().startSurvey(
							new PointGeometry(
									lastPosition.getLatitudeE6() / 1E6,
									lastPosition.getLongitudeE6() / 1E6));
				}
				break;

			case SmartConstants.LINE_SURVEY:
				if (Mission.getInstance() == null) {
					Toast.makeText(
							this,
							getResources()
									.getText(R.string.noMissionInProgress),
							Toast.LENGTH_LONG).show();
				} else {
					Mission.getInstance().startSurvey(GeometryType.LINE);
					Toast.makeText(this,
							getResources().getText(R.string.line_survey),
							Toast.LENGTH_LONG).show();
				}
				break;

			case SmartConstants.POLYGON_SURVEY:
				if (Mission.getInstance() == null) {
					Toast.makeText(
							this,
							getResources()
									.getText(R.string.noMissionInProgress),
							Toast.LENGTH_LONG).show();
				} else {
					Mission.getInstance().startSurvey(GeometryType.POLYGON);
					Toast.makeText(this,
							getResources().getText(R.string.polygon_survey),
							Toast.LENGTH_LONG).show();
				}
				break;

			case SmartConstants.GPS_TRACK:
				// final AlertTrackDialog trackDialog = new
				// AlertTrackDialog(this);
				File trackfolder = new File(SmartConstants.TRACK_PATH);
				trackfolder.mkdir();
				if (!gps.isEnabled(locationManager)) {
					final AlertGPSTrackDialog gpsTrackDialog = new AlertGPSTrackDialog(
							this);
					gpsTrackDialog.show();
					return;
				}
				if (gpsTrack == null) {
					// trackDialog.show();
					new AlertTrackDialog(this, mapView.getListOverlay());
					break;
				} else {
					try {
						gpsTrack.stopTrack();
						gpsTrack = null;

						for (GPSTrackListener l : this.gpsTrackListeners) {
							l.actionPerformed(false);
						}

						trackStarted = false;
						Toast.makeText(this, R.string.track_stop,
								Toast.LENGTH_LONG).show();
					} catch (IOException e) {
						gpsTrack = null;

						Toast.makeText(this, R.string.track_error,
								Toast.LENGTH_LONG).show();
						trackStarted = false;
					}

					trackStarted = false;
					Toast.makeText(this, R.string.track_stop, Toast.LENGTH_LONG)
					.show();
				} 
			
				break;

			case SmartConstants.IMPORT_KML:
				Intent importKMLIntent = FileUtils.createGetContentIntent(
						FileUtils.KML_TYPE,
						Environment.getExternalStorageDirectory() + "");
				startActivityForResult(importKMLIntent,
						SmartConstants.IMPORT_KML_BROWSER_ACTIVITY);
				break;

			case SmartConstants.IMPORT_SHAPE:
				Intent importSHPItent = FileUtils.createGetContentIntent(
						FileUtils.SHP_TYPE,
						Environment.getExternalStorageDirectory() + "");
				startActivityForResult(importSHPItent,
						SmartConstants.IMPORT_SHP_BROWSER_ACTIVITY);
				break;

			case SmartConstants.IMPORT_GEOTIFF:
				Intent importTiffItent = FileUtils.createGetContentIntent(
						FileUtils.TIF_TYPE,
						Environment.getExternalStorageDirectory() + "");
				startActivityForResult(importTiffItent,
						SmartConstants.IMPORT_TIFF_BROWSER_ACTIVITY);
				break;

			case SmartConstants.IMPORT_WMS:
				final WMSDialog dialog = new WMSDialog(this);
				dialog.show();
				break;

			case SmartConstants.EXPORT_MISSION:
				MissionDialogUtils.showExportDialog(this);
				break;

			case SmartConstants.MEASURE:
				AlertMeasureRequestDialog amrd = new AlertMeasureRequestDialog(
						this);
				amrd.show();
				break;

			case SmartConstants.EXPORT_FORM:
				Intent intentForm = FileUtils.createGetContentIntent(
						FileUtils.FORM_TYPE, SmartConstants.APP_PATH);
				startActivityForResult(intentForm,
						SmartConstants.FORM_BROWSER_ACTIVITY);
				break;

			case SmartConstants.DELETE_MISSION:
				MissionDialogUtils.showDeleteDialog(this);
				break;

			default:
				// Mission.getInstance().stopMission();
				break;
			}
		} catch (SmartException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void createGPSTrack(final String name, final TRACK_MODE trackMode) {
		gpsTrack = new GPSTrack(trackMode, name, locationManager, mapView);
		gpsTrack.startTrack();
		Toast.makeText(this, R.string.track_started, Toast.LENGTH_LONG).show();
		for (GPSTrackListener l : this.gpsTrackListeners) {
			l.actionPerformed(true);
		}

		trackStarted = true;
	}

	public void startMission(final String missionName) {
		this.setMissionName(missionName);
		Form form = new Form();
		if (formPath != null) {
			try {
				form = Form.read(formPath);
			} catch (FormIOException e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();

			}
		}
		Mission.createMission(missionName, MenuActivity.this, mapView, form);
		missionCreated = Mission.getInstance().startMission();

		for (MissionListener l : this.missionListeners) {
			l.actionPerformed(true);
		}

		mapView.addGeometryLayer(Mission.getInstance().getPolygonLayer());
		mapView.addGeometryLayer(Mission.getInstance().getLineLayer());
		mapView.addGeometryLayer(Mission.getInstance().getPointLayer());
	}

	public String getMissionName() {
		return missionName;
	}

	public void setMissionName(final String missionName) {
		this.missionName = missionName;
	}

	public void measure(boolean absolute) {
		final MenuActivity ma = this;

		final Measures m = new Measures(mapView);

		m.addStopListener(new MeasureStopListener() {
			@Override
			public void actionPerformed(double distance) {
				AlertMeasureResultDialog amrd = new AlertMeasureResultDialog(
						ma, distance);
				amrd.show();
				m.stop();
			}
		});

		if (absolute) {
			m.measure();
		} else {
			m.measure(new PointGeometry(lastPosition.getLatitudeE6() / 1E6,
					lastPosition.getLongitudeE6() / 1E6));
		}
	}

	public SmartMapView getMapView() {
		return this.mapView;
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.getTileProvider().clearTileCache() ;
		pref.save();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cleanTIFFFolder();
		cleanTmpFolder();
		mapView.getTileProvider().clearTileCache() ;
		System.gc() ;
	}

	private void cleanTmpFolder() {
		final List<File> files = FileUtils.getFileList(SmartConstants.TMP_PATH);
		for (File file : files) {

			file.delete();

		}

	}

	private void cleanTIFFFolder() {
		final List<File> files = FileUtils
				.getFileList(SmartConstants.TIFF_PATH);
		for (File file : files) {
			if (".zip".equals(FileUtils.getExtension(file.getPath()))) {
				file.delete();
			}
		}

	}

	public void startHeightActivityForResult(Form form, Mission miss,
			Geometry geom, Object[] valuesList, int heightIndex) {
		this.form = form;
		this.mission = miss;
		this.geom = geom;
		this.valuesList = valuesList;
		this.heightIndex = heightIndex;

		startActivityForResult(new Intent(this, HeightActivity.class),
				SmartConstants.HEIGHT_ACTIVITY);
	}

	public void createFormDialog(Form form, Geometry g, Mission mission) {
		this.form = form;
		this.mission = mission;
		this.geom = g;
		createDialog(FORM_DIALOG_ID, null);

	}

	private void createDialog(int id, Bundle bundle) {
		if (dialog != null) {
			dialog.dismiss();
		}
		if (id == FORM_DIALOG_ID) {

			// Simple Form Dialog
			if (bundle == null) {
				final FormDialog formDialog = new FormDialog(this, form, geom,
						mission);
				dialog = formDialog.create();
				Log.d("TESTX", "CREATE DIALOG");
				dialog.show();
			}
			// Retrieve what user already filled
		} else if (id == FORM_FILLED_DIALOG_ID) {

			if (bundle.get("height") != null) {
				double height = bundle.getDouble("height");
				valuesList[heightIndex] = height;
			}

			final FormDialog dialogFilled = new FormDialog(this, form, geom,
					mission, valuesList);
			this.dialog = dialogFilled.create();
			dialog.show();

		} else if (id == FORM_MODIFY_DIALOG) {

			if (bundle == null) {
				AlertModifFormDialog modifyDialog = new AlertModifFormDialog(
						this, this.form, this.geom, this.geometryLayer);
				modifyDialog.addFormEditedListener(new FormEditedListener() {

					@Override
					public void actionPerformed(Geometry g) {
						g.setSelected(false);
						mission.setSelectable(true);
						mapView.invalidate();

					}
				});
				this.dialog = modifyDialog.create();
				dialog.show();
			} else {
				double height = 0;
				if (bundle.get("height") != null) {
					height = bundle.getDouble("height");
					valuesList[heightIndex] = height;
				}

				final AlertModifFormDialog modifyDialog = new AlertModifFormDialog(
						this, form, geom, geometryLayer, valuesList);

				modifyDialog.addFormEditedListener(new FormEditedListener() {

					@Override
					public void actionPerformed(Geometry g) {
						g.setSelected(false);
						mission.setSelectable(true);
						mapView.invalidate();

					}
				});
				this.dialog = modifyDialog.create();
				dialog.show();
			}

		}

	}

	/**
	 * 
	 * @param listener
	 */
	public void addMissionListener(MissionListener listener) {
		missionListeners.add(listener);
	}

	/**
	 * 
	 * @param listener
	 */
	public void removeMissionListener(MissionListener listener) {
		missionListeners.remove(listener);
	}

	/**
	 * 
	 * @param listener
	 */
	public void addGPSTrackListener(GPSTrackListener listener) {
		gpsTrackListeners.add(listener);
	}

	/**
	 * 
	 * @param listener
	 */
	public void removeGPSTrackListener(GPSTrackListener listener) {
		gpsTrackListeners.remove(listener);
	}

	public void startModifHeightActivityForResult(GeometryLayer layer,
			Form form2, Geometry geom2, Object[] valuesList2, int heightIndex2) {

		this.geom = geom2;
		this.form = form2;
		this.valuesList = valuesList2;
		this.heightIndex = heightIndex2;
		this.geometryLayer = layer;

		startActivityForResult(new Intent(this, HeightActivity.class),
				SmartConstants.HEIGHT_MODIFY_ACTIVITY);
	}

	public void createModifFormDialog(Form form2, Geometry g, GeometryLayer l,
			Mission m) {
		this.form = form2;
		this.geometryLayer = l;
		this.geom = g;
		this.mission = m;
		createDialog(FORM_MODIFY_DIALOG, null);
	}
	
	@Override
	protected void onStop() {
		mapView.getTileProvider().clearTileCache() ;
		System.gc();
		super.onStop();
	}
	
	
	
}
