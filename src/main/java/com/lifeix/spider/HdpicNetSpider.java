package com.lifeix.spider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lifeix.post.RobotUtils;
import com.lifeix.utils.FileUtils;
import com.lifeix.utils.RandomUtils;
import com.lifeix.utils.ThreadPoolManage;

public class HdpicNetSpider {
private static final Logger LOGGER = LoggerFactory.getLogger(ThefancySpider.class);
	
	public static final String www_url ="www.hdpic.net";
	
	private static final String URL_PREFIX = "http://"+www_url;
	private static Map<String,Integer> urlMap = new HashMap<String, Integer>();
	

	public static void parserUrl(String url){
		
		LOGGER.info(" parse url :"+url);
		
		String response = SpiderUtils.parseResponseStr(url,"GBK",true);
		if (response==null) {
			LOGGER.info(" parse url :"+url+" ===> is no content ");
			return ;
		}
		Document document = Jsoup.parse(response);
		
		Elements elements =  document.select("#listpic a");
		LOGGER.info(" parse url :"+url+" elements size: "+ elements.size());
		for (int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);
			
			String href =element.attr("href");
			if (href==null||href.length()<2||href.contains("javascript")) {
				continue;
			}
			String link =href.startsWith("http://www.hdpic.net")?href: URL_PREFIX+href;
			if (href.endsWith(".html")) {
				
				String pattern = "[0-9]{3,18}.htm";
				
				Matcher matcher = Pattern.compile(pattern).matcher(link);
				if (matcher.find()) {
					if (urlMap.get(link)==null) {
						SpiderUtils.writeLinks(link,www_url);
						ThreadPoolManage.startTask(new ParseUrlThread(link));
						urlMap.put(link, 1);
					}
				}
			}else {
				if (link.startsWith("http://www.hdpic.net")) {
					ThreadPoolManage.startTask(new ParseUrlThread(link));
				}
				
			}
			
			
			
			/*if (i==elements.size()-1) {
				String tempURl = URL_PREFIX+"/?next="+id;
				//parserUrl(tempURl);
			}*/
		}
		/*LOGGER.info(" parse url end :"+url);
		Element next= document.select(".pagination .btn-next").first();
		if (next==null) {
			LOGGER.info("没有更多数据了==="+url );
			ThreadPoolManage.endTask();
			return ;
		}
		String nextLink = URL_PREFIX+next.attr("href");*/
		
		
		
	}
	
	
	
	public static void main(String[] args) {
		
		
		
		String path = RobotUtils.getValueByKey("post_dir");
		String fileName = path+"temp_"+www_url;
		List<String> list = FileUtils.paserFileToStrArr(fileName);
		for (String str : list) {
			urlMap.put(str, 1);
		}
		
		for (int i = 1; i < 6; i++) {
			String url ="http://www.hdpic.net/toupai/";
			if (i>1) {
				url +="default"+i+".html";
			}
			parserUrl(url);
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
