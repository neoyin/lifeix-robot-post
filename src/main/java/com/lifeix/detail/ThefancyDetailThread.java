package com.lifeix.detail;

import com.lifeix.utils.RandomUtils;

public class ThefancyDetailThread implements Runnable{

	private String url ;
	
	public ThefancyDetailThread(String url){
		this.url = url;
	}
	
	@Override
	public void run() {
		
		
		try {
			Thread.sleep(RandomUtils.getRandomNum(0));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ThefancyDetailProcess.getDoveboxDataFromUrl(url);
	}
}
