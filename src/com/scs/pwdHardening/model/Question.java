package com.scs.pwdHardening.model;

public class Question {
	
	String question;
	public String [] choices =  new String[4];
	
	public Question(String question){
		this.question = question;
	}
}
