package fr.umlv.lastproject.smart.form;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.widget.Toast;
import fr.umlv.lastproject.smart.GPS;
import fr.umlv.lastproject.smart.GPSEvent;
import fr.umlv.lastproject.smart.IGPSListener;
import fr.umlv.lastproject.smart.R;

/**
 * Activity which launch the camera device and geoTag it
 * 
 * @author Fad's
 * 
 */
public class PictureActivity extends Activity {

	private File picture;
	private static final  int PICTURE_RESULT = 1;
	private static final  String PICTURE_PATH = Environment
			.getExternalStorageDirectory() + "/SMART/pictures/";

	private GPS gps;
	private LocationManager locationManager;
	private double latitude;
	private double longitude;
	private float bearing;
	private String namePicture;

	private boolean takePicture = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture);

		createPictureFolder();

		initGPS();

		takePhoto();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_picture, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// Si la prise de photo c'est bien d�roul�, on la geoTag
		if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
			// GeoTag picture
			geoTag(namePicture, latitude, longitude, bearing);
			finish();
		}
	}

	/**
	 * Function which init the gps listener to geoTag picture
	 */
	private void initGPS() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		gps = new GPS(locationManager);

		gps.start(1, 1);
		gps.addGPSListener(new IGPSListener() {

			@Override
			public void actionPerformed(GPSEvent event) {
				latitude = event.getLatitude();
				longitude = event.getLongitude();
				bearing = event.getBearing();
			}
		});
	}

	/**
	 * Geotag of a picture
	 * 
	 * @param filename
	 * @param latitude
	 * @param longitude
	 * @param bearing
	 */
	public void geoTag(String filename, double latitude, double longitude,
			float bearing) {
		ExifInterface exif = null;
		try {
			// R�cupration de la photo � geoTag
			exif = new ExifInterface(PICTURE_PATH + filename + ".jpg");
			Toast.makeText(this, filename, Toast.LENGTH_LONG).show();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Transformation de la latitude au format souhait�
		double alat = Math.abs(latitude);
		String dms = Location.convert(alat, Location.FORMAT_SECONDS);
		String[] splits = dms.split(":");
		String[] secnds = (splits[2]).split("\\.");
		String seconds;
		if (secnds.length == 0) {
			seconds = splits[2];
		} else {
			seconds = secnds[0];
		}

		// On set la latitude sur la photo
		String latitudeStr = splits[0] + "/1," + splits[1] + "/1," + seconds
				+ "/1";
		exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latitudeStr);

		exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,
				latitude > 0 ? "N" : "S");

		// Transformation de la longitude au format souhait�
		double alon = Math.abs(longitude);

		dms = Location.convert(alon, Location.FORMAT_SECONDS);
		splits = dms.split(":");
		secnds = (splits[2]).split("\\.");

		if (secnds.length == 0) {
			seconds = splits[2];
		} else {
			seconds = secnds[0];
		}

		// On set la longitude sur la photo
		String longitudeStr = splits[0] + "/1," + splits[1] + "/1," + seconds
				+ "/1";

		exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, longitudeStr);
		exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF,
				longitude > 0 ? "E" : "W");
		exif.setAttribute(ExifInterface.TAG_ORIENTATION,
				String.valueOf(bearing));

		try {
			// Sauvegarde des changements
			exif.saveAttributes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// perform a media scanning after saving the image
		sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://" + Environment.getExternalStorageDirectory())));

	}

	/**
	 * Function which start camera intent and take the picture
	 */
	private void takePhoto() {
		takePicture = true;
		namePicture = getIntent().getExtras().getString("namePicture");
		picture = new File(PICTURE_PATH, namePicture + ".jpg");

		// On r�cup�re l'URI associ�e au fichier
		Uri fileUri = Uri.fromFile(picture);
		// Cr�ation de l'intent de la cam�ra
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// On indique que qu'on enregistre l'image la o� pointe l'Uri
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		// Lancement de l'intent de cam�ra
		startActivityForResult(cameraIntent, PICTURE_RESULT);

	}

	/**
	 * Function which create SMART directory and Picture directory if don't
	 * exists
	 */
	private void createPictureFolder() {
		File appFolder = new File(Environment.getExternalStorageDirectory()
				+ "/SMART/");
		if (!appFolder.exists()) {
			appFolder.mkdir();

		}
		File picuresFolder = new File(Environment.getExternalStorageDirectory()
				+ "/SMART/pictures/");
		if (!picuresFolder.exists()) {
			picuresFolder.mkdir();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("takePicture", takePicture);
	}

}
