package com.lifeix.detail;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lifeix.post.RobotUtils;
import com.lifeix.spider.MeizitucomSpider;
import com.lifeix.spider.SpiderUtils;
import com.lifeix.utils.FileUtils;
import com.lifeix.utils.RandomUtils;
import com.lifeix.utils.ThreadPoolManage;

public class MeizitucomDetailProcess {
private static final Logger LOGGER = LoggerFactory.getLogger(MeizitucomDetailProcess.class);
	
	
	public static void main(String[] args) {
		dowithData();
	}

	/**
	 * 处理URL数据
	 * @param path
	 */
	public static void dowithData(){
		
		String dir = RobotUtils.getValueByKey("post_dir");
		String fileName = dir+"/temp_"+MeizitucomSpider.www_url;
		
		List<String> uList =  FileUtils.paserFileToStrArr(fileName);
	
		String pattern = "[0-9]{3,18}.html";
		
		for (String url : uList) {
			/*Matcher matcher = Pattern.compile(pattern).matcher(url);
			if (matcher.find()) {
				ThreadPoolManage.startTask(new DetailProcessThread(url));
			}*/
			ThreadPoolManage.startTask(new DetailProcessThread(url));
		}
	}
	
	/**
	 * 得到飞鸽数据
	 * @param url
	 */
	public static void getDoveboxDataFromUrl(String url){
		
		if (isExist(url)) {
			LOGGER.info(" url is exist "+url);
			return ;
		}
		LOGGER.info(" parse url :"+url);
		String image ="";
		String desc ="";String title ="";String tags ="";
		String pageUrl =url;
			String tempURL =pageUrl;
			
			String response = SpiderUtils.parseResponseStr(tempURL,"UTF-8",false);
			Document document = Jsoup.parse(response);
			Elements elements = document.select("meta");
			
			for (Element element : elements) {
				String name = element.attr("name");
				String content = element.attr("content");
				/*if (name.endsWith("image")) {
					image = content;
				}*/
				if (name.endsWith("description")) {
					desc = content.replace(MeizitucomSpider.www_url, "");
				}
				if (name.endsWith("keywords")) {
					tags = content;
				}
			}
			Elements images = document.select(".postContent img");
			for (int i = 0; i < images.size(); i++) {
				Element  element =  images.get(i);
				String src = element.attr("src");
				image +=src+",";
			}	
			/*
			Elements tagsEle = document.select(".tag a");
			if (tagsEle!=null&&tagsEle.size()>0) {
				for (int i = 0; i < tagsEle.size(); i++) {
					Element  element =  tagsEle.get(i);
					String t = element.text();
					tags +=t+",";
				}	
			}*/
			
			
			
			title=document.title();
			if (title!=null) {
				title =title.replace("|妹子图","");
			}
			//desc = document.getElementById("ShowCon").text();
		if (image.length()>0) {
			String file = downLoadPic(image);
			if (file!=null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append(file+"\n");
				buffer.append(title+"\n");
				buffer.append(""+"\n");
				buffer.append(tags+"\n");
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
		String fileName = path +MeizitucomSpider.www_url+"/post_"+suffix+".txt";
		FileUtils.writeFile(fileName, str);
	}
	
	private static boolean isExist(String url){
		
		String path = RobotUtils.getValueByKey("post_dir");
		String suffix = url.substring(url.lastIndexOf("/")+1);
		String fileName = path +MeizitucomSpider.www_url+"/post_"+suffix+".txt";
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
	private static String downLoadPic(String images){
		String path = RobotUtils.getValueByKey("post_dir");
		String[] temp = images.split(",");
		
		String buffer ="";
		for (String url : temp) {
			url = url.replaceAll("\\\\", "/");
			
			if (!url.startsWith("http://")) {
				
				url= "http://"+url;
			}
			String suffix = url.substring(url.lastIndexOf("/"));
			String fileName = path+suffix;
			File file = new File(fileName);
			if (file.exists()) {
				LOGGER.info(" file exists == "+fileName);
				continue;
			}
			String pic_path =  FileUtils.downLoadPic(path+MeizitucomSpider.www_url, url,MeizitucomSpider.www_url,false);
			buffer+=pic_path+",";
		}
		return buffer;
	}
	
	private static class DetailProcessThread implements Runnable{

		private String url ;
		
		public DetailProcessThread(String url){
			this.url = url;
		}
		@Override
		public void run() {
			try {
				Thread.sleep(RandomUtils.getRandomNum(0));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			getDoveboxDataFromUrl(url);
		}
	}
	
	
}
