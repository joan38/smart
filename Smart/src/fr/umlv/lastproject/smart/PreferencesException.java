package fr.umlv.lastproject.smart;

public class PreferencesException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PreferencesException() {
		super();
	}

	public PreferencesException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public PreferencesException(String detailMessage) {
		super(detailMessage);
	}

	public PreferencesException(Throwable throwable) {
		super(throwable);
	}
}
