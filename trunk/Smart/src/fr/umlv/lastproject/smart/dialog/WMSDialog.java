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
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;

public class WMSDialog extends AlertDialog.Builder {

	public WMSDialog(final MenuActivity menu) {
		super(menu);
		setCancelable(false);

		LayoutInflater factory = LayoutInflater.from(menu);
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
				final Toast toast = Toast.makeText(menu, R.string.wms_error,
						Toast.LENGTH_LONG);
				URL u = null;
				try {
					u = new URL(wms);
				} catch (MalformedURLException e) {
					Log.d("TESTX",
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
							urlc.setConnectTimeout(1000 * 30); // mTimeout is in
																// seconds

							urlc.connect();
							if (urlc.getResponseCode() == 200) {
								Log.d("TESTX", "CONNECTION PING");

								// mapView.addWMSLayer(url.toString(), wmsName
								// .getText().toString());
								// Toast.makeText(mapView.getContext(),
								// R.string.wms_success, Toast.LENGTH_LONG)
								// .show();

							}
							urlc.disconnect();

							return;
							// toast.show();

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
