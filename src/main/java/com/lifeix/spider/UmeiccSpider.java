package com.lifeix.spider;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lifeix.utils.FileUtils;
import com.lifeix.utils.RandomUtils;
import com.lifeix.utils.ThreadPoolManage;

public class UmeiccSpider {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ThefancySpider.class);

	public static final String www_url = "www.umei.cc";

	private static int index = 1;

	//private static final String URL_PREFIX = "http://" + www_url+ "/p/gaoqing/index-" + index + ".htm";

	public static void parserUrl(String url) {

		LOGGER.info(" parse url :" + url);

		String response = SpiderUtils.parseResponseStr(url,null,true);

		Document document = Jsoup.parse(response);

		Elements elements = document.select("div.t a");
		System.out.println(elements.size());
		for (int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);
			String href = element.attr("href");
			String link = "http://" + www_url + href;

			SpiderUtils.writeLinks(link, www_url);
			/*
			 * if (i==elements.size()-1) { String tempURl =
			 * URL_PREFIX+"/?next="+id; //parserUrl(tempURl); }
			 */
		}

		if (index >= 1997) {
			return;
		}
		index++;
		String nextLink = "http://" + www_url+ "/p/gaoqing/index-" + index + ".htm";;
		ThreadPoolManage.startTask(new ParseUrlThread(nextLink));
	}

	public static void main(String[] args) {
		//UmeiccSpider.parserUrl("http://" + www_url);
		List<String> urls = FileUtils.paserFileToStrArr("/home/neoyin/temp/test/temp_www.umei.cc");
		String pattern = "[0-9]{1,18}.htm";
		
		for (String url : urls) {
			Matcher matcher = Pattern.compile(pattern).matcher(url);
			if (matcher.find()) {
				System.out.println("url: "+url);
			}else {
				ThreadPoolManage.startTask(new ParseUrlThread(url));
			}
		}
		
	
	}
	
	
	private static class ParseUrlThread implements Runnable {

		private static final Logger LOGGER = LoggerFactory
				.getLogger(ParseUrlThread.class);

		private String url;

		public ParseUrlThread(String url) {
			this.url = url;
		}

		@Override
		public void run() {
			try {

				LOGGER.info(" thread sleep == " + url);
				Thread.sleep(RandomUtils.getRandomNum(0));
				LOGGER.info(" thread start ==" + url);
				parserUrl(url);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

}
