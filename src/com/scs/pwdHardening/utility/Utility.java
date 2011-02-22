package com.scs.pwdHardening.utility;

import java.util.Random;

public final class Utility {
	
	private static Random aRandom =  new Random();
	
	public static int getRandomInteger(){
		return 1 + aRandom.nextInt(3); // nextInt returns integer in range (0,arg)
	}
}
