package com.lifeix.spider;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lifeix.post.RobotUtils;
import com.lifeix.utils.FileUtils;


public class SpiderUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpiderUtils.class);
	
	public static Proxy getRandomProxy(){
		String[]t = getRandomProxyStr();
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(t[0],Integer.valueOf(t[1])));
		return proxy;
	}
	
	public static ProxyHost getRandomProxyHost(){
		
		String[]t = getRandomProxyStr();
		ProxyHost proxyHost = new ProxyHost(t[0],Integer.valueOf(t[1]));  
		return proxyHost;
	}
	
	private static Map<String,Integer> proxyMap = new HashMap<String, Integer>();
	
	private static String proxy_path = "";
	
	
	public static String getProxy_path() {
		return proxy_path;
	}

	public static void setProxy_path(String proxy_path) {
		SpiderUtils.proxy_path = proxy_path;
	}

	private static String[] getRandomProxyStr(){
		
		if (proxyMap.size()<1) {
			String path = proxy_path;
			LOGGER.info("proxy_path is :"+path);
			List<String> proxyList = FileUtils.paserFileToStrArr(path);
			for (String proxyStr : proxyList) {
				proxyMap.put(proxyStr,1);
			}
		}
		int i= new Random().nextInt(proxyMap.size());
		Object[] temps = proxyMap.keySet().toArray();
		
		String temp =temps[i].toString().replace("@HTTP", "");
		String[]t = temp.split(":");
		
		return t;
	}
	
	
	/*public static void main(String[] args) {
		String result = parseResponseStr("http://pic.yesky.com/450/33849450.shtml", "gb2312", false);
		
	}*/
	
	private static String USER_AGENT[] ={"Mozilla/5.0 (Windows; U; Windows NT 5.2; en-US) AppleWebKit/532.9 (KHTML, like Gecko) Chrome/5.0.310.0 Safari/532.9",
		"Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803 Fedora/3.5.2-2.fc11 Firefox/3.5.2",
		//"Mozilla/5.0 (iPhone Simulator; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7",
		"Mozilla/5.0 (X11; U; Linux x86_64; en-US) AppleWebKit/540.0 (KHTML, like Gecko) Ubuntu/10.10 Chrome/8.1.0.0 Safari/540.0"};
	
	/**
	 * URL 得到 文本信息
	 * @param url
	 * @return
	 */
	public static String parseResponseStr(String url,String charactor,boolean proxyFlag){
		if (charactor==null) {
			charactor = "UTF-8";
		}
		//String[]t = getRandomProxyStr();
		
		HttpClient client =  new HttpClient();
		ProxyHost proxyHost =null;
		if (proxyFlag) {
			proxyHost =  getRandomProxyHost();
			client.getHostConfiguration().setProxyHost(proxyHost);
		}
		
		client.getParams().setParameter(  
			      HttpMethodParams.HTTP_CONTENT_CHARSET, charactor);  
		
		client.getParams().setParameter(HttpMethodParams.USER_AGENT,USER_AGENT[new Random().nextInt(USER_AGENT.length-1)]);
		
		//client.getHostConfiguration().setProxy(t[0], Integer.valueOf(t[1])); 
		client.getHttpConnectionManager().getParams().setConnectionTimeout(60*1000);
		GetMethod method = new GetMethod(url);
		method.addRequestHeader("Referer", url);
		try {
			client.executeMethod(method);
			
			if (method.getStatusCode()==200) {
				String response = method.getResponseBodyAsString();
				return response;
			}else {
				LOGGER.info(" error status code is "+method.getStatusCode());
				//parseResponseStr(url);
				return null;
			}
		} catch (HttpException e) {
			//e.printStackTrace();
			if (proxyHost!=null) {
				LOGGER.info(" access url:"+url+" error　proxy http :"+proxyHost.getHostName()+"-->"+e.getMessage());
				String key =proxyHost.getHostName()+":"+proxyHost.getPort()+"@HTTP";
				proxyMap.remove(key);
				parseResponseStr(url,charactor,true);
			}
		} catch (IOException e) {
			if (proxyHost!=null) {
				LOGGER.info(" access url:"+url+" error  proxy http :"+proxyHost.getHostName()+"-->"+e.getMessage());
				String key =proxyHost.getHostName()+":"+proxyHost.getPort()+"@HTTP";
				proxyMap.remove(key);
				parseResponseStr(url,charactor,true);
			}
		}
		return null;
	}
	
	/**
	 * 将link 写入文件
	 * @param link
	 */
	public static void writeLinks(String link,String url){
		String path = RobotUtils.getValueByKey("post_dir");
		String fileName = path+"temp_"+url;
		FileUtils.writeFile(fileName,link);
	}
	
	
	
	
	
}
