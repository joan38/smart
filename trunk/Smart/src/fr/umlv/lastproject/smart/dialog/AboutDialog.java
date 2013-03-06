package fr.umlv.lastproject.smart.dialog;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;

public class AboutDialog extends AlertDialog.Builder {

	/**
	 * Constructor
	 * 
	 * @param menu
	 */
	public AboutDialog(final MenuActivity menu) {
		super(menu);
		setCancelable(false);

		final LayoutInflater inflater = LayoutInflater.from(menu);

		final View credit = inflater.inflate(R.layout.credit_dialog, null);

		setView(credit);
		setTitle(R.string.about);

		setPositiveButton(R.string.ok, null);

	}
}
