package hr.fer.zemris.ga_framework.model;


/**
 * Class defining some common constants.
 * 
 * @author Axel
 *
 */
public class Constants {

	public static final String[] ALLOWED_IMAGE_TYPES = {
		"png", "gif", "jpg", "jpeg"
	};
	
	public static final boolean isAllowedImageType(String filename) {
		for (String s : ALLOWED_IMAGE_TYPES) {
			if (filename.endsWith("." + s)) return true;
		}
		return false;
	}
	
}














