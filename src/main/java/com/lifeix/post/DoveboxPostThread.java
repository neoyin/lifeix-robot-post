package com.lifeix.post;

import com.l99.exception.service.L99IllegalDataException;
import com.lifeix.client.AuthorizationData;
import com.lifeix.client.BasicParams;
import com.lifeix.utils.RandomUtils;

public class DoveboxPostThread implements Runnable{

	private DoveboxFormData data;
	
	private AuthorizationData authData;
	
	
	
	
	public DoveboxPostThread(DoveboxFormData data, AuthorizationData authData) {
		this.data = data;
		this.authData = authData;
	}


	@Override
	public void run() {
		try {
			try {
				Thread.sleep(RandomUtils.getRandomNum(1500));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			DoveboxPost.postDovbox(data, authData, new BasicParams());
		} catch (L99IllegalDataException e) {
			e.printStackTrace();
		}
	}

}
