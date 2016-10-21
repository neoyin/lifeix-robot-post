package com.lifeix.spider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class ProductYeskySpider {
private static final Logger LOGGER = LoggerFactory.getLogger(YeskySpider.class);
	
	public static final String www_url ="product.yesky.com";
	
	private static final String URL_PREFIX = "http://"+www_url;
	private static Map<String,Integer> urlMap = new HashMap<String, Integer>();
	

	public static void parserUrl(String url){
		
		LOGGER.info(" parse url :"+url);
		
		String response = SpiderUtils.parseResponseStr(url,"gb2312",false);
		if (response==null) {
			LOGGER.info(" parse url :"+url+" ===> is no content ");
			return ;
		}
		Document document = Jsoup.parse(response);
		
		Elements elements =  document.select("#main .box h2 a");
		LOGGER.info(" parse url :"+url+" elements size: "+ elements.size());
		for (int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);
			String href =element.attr("href");
			if (href==null||href.length()<2||href.contains("javascript")) {
				continue;
			}
			if (urlMap.get(href)==null) {
				SpiderUtils.writeLinks(href,www_url);
				urlMap.put(href, 1);
			}
		}	
		
		Elements eleA =  document.select(".page_tabs font a");
		for (int i = 0; i < eleA.size(); i++) {
			Element element = eleA.get(i);
			if (element.text().equals("下一页")) {
				String href =URL_PREFIX+element.attr("href");
				System.err.println(" pages :"+href);
				ThreadPoolManage.startTask(new ParseUrlThread(href));
				break;
			}
		}
		
		
		//
		/*Elements eleA =  document.select(".pagination_link_on");
		for (int i = 0; i < eleA.size(); i++) {
			Element element = eleA.get(i);
			String href =element.attr("href");
			if (href==null||href.length()<2||href.contains("javascript")) {
				continue;
			}
			if (urlMap.get(href)==null) {
				ThreadPoolManage.startTask(new ParseUrlThread(href));
				urlMap.put(href, 1);
			}
			//SpiderUtils.writeLinks(href,www_url);
			
		}	*/
		
		
		
	}
	
	
	
	public static void main(String[] args) {
		
		String path = RobotUtils.getValueByKey("post_dir");
		String fileName = path+"temp_"+www_url;
		List<String> list = FileUtils.paserFileToStrArr(fileName);
		for (String str : list) {
			urlMap.put(str, 1);
		}
		parserUrl("http://product.yesky.com/more/506301_31372_new_products_650.shtml");
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
