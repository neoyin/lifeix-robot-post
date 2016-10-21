package com.lifeix.utils;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManage {

	final static private int poolsize =10;
	
	//private static ExecutorService pool = Executors.newFixedThreadPool(poolsize);
	private static ExecutorService pool = new ThreadPoolExecutor(poolsize, poolsize * 3,5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
	
	public static void startTask(Runnable test){
		pool.execute(test);
	}
	
	
	public static void endTask(){
		if (pool.isShutdown()) {
			pool.isShutdown();	
		}
	}
	
}
