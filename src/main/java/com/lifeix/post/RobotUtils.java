package com.lifeix.post;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RobotUtils {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(RobotUtils.class);
	
	private static Map<String,String> configMap = new HashMap<String, String>();

	
	private static final String FILE_NAME ="robot_config.properties";
	
	public static String getValueByKey(String key){
		if (configMap.size()<1) {
			initConfig();
		}
		return configMap.get(key);
	}
	
	/**
	 * 初始化配置
	 */
	public static void initConfig(){
		if (configMap.size()<1) {
			LOGGER.info(" =====================  start init config ====================  ");
			String path = "";
			try {
				path = RobotUtils.class.getResource("/").getPath().replaceAll("%20", " ");
			} catch (Exception e) {
				path =System.getProperty("user.dir")+"/";
			}
			
			LOGGER.debug(path+"---FILE_NAME---"+FILE_NAME+"---replaceAll");
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream(path+FILE_NAME));
				Enumeration enu = properties.propertyNames();
			    while (enu.hasMoreElements()) {
					String key = (String) enu.nextElement();
					String value = properties.getProperty(key);
					LOGGER.info(key+" = "+value);
					configMap.put(key, value);
			    }
			} catch (FileNotFoundException e) {
				LOGGER.debug(e.getMessage(),e);
				LOGGER.info(" ===================== init config fail ====================  ");
			} catch (IOException e) {
				LOGGER.debug(e.getMessage(),e);
				LOGGER.info(" ===================== init config fail ====================  ");
			}
		}
	}
	
}
