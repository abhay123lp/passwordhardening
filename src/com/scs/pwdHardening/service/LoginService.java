package com.scs.pwdHardening.service;

import java.util.Map;

import com.scs.pwdHardening.model.Question;
import com.scs.pwdHardening.model.ResponseType;

public class LoginService {
	
	private static final String SKIP_QUESTION = "SKIP_QUESTION";
	
	public boolean evaluateResponse(Map<Question, ResponseType> userResponse, Question question, int userResponseIndex){
		if(question.choices[userResponseIndex].equalsIgnoreCase(SKIP_QUESTION)){
			userResponse.put(question, ResponseType.SKIPPED);
			return true;
		}
		else{
			ResponseType rType = question.getCorrectResponseIndex() == userResponseIndex ? ResponseType.CORRECT : ResponseType.INCORRECT;
			userResponse.put(question, rType);
			return false;
		}
	}
}
