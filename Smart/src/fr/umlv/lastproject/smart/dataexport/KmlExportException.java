package fr.umlv.lastproject.smart.dataexport;

public class KmlExportException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7712793461879198944L;

	public KmlExportException() {
		super();
	}

	public KmlExportException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public KmlExportException(String detailMessage) {
		super(detailMessage);
	}

	public KmlExportException(Throwable throwable) {
		super(throwable);
	}
}
