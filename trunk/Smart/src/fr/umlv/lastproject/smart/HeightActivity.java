package fr.umlv.lastproject.smart;

import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import fr.umlv.lastproject.smart.dialog.AlertHeightDialog;
import fr.umlv.lastproject.smart.utils.SmartLogger;

public class HeightActivity extends Activity implements SensorEventListener {

	private SensorManager sensorManager;
	private static final double DEG_TO_RAD = Math.PI / 180.0;
	public static final String HEIGHT_RESULT = "height";
	public static final String ERROR_RESULT = "error";
	private final Object lock = new Object();
	private float angle;
	private float bottomAngle, topAngle;
	private long firstTouch, secondTouch;
	private double userHeight;
	private static final long delay = 1000;
	private final Logger logger = SmartLogger.getLocator().getLogger();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_height_bottom);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_NORMAL);
		bottomAngle = Float.MIN_VALUE;
		topAngle = Float.MIN_VALUE;

		logger.log(Level.INFO, "Height measure started");

		final AlertHeightDialog dialog = new AlertHeightDialog(this);
		dialog.show();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		synchronized (lock) {
			super.onTouchEvent(event);
			setAngle(angle);
		}
		return true;

	}

	private void setAngle(final float angle) {
		if (bottomAngle == Float.MIN_VALUE) {
			if (!checkAngle(angle)) {
				return;
			}
			bottomAngle = -90 + angle;
			firstTouch = System.currentTimeMillis();
			setContentView(R.layout.activity_height_top);

		} else {
			if (!checkAngle(angle)) {
				return;
			}
			secondTouch = System.currentTimeMillis();
			if (secondTouch - firstTouch < delay) {
				return;
			}
			secondTouch = firstTouch + 2 * delay;
			topAngle = 90 - angle;

			finishWithResult(userHeight, bottomAngle, topAngle);
		}
	}

	private boolean checkAngle(final float angle) {
		if (angle < 0 || angle > 90) {
			Toast.makeText(this, getString(R.string.height_error),
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_height, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		sensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		int sensor = event.sensor.getType();

		if (sensor != Sensor.TYPE_ORIENTATION) {
			return;
		}
		synchronized (lock) {
			final float yOrientationDegrees = event.values[2];
			final float x = event.values[0];
			final float z = event.values[1];
			angle = yOrientationDegrees;
			// Log.d("TEST2",
			// "Orientation Y: " + yOrientationDegrees + " / "
			// + Math.tan(yOrientationDegrees * DEG_TO_RAD)
			// + " //// Orientation Z : " + z + " / "
			// + Math.tan(z * DEG_TO_RAD)
			// + " ////Orientation X : " + x + " / "
			// + Math.tan(x * DEG_TO_RAD));
			Log.d("TEST2", "Orientation Y: " + yOrientationDegrees
					+ " //// Orientation Z : " + z + " / "

					+ " ////Orientation X : " + x);
			// Log.d("TEST2",
			// "Orientation X: " + x + " / " + Math.tan(x * DEG_TO_RAD));
			// Log.d("TEST2",
			// "Orientation Z: " + z + " / " + Math.tan(z * DEG_TO_RAD));

		}

	}

	public void finishWithResult(int errorId) {
		final Intent intent = new Intent();
		intent.putExtra(ERROR_RESULT, getString(errorId));
		setResult(RESULT_OK, intent);
		logger.log(Level.INFO, "Height measure end");
		finish();

	}

	private void finishWithResult(final double userPOVHeight,
			final float bottomAngle, final float topAngle) {

		final float firstAngle = bottomAngle + 90.0f;
		final double distance = Math.tan(DEG_TO_RAD * firstAngle)
				* userPOVHeight;
		double result = Math.tan(DEG_TO_RAD * topAngle) * distance
				+ userPOVHeight;
		if (result <= 0) {
			finishWithResult(R.string.height_result_error);
		}
		result /= 100;
		final Intent data = new Intent();
		data.putExtra(HEIGHT_RESULT, result);
		setResult(RESULT_OK, data);
		finish();
	}

	public void setHeight(double height) {
		if (height <= 0) {
			Toast.makeText(this, getString(R.string.height_field_missing),
					Toast.LENGTH_LONG);
			final AlertHeightDialog dialog = new AlertHeightDialog(this);
			dialog.show();
			return;
		}
		userHeight = height;

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation/keyboard change
		super.onConfigurationChanged(newConfig);
	}
}
