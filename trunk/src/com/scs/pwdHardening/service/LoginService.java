package com.scs.pwdHardening.service;

import java.util.Map;

import com.scs.pwdHardening.model.Question;
import com.scs.pwdHardening.model.ResponseType;
import com.scs.pwdHardening.model.User;

public class LoginService {
	
	private static final String SKIP_QUESTION = "SKIP_QUESTION";
	
	public boolean evaluateResponse(Map<Question, ResponseType> userResponse, Question question, int userResponseIndex){
		if(question.choices[userResponseIndex].equalsIgnoreCase(SKIP_QUESTION)){
			userResponse.put(question, ResponseType.SKIPPED);
			return true;
		}
		else {
			ResponseType rType = question.getCorrectResponseIndex() == userResponseIndex ? ResponseType.CORRECT : ResponseType.INCORRECT;
			userResponse.put(question, rType);
			return false;
		}
	}
	
	public void createInstructionTable(String userName, String password, Map<Question, ResponseType> userResponse){
		
	}
	
	public void initializeUser(User user, int nDistinguishingFeatures){
		user.initializeInstructionTable(nDistinguishingFeatures);
	}
	
}
