package com.lifeix.detail;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lifeix.post.RobotUtils;
import com.lifeix.spider.HuabanSpider;
import com.lifeix.spider.SpiderUtils;
import com.lifeix.utils.FileUtils;
import com.lifeix.utils.RandomUtils;
import com.lifeix.utils.ThreadPoolManage;

public class HuabancomDetailProcess {
private static final Logger LOGGER = LoggerFactory.getLogger(HuabancomDetailProcess.class);
	
	
	public static void main(String[] args) {
		dowithData();
	}

	/**
	 * 处理URL数据
	 * @param path
	 */
	public static void dowithData(){
		
		String dir = RobotUtils.getValueByKey("post_dir");
		String fileName = dir+"/temp_"+HuabanSpider.www_url;
		
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
			
			if (response==null) {
				return ;
			}
			
			Document document = Jsoup.parse(response);
			Elements elements = document.select("meta");
			
			for (Element element : elements) {
				String name = element.attr("name");
				String content = element.attr("content");
				/*if (name.endsWith("image")) {
					image = content;
				}*/
				if (name.endsWith("description")) {
					desc = content.replace(HuabanSpider.www_url, "");
				}
				if (name.endsWith("keywords")) {
					tags = content;
				}
				
			}
			
			Element eleScript = document.select("script").first();
			String temp = eleScript.html();
			//System.out.println(temp);
			if (temp==null||temp.length()<1) {
				return ;
			}
			String scriptTemp[] =  temp.split(";\n");
			for (String str : scriptTemp) {
				if (str.startsWith("app[\"page\"]")) {
					//System.out.println(str);
					String page =  str.split(" = ")[1];
					//System.out.println(page);
					try {
						JSONObject object = new JSONObject(page);
						JSONObject pin = object.getJSONObject("pin");
						String orig_source = pin.getString("orig_source");
						if (orig_source.contains("moko.hk")) {
							orig_source ="";
						}
						
						JSONObject board = pin.getJSONObject("board");
						if (board!=null) {
							String tempTag = board.getString("title");
							if (tempTag!=null) {
								tags = tempTag;
							}
						}
						//System.out.println(orig_source);
						String raw_text = pin.getString("raw_text");
						title = raw_text;
						JSONObject file = pin.getJSONObject("file");
						if (orig_source!=null&&orig_source.length()>10) {
							image +=orig_source+",";
						}else {
							String key = file.getString("key");
							image = "http://img.hb.aicdn.com/"+key+"_fw554";
							image = image+",";
						}
						
						/*image +=image+",";
						System.out.println(key);
						*/
					} catch (JSONException e) {
						e.printStackTrace();
						return ;
						
					}
				}
			}
			
		
			Element eleTitle = document.select("#pin_caption .text").first();
			if (eleTitle!=null) {
				title = eleTitle.text();
			}
			
			
			/*title=document.title();
			if (title!=null) {
				title =title.replace("|妹子图","");
			}*/
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
		String fileName = path +HuabanSpider.www_url+"/post_"+suffix+".txt";
		FileUtils.writeFile(fileName, str);
	}
	
	private static boolean isExist(String url){
		
		String path = RobotUtils.getValueByKey("post_dir");
		String suffix = url.substring(url.lastIndexOf("/")+1);
		String fileName = path +HuabanSpider.www_url+"/post_"+suffix+".txt";
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
			String pic_path =  FileUtils.downLoadPic(path+HuabanSpider.www_url, url,HuabanSpider.www_url,false);
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
