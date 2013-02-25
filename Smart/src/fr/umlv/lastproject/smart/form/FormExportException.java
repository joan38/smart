package fr.umlv.lastproject.smart.form;

/**
 * The exception class for the Kml export.
 * 
 * @author joan
 *
 */
public class FormExportException extends Exception {

	private static final long serialVersionUID = 7712793461879198944L;

	public FormExportException() {
		super();
	}

	public FormExportException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public FormExportException(String detailMessage) {
		super(detailMessage);
	}

	public FormExportException(Throwable throwable) {
		super(throwable);
	}
}
