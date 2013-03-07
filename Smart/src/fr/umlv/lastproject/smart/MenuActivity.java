package fr.umlv.lastproject.smart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.DirectedLocationOverlay;
import org.osmdroid.views.overlay.OverlayManager;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import fr.umlv.lastproject.smart.GPSTrack.TRACK_MODE;
import fr.umlv.lastproject.smart.browser.utils.FileUtils;
import fr.umlv.lastproject.smart.data.DataImport;
import fr.umlv.lastproject.smart.data.TMSOverlay;
import fr.umlv.lastproject.smart.dialog.AboutDialog;
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
import fr.umlv.lastproject.smart.form.AlertPolygonTrackDialog;
import fr.umlv.lastproject.smart.form.Form;
import fr.umlv.lastproject.smart.form.FormEditedListener;
import fr.umlv.lastproject.smart.form.FormIOException;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.form.MissionListener;
import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.layers.GeometryType;
import fr.umlv.lastproject.smart.layers.PointGeometry;
import fr.umlv.lastproject.smart.layers.PolygonGeometry;
import fr.umlv.lastproject.smart.layers.PolygonSymbology;
import fr.umlv.lastproject.smart.survey.MeasureStopListener;
import fr.umlv.lastproject.smart.survey.Measures;
import fr.umlv.lastproject.smart.survey.Survey;
import fr.umlv.lastproject.smart.survey.SurveyStopListener;
import fr.umlv.lastproject.smart.utils.PolygonArea;
import fr.umlv.lastproject.smart.utils.SmartConstants;
import fr.umlv.lastproject.smart.utils.SmartException;
import fr.umlv.lastproject.smart.utils.SmartLogger;

/**
 * 
 * @author thibault Brun
 * @author tanios Faddoul
 * @author EVERYBODY !
 * 
 * @Description : This class contains the Menus container
 * 
 */
public class MenuActivity extends Activity {

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
	private boolean polygonTrackStarted = false;
	private String missionName;
	private GPSTrack gpsTrack;
	private GPSTrack polygonTrack;
	private AlertCreateMissionDialog missionDialog;
	private Preferences pref;
	private List<MissionListener> missionListeners = new ArrayList<MissionListener>();
	private List<GPSTrackListener> gpsTrackListeners = new ArrayList<GPSTrackListener>();
	private List<PolygonTrackListener> polygonTrackListeners = new ArrayList<PolygonTrackListener>();
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
	private MenuAction trackType;

	private final Logger logger = SmartLogger.getLocator().getLogger();

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		BundleCreator.savePosition(outState, mapView);
		BundleCreator.saveMission(outState, missionCreated);

