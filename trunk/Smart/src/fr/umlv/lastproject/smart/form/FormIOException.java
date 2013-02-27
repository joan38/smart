package fr.umlv.lastproject.smart.form;

/**
 * The exception class for the Kml export.
 * 
 * @author joan
 *
 */
public class FormIOException extends Exception {

	private static final long serialVersionUID = 7712793461879198944L;

	public FormIOException() {
		super();
	}

	public FormIOException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public FormIOException(String detailMessage) {
		super(detailMessage);
	}

	public FormIOException(Throwable throwable) {
		super(throwable);
	}
}
