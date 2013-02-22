package fr.umlv.lastproject.smart.data;

/**
 * The exception class for the Kml export.
 * 
 * @author joan
 *
 */
public class CsvExportException extends Exception {

	private static final long serialVersionUID = 7712793461879198944L;

	public CsvExportException() {
		super();
	}

	public CsvExportException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public CsvExportException(String detailMessage) {
		super(detailMessage);
	}

	public CsvExportException(Throwable throwable) {
		super(throwable);
	}
}
