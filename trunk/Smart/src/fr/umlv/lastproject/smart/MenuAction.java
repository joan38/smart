package fr.umlv.lastproject.smart;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import fr.umlv.lastproject.smart.browser.utils.FileUtils;
import fr.umlv.lastproject.smart.dialog.CreateFormDialog;
import fr.umlv.lastproject.smart.dialog.GPSTrackDialog;
import fr.umlv.lastproject.smart.dialog.MeasureRequestDialog;
import fr.umlv.lastproject.smart.dialog.MeasureResultDialog;
import fr.umlv.lastproject.smart.dialog.PolygonTrackDialog;
import fr.umlv.lastproject.smart.dialog.TrackDialog;
import fr.umlv.lastproject.smart.dialog.MissionDialogUtils;
import fr.umlv.lastproject.smart.dialog.WMSDialog;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.layers.GeometryType;
import fr.umlv.lastproject.smart.layers.PointGeometry;
import fr.umlv.lastproject.smart.layers.PolygonGeometry;
import fr.umlv.lastproject.smart.layers.PolygonSymbology;
import fr.umlv.lastproject.smart.survey.PolygonArea;
import fr.umlv.lastproject.smart.survey.Survey;
import fr.umlv.lastproject.smart.survey.SurveyStopListener;
import fr.umlv.lastproject.smart.utils.SmartConstants;
import fr.umlv.lastproject.smart.utils.SmartException;
import fr.umlv.lastproject.smart.utils.SmartLogger;

public enum MenuAction {

