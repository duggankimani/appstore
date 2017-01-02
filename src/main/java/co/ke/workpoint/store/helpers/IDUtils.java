package co.ke.workpoint.store.helpers;

import org.apache.commons.lang3.RandomStringUtils;

public class IDUtils {

	public static String generateId(){
		//String uuid = UUID.randomUUID().toString();
		//uuid.replaceAll("-", "");
		return RandomStringUtils.random(16, true, true);
	}
	
	public static String generateTempPassword(){
		return RandomStringUtils.random(6,true,true);
	}
}
