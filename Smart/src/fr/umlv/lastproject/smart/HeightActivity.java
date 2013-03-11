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
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import fr.umlv.lastproject.smart.dialog.HeightDialog;
import fr.umlv.lastproject.smart.utils.SmartLogger;

public class HeightActivity extends Activity implements SensorEventListener {

	private static final int ANGLE_90 = 90;
	private static final int ANGLE_0 = 0;
	private static final int ANGLE_N90 = -90;

	private static final double DANGLE_180 = 180.0;
	private static final float FANGLE_90 = 90.0f;

	private static final int METERS = 100;

	private SensorManager sensorManager;
	private static final double DEG_TO_RAD = Math.PI / DANGLE_180;
	public static final String HEIGHT_RESULT = "height";
	public static final String ERROR_RESULT = "error";
	private final Object lock = new Object();
	private float angle;
	private float bottomAngle, topAngle;
	private long firstTouch, secondTouch;
	private double userHeight;
	private static final long DELAY = 1000;
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

		final HeightDialog dialog = new HeightDialog(this);
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
			bottomAngle = ANGLE_N90 + angle;
			firstTouch = System.currentTimeMillis();
			setContentView(R.layout.activity_height_top);

		} else {
			if (!checkAngle(angle)) {
				return;
			}
			secondTouch = System.currentTimeMillis();
			if (secondTouch - firstTouch < DELAY) {
				return;
			}
			secondTouch = firstTouch + 2 * DELAY;
			topAngle = ANGLE_90 - angle;

			finishWithResult(userHeight, bottomAngle, topAngle);
		}
	}

	private boolean checkAngle(final float angle) {
		if (angle < ANGLE_0 || angle > ANGLE_90) {
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
			angle = yOrientationDegrees;
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

		final float firstAngle = bottomAngle + FANGLE_90;
		final double distance = Math.tan(DEG_TO_RAD * firstAngle)
				* userPOVHeight;
		double result = Math.tan(DEG_TO_RAD * topAngle) * distance
				+ userPOVHeight;
		if (result <= 0) {
			finishWithResult(R.string.height_result_error);
		}
		result /= METERS;
		final Intent data = new Intent();
		data.putExtra(HEIGHT_RESULT, result);
		setResult(RESULT_OK, data);
		finish();
	}

	public void setHeight(double height) {
		if (height <= 0) {
			Toast.makeText(this, getString(R.string.height_field_missing),
					Toast.LENGTH_LONG).show();
			final HeightDialog dialog = new HeightDialog(this);
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