		BundleCreator
				.saveGeomtryLayers(outState, mapView.getGeometryOverlays());
		logger.log(Level.INFO, "Saving the application bundle");
		BundleCreator.saveGeotiffs(outState, mapView.getGeoTIFFOverlays());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			pref = Preferences.getInstance(this);
		} catch (PreferencesException e) {
			Toast.makeText(this, getString(R.string.unableLoadPref),
					Toast.LENGTH_LONG).show();
			return;
		}

		setTheme(pref.theme);

		File f = new File(SmartConstants.APP_PATH);
		f.mkdir();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_smart);

		initMap();

		if (savedInstanceState != null) {
			BundleCreator.loadPosition(savedInstanceState, mapView);
			missionCreated = BundleCreator.loadMission(savedInstanceState,
					mapView, this);
			BundleCreator.loadGeometryLayers(savedInstanceState, this, mapView);
			BundleCreator.loadGeotiffs(savedInstanceState, mapView, this);
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
				homeActivity.putExtra("polygonTrackStarted",
						polygonTrackStarted);
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

		for (Integer menuAction : pref.shortcuts) {
			showShortcut(MenuAction.getFromId(menuAction));
		}
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
		menu.add(0, 3, 0, R.string.theme);
		menu.add(0, 4, 0, R.string.help);
		menu.add(0, 5, 0, R.string.about);

		return true;
	}

	/**
	 * This method is used to init the map
	 */
	public void initMap() {
		logger.log(Level.INFO, "Init application map");
		mapView = (SmartMapView) findViewById(R.id.mapview);
		mapController = mapView.getController();
		overlayManager = mapView.getOverlayManager();
		mapView.setTileSource(TileSourceFactory.MAPNIK);
		mapView.setClickable(true);
		mapView.setMultiTouchControls(true);
		mapController.setZoom(15);
		mapController.setCenter(new GeoPoint(48.85, 2.35));
		directedLocationOverlay = new DirectedLocationOverlay(
				getApplicationContext());
		directedLocationOverlay.setShowAccuracy(true);
		overlayManager.add(directedLocationOverlay);
		mapView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				isMapTracked = false;
				centerMap.setVisibility(View.VISIBLE);
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

	}

	// private void checkGPSEnabled() {
	// if (!gps.isEnabled(locationManager)) {
	// final AlertGPSSettingDialog gpsSettingDialog = new AlertGPSSettingDialog(
	// this);
	// gpsSettingDialog.show();
	// }
	// }

	/**
	 * This method is use to connect the GPS to the positionOverlay
	 */
	public void initGps() {
		logger.log(Level.INFO, "Init application GPS");
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
			if (!missionCreated) {
				new AlertThemeDialog(this);
			} else {
				Toast.makeText(this, "Stop the mission before",
						Toast.LENGTH_LONG).show();
			}
			break;

		case 4:
			final AlertHelpDialog helpDialog = new AlertHelpDialog(this,
					R.string.helpMap);
			helpDialog.show();
			break;

		case 5:
			final AboutDialog about = new AboutDialog(this);
			about.show();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			// ArrayList<Integer> shortcuts = data
			// .getIntegerArrayListExtra("shortcut");
			// for (Integer i : shortcuts) {
			// createShortcut(MenuAction.getFromId(i));
			// }

			Integer index = (Integer) data.getSerializableExtra("position");
			switch (requestCode) {
			case SmartConstants.HOME_VIEW:
				doAction(MenuAction.getFromId(index));
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
				logger.log(Level.INFO,
						"Getting mission path from brower activity : "
								+ formPath);
				missionDialog.setPathForm(formPath);
				break;

			case SmartConstants.FORM_BROWSER_ACTIVITY:
				logger.log(Level.INFO, "Export form by email");
				Uri file = data.getData();

				Intent sendIntent = new Intent(Intent.ACTION_SEND);
				sendIntent.setType("application/formulaire");
				sendIntent.putExtra(Intent.EXTRA_STREAM, file);
				startActivity(Intent.createChooser(sendIntent,
						"Select E-Mail Application"));

				break;

			case SmartConstants.IMPORT_KML_SHP_BROWSER_ACTIVITY:
				String path = data.getData().getPath();
				String extension = FileUtils.getExtension(path);
				if (extension.equalsIgnoreCase(".kml")) {
					try {
						mapView.addGeometryLayers(DataImport.importKml(this,
								path));
						Toast.makeText(this, R.string.kmlImport,
								Toast.LENGTH_SHORT).show();
					} catch (XmlPullParserException e) {
						Toast.makeText(this, R.string.kmlParseError,
								Toast.LENGTH_SHORT).show();
					} catch (IOException e) {
						Toast.makeText(this, R.string.kmlReadError,
								Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				} else if (extension.equalsIgnoreCase(".shp")) {
					GeometryLayer gl = DataImport.importShapeFile(this, path);
					mapView.addGeometryLayer(gl);
					mapView.getController().setCenter(
							gl.getExtent().getCenter());
					mapView.invalidate();

					Toast.makeText(this, R.string.shpImport, Toast.LENGTH_SHORT)
							.show();
				}
				break;

			// case SmartConstants.IMPORT_SHP_BROWSER_ACTIVITY:
			// String shpPath = data.getData().getPath();
			// GeometryLayer gl = DataImport.importShapeFile(this,
			// shpPath) ;
			// mapView.addGeometryLayer(gl);
			// mapView.getController().setCenter(gl.getExtent().getCenter()) ;
			// mapView.invalidate();
			//
			// Toast.makeText(this, R.string.shpImport, Toast.LENGTH_SHORT)
			// .show();
			// break;

			case SmartConstants.IMPORT_TIFF_BROWSER_ACTIVITY:
				final String tiffPath = data.getData().getPath();
				final ProgressDialog progressDialog = ProgressDialog.show(this,
						getString(R.string.tiff_progress_title),
						getString(R.string.tiff_progress));
				logger.log(Level.INFO,
						"Getting TIFF path from browser activity :" + tiffPath);
				final Thread tiffThread = new Thread(new Runnable() {

					@Override
					public void run() {
						TMSOverlay tms = null;
						try {

							tms = DataImport.importGeoTIFFFileFolder(tiffPath,
									MenuActivity.this);
							logger.log(Level.INFO,
									"Trying to import TIFF file :" + tiffPath);

						} catch (IOException e) {
							logger.log(Level.SEVERE,
									"TIFF file can't be imported :" + tiffPath);
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
									mapView.getController().setCenter(
											overlay.getExtent().getCenter());
									mapView.getController().setZoom(12);
								}

								progressDialog.dismiss();

							}
						});

					}
				});
				tiffThread.start();
				break;

			case SmartConstants.HEIGHT_ACTIVITY:
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
				ArrayList<Integer> shortcuts = data
						.getIntegerArrayListExtra("shortcut");
				for (Integer i : shortcuts) {
					createShortcut(MenuAction.getFromId(i));
				}
			} else if (requestCode == SmartConstants.GPS_ACTIVITY) {
				if (!gps.isEnabled(locationManager)) {
					logger.log(Level.WARNING, "GPS want to be enabled");
					Toast.makeText(this, R.string.track_notstarted,
							Toast.LENGTH_LONG).show();
					return;
				}

				switch (trackType) {
				case GPS_TRACK:
					if (gpsTrack == null) {
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
							logger.log(Level.SEVERE, "Error on the track stop");
							gpsTrack = null;
							Toast.makeText(this, R.string.track_error,
									Toast.LENGTH_LONG).show();
							trackStarted = false;
						}
					}
					break;
				case POLYGON_TRACK:
					if (polygonTrack == null) {
						new AlertPolygonTrackDialog(this);
					} else {
						try {
							polygonTrack.stopTrack();
							Mission.getInstance().trackInProgress(false);
							polygonTrack = null;

							for (PolygonTrackListener l : this.polygonTrackListeners) {
								l.actionPerformed(false);
							}

							polygonTrackStarted = false;
							Toast.makeText(this, R.string.polygon_track_stoped,
									Toast.LENGTH_LONG).show();
						} catch (IOException e) {
							polygonTrack = null;
							Toast.makeText(this, R.string.track_error,
									Toast.LENGTH_LONG).show();
							polygonTrackStarted = false;
						}
					}
					break;
				default:
					throw new IllegalStateException("MenuAction "
							+ trackType.toString() + " not handled");
				}
			} else if (requestCode == SmartConstants.HEIGHT_ACTIVITY) {
				createDialog(FORM_FILLED_DIALOG_ID, new Bundle());
				return;

			} else if (requestCode == SmartConstants.HEIGHT_MODIFY_ACTIVITY) {
				createDialog(FORM_MODIFY_DIALOG, new Bundle());
				return;
			}
		}
	}

	private void createShortcut(final MenuAction menuItem) {
		pref.shortcuts.add(menuItem.getId());
		showShortcut(menuItem);
	}

	private void showShortcut(final MenuAction menuAction) {
		final LinearLayout shortcutsView = (LinearLayout) findViewById(R.id.shortcuts);
		final ImageView shortcut = new ImageView(this) {

			@Override
			public boolean showContextMenu() {
				return true;
			}
		};
		shortcut.setImageResource(SmartConstants.icons[menuAction.getId()]);
		shortcut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doAction(menuAction);
			}
		});
		shortcut.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				shortcutsView.removeView(shortcut);
				pref.shortcuts.remove(Integer.valueOf(menuAction.getId()));
				Toast.makeText(MenuActivity.this, R.string.shortcut_remove,
						Toast.LENGTH_SHORT).show();
				shortcutsView.invalidate();
				return false;
			}
		});

		switch (menuAction) {
		case CREATE_MISSION:
			if (Mission.getInstance() != null
					&& Mission.getInstance().isStatus()) {
				Log.d("debug", (Mission.getInstance() + " " + Mission
						.getInstance().isStatus()));
				shortcut.setImageResource(SmartConstants.icons[MenuAction.STOP_MISSION
						.getId()]);
			}

			this.addMissionListener(new MissionListener() {

				@Override
				public void actionPerformed(boolean status) {
					if (status) {
						shortcut.setImageResource(SmartConstants.icons[MenuAction.STOP_MISSION
								.getId()]);
						shortcutsView.invalidate();
					} else {
						shortcut.setImageResource(SmartConstants.icons[MenuAction.CREATE_MISSION
								.getId()]);
						shortcutsView.invalidate();
					}

				}
			});
			break;

		case GPS_TRACK:
			if (gpsTrack != null && gpsTrack.isStarted()) {
				Log.d("debug", gpsTrack + " " + gpsTrack.isStarted());
				shortcut.setImageResource(SmartConstants.icons[MenuAction.STOP_GPS_TRACK
						.getId()]);
			}
			this.addGPSTrackListener(new GPSTrackListener() {

				@Override
				public void actionPerformed(boolean status) {
					if (status) {
						shortcut.setImageResource(SmartConstants.icons[MenuAction.STOP_GPS_TRACK
								.getId()]);
						shortcutsView.invalidate();
					} else {
						shortcut.setImageResource(SmartConstants.icons[MenuAction.GPS_TRACK
								.getId()]);
						shortcutsView.invalidate();
					}
				}
			});
			break;
		case POLYGON_TRACK:
			if (polygonTrack != null && polygonTrack.isStarted()) {
				Log.d("debug", polygonTrack + " " + polygonTrack.isStarted());
				shortcut.setImageResource(SmartConstants.icons[MenuAction.STOP_POLYGON_TRACK
						.getId()]);
			}
			this.addPolygonTrackListener(new PolygonTrackListener() {

				@Override
				public void actionPerformed(boolean status) {
					if (status) {
						shortcut.setImageResource(SmartConstants.icons[MenuAction.STOP_POLYGON_TRACK
								.getId()]);
						shortcutsView.invalidate();
					} else {
						shortcut.setImageResource(SmartConstants.icons[MenuAction.POLYGON_TRACK
								.getId()]);
						shortcutsView.invalidate();
					}

				}
			});
		}
		shortcutsView.addView(shortcut);
		shortcutsView.invalidate();
	}

	private void doAction(MenuAction index) {
		try {
			switch (index) {
			case CREATE_MISSION:
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

			case CREATE_FORM:
				new AlertCreateFormDialog(this);
				break;

			case POINT_SURVEY:
				if (Mission.getInstance() == null) {
					logger.log(Level.WARNING,
							"Impossible point survey : mission not created");
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

			case POINT_SURVEY_POSITION:
				if (Mission.getInstance() == null) {
					logger.log(Level.WARNING,
							"Impossible position point survey : mission not created");
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

			case LINE_SURVEY:
				if (Mission.getInstance() == null) {
					logger.log(Level.WARNING,
							"Impossible line survey : mission not created");
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

			case POLYGON_SURVEY:
				if (Mission.getInstance() == null) {
					logger.log(Level.WARNING,
							"Impossible polygon survey : mission not created");
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

			case GPS_TRACK:
				File trackfolder = new File(SmartConstants.TRACK_PATH);
				trackfolder.mkdir();
				if (!gps.isEnabled(locationManager)) {
					final AlertGPSTrackDialog gpsTrackDialog = new AlertGPSTrackDialog(
							this);
					trackType = MenuAction.GPS_TRACK;
					gpsTrackDialog.show();
					return;
				}
				if (gpsTrack == null) {
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
						logger.log(Level.SEVERE, "Error while stopping track");
						Toast.makeText(this, R.string.track_error,
								Toast.LENGTH_LONG).show();
						trackStarted = false;
					}

					trackStarted = false;
					Toast.makeText(this, R.string.track_stop, Toast.LENGTH_LONG)
							.show();
				}
				break;

			case POLYGON_TRACK:
				if (Mission.getInstance() == null) {
					Toast.makeText(
							this,
							getResources()
									.getText(R.string.noMissionInProgress),
							Toast.LENGTH_LONG).show();
				} else {
					if (!gps.isEnabled(locationManager)) {
						final AlertGPSTrackDialog gpsTrackDialog = new AlertGPSTrackDialog(
								this);
						trackType = MenuAction.POLYGON_TRACK;
						gpsTrackDialog.show();
						return;
					}
					if (polygonTrack == null) {
						new AlertPolygonTrackDialog(this);
						Mission.getInstance().trackInProgress(true);
						break;
					} else {
						try {
							polygonTrack.stopTrack();
							Mission.getInstance().trackInProgress(false);
							polygonTrack = null;

							for (PolygonTrackListener l : this.polygonTrackListeners) {
								l.actionPerformed(false);
							}

							polygonTrackStarted = false;
							Toast.makeText(this, R.string.polygon_track_stoped,
									Toast.LENGTH_LONG).show();
						} catch (IOException e) {
							polygonTrack = null;

							Toast.makeText(this, R.string.track_error,
									Toast.LENGTH_LONG).show();
							polygonTrackStarted = false;
						}
					}
				}
				break;

			case IMPORT_KML_SHP:
				Intent importKMLIntent = FileUtils.createGetContentIntent(
						FileUtils.KML_SHP_TYPE,
						Environment.getExternalStorageDirectory() + "");
				startActivityForResult(importKMLIntent,
						SmartConstants.IMPORT_KML_SHP_BROWSER_ACTIVITY);
				break;

			// case SmartConstants.IMPORT_SHAPE:
			// Intent importSHPItent = FileUtils.createGetContentIntent(
			// FileUtils.SHP_TYPE,
			// Environment.getExternalStorageDirectory() + "");
			// startActivityForResult(importSHPItent,
			// SmartConstants.IMPORT_SHP_BROWSER_ACTIVITY);
			// break;

			case IMPORT_GEOTIFF:
				Intent importTiffItent = FileUtils.createGetContentIntent(
						FileUtils.TIF_TYPE,
						Environment.getExternalStorageDirectory() + "");
				startActivityForResult(importTiffItent,
						SmartConstants.IMPORT_TIFF_BROWSER_ACTIVITY);
				break;

			case IMPORT_WMS:
				final WMSDialog dialog = new WMSDialog(this);
				dialog.show();
				break;

			case MEASURE:
				AlertMeasureRequestDialog amrd = new AlertMeasureRequestDialog(
						this);
				amrd.show();
				break;

			case EXPORT_MISSION:
				MissionDialogUtils.showExportDialog(this);
				break;

			case EXPORT_FORM:
				Intent intentForm = FileUtils.createGetContentIntent(
						FileUtils.FORM_TYPE, SmartConstants.APP_PATH);
				startActivityForResult(intentForm,
						SmartConstants.FORM_BROWSER_ACTIVITY);
				break;

			case DELETE_MISSION:
				MissionDialogUtils.showDeleteDialog(this);
				break;

			case AREA_MEASURE:
				Mission.getInstance().setSelectable(false);
				Log.d("AIRE", "SMART CONSTANT");
				final Survey areaSurvey = new Survey(mapView);
				final GeometryLayer areaLayer = new GeometryLayer(this);
				areaLayer.setName("AREA_MEASURE");
				areaLayer.setType(GeometryType.POLYGON);
				areaLayer.setSymbology(new PolygonSymbology());
				areaSurvey.addStopListeners(new SurveyStopListener() {

					@Override
					public void actionPerformed(Geometry g) {
						Mission.getInstance().setSelectable(true);
						Log.d("AIRE", "STOP LISTENER");
						final double result = PolygonArea
								.getPolygonArea((PolygonGeometry) g) / 1E6;
						areaSurvey.stop();
						mapView.removeGeometryLayer(areaLayer);
						final AlertMeasureResultDialog areaDialog = new AlertMeasureResultDialog(
								MenuActivity.this, result, " km²");
						areaDialog.show();

					}
				});
				areaSurvey.startSurvey(areaLayer);
				mapView.addGeometryLayer(areaLayer);
				break;

			default:
				throw new IllegalStateException("MenuAction not handled");
			}
		} catch (SmartException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void createGPSTrack(final String name, final TRACK_MODE trackMode) {
		gpsTrack = new GPSTrack(trackMode, name, locationManager, mapView,
				GeometryType.LINE);
		gpsTrack.startTrack();
		Toast.makeText(this, R.string.track_started, Toast.LENGTH_LONG).show();
		for (GPSTrackListener l : this.gpsTrackListeners) {
			l.actionPerformed(true);
		}

		trackStarted = true;
	}

	public void createPolygonTrack(final TRACK_MODE trackMode) {
		polygonTrack = new GPSTrack(trackMode, missionName, locationManager,
				mapView, GeometryType.POLYGON,
				(GeometryLayer) mapView.getOverlay(this.missionName
						+ "_POLYGON"), this.form, MenuActivity.this);
		polygonTrack.startTrack();
		Mission.getInstance().trackInProgress(true);
		Toast.makeText(this, R.string.polygon_track_started, Toast.LENGTH_LONG)
				.show();
		for (PolygonTrackListener l : this.polygonTrackListeners) {
			l.actionPerformed(true);
		}

		polygonTrackStarted = true;
	}

	public void startMission(final String missionName) {
		this.setMissionName(missionName);
		form = new Form();
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
						ma, distance, " m");
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
		try {
			pref.save();
		} catch (PreferencesException e) {
			Toast.makeText(this, getString(R.string.unableLoadPref),
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cleanTIFFFolder();
		cleanTmpFolder();
		mapView.getTileProvider().clearTileCache();
		System.gc();
	}

	private void cleanTmpFolder() {
		logger.log(Level.INFO, "tmp folder cleaned");
		final List<File> files = FileUtils.getFileList(SmartConstants.TMP_PATH);
		for (File file : files) {

			file.delete();

		}

	}

	private void cleanTIFFFolder() {
		final List<File> files = FileUtils
				.getFileList(SmartConstants.TIFF_PATH);
		logger.log(Level.INFO, "Tiff folder cleaned");
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

	/**
	 * 
	 * @param listener
	 */
	public void addPolygonTrackListener(PolygonTrackListener listener) {
		polygonTrackListeners.add(listener);
	}

	/**
	 * 
	 * @param listener
	 */
	public void removePolygonTrackListener(PolygonTrackListener listener) {
		polygonTrackListeners.remove(listener);
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
		mapView.getTileProvider().clearTileCache();
		System.gc();
		super.onStop();
	}

}
