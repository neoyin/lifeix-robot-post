package com.lifeix.spider;


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

public class ThefancySpider {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(ThefancySpider.class);
	
	public static final String www_url ="www.thefancy.com";
	
	private static final String URL_PREFIX = "http://"+www_url;
//	private static Map<String,Integer> urlMap = new HashMap<String, Integer>();
	

	public static void parserUrl(String url){
		
		LOGGER.info(" parse url :"+url);
		
		String response = SpiderUtils.parseResponseStr(url,null,false);
		
		Document document = Jsoup.parse(response);
		
		Elements elements =  document.select("ol.stream li a.anchor");
		System.out.println(elements.size());
		for (int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);
			String id = element.attr("id").replace("thing-","");
			String link = URL_PREFIX+"/things/"+id;
			SpiderUtils.writeLinks(link,www_url);
			/*if (i==elements.size()-1) {
				String tempURl = URL_PREFIX+"/?next="+id;
				//parserUrl(tempURl);
			}*/
		}
		LOGGER.info(" parse url end :"+url);
		Element next= document.select(".pagination .btn-next").first();
		if (next==null) {
			LOGGER.info("没有更多数据了==="+url );
			ThreadPoolManage.endTask();
			return ;
		}
		String nextLink = URL_PREFIX+next.attr("href");
		
		ThreadPoolManage.startTask(new ParseUrlThread(nextLink));
		
	}
	
	
	
	public static void main(String[] args) {
		parserUrl(URL_PREFIX);
		
		
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
