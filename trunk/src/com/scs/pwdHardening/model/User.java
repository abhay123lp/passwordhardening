package com.scs.pwdHardening.model;

import java.math.BigInteger;
import java.util.Random;

import com.scs.pwdHardening.utility.Utility;

public class User {
	String userName;
	String password;
	private Integer [] polynomialCoefficients;
	private BigInteger[][] instructionTable;
	private BigInteger q = BigInteger.probablePrime(160, new Random());
	
	public User(String userName, String password, int nDistinguishingFeatures){
		this.userName = userName;
		this.password = password;
		this.polynomialCoefficients = new Integer [nDistinguishingFeatures];
		this.instructionTable = new BigInteger[nDistinguishingFeatures][2];
	}
	
	public void initializeInstructionTable(int nDistinguishingFeatures){
		generatePolynomialFunction();
		for(int i = 0; i < nDistinguishingFeatures; i++){
			int x = 2*(i+1);
			instructionTable[i][0] = BigInteger.valueOf(f(x)).add(Utility.calculateHMAC(password, x).mod(q));
			instructionTable[i][1] = BigInteger.valueOf(f(x+1)).add(Utility.calculateHMAC(password, x+1).mod(q));
		}
	}
	
	private void generatePolynomialFunction(){
		// Polynomial function is of the form y = a[0] + a[1]x + a[2]x^2 + ... a[m-1]x^(m-1)
		for(int i = 0; i < polynomialCoefficients.length; ++i){
			polynomialCoefficients[i] = Utility.getRandomInteger(0, 10);
		}
	}
	
	private long f(int x){
		long y = 0;
		for(int i = 0; i < polynomialCoefficients.length; ++i){
			y += polynomialCoefficients[i]*Math.pow(x, i); 
		}
		return y;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof User){
			User user = (User)o;
			return this.userName.equalsIgnoreCase(user.userName);
		}
		return false;	
	}
	
	@Override
	public int hashCode(){
		return this.userName.hashCode();
	}
}
