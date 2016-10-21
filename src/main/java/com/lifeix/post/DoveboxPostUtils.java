package com.lifeix.post;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.lifeix.utils.FileUtils;

public class DoveboxPostUtils {

	
	public static List<DoveboxFormData> getDataFromFile(String dir){
		
		List<DoveboxFormData> dataList = new ArrayList<DoveboxFormData>();
		
		File file = new File(dir);
		
		String[] filesStr = file.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".txt")) {
					return true;
				}
				return false;
			}
		});
		
	/*	File[] files = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".txt")) {
					return true;
				}
				return false;
			}
		});*/
		if (filesStr==null||filesStr.length<1) {
			return dataList;
		}
		for (int i = 0; i < filesStr.length; i++) {
			//File temp = files[i];
			String path = filesStr[i];
			String str= FileUtils.readFile(file.getAbsolutePath()+"/"+path);
			DoveboxFormData data = parseData(str);
			if (data!=null) {
			
				data.setFileName(file.getAbsolutePath()+"/"+path);
				dataList.add(data);
			}
		}
		
		return dataList;
		
	}
	
	
	
	
	public static DoveboxFormData parseData(String str){
		if (str==null||str.length()<1) {
			return null;
		}
		String[] temp = str.split("\n");
		if (temp!=null&&temp.length>1) {
			String desc ="";
			try {
				desc = temp[2];

			} catch (Exception e) {
				desc ="";
			}
			String tags ="";
			try {
				tags = temp[3];

			} catch (Exception e) {
				tags ="";
			}
			
			String pics = temp[0];
			String[] pic_temp = pics.split(",");
			if (pic_temp==null||pic_temp.length<1) {
				return null;
			}
			DoveboxFormData data = new DoveboxFormData(temp[1],desc, Arrays.asList(pic_temp),tags);
			return data;
		}
		return null;
	}
	
	
	
	public static void main(String[] args) {
		List<DoveboxFormData> datas= getDataFromFile("/home/neoyin/temp/test/www.thefancy.com");
		for (DoveboxFormData data : datas) {
			System.out.println(data);
		}
	}
	
	
}
