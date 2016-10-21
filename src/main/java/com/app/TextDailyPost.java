package com.app;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TextDailyPost {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(TextDailyPost.class);
	
	
	/**
	 * @param username
	 * @param password
	 * @throws IOException
	 */
	protected static String loginAndGetCookie(String username,String password) throws IOException{
		String cookieVal = "";
		URL url = new URL("http://www.l99.com/EditAccount_login.action");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();  
		/** 
		 * 然后把连接设为输出模式。URLConnection通常作为输入来使用，比如下载一个Web页。 
		 * 通过把URLConnection设为输出，你可以把数据向你个Web页传送。下面是如何做： 
		 */  
		connection.setDoOutput(true);  
		
		/** 
		 * 最后，为了得到OutputStream，简单起见，把它约束在Writer并且放入POST信息中，例如： ... 
		 */  
		OutputStreamWriter out = new OutputStreamWriter(connection  
		        .getOutputStream(), "UTF-8");  
		              //其中的memberName和password也是阅读html代码得知的，即为表单中对应的参数名称  
		out.write("e="+username+"&m="+password+"&a=true"); // post的关键所在！  
		// remember to clean up  
		out.flush();  
		out.close();  
		List<String> tempCookie = null;
		Map<String, List<String>> temp = connection.getHeaderFields();
		for (String t:temp.keySet()) {
			if (t!=null&&t.equals("Set-Cookie")) {
				tempCookie =temp.get(t);
			}
		}
		if (tempCookie!=null) {
			cookieVal =  tempCookie.toString();
		}
		return cookieVal;
	}
	
	/**
	 * 发表文章
	 * @param cookieVal
	 * @param href
	 * @throws IOException
	 */
	public static int saveText(String cookieVal,String title,String desc,String tags,String params) throws IOException{
		
		String draft = DailyRobotTask.spiderconfig.getDraft();
		URL url = null;
		if (draft!=null) {
			url= new URL("http://www.l99.com/timeline_saveDraft.action");  
		}else {
			url= new URL("http://www.l99.com/timeline_saveText.action");  
		}
		
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
	    if (cookieVal != null) {  
	        //发送cookie信息上去，以表明自己的身份，否则会被认为没有权限  
	        conn.setRequestProperty("Cookie", cookieVal);  
	    }  
	    
	    conn.setDoOutput(true);
	    conn.setDoInput(true);
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.84 Safari/537.22");
	    //String catId = "text.categoryId="+8189;
	    String mediaFlag = "mediaFlag=true";
	    String titleField ="text.textTitle="+URLEncoder.encode(title, "utf-8");
	    String descField = "text.textContent="+URLEncoder.encode(desc, "utf-8");
	    if (draft!=null) {
	    	descField = "text.categoryId="+draft+"&text.textAbstract="+URLEncoder.encode(desc, "utf-8");
		}else {
			descField = "text.textContent="+URLEncoder.encode(desc, "utf-8");
		}
		
	    
	    conn.connect();  
	    
	    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");  
		              //其中的memberName和password也是阅读html代码得知的，即为表单中对应的参数名称  
		
        // 正文，正文内容其实跟get的URL中'?'后的参数字符串一致
        String content = mediaFlag+"&"+titleField+parseTags(tags);
        if (params!=null&&params.length()>0) {
			content +="&"+params;
        	//content+="&targetAccountIds=304705&dashboardAddress=中国上海&lat=31.230393&lng=121.473704&typeIds=10";
        }
        
        content+="&"+descField;
        // DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写道流里面
        out.write(content); 

        out.flush();
        out.close(); 
	    
	    
        int responseCode = conn.getResponseCode();
        LOGGER.info("Status Msg :"+conn.getResponseMessage()+" Status Code :"+conn.getResponseCode());
	    
	   /* InputStream urlStream = conn.getInputStream();  
	    BufferedReader bufferedReader = new BufferedReader(  
	            new InputStreamReader(urlStream));  
	    String ss = null;  
	    String total = "";  
	    while ((ss = bufferedReader.readLine()) != null) {  
	        total += ss+"\n";  
	    } 
	    System.out.println(total);*/
	    
	    
	    return responseCode;
	}
	
	/**
	 * 发表文章
	 * @param cookieVal
	 * @param href
	 * @throws IOException
	 */
	public static int savePhoto(String cookieVal,List<Long> photoIds,String title,String desc,String tags,String params) throws IOException{
	    if (photoIds==null||photoIds.size()<1) {
	    	LOGGER.info("无照片数据");
			return 0;
		}
	    String photoStr ="";
	    for (Long pId : photoIds) {
			photoStr+="i="+pId+"&spinData=0&";
		}
		
		URL url = new URL("http://www.l99.com/timeline_savePhoto.action");  
	    HttpURLConnection conn = (HttpURLConnection) url  
	            .openConnection();  
	    if (cookieVal != null) {  
	                          //发送cookie信息上去，以表明自己的身份，否则会被认为没有权限  
	    	LOGGER.info("cookie info "+cookieVal);
	        conn.setRequestProperty("Cookie", cookieVal);  
	    }  
	    
	    conn.setDoOutput(true);
	    conn.setDoInput(true);
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.84 Safari/537.22");
	    //String catId = "text.categoryId="+8189;
	    String mediaFlag = "mediaFlag=true";
	    String titleField ="title="+URLEncoder.encode(title, "utf-8");
	    String descField = "context="+URLEncoder.encode(desc, "utf-8");
	    
	    conn.connect();  
	    
	    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");  
		              //其中的memberName和password也是阅读html代码得知的，即为表单中对应的参数名称  
		
        // 正文，正文内容其实跟get的URL中'?'后的参数字符串一致
        String content = photoStr+mediaFlag+"&"+titleField+parseTags(tags);
        if (params!=null&&params.length()>0) {
			content +="&"+params;
        	//content+="&targetAccountIds=304705&dashboardAddress=中国上海&lat=31.230393&lng=121.473704&typeIds=10";
        }
        
        content+="&"+descField;
        // DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写道流里面
        out.write(content); 

        out.flush();
        out.close(); 
	    
	    
        int responseCode = conn.getResponseCode();
        LOGGER.info("Status Msg :"+conn.getResponseMessage()+" Status Code :"+conn.getResponseCode());
	    
	   /* InputStream urlStream = conn.getInputStream();  
	    BufferedReader bufferedReader = new BufferedReader(  
	            new InputStreamReader(urlStream));  
	    String ss = null;  
	    String total = "";  
	    while ((ss = bufferedReader.readLine()) != null) {  
	        total += ss+"\n";  
	    } 
	    System.out.println(total);*/
	    
	    
	    return responseCode;
	}
	
	
	
	
	private static String parseTags(String tags){
		
		String temp ="";
		if (tags==null||tags.length()<1) {
			return temp;
		}
		String tempArr[] = tags.split(",");
		if (tempArr==null||tempArr.length<1) {
			return temp;
		}
		try {
			for (String s : tempArr) {
					temp+="&dashboardTags="+URLEncoder.encode(s, "utf-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return temp;
	}
	
	
	
	
	
	public static void main(String[] args) throws IOException {
		String cookieVal = loginAndGetCookie("306364","000000");
		
	}
	
}
