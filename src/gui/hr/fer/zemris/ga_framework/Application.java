package hr.fer.zemris.ga_framework;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;





/**
 * An obverse to the System class, but specific
 * for this program.
 * 
 * @author Axel
 *
 */
public class Application {
	
	
	private static Properties globalconfig = new Properties();
	private static Properties userconfig = new Properties();
	private static Logger logger = null;
	
	static {
		// prepare logger
		logger = Logger.getLogger("application.log");
		try {
			FileHandler fh = new FileHandler("app.log", true);
			logger.addHandler(fh);
			logger.setLevel(Level.ALL);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (Exception e) {
			System.err.println("Could not prepare logger.");
			System.exit(1);
		}
		
		// read properties file
		try {
			globalconfig.load(new FileInputStream("global.conf"));
		} catch (Exception e) {
			logexcept("Could not load global config file.", e);
			System.exit(1);
		}
		
		// read user properties file
		try {
			userconfig.load(new FileInputStream("user.conf"));
		} catch (Exception e) {
			logexcept("Could not load user config file.", e);
		}
	}
	
	
	public synchronized static String getProperty(String key) {
		return globalconfig.getProperty(key);
	}
	
	public synchronized static String getUserProperty(String key) {
		return userconfig.getProperty(key);
	}
	
	public synchronized static void setUserProperty(String key, String value) {
		userconfig.setProperty(key, value);
	}
	
	public synchronized static void saveUserProperties() {
		try {
			userconfig.store(new FileOutputStream("user.conf"), "");
		} catch (Exception e) {
			logexcept("Could not save properties.", e);
		}
	}
	
	public synchronized static void logerror(String title, String errorMessage) {
		logger.log(Level.SEVERE, title + "\n" + errorMessage);
	}
	
	public synchronized static void logexcept(String title, Exception e) {
		// build message
		StringBuilder sb = new StringBuilder();
		appendException(sb, e);
		Throwable cause = e.getCause();
		if (cause != null) {
			sb.append("Caused by: ");
			appendException(sb, cause);
		}
		
		// log
		logger.log(Level.SEVERE, title + "\n" + sb.toString());
	}
	
	private static void appendException(StringBuilder sb, Throwable e) {
		sb.append(e.getClass().getSimpleName());
		String message = e.getMessage();
		sb.append(": ");
		sb.append((message == null) ? "(no exception message)" : message);
		sb.append("\n");
		for (StackTraceElement elem : e.getStackTrace()) {
			sb.append(elem.toString()).append("\n");
		}
		sb.append("\n");
		if (e.getCause() != null) {
			sb.append("Caused by: ");
			appendException(sb, e.getCause());
		}
	}
	
}














