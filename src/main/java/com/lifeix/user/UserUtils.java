package com.lifeix.user;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l99.exception.service.L99IllegalOperateException;
import com.lifeix.client.AuthorizationData;
import com.lifeix.client.DoveboxClientUtils;
import com.lifeix.post.RobotUtils;
import com.lifeix.utils.FileUtils;

public class UserUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserUtils.class);
	
	
	
	/**
	 * 通过URL 得到相关用户
	 * @param url
	 * @return
	 */
	public static List<AuthorizationData> getUserByUrl(String url){
		
		List<AuthorizationData> userList = new ArrayList<AuthorizationData>();
		
		if (url==null||url.length()<1) {
			LOGGER.info(" url is null ");
			return null;
		}
		String userPath = RobotUtils.getValueByKey("user_info");
		LOGGER.info(" user path "+userPath);
		List<String> users =  FileUtils.paserFileToStrArr(userPath);
		for (String user : users) {
			String temp[] = user.split(" ");
			if (temp==null||temp.length<3) {
				continue;
			}
			if (url.toLowerCase().contains(temp[2].toLowerCase())) {
				try {
					AuthorizationData data = new AuthorizationData(temp[0], temp[1], DoveboxClientUtils.client_version);
					data.setIpAddress(url);
					userList.add(data);
					LOGGER.info(" 负责 url :"+url+" 用户为"+data);
				} catch (L99IllegalOperateException e) {
					e.printStackTrace();
				}
			}
		}
		return userList;
	}
	
}
