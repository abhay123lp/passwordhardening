package com.scs.pwdHardening.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.Scanner;

import com.scs.pwdHardening.History;
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
	
	public void initializeUser(User user){
		user.initializeInstructionTable();
		user.setHistoryFile(Utility.getHistoryFileFromUserName(user.getUserName()));
	}
	
	public boolean verifyUser(User user, Map<Question, ResponseType> userResponse){
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
		hpwd = BigInteger.valueOf(user.getPolynomialCoefficients()[0]);
		
		if(verifyHistoryFileContents(user, hpwd)){
			updateInstructionTable(user, userResponse);
			return true;
		}
		else{
			return false;
		}
	}
	
	public void updateInstructionTable(User user, Map<Question, ResponseType> userResponse){
		Scanner decContentsScanner = new Scanner(user.decFileContents);
		int [] categoryResultsCount = new int[Category.values().length]; 
		int totalFeatureVectorCount = 0;
		// Get previous history results
		while(decContentsScanner.hasNextLine()){
			String line = decContentsScanner.nextLine();
			if(!line.contains("Decryption")){
				++totalFeatureVectorCount;
				// This line must be feature vector.
				for(int idx = 0; idx < line.length(); idx++){
					if(Character.digit(line.charAt(idx),10) == ResponseType.CORRECT.value){
						++categoryResultsCount[idx];
					}
				}
			}
		}
		++totalFeatureVectorCount;
		// Update count with current results
		for(Question q : userResponse.keySet()){
			if(userResponse.get(q).equals(ResponseType.CORRECT)){
				++categoryResultsCount[q.getCategory().index];
			}
		}
		// Update polynomial function with new polynomial function
		user.updateInstructionTable(categoryResultsCount, totalFeatureVectorCount);	
	}
	
	private BigInteger computeHPWDFromCoordinates(BigInteger[][] coordinates, BigInteger q){
		BigInteger hpwd = BigInteger.ZERO;
		for(int i = 0; i < Category.values().length; i++){
			BigInteger lambda = BigInteger.ONE;
			for(int j = 0; j < Category.values().length; j++){
				if(i != j){
					lambda = lambda.multiply(coordinates[j][0].multiply(coordinates[j][0].subtract(coordinates[i][0]).modInverse(q)));
					//lambda = lambda * (coordinates[j][0]/(coordinates[j][0]-coordinates[i][0]));
				}
			}
			//hpwd = hpwd.add(BigInteger.valueOf(Math.round(coordinates[i][1] * lambda)));
			hpwd = hpwd.add(coordinates[i][1].multiply(lambda));
		}
		return hpwd.mod(q);
	}
	
	private boolean verifyHistoryFileContents(User user, BigInteger hpwd){
		Scanner scanner = null;
		try{
		File historyFile = user.getHistoryFile();
		//String lineSep = System.getProperty("\n");
		scanner = new Scanner(new FileReader(historyFile));
		scanner.useDelimiter("q");
		StringBuffer sb = new StringBuffer();
		while(scanner.hasNext()){
			sb.append(scanner.next()).append("q");
		}
		byte[] ciphertext = sb.substring(0, sb.length() - 1).getBytes("UTF-8");
		String plaintext = History.decrypt(hpwd.toByteArray(), ciphertext, user.getIv());
		if(plaintext.substring(0, 21).equals("Decryption Successful"))
			return true;
		return false;
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			scanner.close();
		}
		return true;
	}
	
	public void createHistoryFile(User currentUser, Map<Question, ResponseType> userResponse) throws IOException{
		if(!currentUser.getHistoryFile().exists()){
			currentUser.getHistoryFile().createNewFile();
		}
		FileOutputStream fos = null;
		//RandomAccessFile randomAccessFile= new RandomAccessFile(currentUser.getHistoryFile(), "rw");
		try{
			fos = new FileOutputStream(currentUser.getHistoryFile());
			Integer[] poly = currentUser.getPolynomialCoefficients();
			byte[] ciphertext = History.encrypt(BigInteger.valueOf(poly[0]), userResponse, currentUser);
			fos.write(ciphertext);
			//randomAccessFile.write(ciphertext);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			fos.close();
			//randomAccessFile.close();
		}
	}
}
