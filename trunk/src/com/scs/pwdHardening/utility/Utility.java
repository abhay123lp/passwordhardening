package com.scs.pwdHardening.utility;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class Utility {
	
	private static Random aRandom =  new Random();
	
	public static int getRandomInteger(){
		return 1 + aRandom.nextInt(3); // nextInt returns integer in range (0,arg)
	}
	
	public static int getRandomInteger(int lowerBound, int upperBound){
		return lowerBound + aRandom.nextInt(upperBound - lowerBound);
	}
	
	public static byte[] calculateHMAC(String key, String plainText){
		Mac mac = null;
		SecretKeySpec scKeySpec = null;
		try{
			scKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "HmacMD5");
			mac = Mac.getInstance("HmacMD5");
			mac.init(scKeySpec);
		}
		catch(NoSuchAlgorithmException nse){
			nse.printStackTrace();
		} catch (InvalidKeyException ike) {
			ike.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return mac.doFinal(plainText.getBytes());
	}
	
	public static BigInteger calculateHMAC(String key, int pText){
		return new BigInteger(calculateHMAC(key, String.valueOf(pText)));
	}
}
