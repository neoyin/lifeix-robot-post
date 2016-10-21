package com.app;


import static com.app.DailyRobotTask.spiderconfig;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
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


public class DailySpider {
	//http://app.tongbu.com/article/44907.html
	
	
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DailySpider.class);
	
	/**
	 * 得到飞鸽数据 desc title tag 
	 * @param url
	 * @param charType
	 */
	public static DoveboxFormData getDoveboxDataFromUrl(String url,String charset,boolean proxyflag){
		
	
		LOGGER.info(" start parse url :"+url);
		String image ="";
		String desc ="";String title ="";String tags ="";
		String pageUrl =url;
		String tempURL =pageUrl;
		
		String response = SpiderUtils.parseResponseStr(tempURL,charset,proxyflag);
		Document document = Jsoup.parse(response);
		Elements elements = document.select("meta");
		
		for (Element element : elements) {
			String name = element.attr("name");
			String content = element.attr("content");
			
			if (name.endsWith("keywords")) {
				tags = content;
				tags = tags.replaceAll(" ", ",");
				
			}
			if (name.endsWith("description")) {
				desc = content;
			}
		}
		
		if (spiderconfig.getDescOffset()!=null&&spiderconfig.getDescOffset().length()>0) {
			Elements descEle = document.select(spiderconfig.getDescOffset());
			if (descEle!=null) {
				desc =descEle.html();
			}
			
		}
		title = document.title();
		if (spiderconfig.getTitleOffset()!=null&&spiderconfig.getTitleOffset().length()>0) {
			Element titleEle = document.select(spiderconfig.getTitleOffset()).first();
			if (titleEle!=null) {
				title =  titleEle.text();
			}
		}
		
		if (spiderconfig.getTagsOffset()!=null&&spiderconfig.getTagsOffset().length()>0) {
			Elements tagsEle = document.select(spiderconfig.getTagsOffset());
			if (tagsEle!=null&&tagsEle.size()>0) {
				tags ="";
				for (int i = 0; i < tagsEle.size(); i++) {
					Element  element =  tagsEle.get(i);
					String t = element.text().replaceAll("&", ",");
					tags +=t+",";
				}	
			}
		}

		DoveboxFormData data = new DoveboxFormData(title, desc, null, tags);
		data.setHtml(response);
		return data;
	}

	public static List<Long> getPhotoIds(String html,String name,String mPassword){
		
		Document document = Jsoup.parse(html);
		Elements imgEles = document.select(spiderconfig.getPhotoOffset());
		if (imgEles==null||imgEles.size()<1) {
			return null;
		}
		//Map<String,String> imgMap = new HashMap<String, String>();
		List<Long> photoIds = new ArrayList<Long>();
		for (int i = 0; i < imgEles.size(); i++) {
			Element imgEle = imgEles.get(i);
			String src="";
			if (spiderconfig.getPhotoSrc()==null||spiderconfig.getPhotoSrc().length()<1) {
				src= imgEle.attr("src");
			}else {
				src=imgEle.attr(spiderconfig.getPhotoSrc());
			}
			 
			String temp = spiderconfig.getSpiderUrl().replace("http://", "");
			temp = temp.substring(0,temp.indexOf("/")+1) ;
			
			if (!src.startsWith("http://")&&!src.startsWith(temp)) {
				/*String temp = spiderconfig.getSpiderUrl().replace("http://", "");
				temp = temp.substring(0,temp.indexOf("/")+1) ;*/
				String tempSrc= src.replace("http://","");
				tempSrc = tempSrc.replace(temp, "");
				
				src ="http://"+temp+tempSrc;
			}
			
			//上２
			try {
				
				String pic_path =  FileUtils.downLoadPic(spiderconfig.getImagePath(), src,spiderconfig.getSpiderUrl(),false);
				long pId = DoveboxClientUtils.uploadAndGetId(new File(pic_path), "", new BasicParams(), new AuthorizationData(name, mPassword, DoveboxClientUtils.client_version));
				//String online_path = DoveboxClientUtils.uploadAndGetPath(new File(pic_path), "", new BasicParams(), new AuthorizationData(name, mPassword, DoveboxClientUtils.client_version));
				//online_path = "http://photo.l99.com/bigger/"+online_path;
				//imgMap.put(initialSrc, online_path);
				photoIds.add(pId);
			} catch (L99IllegalOperateException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
			//imgMap.put(src, src);
		}
		return photoIds;
	}
	
	
	/**
	 * 替换内容中的图片
	 * @param desc
	 * @return
	 */
	public static String replceImgs(String desc,String name,String mPassword){
		
		if (spiderconfig.getImageOffset()==null||spiderconfig.getImageOffset().length()<1) {
			return desc;
		}
		Document document = Jsoup.parse(desc);
		if (spiderconfig.getFilterOffset()!=null&&spiderconfig.getFilterOffset().length()>0) {
			String temps[] = spiderconfig.getFilterOffset().split(",");
			if (temps!=null&&temps.length>0) {
				for (String t : temps) {
					Elements filtersEle = document.select(t);
					if (filtersEle!=null&&filtersEle.size()>0) {
						filtersEle.remove();
						/*for (Element element : filtersEle) {
							String filterStr =  element.html();
							desc = desc.replace(filterStr, "");
						}*/
					}
				}
			}
			desc = document.html();
			document = Jsoup.parse(desc);
		}
		
		Elements imgEles = document.select(spiderconfig.getImageOffset());
		if (imgEles==null||imgEles.size()<1) {
			return desc;
		}
		Map<String,String> imgMap = new HashMap<String, String>();
		for (int i = 0; i < imgEles.size(); i++) {
			Element imgEle = imgEles.get(i);
			String src="";
			if (spiderconfig.getPhotoSrc()==null||spiderconfig.getPhotoSrc().length()<1) {
				src= imgEle.attr("src");
			}else {
				src=imgEle.attr(spiderconfig.getPhotoSrc());
			}
			String initialSrc = src;
			String temp = spiderconfig.getSpiderUrl().replace("http://", "");
			temp = temp.substring(0,temp.indexOf("/")+1) ;
			
			if (!src.startsWith("http://")&&!src.startsWith(temp)) {
				/*String temp = spiderconfig.getSpiderUrl().replace("http://", "");
				temp = temp.substring(0,temp.indexOf("/")+1) ;*/
				String tempSrc= src.replace("http://","");
				tempSrc = tempSrc.replace(temp, "");
				
				src ="http://"+temp+tempSrc;
			}
			if (src.equals("http://"+temp)) {
				continue;
			}
			//上２
			try {
				
				String pic_path =  FileUtils.downLoadPic(spiderconfig.getImagePath(), src,spiderconfig.getSpiderUrl(),false);
				
				String online_path = DoveboxClientUtils.uploadAndGetPath(new File(pic_path), "", new BasicParams(), new AuthorizationData(name, mPassword, DoveboxClientUtils.client_version));
				online_path = "http://photo.l99.com/bigger/"+online_path;
				imgMap.put(initialSrc, online_path);
			} catch (L99IllegalOperateException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
			//imgMap.put(src, src);
		}
		for(String key:imgMap.keySet()){
			desc = desc.replaceAll(key, imgMap.get(key));
		}
		return desc;
	}
	
	
	
	/*public static void main(String[] args) {
		//getDoveboxDataFromUrl("http://app.tongbu.com/article/44907.html", "UTF-8");
		String temp = "www.techcn.com.cn/index.php?doc-view-140739.html".replace("http://", "");
		temp = temp.substring(0,temp.indexOf("/")+1) ;
		String src ="www.techcn.com.cn/uploads/201001/1263038110E6w8mWVv.jpg";
		if (!src.startsWith("http://")&&!src.startsWith(temp)) {
			String temp = spiderconfig.getSpiderUrl().replace("http://", "");
			temp = temp.substring(0,temp.indexOf("/")+1) ;
			String tempSrc= src.replace("http://","");
			tempSrc = tempSrc.replace(temp, "");
			
			src ="http://"+temp+tempSrc;
			
		}
		System.err.println(src);
		String pic_path =  FileUtils.downLoadPic(spiderconfig.getImagePath(), src,false);
	}*/
	
	public static void main(String[] args) throws URISyntaxException {
		/*String ts ="http://www.pixabay.com//static/uploads/photo/2013/12/15/15/18/lighthouse-228911_640.jpg";
		URI uri = new URI(ts);
		System.out.println(uri.getQuery());
		String f = ts.replaceAll("\\?"+uri.getQuery(), "");
		System.out.println(f);*/
		String temp ="/test";
		System.out.println(temp.substring(1));
		
		
	}
	
}
