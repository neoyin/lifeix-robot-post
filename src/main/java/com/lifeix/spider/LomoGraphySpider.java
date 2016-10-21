package com.lifeix.spider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lifeix.post.RobotUtils;
import com.lifeix.utils.FileUtils;
import com.lifeix.utils.RandomUtils;
import com.lifeix.utils.ThreadPoolManage;

public class LomoGraphySpider {
private static final Logger LOGGER = LoggerFactory.getLogger(PhotographySpider.class);
	
	public static final String www_url ="www.lomography.cn";
	
	private static final String URL_PREFIX = "http://"+www_url;
	private static Map<String,Integer> urlMap = new HashMap<String, Integer>();
	

	public static void parserUrl(String url){
		
		LOGGER.info(" parse url :"+url);
		
		String response = SpiderUtils.parseResponseStr(url,"UTF-8",false);
		if (response==null) {
			LOGGER.info(" parse url :"+url+" ===> is no content ");
			return ;
		}
		try {
			//JSONObject json = new JSONObject(response);
			JSONArray array = new JSONArray(response);
			
			System.err.println(" length is =="+array.length());
			
			for(int i =0; i<array.length();i++){
				JSONObject temp = array.getJSONObject(i);
				//{"alt":"","width":96,"url":"/photos/6034054","height":144,"src":"http://cloud.lomography.com/96/144/bb/a7d98d732c31cc7b162af355b22e323c287d8c.jpg","id":6034054}
				String link = URL_PREFIX+temp.getString("url");
				if (urlMap.get(link)==null) {
					SpiderUtils.writeLinks(link,www_url);
					urlMap.put(link, 1);
				}
			}
			
			
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		//ThreadPoolManage.startTask(new ParseUrlThread(href));
	}
	
	
	
	public static void main(String[] args) {
		
		String path = RobotUtils.getValueByKey("post_dir");
		String fileName = path+"temp_"+www_url;
		List<String> list = FileUtils.paserFileToStrArr(fileName);
		for (String str : list) {
			urlMap.put(str, 1);
		}
		parserUrl("http://www.lomography.cn/photos/stream");
		for(int i=1000;i<2000;i++){
			String url ="http://www.lomography.cn/photos/stream.json?offset="+i;
			ThreadPoolManage.startTask(new ParseUrlThread(url));
		}
	}
	
	
	private static class ParseUrlThread implements Runnable{
		
		private static final Logger LOGGER = LoggerFactory.getLogger(ParseUrlThread.class);
		
		private String url ;
		
		public ParseUrlThread(String url){
			this.url =url;
		}
		
		
		@Override
		public void run() {
			try {
				
				LOGGER.info(" thread sleep == "+url);
				Thread.sleep(RandomUtils.getRandomNum(0));
				LOGGER.info(" thread start =="+url);
				parserUrl(url);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
}
