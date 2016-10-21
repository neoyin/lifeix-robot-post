package com.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleGeoUtil {

	/**
	 *  G_GEO_SUCCESS (200) 	未出现错误，已对地址成功地进行了语法分析，并返回其地址解析。（自 2.55 开始）
		G_GEO_BAD_REQUEST (400) 	无法成功解析行车路线请求。（自 2.81 开始）
		G_GEO_SERVER_ERROR (500) 	无法成功处理地址解析或行车路线请求，但是确切的失败原因未知。（自 2.55 开始）
		G_GEO_MISSING_QUERY (601) 	HTTP q 参数缺失或没有值。对于地址解析请求，这意味着将空地址指定为输入。对于行车路线请求，这意味着在输入中未指定查询。（自 2.81 开始）
		G_GEO_MISSING_ADDRESS (601) 	G_GEO_MISSING_QUERY 的同义名。（自 2.55 开始）
		G_GEO_UNKNOWN_ADDRESS (602) 	找不到指定地址的对应地理位置。这可能是地址比较新，或地址不正确。（自 2.55 开始）
		G_GEO_UNAVAILABLE_ADDRESS (603) 	由于合法性或合同原因，无法返回给定地址的地址解析或给定驾车路线查询的路线。（自 2.55 开始）
		G_GEO_UNKNOWN_DIRECTIONS (604) 	Gdirections 对象无法计算查询中提到的两点之间的行车路线。这通常是因为两点之间无可用路线，或我们在该地区没有路线数据。（自 2.81 开始）
		G_GEO_BAD_KEY (610) 	给定的密钥无效或与给定的域不匹配。（自 2.55 开始）
		G_GEO_TOO_MANY_QUERIES (620) 	给定的密钥超出了 24 小时的请求限制。（自 2.55 开始）
	 */
	
	
	private static final String GOOGLE_KEY ="key=ABQIAAAA_tUj5nUNECV21iHXIPY-KBQn8fFaaC-v8-l6GHMlowt97nhgPxRnTysNKTuL1miviYZRRf4h5mA7aA";
	private static final String OUTPUT_CVS ="output=csv";
	private static final String GOOGLE_URL ="http://ditu.google.cn/maps/geo?q=";
	private static final String OUTPUT_JSON ="output=json";
	private static final String OUTPUT_XML ="output=xml";
	
	private static final Logger LOG = LoggerFactory.getLogger(GoogleGeoUtil.class); 
	/**
	 * 得到经纬度
	 * @param localName
	 * @return 数组 【0】lat 【1】 lng
	 * @throws JSONException 
	 * @throws UnsupportedEncodingException 
	 */
	public static JSONObject getLatLng(String localName) throws JSONException, UnsupportedEncodingException{
		String msg = "通过google 没有查找到经纬度";
		String gGeoStr=getLocalByGoogle(localName,OUTPUT_JSON);
		if(gGeoStr==null||gGeoStr.equals("")){
			LOG.info(msg);
			return null;
		}
		JSONObject geoJsonObject = new JSONObject(gGeoStr);
		if (geoJsonObject.has("Status")) {
			JSONObject statusJson = geoJsonObject.getJSONObject("Status"); 
			if (!statusJson.has("code")||statusJson.getInt("code")!=200) {
				LOG.info(msg);
				return null;
			}
		}else {
			LOG.info(msg);
			return null;
		}
		JSONObject placeJson = geoJsonObject.getJSONArray("Placemark").getJSONObject(0);
		JSONObject pointJson = placeJson.getJSONObject("Point");
		JSONArray latLngArrJson= pointJson.getJSONArray("coordinates"); 
		String address = placeJson.getString("address").replaceAll(" 邮政编码: [0-9]+","");
		JSONObject object = new JSONObject();
		object.put("lng", latLngArrJson.getDouble(0));
		object.put("lat", latLngArrJson.getDouble(1));
		object.put("dashboardAddress",address);
		object.put("addressParams","dashboardAddress="+URLEncoder.encode(address,"UTF-8")+"&lat="+latLngArrJson.getDouble(1)+"&lng="+latLngArrJson.getDouble(0));
		LOG.info("解析经纬度地址为："+object.toString());
		return object;
	}
	/**
	 * geocode 
	 * @param localName
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws JSONException
	 */
	public static JSONObject getGoogleLatLng(String localName) throws UnsupportedEncodingException, JSONException{
		String msg = "通过google 没有查找到经纬度";
		String queryStr =URLEncoder.encode(localName, "utf8");
		String qUrl="http://maps.google.com/maps/api/geocode/json?address="+queryStr+"&language=zh-CN&sensor=false";
		String gGeoStr= requestUrl(qUrl);
		if(gGeoStr==null||gGeoStr.equals("")){
			LOG.info(msg);
			return null;
		}
		JSONObject gObject = new JSONObject(gGeoStr);
		if (gObject.has("status")&&gObject.getString("status").toLowerCase().equals("ok")) {
			JSONArray results= gObject.getJSONArray("results");
			if (results!=null&&results.length()>0) {
				JSONObject address = results.getJSONObject(0);
				JSONObject geometry= address.getJSONObject("geometry");
				JSONObject location = geometry.getJSONObject("location");
				JSONObject object = new JSONObject();
				object.put("lng", location.getDouble("lng"));
				object.put("lat", location.getDouble("lat"));
				object.put("dashboardAddress",localName);
				object.put("addressParams","dashboardAddress="+URLEncoder.encode(localName,"UTF-8")+"&lat="+location.getDouble("lat")+"&lng="+location.getDouble("lng"));
				LOG.info("解析经纬度地址为："+object.toString());
				return object;
			}
			return null;
		}else {
			LOG.info(msg);
			return null;
		}
	}
	
	
	
	/**
	 * 得到Google map 解析地址返回值 
	 * @param localName
	 * @return 
	 */
	public static String getLocalByGoogle(String localName,String outPut){
		String lanLng="";
		try {
			String queryStr =URLEncoder.encode(localName, "utf8");
			String qUrl =GOOGLE_URL+queryStr+"&"+GOOGLE_KEY+"&"+outPut;
			//System.out.println(qStr);
			URL httpUrl = new URL(qUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) httpUrl.openConnection();
			urlConnection.setConnectTimeout(500);
			urlConnection.connect();
			InputStream in = urlConnection.getInputStream();
		    BufferedReader breader = new BufferedReader(
		      new InputStreamReader(in, "utf8"));
		    String currentLine;
		    while ((currentLine = breader.readLine()) != null) {
		    	lanLng += currentLine;
		    }
			return lanLng;
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage(), e);
			return lanLng;
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return lanLng;
		}
	}
	
	private static String requestUrl(String qUrl) {
		String buffer = "";
		try {
			URL httpUrl = new URL(qUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) httpUrl
					.openConnection();
			urlConnection.setConnectTimeout(500);
			urlConnection.connect();
			InputStream in = urlConnection.getInputStream();
			BufferedReader breader = new BufferedReader(new InputStreamReader(
					in, "utf8"));
			String currentLine;
			while ((currentLine = breader.readLine()) != null) {
				buffer += currentLine;
			}
			return buffer;
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage(), e);
			return buffer;
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return buffer;
		}
	}
	
	/*public static void getFileEncoding(URL url) throws MalformedURLException, IOException {
		CodepageDetectorProxy codepageDetectorProxy = CodepageDetectorProxy.getInstance(); 
		codepageDetectorProxy.add(JChardetFacade.getInstance());
		codepageDetectorProxy.add(ASCIIDetector.getInstance()); 
		codepageDetectorProxy.add(UnicodeDetector.getInstance());
		codepageDetectorProxy.add(new ParsingDetector(false));
		codepageDetectorProxy.add(new ByteOrderMarkDetector());
		Charset charset = codepageDetectorProxy.detectCodepage(url);
		System.out.println(charset.name());    
	}*/
	

	public static void main(String[] args) throws ParseException, JSONException, MalformedURLException, IOException {
		
		/*String time = "2013-12-28"; // year month day
		java.util.Calendar date = java.util.Calendar.getInstance();
		date.setTime((new java.text.SimpleDateFormat("yyyy-MM-dd")).parse(time));
		System.err.println("Week of year = " + date.get(java.util.Calendar.WEEK_OF_YEAR));*/
		
		//getGoogleLatLng("美国波士顿");
		
		//getFileEncoding(new URL("http://www.dytt8.net/html/gndy/rihan/index.html"));
		//getFileEncoding(new URL("http://www.21cad.cn/news/1101.html"));
		
		
		
		
	}
	
}
