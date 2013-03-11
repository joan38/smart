package fr.umlv.lastproject.smart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.DirectedLocationOverlay;
import org.osmdroid.views.overlay.OverlayManager;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;
import fr.umlv.lastproject.smart.GPSTrack.TrackMode;
import fr.umlv.lastproject.smart.browser.utils.FileUtils;
import fr.umlv.lastproject.smart.data.DataImport;
import fr.umlv.lastproject.smart.data.TMSOverlay;
import fr.umlv.lastproject.smart.dialog.ExitSmartDialog;
import fr.umlv.lastproject.smart.dialog.GPSSettingDialog;
import fr.umlv.lastproject.smart.dialog.HelpDialog;
import fr.umlv.lastproject.smart.dialog.MeasureResultDialog;
import fr.umlv.lastproject.smart.dialog.ModifFormDialog;
import fr.umlv.lastproject.smart.dialog.PolygonTrackDialog;
import fr.umlv.lastproject.smart.dialog.SettingInfoDialog;
import fr.umlv.lastproject.smart.dialog.SymbologyDialog;
import fr.umlv.lastproject.smart.dialog.ThemeDialog;
import fr.umlv.lastproject.smart.dialog.TrackDialog;
import fr.umlv.lastproject.smart.dialog.FormDialog;
import fr.umlv.lastproject.smart.form.Form;
import fr.umlv.lastproject.smart.form.FormEditedListener;
import fr.umlv.lastproject.smart.form.FormIOException;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.form.MissionListener;
import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.layers.GeometryType;
import fr.umlv.lastproject.smart.layers.Layer;
import fr.umlv.lastproject.smart.layers.PointGeometry;
import fr.umlv.lastproject.smart.survey.MeasureStopListener;
import fr.umlv.lastproject.smart.survey.Measures;
import fr.umlv.lastproject.smart.utils.SmartConstants;
import fr.umlv.lastproject.smart.utils.SmartException;
import fr.umlv.lastproject.smart.utils.SmartLogger;
import fr.umlv.lastproject.smart.utils.SmartParameters;

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
	private InfoOverlay infoOverlay;
	private DirectedLocationOverlay directedLocationOverlay;
	private View centerMap;
	private boolean isMapTracked = true;
	private GeoPoint lastPosition = new GeoPoint(0, 0);
	private String formPath;
	private GPSTrack gpsTrack = null;
	private GPSTrack polygonTrack = null;
	private Preferences pref;
	private final List<GPSTrackListener> gpsTrackListeners = new ArrayList<GPSTrackListener>();
	private final List<PolygonTrackListener> polygonTrackListeners = new ArrayList<PolygonTrackListener>();
	private Form form;
	private Geometry geom;
	private Object[] valuesList;
	private static final int FORM_DIALOG_ID = 1;
	private static final int FORM_FILLED_DIALOG_ID = 2;
	private static final int FORM_MODIFY_DIALOG = 3;
	private int heightIndex;
	private Dialog dialog;
	private GeometryLayer geometryLayer;
	private AlertDialog createMissionDialog;

	private static final Logger logger = SmartLogger.getLocator().getLogger();

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putAll(BundleCreator.createBundle(mapView, gpsTrack,
				polygonTrack));
		logger.log(Level.INFO, "Saving the application bundle");
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

		setTheme(pref.getTheme());
		setRequestedOrientation(pref.getOrientation());

		File f = new File(SmartConstants.APP_PATH);
		f.mkdir();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_smart);

		initMap();
		initGps();

		ImageView home = (ImageView) findViewById(R.id.home);
		home.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent homeActivity = new Intent(MenuActivity.this,
						HomeActivity.class);
				homeActivity.putExtra("trackStarted", (gpsTrack == null ? false
						: gpsTrack.isStarted()));
				homeActivity.putExtra(
						"polygonTrackStarted",
						(polygonTrack == null ? false : polygonTrack
								.isStarted()));
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
						&& Mission.getInstance().isStarted()) {
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

		for (Integer menuAction : pref.getShortcuts()) {
			showShortcut(MenuAction.getFromId(menuAction));
		}
		if (savedInstanceState != null) {
			BundleCreator.loadPosition(savedInstanceState, mapView);
			BundleCreator.loadMission(savedInstanceState, mapView, this);
			BundleCreator.loadGeometryLayers(savedInstanceState, this, mapView);
			BundleCreator.loadGeotiffs(savedInstanceState, mapView, this);
			gpsTrack = BundleCreator.loadTrack(savedInstanceState, mapView,
					this, gps.getLocationManager(), this);
			for (GPSTrackListener l : gpsTrackListeners) {
				l.actionPerformed(gpsTrack == null ? false : gpsTrack
						.isStarted());
			}
			if (Mission.getInstance() != null) {
				polygonTrack = BundleCreator.loadPolygonTrack(
						savedInstanceState, mapView, Mission.getInstance()
								.getPolygonLayer(), gps.getLocationManager(),
						this);
			}
			for (PolygonTrackListener l : polygonTrackListeners) {
				l.actionPerformed(polygonTrack == null ? false : polygonTrack
						.isStarted());
			}

		}

	}

	@Override
	public void onBackPressed() {
		final ExitSmartDialog exitDialog = new ExitSmartDialog(this);
		exitDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_smart, menu);
		menu.add(0, SmartConstants.PARAMS_INFO_SETTINGS, 0, R.string.infoSettings);
		menu.add(0, SmartConstants.PARAMS_HIDE_INFO_ZONE, 0, R.string.hideInfoZone);
		menu.add(0, SmartConstants.PARAMS_GPS_SETTINGS, 0, R.string.gpsSettings);
		menu.add(0, SmartConstants.PARAMS_SETTINGS, 0, R.string.settings);
		menu.add(0, SmartConstants.PARAMS_HELP, 0, R.string.help);
		// menu.add(0, 5, 0, R.string.about);

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
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		gps = new GPS(locationManager);

		if (!gps.isEnabled()) {
			final GPSSettingDialog gpsSettingDialog = new GPSSettingDialog(this);
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
		case SmartConstants.PARAMS_INFO_SETTINGS:
			new SettingInfoDialog(this, findViewById(R.id.table), infoOverlay);
			break;

		case SmartConstants.PARAMS_HIDE_INFO_ZONE:
			infoOverlay.hideInfoZone(findViewById(R.id.table), item);
			break;

		case SmartConstants.PARAMS_GPS_SETTINGS:
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
			break;

		case SmartConstants.PARAMS_SETTINGS:
			if (Mission.isCreated() && Mission.getInstance().isStarted()) {
				Toast.makeText(this, "Stop the mission before",
						Toast.LENGTH_LONG).show();
			} else {
				new ThemeDialog(this);
			}
			break;

		case SmartConstants.PARAMS_HELP:
			final HelpDialog helpDialog = new HelpDialog(this, R.string.helpMap);
			helpDialog.show();
			break;

		// case 5:
		// final AboutDialog about = new AboutDialog(this);
		// about.show();
		// break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {

			Integer index = (Integer) data.getSerializableExtra("position");
			switch (requestCode) {
			case SmartConstants.HOME_VIEW:
				MenuAction.getFromId(index).doAction(this);
				break;

			case SmartConstants.LAYERS_VIEW:
				ListOverlay listOverlay = (ListOverlay) data
						.getSerializableExtra("overlays");

				final Bundle extras = data.getExtras();
				Object oZoomTo = extras.get("zoomTo");
				if (oZoomTo != null) {

					try {
						final Layer layer = mapView.getLayer(listOverlay
								.get(Integer.parseInt(oZoomTo.toString())));
						mapView.zoomToBoundingBox(layer.getExtent()
								.getBoundingBox());
						// TODO set zoom
						if (layer.getExtent().getZoom() != -1) {
							mapView.getController().setZoom(
									layer.getExtent().getZoom());
						}
					} catch (Exception e) {
						//
					}

				}

				if ((Boolean) data.getSerializableExtra("editSymbo")) {
					GeometryLayer layer = (GeometryLayer) mapView
							.getOverlay(listOverlay
									.get((Integer) data
											.getSerializableExtra("symboToEdit"))
									.getName());
					new SymbologyDialog(this, layer,
							listOverlay.get((Integer) data
									.getSerializableExtra("symboToEdit")));

				}
				Collections.reverse(listOverlay.toList());
				mapView.setReorderedLayers(listOverlay);
				break;

			case SmartConstants.MISSION_BROWSER_ACTIVITY:
				formPath = data.getData().getPath();

				TextView pathTextView = (TextView) createMissionDialog
						.findViewById(R.id.selectFormButton);
				pathTextView.setText(data.getData().getPath());

				logger.log(Level.INFO,
						"Getting mission path from brower activity : "
								+ formPath);
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
				final String path = data.getData().getPath();
				final String extension = FileUtils.getExtension(path);
				final ProgressDialog progressDialogKmlShp = ProgressDialog
						.show(this, getString(R.string.vector_progress_title),
								getString(R.string.vector_progress));

				logger.log(Level.INFO, "Getting vector data :" + path);
				final Thread vectorThread = new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							List<GeometryLayer> geometryLayersToImport = new ArrayList<GeometryLayer>();
							int err = R.string.kmlParseError;
							int succ = R.string.kmlImport;

							if (extension.equalsIgnoreCase(".kml")) {
								try {
									geometryLayersToImport = DataImport
											.importKml(MenuActivity.this, path);
									succ = R.string.kmlImport;

								} catch (XmlPullParserException e) {
									err = R.string.kmlParseError;

								} catch (IOException e) {
									err = R.string.kmlReadError;

								}
							} else if (extension.equalsIgnoreCase(".shp")) {
								try{
									final GeometryLayer lay=DataImport
											.importShapeFile(MenuActivity.this,
													path);
									if(lay!=null){
										geometryLayersToImport.add(lay);
										succ = R.string.shpImport;
										
									}
									else{
										err=R.string.errorShp;
									}
									
								}
								catch(SmartException e){
									err=R.string.errorShp;

								}
								

							}
							final int error = err;
							final int success = succ;
							final List<GeometryLayer> layers = geometryLayersToImport;
							runOnUiThread(new Runnable() {
								public void run() {
									if (layers == null || layers.isEmpty()) {
										Toast.makeText(MenuActivity.this,
												error, Toast.LENGTH_SHORT)
												.show();
									} else {
										mapView.addGeometryLayers(layers);
										Toast.makeText(MenuActivity.this,
												success, Toast.LENGTH_SHORT)
												.show();
										BoundingBoxE6 vectorBB = layers.get(0)
												.getExtent().getBoundingBox();
										if (layers.size() > 1) {
											int northBB = layers.get(0)
													.getExtent()
													.getBoundingBox()
													.getLatNorthE6(), southBB = layers
													.get(0).getExtent()
													.getBoundingBox()
													.getLatSouthE6(), eastBB = layers
													.get(0).getExtent()
													.getBoundingBox()
													.getLonEastE6(), westBB = layers
													.get(0).getExtent()
													.getBoundingBox()
													.getLonWestE6();
											for (int i = 0; i < layers.size(); i++) {
												BoundingBoxE6 tmpBB = layers
														.get(i).getExtent()
														.getBoundingBox();
												int tmpNorth = tmpBB
														.getLatNorthE6();
												int tmpSouth = tmpBB
														.getLatSouthE6();
												int tmpEast = tmpBB
														.getLonEastE6();
												int tmpWest = tmpBB
														.getLonWestE6();
												if (tmpNorth > northBB) {
													northBB = tmpNorth;
												}
												if (tmpSouth < southBB) {
													southBB = tmpSouth;
												}
												if (tmpEast > eastBB) {
													eastBB = tmpEast;
												}
												if (tmpWest < westBB) {
													westBB = tmpWest;
												}
											}
											vectorBB = new BoundingBoxE6(
													northBB, eastBB, southBB,
													westBB);
										}
										mapView.zoomToBoundingBox(vectorBB);
									}
									progressDialogKmlShp.dismiss();
								}
							});
						} catch (OutOfMemoryError outOfMemory) {
							mapView.getTileProvider().clearTileCache();

							progressDialogKmlShp.dismiss();
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(MenuActivity.this, "Error",
											Toast.LENGTH_LONG).show();

								}
							});
						}

					}

				});
				vectorThread.start();

				break;

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
											overlay.getExtent()
													.getBoundingBox()
													.getCenter());
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
				if (!gps.isEnabled()) {
					logger.log(Level.WARNING, "GPS want to be enabled");
					Toast.makeText(this, R.string.track_notstarted,
							Toast.LENGTH_LONG).show();
					return;
				}

				MenuAction trackType = (MenuAction) data
						.getSerializableExtra("trackType");
				switch (trackType) {
				case GPS_TRACK:
					if (gpsTrack == null) {
						new TrackDialog(this, mapView.getListOverlay());
					} else {
						try {
							gpsTrack.stopTrack();
							gpsTrack = null;

							for (GPSTrackListener l : this.gpsTrackListeners) {
								l.actionPerformed(false);
							}

							Toast.makeText(this, R.string.track_stop,
									Toast.LENGTH_LONG).show();
						} catch (IOException e) {
							logger.log(Level.SEVERE, "Error on the track stop");
							gpsTrack = null;
							Toast.makeText(this, R.string.track_error,
									Toast.LENGTH_LONG).show();
						}
					}
					break;
				case POLYGON_TRACK:
					if (polygonTrack == null) {
						new PolygonTrackDialog(this);
					} else {
						try {
							polygonTrack.stopTrack();
							Mission.getInstance().trackInProgress(false);
							polygonTrack = null;

							for (PolygonTrackListener l : this.polygonTrackListeners) {
								l.actionPerformed(false);
							}

							Toast.makeText(this, R.string.polygon_track_stoped,
									Toast.LENGTH_LONG).show();
						} catch (IOException e) {
							polygonTrack = null;
							Toast.makeText(this, R.string.track_error,
									Toast.LENGTH_LONG).show();
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

	private boolean createShortcut(final MenuAction menuItem) {
		if (pref.getShortcuts().contains(menuItem.getId())) {
			return false;
		}

		pref.getShortcuts().add(menuItem.getId());
		showShortcut(menuItem);

		return true;
	}

	private void showShortcut(final MenuAction menuAction) {
		final LinearLayout shortcutsView = (LinearLayout) findViewById(R.id.shortcuts);
		final ImageView shortcut = new ImageView(this) {

			@Override
			public boolean showContextMenu() {
				return true;
			}
		};
		shortcut.setImageResource(SmartConstants.getIcons()[menuAction.getId()]);
		shortcut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				menuAction.doAction(MenuActivity.this);
			}
		});
		shortcut.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				shortcutsView.removeView(shortcut);
				pref.getShortcuts().remove(Integer.valueOf(menuAction.getId()));
				Toast.makeText(MenuActivity.this, R.string.shortcut_remove,
						Toast.LENGTH_SHORT).show();
				shortcutsView.invalidate();
				return false;
			}
		});

		switch (menuAction) {
		case CREATE_MISSION:
			if (Mission.isCreated() && Mission.getInstance().isStarted()) {
				Log.d("debug", (Mission.getInstance() + " " + Mission
						.getInstance().isStarted()));
				shortcut.setImageResource(SmartConstants.getIcons()[MenuAction.STOP_MISSION
						.getId()]);
			}

			Mission.addMissionListener(new MissionListener() {

				@Override
				public void actionPerformed(boolean status) {
					if (status) {
						shortcut.setImageResource(SmartConstants.getIcons()[MenuAction.STOP_MISSION
								.getId()]);
						shortcutsView.invalidate();
					} else {
						shortcut.setImageResource(SmartConstants.getIcons()[MenuAction.CREATE_MISSION
								.getId()]);
						shortcutsView.invalidate();
					}
				}
			});
			break;

		case GPS_TRACK:
			if (gpsTrack != null && gpsTrack.isStarted()) {
				Log.d("debug", gpsTrack + " " + gpsTrack.isStarted());
				shortcut.setImageResource(SmartConstants.getIcons()[MenuAction.STOP_GPS_TRACK
						.getId()]);
			}
			this.addGPSTrackListener(new GPSTrackListener() {

				@Override
				public void actionPerformed(boolean status) {
					if (status) {
						shortcut.setImageResource(SmartConstants.getIcons()[MenuAction.STOP_GPS_TRACK
								.getId()]);
						shortcutsView.invalidate();
					} else {
						shortcut.setImageResource(SmartConstants.getIcons()[MenuAction.GPS_TRACK
								.getId()]);
						shortcutsView.invalidate();
					}
				}
			});
			break;
		case POLYGON_TRACK:
			if (polygonTrack != null && polygonTrack.isStarted()) {
				Log.d("debug", polygonTrack + " " + polygonTrack.isStarted());
				shortcut.setImageResource(SmartConstants.getIcons()[MenuAction.STOP_POLYGON_TRACK
						.getId()]);
			}
			this.addPolygonTrackListener(new PolygonTrackListener() {

				@Override
				public void actionPerformed(boolean status) {
					if (status) {
						shortcut.setImageResource(SmartConstants.getIcons()[MenuAction.STOP_POLYGON_TRACK
								.getId()]);
						shortcutsView.invalidate();
					} else {
						shortcut.setImageResource(SmartConstants.getIcons()[MenuAction.POLYGON_TRACK
								.getId()]);
						shortcutsView.invalidate();
					}

				}
			});
		}
		shortcutsView.addView(shortcut);
		shortcutsView.invalidate();
	}

	public void createGPSTrack(final String name, final TrackMode trackMode) {
		gpsTrack = new GPSTrack(trackMode, name, gps.getLocationManager(),
				mapView, GeometryType.LINE);
		gpsTrack.startTrack();
		formPath = null;
		Toast.makeText(this, R.string.track_started, Toast.LENGTH_LONG).show();

		for (GPSTrackListener l : this.gpsTrackListeners) {
			l.actionPerformed(true);
		}
	}

	public void createPolygonTrack(final TrackMode trackMode) {
		polygonTrack = new GPSTrack(trackMode,
				Mission.getInstance().getTitle(), gps.getLocationManager(),
				GeometryType.POLYGON, Mission.getInstance().getPolygonLayer(),
				MenuActivity.this);
		polygonTrack.startTrack();
		Mission.getInstance().trackInProgress(true);
		Toast.makeText(this, R.string.polygon_track_started, Toast.LENGTH_LONG)
				.show();
		for (PolygonTrackListener l : this.polygonTrackListeners) {
			l.actionPerformed(true);
		}
	}

	public void startMission(final String missionName) {
		form = new Form(SmartConstants.DEFAULT_NAME);
		if (formPath != null) {
			try {
				form = Form.read(formPath);
			} catch (FormIOException e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
		Mission.createMission(missionName, this, mapView, form).startMission();
		formPath = null;
		mapView.addGeometryLayer(Mission.getInstance().getPolygonLayer());
		mapView.addGeometryLayer(Mission.getInstance().getLineLayer());
		mapView.addGeometryLayer(Mission.getInstance().getPointLayer());
	}

	public void measure(boolean absolute) {
		final MenuActivity ma = this;
		if (Mission.isCreated()) {
			Mission.getInstance().setSelectable(false);
		}

		final Measures m = new Measures(mapView);

		m.addStopListener(new MeasureStopListener() {
			@Override
			public void actionPerformed(double distance) {
				MeasureResultDialog amrd = new MeasureResultDialog(ma,
						distance, " m");
				amrd.show();
				m.stop();
				if (Mission.isCreated()) {
					Mission.getInstance().setSelectable(true);
				}
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

	public GPSTrack getGpsTrack() {
		return gpsTrack;
	}

	public void killGpsTrack() {
		gpsTrack = null;
	}

	public void setCreateMissionDialog(AlertDialog createMissionDialog) {
		this.createMissionDialog = createMissionDialog;
	}

	public GPSTrack getPolygonTrack() {
		return polygonTrack;
	}

	public GeoPoint getLastPosition() {
		return lastPosition;
	}

	public GPS getGps() {
		return gps;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		pref.setOrientation(SmartParameters.getParameters()
				.getScreenOrientation());
		pref.setTheme(SmartParameters.getParameters().getApplicationTheme());
		try {
			pref.save();
		} catch (PreferencesException e) {
			Toast.makeText(this, getString(R.string.unableLoadPref),
					Toast.LENGTH_LONG).show();
		}
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

	public void startHeightActivityForResult(Form form, Geometry geom,
			final Object[] valuesList, int heightIndex) {
		this.form = form;
		this.geom = geom;
		this.valuesList = valuesList.clone();
		this.heightIndex = heightIndex;

		startActivityForResult(new Intent(this, HeightActivity.class),
				SmartConstants.HEIGHT_ACTIVITY);
	}

	public void createFormDialog(Form form, Geometry g) {
		this.form = form;
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
						Mission.getInstance());
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
					Mission.getInstance(), valuesList);
			this.dialog = dialogFilled.create();
			dialog.show();
		} else if (id == FORM_MODIFY_DIALOG) {
			if (bundle == null) {
				ModifFormDialog modifyDialog = new ModifFormDialog(this,
						this.form, this.geom, this.geometryLayer, null);
				modifyDialog.addFormEditedListener(new FormEditedListener() {

					@Override
					public void actionPerformed(Geometry g) {
						g.setSelected(false);
						Mission.getInstance().setSelectable(true);
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

				final ModifFormDialog modifyDialog = new ModifFormDialog(this,
						form, geom, geometryLayer, valuesList);

				modifyDialog.addFormEditedListener(new FormEditedListener() {

					@Override
					public void actionPerformed(Geometry g) {
						g.setSelected(false);
						Mission.getInstance().setSelectable(true);
						mapView.invalidate();
					}
				});
				this.dialog = modifyDialog.create();
				dialog.show();
			}
		}
	}

	public List<GPSTrackListener> getGpsTrackListeners() {
		return gpsTrackListeners;
	}

	public List<PolygonTrackListener> getPolygonTrackListeners() {
		return polygonTrackListeners;
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
			Form form2, Geometry geom2, final Object[] valuesList2,
			int heightIndex2) {
		this.geom = geom2;
		this.form = form2;
		this.valuesList = valuesList2.clone();
		this.heightIndex = heightIndex2;
		this.geometryLayer = layer;

		startActivityForResult(new Intent(this, HeightActivity.class),
				SmartConstants.HEIGHT_MODIFY_ACTIVITY);
	}

	public void createModifFormDialog(Form form, Geometry g, GeometryLayer l) {
		this.form = form;
		this.geometryLayer = l;
		this.geom = g;
		createDialog(FORM_MODIFY_DIALOG, null);
	}

	@Override
	protected void onStop() {
		mapView.getTileProvider().clearTileCache();
		System.gc();
		super.onStop();
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.getTileProvider().clearTileCache();
	}

}
