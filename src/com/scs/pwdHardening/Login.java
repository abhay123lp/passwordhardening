package com.scs.pwdHardening;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.scs.pwdHardening.model.Category;
import com.scs.pwdHardening.model.Question;
import com.scs.pwdHardening.model.Questionnaire;
import com.scs.pwdHardening.model.ResponseType;
import com.scs.pwdHardening.model.User;
import com.scs.pwdHardening.service.LoginService;
import com.scs.pwdHardening.utility.Utility;

public class Login {
	
	private static Scanner scanner = new Scanner(System.in);
	private static Map<Question, ResponseType> userResponse = new HashMap<Question, ResponseType>();
	private static final int NO_MANDATORY_QUESTIONS = 3;
	private static LoginService loginService = new LoginService();
	private static Set<User> userList = new HashSet<User>();
	
	public static void main(String[] args) throws IOException {

		System.out.print("Enter userName : ");
		String userName = scanner.nextLine();
		
		System.out.print("Enter password : ");
		String password = scanner.nextLine();
		
		User currentUser = null;
		for(User user : userList){
			if(userName.equalsIgnoreCase(user.getUserName())){
				currentUser = user;
				break;
			}
		}
		askQuestions();
		if(currentUser == null){
			System.out.println("User " + userName +" does not exist. Creating new user");
			currentUser = new User(userName, password, Category.values().length);
			loginService.initializeUser(currentUser);
			loginService.createHistoryFile(currentUser, userResponse);
		}
		else {
			currentUser.setHistoryFile(Utility.getHistoryFileFromUserName(currentUser.getUserName()));
			if(loginService.verifyUser(currentUser, userResponse)){ 
				System.out.println("Successfully verified user : " + currentUser.getUserName());
			}
			else{
				System.out.println("User verification failed : " + currentUser.getUserName());
			}
		}
		
	}
	
	private static void askQuestions(){
		int answeredQuestionsCount = 0, curIndex = 1;
		
		for(Category currCategory : Category.values()){
			if(answeredQuestionsCount < NO_MANDATORY_QUESTIONS){
				boolean canSkipQuestion = Category.values().length - curIndex > NO_MANDATORY_QUESTIONS - answeredQuestionsCount;
				boolean skipped = askQuestionFromList(Questionnaire.ALL_QUESTIONS.get(currCategory), canSkipQuestion);
				answeredQuestionsCount = skipped ? answeredQuestionsCount : ++answeredQuestionsCount;
			}
			else{
				userResponse.put(Questionnaire.ALL_QUESTIONS.get(currCategory).get(0), ResponseType.SKIPPED);
			}
			++curIndex;
		}
	}
	
	private static boolean askQuestionFromList(ArrayList<Question> categoryQuestions, boolean canSkipQuestion){
		int randIndex = Utility.getRandomInteger();
		Question q = categoryQuestions.get(randIndex);
		System.out.println(q.getQuestion());
		System.out.println("Your choices are : ");
		categoryQuestions.remove(randIndex);	// We have asked this question. Remove it so that we don't repeat it.
		
		int userResponseIndex = getResponse(q.getChoices(), canSkipQuestion) - 1;
		
		return loginService.evaluateResponse(userResponse, q, userResponseIndex);
	}
	
	private static int getResponse(String [] choices, boolean canSkipQuestion){
		int chosenResponse, ch = 1;
		for(String choice : choices){
			System.out.println(ch++ + ") " + choice);
		}
		System.out.print("Response : ");
		try{
			chosenResponse = Integer.parseInt(scanner.nextLine());
			if(chosenResponse < 1 || chosenResponse > choices.length){
				System.out.println("Invalid choice. Please choose from one of the below responses");
				chosenResponse = getResponse(choices, canSkipQuestion);
			}
			if(!canSkipQuestion && choices[chosenResponse - 1].equalsIgnoreCase("SKIP_QUESTION")){
				System.out.println("Cannot skip any more questions. Please choose one of the below responses");
				return getResponse(choices, canSkipQuestion);
			}
		}
		catch(NumberFormatException nfe){
			System.out.println("Invalid choice. Please choose from one of the below responses");
			return getResponse(choices, canSkipQuestion);
		}
		if(chosenResponse < 1 || chosenResponse > choices.length){
			System.out.println("Invalid choice. Please choose from one of the below responses");
			return getResponse(choices, canSkipQuestion);
		}
		return chosenResponse;
	}
} 
