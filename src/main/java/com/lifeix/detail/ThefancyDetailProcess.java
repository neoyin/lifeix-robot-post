package com.lifeix.detail;

import java.io.File;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lifeix.post.RobotUtils;
import com.lifeix.spider.SpiderUtils;
import com.lifeix.spider.ThefancySpider;
import com.lifeix.utils.FileUtils;
import com.lifeix.utils.ThreadPoolManage;

public class ThefancyDetailProcess {

	private static final Logger LOGGER = LoggerFactory.getLogger(ThefancyDetailProcess.class);
	
	
	/**
	 * 处理URL数据
	 * @param path
	 */
	public static void dowithData(String path){
		
		String dir = RobotUtils.getValueByKey("post_dir");
		String fileName = dir+"/temp_"+ThefancySpider.www_url;
		
		List<String> uList =  FileUtils.paserFileToStrArr(fileName);
		for (String url : uList) {
			ThreadPoolManage.startTask(new ThefancyDetailThread(url));
		}
	}
	
	
	
	/**
	 * 得到飞鸽数据
	 * @param url
	 */
	public static void getDoveboxDataFromUrl(String url){
		LOGGER.info(" parse url :"+url);
		if (isExist(url)) {
			LOGGER.info(" url is exist "+url);
			return ;
		}
		String response = SpiderUtils.parseResponseStr(url,null,false);
		Document document = Jsoup.parse(response);
		Elements elements = document.select("meta");
		String image ="";String desc ="";
		
		for (Element element : elements) {
			String name = element.attr("name");
			
			String content = element.attr("content");
			if (name.endsWith("image")) {
				image = content;
			}
			if (name.endsWith("description")) {
				desc = content;
			}
		}
		String title =document.title(); 
		if (image.length()>0) {
			String file = downLoadPic(image);
			if (file!=null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append(file+"\n");
				buffer.append(title+"\n");
				buffer.append(desc);
				saveDoveData(url, buffer.toString());
			}else {
				LOGGER.info(" download image fail:"+url);
			}
		}else {
			LOGGER.info(" no image :"+url);
		}
	}

	
	private static void saveDoveData(String url,String str){
		String path = RobotUtils.getValueByKey("post_dir");
		String suffix = url.substring(url.lastIndexOf("/")+1);
		String fileName = path +ThefancySpider.www_url+"/post_"+suffix+".txt";
		FileUtils.writeFile(fileName, str);
	}
	
	private static boolean isExist(String url){
		
		String path = RobotUtils.getValueByKey("post_dir");
		String suffix = url.substring(url.lastIndexOf("/")+1);
		String fileName = path +ThefancySpider.www_url+"/post_"+suffix+".txt";
		File file = new File(fileName);
		if (file.exists()) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * 存储图片
	 * @param url
	 * @return
	 */
	private static String downLoadPic(String url){
		String path = RobotUtils.getValueByKey("post_dir");
		
		String suffix = url.substring(url.lastIndexOf("/"));
		String fileName = path+suffix;
		File file = new File(fileName);
		if (file.exists()) {
			LOGGER.info(" file exists == "+fileName);
			return null;
		}
		
		return  FileUtils.downLoadPic(path+ThefancySpider.www_url, url,ThefancySpider.www_url,false);
	}
}
