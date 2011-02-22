package com.scs.pwdHardening.model;

public class Question {
	
	String question;
	public String [] choices =  new String[4];
	private int correctResponseIndex;
	
	public Question(String question){
		this.question = question;
	}

	public String getQuestion() {
		return question;
	}

	public String[] getChoices() {
		return choices;
	}
	
	public int getCorrectResponseIndex(){
		return this.correctResponseIndex;
	}
	
	public void setCorrectResponseIndex(int cIndex){
		this.correctResponseIndex = cIndex;
	}
	
	@Override
	public int hashCode(){
		return question.hashCode();
	}
	
}
