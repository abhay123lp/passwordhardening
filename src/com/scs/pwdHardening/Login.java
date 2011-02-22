package com.scs.pwdHardening;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.scs.pwdHardening.model.Category;
import com.scs.pwdHardening.model.Question;
import com.scs.pwdHardening.model.Questionnaire;
import com.scs.pwdHardening.model.ResponseType;
import com.scs.pwdHardening.service.LoginService;
import com.scs.pwdHardening.utility.Utility;

public class Login {
	
	private static Scanner scanner = new Scanner(System.in);
	private static LoginService loginService = new LoginService();
	private static Map<Question, ResponseType> userResponse = new HashMap<Question, ResponseType>();
	
	public static void main(String[] args) {
		Questionnaire questionnaire = new Questionnaire();

		System.out.print("Enter userName : ");
		String userName = scanner.nextLine();
		
		System.out.print("Enter password : ");
		String password = scanner.nextLine();
		
		int answeredQuestionsCount = 0;
		
		for(Category currCategory : Category.values()){
			if(answeredQuestionsCount <= 3){
				boolean skipped = askQuestionFromList(Questionnaire.ALL_QUESTIONS.get(currCategory));
				answeredQuestionsCount += skipped ? answeredQuestionsCount : ++answeredQuestionsCount;
			}
			else{
				break;			
			}
		}
	}
	
	private static boolean askQuestionFromList(ArrayList<Question> categoryQuestions){
		int randIndex = Utility.getRandomInteger();
		Question q = categoryQuestions.get(randIndex);
		System.out.println(q.getQuestion());
		System.out.println("Your choices are : ");
		categoryQuestions.remove(randIndex);	// We have asked this question. Remove it so that we don't repeat it.
		
		int userResponseIndex = getResponse(q.getChoices()) - 1;
		
		return loginService.evaluateResponse(userResponse, q, userResponseIndex);
	}
	
	private static int getResponse(String [] choices){
		int chosenResponse, ch = 1;
		for(String choice : choices){
			System.out.println(ch++ + ") " + choice);
		}
		System.out.print("Response : ");
		try{
			chosenResponse = Integer.parseInt(scanner.nextLine());
		}
		catch(NumberFormatException nfe){
			System.out.println("Invalid choice. Please choose from one of the below responses");
			return getResponse(choices);
		}
		if(chosenResponse < 1 || chosenResponse > choices.length){
			System.out.println("Invalid choice. Please choose from one of the below responses");
			return getResponse(choices);
		}
		return chosenResponse;
	}
}
