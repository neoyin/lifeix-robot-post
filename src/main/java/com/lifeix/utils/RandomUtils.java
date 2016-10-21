package com.lifeix.utils;

import java.util.Random;

public class RandomUtils {

	
	private static Random random = new Random();
	
	public static int getRandomNum(int sec){
		
		if (sec<1) {
			sec= 30;
		}
		
		return sec+random.nextInt(2000);
	}
}
