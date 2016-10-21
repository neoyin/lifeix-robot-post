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

public class PconlineSpider {
private static final Logger LOGGER = LoggerFactory.getLogger(TopspeedcomSpider.class);
	
	public static final String www_url ="dp.pconline.com.cn";
	
	private static final String URL_PREFIX = "http://"+www_url;
	private static Map<String,Integer> urlMap = new HashMap<String, Integer>();
	

	public static void parserUrl(String url){
		
		LOGGER.info(" parse url :"+url);
		
		String response = SpiderUtils.parseResponseStr(url,"UTF-8",false);
		if (response==null) {
			LOGGER.info(" parse url :"+url+" ===> is no content ");
			return ;
		}
		Document document = Jsoup.parse(response);
		
		Elements elements =  document.select(".txt .center a");
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
		for (int i = 1; i < 5006; i++) {
			//http://dp.pconline.com.cn/list/m3_p1.html
			
			String url ="http://dp.pconline.com.cn/list/m3_p"+i+".html";
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