	CREATE_MISSION(0) {

		@Override
		public void doAction(MenuActivity activity) {
			if (Mission.isCreated() && Mission.getInstance().isStarted()) {
				if (activity.getPolygonTrack() != null
						&& activity.getPolygonTrack().isStarted()) {
					Toast.makeText(activity,
							activity.getText(R.string.polygon_track_error),
							Toast.LENGTH_LONG).show();
				} else {
					Mission.getInstance().stopMission();
					Toast.makeText(activity,
							activity.getText(R.string.missionStop),
							Toast.LENGTH_LONG).show();
				}
			} else {
				activity.setCreateMissionDialog(MissionDialogUtils
						.showCreateDialog(activity, activity.getMapView()
								.getListOverlay()));
			}
		}
	},
	CREATE_FORM(1) {

		@Override
		public void doAction(MenuActivity activity) {
			new CreateFormDialog(activity);
		}
	},
	POINT_SURVEY(2) {

		@Override
		public void doAction(MenuActivity activity) {
			if (!Mission.isCreated()) {
				LOGGER.log(Level.WARNING,
						"Impossible point survey : mission not created");
				Toast.makeText(
						activity,
						activity.getResources().getText(
								R.string.noMissionInProgress),
						Toast.LENGTH_LONG).show();
			} else {
				Mission.getInstance().startSurvey(GeometryType.POINT);
				Toast.makeText(activity,
						activity.getResources().getText(R.string.point_survey),
						Toast.LENGTH_LONG).show();
			}
		}
	},
	POINT_SURVEY_POSITION(3) {

		@Override
		public void doAction(MenuActivity activity) {
			if (!Mission.isCreated()) {
				LOGGER.log(Level.WARNING,
						"Impossible position point survey : mission not created");
				Toast.makeText(
						activity,
						activity.getResources().getText(
								R.string.noMissionInProgress),
						Toast.LENGTH_LONG).show();
			} else {
				Mission.getInstance()
						.startSurvey(
								new PointGeometry(activity.getLastPosition()
										.getLatitudeE6() / VALUE_1E6, activity
										.getLastPosition().getLongitudeE6()
										/ VALUE_1E6));
			}
		}
	},
	LINE_SURVEY(4) {

		@Override
		public void doAction(MenuActivity activity) {
			if (Mission.getInstance() == null) {
				LOGGER.log(Level.WARNING,
						"Impossible line survey : mission not created");
				Toast.makeText(
						activity,
						activity.getResources().getText(
								R.string.noMissionInProgress),
						Toast.LENGTH_LONG).show();
			} else {
				Mission.getInstance().startSurvey(GeometryType.LINE);
				Toast.makeText(activity,
						activity.getResources().getText(R.string.line_survey),
						Toast.LENGTH_LONG).show();
			}
		}
	},
	POLYGON_SURVEY(5) {

		@Override
		public void doAction(MenuActivity activity) {
			if (Mission.getInstance() == null) {
				LOGGER.log(Level.WARNING,
						"Impossible polygon survey : mission not created");
				Toast.makeText(
						activity,
						activity.getResources().getText(
								R.string.noMissionInProgress),
						Toast.LENGTH_LONG).show();
			} else {
				Mission.getInstance().startSurvey(GeometryType.POLYGON);
				Toast.makeText(
						activity,
						activity.getResources()
								.getText(R.string.polygon_survey),
						Toast.LENGTH_LONG).show();
			}
		}
	},
	POLYGON_TRACK(6) {

		@Override
		public void doAction(MenuActivity activity) {
			if (Mission.getInstance() == null) {
				Toast.makeText(
						activity,
						activity.getResources().getText(
								R.string.noMissionInProgress),
						Toast.LENGTH_LONG).show();
			} else {
				if (!activity.getGps().isEnabled()) {
					final GPSTrackDialog gpsTrackDialog = new GPSTrackDialog(
							activity, this);
					gpsTrackDialog.show();
					return;
				}

				GPSTrack polygonTrack = activity.getPolygonTrack();
				if (polygonTrack == null || polygonTrack.isFinished()) {
					new PolygonTrackDialog(activity);
				} else {
					try {
						polygonTrack.stopTrack();

						for (PolygonTrackListener l : activity
								.getPolygonTrackListeners()) {
							l.actionPerformed(false);
						}

						Toast.makeText(activity, R.string.polygon_track_stoped,
								Toast.LENGTH_LONG).show();
					} catch (IOException e) {
						Toast.makeText(activity, R.string.track_error,
								Toast.LENGTH_LONG).show();
					}
				}
			}
		}
	},
	GPS_TRACK(7) {

		@Override
		public void doAction(MenuActivity activity) {
			File trackfolder = new File(SmartConstants.TRACK_PATH);
			trackfolder.mkdir();
			if (!activity.getGps().isEnabled()) {
				final GPSTrackDialog gpsTrackDialog = new GPSTrackDialog(
						activity, this);
				gpsTrackDialog.show();
				return;
			}

			GPSTrack gpsTrack = activity.getGpsTrack();
			if (gpsTrack == null) {
				new TrackDialog(activity, activity.getMapView()
						.getListOverlay());
			} else {
				try {
					gpsTrack.stopTrack();
					activity.killGpsTrack();
					for (GPSTrackListener l : activity.getGpsTrackListeners()) {
						l.actionPerformed(false);
					}

					Toast.makeText(activity, R.string.track_stop,
							Toast.LENGTH_LONG).show();
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE, "Error while stopping track");
					Toast.makeText(activity, R.string.track_error,
							Toast.LENGTH_LONG).show();
				}
			}
		}
	},
	IMPORT_KML_SHP(8) {

		@Override
		public void doAction(MenuActivity activity) {
			Intent intent = FileUtils.createGetContentIntent(
					FileUtils.KML_SHP_TYPE,
					Environment.getExternalStorageDirectory() + "");
			activity.startActivityForResult(intent,
					SmartConstants.IMPORT_KML_SHP_BROWSER_ACTIVITY);
		}
	},
	IMPORT_GEOTIFF(9) {

		@Override
		public void doAction(MenuActivity activity) {
			Intent intent = FileUtils.createGetContentIntent(
					FileUtils.TIF_TYPE,
					Environment.getExternalStorageDirectory() + "");
			activity.startActivityForResult(intent,
					SmartConstants.IMPORT_TIFF_BROWSER_ACTIVITY);
		}
	},
	IMPORT_WMS(10) {

		@Override
		public void doAction(MenuActivity activity) {
			final WMSDialog dialog = new WMSDialog(activity.getMapView());
			dialog.show();
		}
	},
	MEASURE(11) {

		@Override
		public void doAction(MenuActivity activity) {
			MeasureRequestDialog amrd = new MeasureRequestDialog(
					activity);
			amrd.show();
		}
	},
	AREA_MEASURE(12) {

		@Override
		public void doAction(final MenuActivity activity) {
			if (Mission.isCreated()) {
				Mission.getInstance().setSelectable(false);
			}

		
			final Survey areaSurvey = new Survey(activity.getMapView());
			final GeometryLayer areaLayer = new GeometryLayer(activity);
			areaLayer.setName("AREA_MEASURE");
			areaLayer.setType(GeometryType.POLYGON);
			areaLayer.setSymbology(new PolygonSymbology());
			areaSurvey.addStopListeners(new SurveyStopListener() {

				@Override
				public void actionPerformed(Geometry g) {
					if (Mission.isCreated()) {
						Mission.getInstance().setSelectable(true);
					}
					
					final double result = PolygonArea
							.getPolygonArea((PolygonGeometry) g) / VALUE_1E6;
					areaSurvey.stop();
					activity.getMapView().removeGeometryLayer(areaLayer);
					final MeasureResultDialog areaDialog = new MeasureResultDialog(
							activity, result, " km²");
					areaDialog.show();
				}
			});
			areaSurvey.startSurvey(areaLayer);
			activity.getMapView().addGeometryLayer(areaLayer);
			Toast.makeText(activity, R.string.area_measure_start,
					Toast.LENGTH_LONG).show();
		}
	},
	EXPORT_MISSION(13) {

		@Override
		public void doAction(MenuActivity activity) {
			try {
				MissionDialogUtils.showExportDialog(activity);
			} catch (SmartException e) {
				Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG)
						.show();
			}
		}
	},
	EXPORT_FORM(14) {

		@Override
		public void doAction(MenuActivity activity) {
			Intent intentForm = FileUtils.createGetContentIntent(
					FileUtils.FORM_TYPE, SmartConstants.APP_PATH);
			activity.startActivityForResult(intentForm,
					SmartConstants.FORM_BROWSER_ACTIVITY);
		}
	},
	DELETE_MISSION(15) {

		@Override
		public void doAction(MenuActivity activity) {
			try {
				MissionDialogUtils.showDeleteDialog(activity);
			} catch (SmartException e) {
				Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG)
						.show();
			}
		}
	},
	STOP_MISSION(16) {

		@Override
		public void doAction(MenuActivity activity) {
			// Stop

		}
	},
	STOP_GPS_TRACK(17) {

		@Override
		public void doAction(MenuActivity activity) {
			// Stop
		}
	},
	STOP_POLYGON_TRACK(18) {

		@Override
		public void doAction(MenuActivity activity) {
			// Stop

		}
	};

	private static final Logger LOGGER = SmartLogger.getLocator().getLogger();
	private final int id;
	private static final double VALUE_1E6 = 1E6;

	private MenuAction(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public abstract void doAction(MenuActivity activity);

	public static MenuAction getFromId(int id) {
		for (MenuAction menuAction : values()) {
			if (menuAction.id == id) {
				return menuAction;
			}
		}

		return null;
	}
}
