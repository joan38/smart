package fr.umlv.lastproject.smart.utils;


/**
 * This class is used to generate a custom exception
 * 
 * @author Maelle Cabot
 *
 */
public class SmartException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SmartException(String message) {
		super(message);
	}

	public SmartException(Exception e, String string) {
		super(string, e);
	}
	
	

}
