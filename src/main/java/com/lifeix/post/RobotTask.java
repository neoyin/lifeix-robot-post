package com.lifeix.post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lifeix.client.AuthorizationData;
import com.lifeix.detail.ThefancyDetailProcess;
import com.lifeix.spider.ThefancySpider;
import com.lifeix.user.UserUtils;
import com.lifeix.utils.FileUtils;
import com.lifeix.utils.ThreadPoolManage;


public class RobotTask {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(RobotTask.class);
	
	
	public static void main(String[] args) {
		if (args==null||args.length<1||args[0]==null||args[0].length()<1) {
			System.out.println("请输入要执行的任务...");
			return ;
		}
		
		RobotUtils.initConfig();
		
		String url =args[0];
	
		//spiderTask(url);
		startPostTask(url);
		//startPostTask(url);
		
		//startPostTask(url);
		
	}
	
	
	
	private static void spiderTask(String url){
		ThefancySpider.parserUrl("http://"+url);
		
	}
	
	private static void detailProcess(String url){
		ThefancyDetailProcess.dowithData(url);
	}
	
	
	private static void startPostTask(String url){
		
		List<AuthorizationData> userDatas = UserUtils.getUserByUrl(url);
		if (userDatas==null||userDatas.size()<1) {
			LOGGER.info("=== 没有相关负责账号 ===");
			
			return ;
		}
		String dir = RobotUtils.getValueByKey("post_dir");
		
		List<DoveboxFormData> datas= DoveboxPostUtils.getDataFromFile(dir+"/"+url);
		
		LOGGER.info(" 可发表文件数　："+datas.size());
		
		
		for (int i = 0; i < datas.size(); i++) {
			
			DoveboxFormData data  = datas.get(i);
			if (isDoit(url, data.getFileName())) {
				
				LOGGER.info(" this data has post "+i+"--> "+data);
				continue;
			}
				
				if (data.getTitle()!=null) {
					/*
					byte[] bytes;
					try {
						bytes = data.getTitle().getBytes("GB2312");
						data.setTitle(new String(bytes,"UTF-8"));
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}*/
					data.setTitle(data.getTitle().replace("Fancy - ", ""));
				}
				if (data.getDesc()==null||data.getDesc().length()<1) {
					data.setDesc(data.getTitle());
				}
				
				data.setTag(data.getTag());//"美女,"+
				
				AuthorizationData authData = userDatas.get(new Random().nextInt(userDatas.size()));
				LOGGER.info(" start post dovebox :"+i+"/"+datas.size()+"-->"+authData+data);
				//ThreadPoolManage.startTask(new DoveboxPostThread(data, authData));
				
				try {
					ThreadPoolManage.startTask(new DoveboxPostThread(data, authData));
					
					//DoveboxPost.postDovbox(data, authData, new BasicParams());
				
					try {
						Thread.sleep(25*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				writeOkFile(url, data.getFileName());
				
				LOGGER.info(" post dovebox post done index :"+i+"/"+datas.size());
				
		}
		ThreadPoolManage.endTask();
	}
	
	
	private static Map<String, Integer> doneMap = new HashMap<String, Integer>();
	
	private static boolean isDoit(String url, String str){
		if (doneMap.size()<1) {
			String path = RobotUtils.getValueByKey("post_dir");
			String fileName = path+url+"_done.txt";
			String buff= FileUtils.readFile(fileName);
			if (buff==null) {
				return false;
			}
			String[] temp = buff.split("\n");
			if (temp!=null&&temp.length>0) {
				for (String name : temp) {
					doneMap.put(name,1);
				}
			}
			
		}
		return (doneMap.get(str)!=null);
		
	}
	
	
	
	
	private static void writeOkFile(String url,String str){
		
		String path = RobotUtils.getValueByKey("post_dir");
		String fileName = path+url+"_done.txt";
		FileUtils.writeFile(fileName, str);
		
	}
	
	
}
