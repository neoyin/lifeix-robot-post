package com.app;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l99.exception.service.L99IllegalOperateException;
import com.lifeix.client.AuthorizationData;
import com.lifeix.client.BasicParams;
import com.lifeix.client.DoveboxClientUtils;
import com.lifeix.post.DoveboxFormData;
import com.lifeix.spider.SpiderUtils;
import com.lifeix.utils.FileUtils;

public class DailyRobotTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(DailyRobotTask.class);
	
	
	private static Map<String,String> configMap = new HashMap<String, String>();
	
	public static SpiderPojo spiderconfig ;
	
	private static String cookieVal =null;
	
	private static boolean proxyFlag = false;
	
	
	/**
	 * 得到相关配置信息
	 * @param key
	 * @return
	 */
	public static String getConfig(String key){
		return configMap.get(key);
	}
	
	
	public static String getUrlPrefix(String url){
		
		String temp = url.replace("http://", "");
		temp = temp.substring(0,temp.indexOf("/")+1) ;
		return temp;
	}
	
	/**
	 * 得到cookie
	 * @return
	 */
	public static String getCookie(){
		if (cookieVal==null) {
			try {
				cookieVal= TextDailyPost.loginAndGetCookie(spiderconfig.getLongNO(),spiderconfig.getPassword());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return cookieVal;
	}
	
	
	/**
	 * 初始化
	 * @param filename
	 */
	public static void init(String filename){
		
		if (filename==null) {
			
			LOGGER.info("请输入配置文件的路径");
			return ;
		}
		try {
			Properties properties = new Properties();
			properties.load(new InputStreamReader(new FileInputStream(new File(filename)) , "UTF-8"));
			LOGGER.info("各配置如下:");
			Enumeration enu = properties.propertyNames();
		    while (enu.hasMoreElements()) {
				String key = (String) enu.nextElement();
				String value = properties.getProperty(key);
				
				LOGGER.info(key+"=="+value);
				
				configMap.put(key, value);
		    }
		    String doveboxPath =properties.getProperty("doveboxPath");
			LOGGER.info("飞鸽项目路径:"+doveboxPath);
		    DoveboxClientUtils.init(doveboxPath);
		    
		    spiderconfig = new SpiderPojo();
		    
		    spiderconfig.setSpiderUrl(properties.getProperty("spiderUrl"));
		    spiderconfig.setImagePath(properties.getProperty("imagePath"));
		    spiderconfig.setDescOffset(properties.getProperty("descOffset"));
		    spiderconfig.setImageOffset(properties.getProperty("imageOffset"));
		    spiderconfig.setLongNO(properties.getProperty("longNO"));
		    spiderconfig.setmPassword(properties.getProperty("mPassword"));
		    spiderconfig.setPassword(properties.getProperty("password"));
		    spiderconfig.setTagsOffset(properties.getProperty("tagsOffset"));
		    spiderconfig.setTitleOffset(properties.getProperty("titleOffset"));
		    spiderconfig.setFilterOffset(properties.getProperty("filterOffset"));
		    spiderconfig.setCharSet(((properties.getProperty("charSet")==null)||properties.getProperty("charSet").length()<1)?"UTF-8":properties.getProperty("charSet"));
		    spiderconfig.setPostPath(properties.getProperty("postPath"));
		    spiderconfig.setPostParams(properties.getProperty("postParams"));
		    spiderconfig.setPostLocal(properties.getProperty("postLocal"));
		    spiderconfig.setPhotoOffset(properties.getProperty("photoOffset"));
		    spiderconfig.setPhotoSrc(properties.getProperty("photoSrc"));
		    spiderconfig.setPageOffset(properties.getProperty("pageOffset"));
		    spiderconfig.setDraft(properties.getProperty("draft"));
		    
		    LOGGER.info(spiderconfig.toString());
		    
		    if (configMap.get("proxy_file")!=null&&configMap.get("proxy_file").length()>0) {
				SpiderUtils.setProxy_path(configMap.get("proxy_file"));
				proxyFlag =  true;
			};
		    
		    
		    
		} catch (FileNotFoundException e) {
			LOGGER.info("配置文件出错..."+e.getMessage(),e);
		} catch (IOException e) {
			LOGGER.info("配置文件出错..."+e.getMessage(),e);
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		
		if (args==null||args.length<1) {
			LOGGER.info("没有相关配置文件的地址...如下：");
			StringBuffer buffer = new StringBuffer("\n");
			buffer.append("doveboxPath=http://dbapi.l99.com/  #飞鸽地址 \n");
			buffer.append("spiderUrl=http://www.36kr.com/p/202111.html  #要解析的文章地址 \n");
			buffer.append("imagePath=/home/neoyin/temp/bigger/ #照片存储地址 --\n ");
			
			buffer.append("descOffset=.mainContent  #文章内容选择器 -- \n");
			buffer.append("imageOffset=p img  #照片选择器 \n");
			buffer.append("photoOffset=");
			buffer.append("photoSrc=");
			buffer.append("tagsOffset=    #标签选择器 \n");
			buffer.append("titleOffset=   #标题选择器 \n");
			buffer.append("longNO=3251037 #龙号 \n");
			
			buffer.append("filterOffset=.related_topics  #过滤选择器 \n");
			buffer.append("mPassword=b0363b437e0eec45de9836e4a5af4bf8 　#用户密文密码　\n");
			buffer.append("password=321654  #用户密码 \n");
			buffer.append("charSet=UTF-8　　#字符编码　\n");
			buffer.append("postPath=...txt #url保存地址");
			LOGGER.info(buffer.toString());
			
			return ;
		}
		init(args[0]);
	
		if (!indexUrl()) {
			startTask(spiderconfig.getSpiderUrl());
		}
	}
	
	private static boolean indexUrl(){
		
		String temp = getConfig("post_interval");
		
		int interval = (temp==null||temp.length()<1)?1:Integer.parseInt(temp);
		
		if (getConfig("urlIndexFilter")!=null&&getConfig("urlIndexFilter").length()>0) {
			LOGGER.info("检测到有url列表配置....开始解析");
			List<String> linkList = parseLink(null,getConfig("urlIndexFilter"));
			LOGGER.info("解析url 个数为:"+linkList.size());
		
			for (int i = linkList.size()-1; i >0 ; i--) {
				String link = linkList.get(i);
			
			//for (String link : linkList) {
				if (isDoit(link)) {
					LOGGER.info("此url 已处理过:"+link);
					continue;
				}
				try {
					startTask(link);
					Thread.sleep(60*1000*interval);
				} catch (IOException e) {
					LOGGER.info(e.getMessage(),e);
				} catch (InterruptedException e) {
					LOGGER.info(e.getMessage(),e);
				}
			}
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 解析url 列表
	 * @param offset
	 * @return
	 */
	public static List<String> parseLink(String html,String offset){
		
		String parseUrl =spiderconfig.getSpiderUrl();
		
		String response ="";
		if (html==null||html.length()<1) {
			LOGGER.info("ready to get url list : start parse url :"+parseUrl);
			response = SpiderUtils.parseResponseStr(parseUrl,spiderconfig.getCharSet(),proxyFlag);
		}else {
			response = html;
		}
		List<String> linkList = new ArrayList<String>();
		try {
			Document document = Jsoup.parse(response);
			if (document==null||offset==null) {
				LOGGER.info(" url get document is null :"+parseUrl+"==="+offset);
				return linkList;
			}
			Elements elements = document.select(offset);
			
			
			String temp = getUrlPrefix(parseUrl);
			
			for (Element element : elements) {
				String link = element.attr("href");
				if (!link.startsWith("http://")&&!link.startsWith(temp)) {
					
					if (!link.startsWith("/")) {
						if (spiderconfig.getSpiderUrl().endsWith("/")) {
							link = spiderconfig.getSpiderUrl()+link;
						}else {
							link = spiderconfig.getSpiderUrl()+"/"+link;
						}
						
						
					}else {
						/*String temp = spiderconfig.getSpiderUrl().replace("http://", "");
						temp = temp.substring(0,temp.indexOf("/")+1) ;*/
						String tempSrc= link.replace("http://","");
						tempSrc = tempSrc.replace(temp, "");
						if (temp.endsWith("/")&&tempSrc.startsWith("/")) {
							link ="http://"+temp+tempSrc.substring(1);
						}else {
							link ="http://"+temp+tempSrc;
						}
						
					}
				}
				
				linkList.add(link);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return linkList;
	}
	
	
	
	
	/**
	 * 开始任务
	 * @throws IOException
	 */
	private static void startTask(String url) throws IOException{
		
		LOGGER.info("任务开始...");
		LOGGER.info("用户登陆...");
		//String cookieVal = getCookie();
		if (cookieVal==null||cookieVal.length()<1) {
			cookieVal = getCookie();
			if (cookieVal==null||cookieVal.length()<1) {
				LOGGER.info("用户登陆失败...没有得到cookie");
				return ;
			}
		}
		LOGGER.info("开始爬取数据...");
		DoveboxFormData data = DailySpider.getDoveboxDataFromUrl(url, spiderconfig.getCharSet(),proxyFlag);
		//是否发text
		boolean textFlag = true;
		List<Long> photoIds = new ArrayList<Long>();
		if (spiderconfig.getPhotoOffset()!=null&&spiderconfig.getPhotoOffset().length()>0) {
			LOGGER.info("解析照片并得到照片ids ...");
			//发照片飞鸽
			textFlag =false;
			photoIds =  DailySpider.getPhotoIds(data.getHtml(), spiderconfig.getLongNO(),spiderconfig.getmPassword());
			
			List<String> pageLinks =parseLink(data.getHtml(), spiderconfig.getPageOffset());
			Set<String> pl = new HashSet<String>(pageLinks);
			
			for (String pageUrl : pl) {
				try {
					LOGGER.info("add  page url :"+pageUrl);
					DoveboxFormData tempData = DailySpider.getDoveboxDataFromUrl(pageUrl, spiderconfig.getCharSet(),proxyFlag);
					if (tempData==null) {
						continue;
					}
					List<Long> tempPhotoIds  =  DailySpider.getPhotoIds(tempData.getHtml(), spiderconfig.getLongNO(),spiderconfig.getmPassword());
					//data.setDesc(data.getDesc()+tempDesc);
					if (tempPhotoIds==null) {
						continue;
					}
					photoIds.addAll(tempPhotoIds);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			data.setDesc("");
		
		}
		
		if (spiderconfig.getImageOffset()!=null&&spiderconfig.getImageOffset().length()>0) {
			textFlag =true;
			LOGGER.info("上传并照片并替换...");
			
			String temp = DailySpider.replceImgs(data.getDesc(), spiderconfig.getLongNO(),spiderconfig.getmPassword());
			List<String> pageLinks =parseLink(data.getHtml(), spiderconfig.getPageOffset());
			data.setDesc(temp);
			
			Set<String> pl = new HashSet<String>(pageLinks);
			for (String pageUrl : pl) {
				try {
					LOGGER.info("add  page url :"+pageUrl);
					DoveboxFormData tempData = DailySpider.getDoveboxDataFromUrl(pageUrl, spiderconfig.getCharSet(),proxyFlag);
					if (tempData!=null) {
						String tempDesc = DailySpider.replceImgs(tempData.getDesc(), spiderconfig.getLongNO(),spiderconfig.getmPassword());
						data.setDesc(data.getDesc()+tempDesc);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		
		
		// 处理数据　过滤等 ###################################################################
		String filterTitle = getConfig("filterTitle");
		if (filterTitle!=null&&filterTitle.length()>0) {
			LOGGER.info("过滤标题字符..."+filterTitle);
			String finalTitle = data.getTitle().replaceAll(filterTitle, "");
			data.setTitle(finalTitle);
			LOGGER.info("过滤内容字符..."+filterTitle);
			String finalDesc = data.getDesc().replaceAll(filterTitle, "");
			data.setDesc(finalDesc);
			String finalTag = data.getTag().replaceAll(filterTitle, "");
			data.setTag(finalTag);
			
		}
		String descDetal = getConfig("descDetal");
		if (descDetal!=null&&descDetal.length()>0) {
			data.setDesc(data.getDesc()+descDetal);
		}
		
		// 地点等其它参数 #########################################
		String params =spiderconfig.getPostParams();
		if (spiderconfig.getTagsOffset().equals("none")) {
			data.setTag("");
		}
		/*if (spiderconfig.getPostLocal()!=null&&spiderconfig.getPostLocal().length()>0) {
			LOGGER.info("开始解析经纬度...");
			try {
				JSONObject object = GoogleGeoUtil.getGoogleLatLng(spiderconfig.getPostLocal());
				if (object!=null) {
					String tempParams = object.getString("addressParams");
					//spiderconfig.setPostParams(spiderconfig.getPostParams()+"&"+tempParams);
					params = spiderconfig.getPostParams()+"&"+tempParams;
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}*/
		
		//发表数据###################################################
		if (textFlag) {
			/**
			 * <html>
			 <head></head>
			 <body></body>
			</html>
			 */
			String temp = data.getDesc().replaceAll("</?body>", "").replaceAll("</?head>", "").replaceAll("</?html>", "").replaceAll("\n","");
			data.setDesc(temp);
			LOGGER.info("发表文本飞鸽...");
			if (data.getDesc()==null||data.getDesc().length()<20||data.getTitle()==null||data.getTitle().length()<3) {
				LOGGER.info("抓取飞鸽内容过短...跳过");
			}else {
				TextDailyPost.saveText(cookieVal, data.getTitle(),data.getDesc(),data.getTag(),params);
			}
		}else {
			LOGGER.info("发表照片飞鸽...");
			//
			//TextDailyPost.savePhoto(cookieVal, photoIds, data.getTitle(),data.getDesc(),data.getTag(),params);
			try {
				if (photoIds==null||photoIds.size()<1) {
					LOGGER.info("抓取照片数据为0...跳过");
				}else {
					DoveboxClientUtils.photoDoveByIds(photoIds, null, data.getDesc(), 40, data.getTitle(), data.getTag(), new BasicParams(), new AuthorizationData(spiderconfig.getLongNO(),spiderconfig.getmPassword(), DoveboxClientUtils.client_version));
				}
			} catch (L99IllegalOperateException e) {
				e.printStackTrace();
			}
		}
		
		LOGGER.info("任务完全...");
		
	}

	
	
	private static Map<String, Integer> doneMap = new HashMap<String, Integer>();
	private static boolean isDoit(String str){
		
		boolean flag = false;
		String path = getConfig("postPath");
		if (doneMap.size()<1) {
			String buff= FileUtils.readFile(path);
			if (buff==null) {
				flag = false;
			}else {
				String[] temp = buff.split("\n");
				if (temp!=null&&temp.length>0) {
					for (String name : temp) {
						doneMap.put(name,1);
					}
				}
			}
		}
		flag = (doneMap.get(str)!=null);
		if (!flag) {
			FileUtils.writeFile(path, str);
			doneMap.put(str,1);
		}
		
		return flag;
	}
	
}
