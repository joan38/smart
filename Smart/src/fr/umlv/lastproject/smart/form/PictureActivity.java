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
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;
import fr.umlv.lastproject.smart.GPS;
import fr.umlv.lastproject.smart.GPSEvent;
import fr.umlv.lastproject.smart.IGPSListener;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;

/**
 * Activity which launch the camera device and geoTag it
 * 
 * @author Fad's
 * 
 */
public class PictureActivity extends Activity {

	private File picture;
	private final static int PICTURE_RESULT = 1;
	private final static String PICTURE_PATH = Environment
			.getExternalStorageDirectory() + "/SMART/pictures/";

	private GPS gps;
	private LocationManager locationManager;
	private double latitude;
	private double longitude;
	private float bearing;
	private String namePicture;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture);

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

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		gps = new GPS(locationManager);

		gps.start(1, 1);
		gps.addGPSListener(new IGPSListener() {

			@Override
			public void actionPerformed(GPSEvent event) {
				latitude = event.getLatitude();
				longitude = event.getLongitude();
				bearing = event.getBearing();
				Log.d("actionPerform", "je caaaaapte");
			}
		});

		// L'endroit o� sera enregistr�e la photo
		// Remarquez que mFichier est un attribut de ma classe
		namePicture = getIntent().getExtras().getString("namePicture");

		picture = new File(PICTURE_PATH, namePicture + ".jpg");
		Log.d("name", picture.toString());

		// On r�cup�re ensuite l'URI associ�e au fichier
		Uri fileUri = Uri.fromFile(picture);

		// Maintenant, on cr�e l'intent
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// Et on d�clare qu'on veut que l'image soit enregistr�e l� o� pointe
		// l'URI
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		// Enfin, on lance l'intent pour que l'application de photo se lance
		startActivityForResult(cameraIntent, PICTURE_RESULT);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_picture, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Si on revient de l'activit� qu'on avait lanc�e avec le code
		// PHOTO_RESULT

		if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
			// Si l'image est une miniature

			// On sait ici que le fichier point� par mFichier est
			// accessible, on peut donc faire ce qu'on veut avec, par
			// exemple en faire un Bitmap
			geoTag(namePicture, latitude, longitude, bearing);

			Intent intentReturn = new Intent(PictureActivity.this,
					MenuActivity.class);
			intentReturn.putExtra("pictureName", "smartPictureName");

		}
	}

	public void geoTag(String filename, double latitude, double longitude,
			float bearing) {
		ExifInterface exif = null;
		Log.d("geotag", filename);
		try {
			exif = new ExifInterface(PICTURE_PATH + filename + ".jpg");
			Toast.makeText(this, filename, Toast.LENGTH_LONG).show();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// String latitudeStr = "90/1,12/1,30/1";

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

		String latitudeStr = splits[0] + "/1," + splits[1] + "/1," + seconds
				+ "/1";
		exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latitudeStr);

		exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,
				latitude > 0 ? "N" : "S");

		double alon = Math.abs(longitude);

		dms = Location.convert(alon, Location.FORMAT_SECONDS);
		splits = dms.split(":");
		secnds = (splits[2]).split("\\.");

		if (secnds.length == 0) {
			seconds = splits[2];
		} else {
			seconds = secnds[0];
		}
		String longitudeStr = splits[0] + "/1," + splits[1] + "/1," + seconds
				+ "/1";

		exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, longitudeStr);
		exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF,
				longitude > 0 ? "E" : "W");
		exif.setAttribute(ExifInterface.TAG_ORIENTATION,
				String.valueOf(bearing));

		try {
			exif.saveAttributes();
			finish();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://" + Environment.getExternalStorageDirectory())));

	}
}
