package fr.umlv.lastproject.smart;

/**
 * 
 * @author thibault
 * 
 */
public interface GpsListener {
	/**
	 * 
	 * @param event
	 *            contains the coordinate
	 */
	void locationUpdated(GpsEvent event);
}
