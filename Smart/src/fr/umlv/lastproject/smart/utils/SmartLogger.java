package fr.umlv.lastproject.smart.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SmartLogger {
	
	 private static SmartLogger locator = new SmartLogger();
	 private Logger logger;

	 /**
	     * 
	     */
	    private SmartLogger() {
	    	initLogger();
	    }

	    /**
	     * @return
	     */
	    public static SmartLogger getLocator(){
	    	return locator;
	    }

	    /**
	     * @return
	     */
	    public Logger getLogger() {
	    	return logger;
	    }

	    /**
	     * 
	     */
	    private void initLogger() {
	    	File f = new File(SmartConstants.LOG_PATH);
	    	f.mkdir();
	    	logger = Logger.getLogger("My General Logger");
	    	FileHandler fh;
	    	try {
	    		// This block configure the logger with handler and formatter
	    		fh = new FileHandler(SmartConstants.LOG_PATH+"/smart.log",5242880,1, true);
	    		logger.addHandler(fh);
	    		logger.setLevel(Level.ALL);
	    		logger.setUseParentHandlers(false);
	    		fh.setFormatter(new SimpleFormatter());
	    	} catch (final SecurityException e) {
	    		
	    	} catch (final IOException e) {
	    		
	    	}
	    }
}
