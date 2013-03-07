package fr.umlv.lastproject.smart.dialog;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.SmartMapView;

public class WMSDialog extends AlertDialog.Builder {

	private static int SECOND = 1000;
	private static int TIMEOUT = 1000;
	private static int PING = 200;

	public WMSDialog(final SmartMapView mapView) {
		super(mapView.getContext());
		setCancelable(false);

		LayoutInflater factory = LayoutInflater.from(mapView.getContext());
		final View alertDialogView = factory.inflate(
				fr.umlv.lastproject.smart.R.layout.import_wms, null);

		setView(alertDialogView);
		setTitle(alertDialogView.getResources().getString(R.string.wmstitle));

		final EditText wmsUrl = (EditText) alertDialogView
				.findViewById(fr.umlv.lastproject.smart.R.id.wmsurl);
		final EditText wmsName = (EditText) alertDialogView
				.findViewById(fr.umlv.lastproject.smart.R.id.wmsname);

		setPositiveButton(R.string.validate, new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				final String wms = wmsUrl.getText().toString();
				final Toast toast = Toast.makeText(mapView.getContext(),
						R.string.wms_error, Toast.LENGTH_LONG);
				URL u = null;
				try {
					u = new URL(wms);
				} catch (MalformedURLException e) {
					Log.d("TEST2",
							"CONNECTION MALFORMED URL : " + e.getMessage()
									+ " / " + u + "/ " + wmsUrl);

					toast.show();
					return;
				}
				final URL url = u;
				Log.d("TEST2", "url to string : " + url.toString());

				final Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							final HttpURLConnection urlc = (HttpURLConnection) url
									.openConnection();
							urlc.setRequestProperty("User-Agent",
									"Android Application:2.2");
							urlc.setRequestProperty("Connection", "close");
							// mTimeout is in seconds
							urlc.setConnectTimeout(SECOND * TIMEOUT);

							urlc.connect();
							if (urlc.getResponseCode() == PING) {
								Log.d("TEST2", "CONNECTION PING");

								mapView.addWMSLayer(url.toString(), wmsName
										.getText().toString());

								return;
							}
							toast.show();

						} catch (IOException e) {
							toast.show();
						}

					}
				});
				t.start();

			}
		}).setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// nothing
			}
		}).create();

	}

}
