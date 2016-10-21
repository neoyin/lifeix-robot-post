package com.lifeix.post;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.l99.exception.service.L99IllegalDataException;
import com.lifeix.client.AuthorizationData;
import com.lifeix.client.BasicParams;
import com.lifeix.client.DoveboxClientUtils;


public class DoveboxPost {

	
	public static void main(String[] args) {
		String temp ="aaaaaaaaaaaaaaaaa,";
		temp = temp.substring(0,temp.lastIndexOf(","));
		System.out.println(temp);
	}
	
	
	public static void postDovbox(DoveboxFormData data,AuthorizationData authData,BasicParams params) throws L99IllegalDataException{
		
		String dovebox_path = RobotUtils.getValueByKey("dovebox_path");
		DoveboxClientUtils.init(dovebox_path);
		
		List<File> files = new ArrayList<File>();
		for (String pic : data.getPic()) {
			if (pic!=null&&pic.length()>10) {
				if (pic.endsWith(",")) {
					pic = pic.substring(0,pic.lastIndexOf(","));
					pic.replaceAll(" ","");
				}
				
				
				File f = new File(pic);
				if (f.exists()) {
					files.add(f);
				}
			}
		}
		DoveboxClientUtils.photoDove(0, files, null, data.getDesc(), 40, data.getTitle(), data.getTag(), params, authData);
	}

	
	
}
