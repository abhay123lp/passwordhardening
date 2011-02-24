package com.scs.pwdHardening.service;

import java.math.BigInteger;
import java.util.Map;

import com.scs.pwdHardening.model.Category;
import com.scs.pwdHardening.model.Question;
import com.scs.pwdHardening.model.ResponseType;
import com.scs.pwdHardening.model.User;
import com.scs.pwdHardening.utility.Utility;

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
	
	public void initializeUser(User user){
		user.initializeInstructionTable();
		user.setHistoryFile(Utility.getHistoryFileFromUserName(user.getUserName()));
	}
	
	public void verifyUser(User user, Map<Question, ResponseType> userResponse){
		BigInteger [][] coordinates = new BigInteger[userResponse.size()][2];
		// First obtain the (x,y) values
		for(Question question : userResponse.keySet()){
			// Every category has an index. We need to maintain the order in instruction table
			int iTableIndex = question.getCategory().index;
			int x = 2 * (iTableIndex + 1);
			coordinates[iTableIndex][0] = BigInteger.valueOf(x);
			if(userResponse.get(question).equals(ResponseType.CORRECT)){
				// For correct get the alpha value from instruction table
				coordinates[iTableIndex][1] = (user.getInstructionTable()[iTableIndex][0]).subtract(Utility.calculateHMAC(user.getPassword(), x).mod(user.q));
			}
			else{
				// For incorrect and skipped get beta value from instruction table
				coordinates[iTableIndex][1] = (user.getInstructionTable()[iTableIndex][1]).subtract(Utility.calculateHMAC(user.getPassword(), x).mod(user.q));
			}
		}
		BigInteger hpwd = computeHPWDFromCoordinates(coordinates, user.q);
	}
	
	private BigInteger computeHPWDFromCoordinates(BigInteger[][] coordinates, BigInteger q){
		BigInteger hpwd = BigInteger.ZERO;
		for(int i = 0; i < Category.values().length; i++){
			BigInteger lambda = BigInteger.ONE;
			for(int j = 0; j < Category.values().length; j++){
				lambda = lambda.multiply(coordinates[j][0].divide(coordinates[j][0].subtract(coordinates[i][0])));
			}
			hpwd.add(coordinates[i][1].multiply(lambda));
		}
		return hpwd.mod(q);
	}
	
}
