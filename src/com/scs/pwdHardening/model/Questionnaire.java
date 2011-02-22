package com.scs.pwdHardening.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Questionnaire {
	
	private static final String CATEGORY = "Category";
	private static final String QUESTION = "Question";
	private static final String RESPONSE= "Response";
	private static final String CATEGORY_NAME = "name";
	private static final String QUESTION_TEXT = "text";
	
	private static File questionnaireFile = new File("P:\\EclWorkspace\\SecureComputerSystems\\data\\Questionnaire.xml");
	
	private enum Category { SPORTS, TECHNOLOGY, ENTERTAINMENT, RELIGION, HUMANITIES, MOVIES };
	
	private static HashMap<Category, ArrayList<Question>> ALL_QUESTIONS = new HashMap<Category, ArrayList<Question>>();
	
	static {
		for(Category c : Category.values()){
			ALL_QUESTIONS.put(c, new ArrayList<Question>());
		}
		populateQuestionnaire();
	}
	
	private static void populateQuestionnaire(){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//factory.setValidating(true);
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(questionnaireFile);
			NodeList categoryNodes = document.getElementsByTagName(CATEGORY);
			for(int categoryIdx = 0; categoryIdx < categoryNodes.getLength(); categoryIdx++){
				Element categoryNode = (Element)categoryNodes.item(categoryIdx);
				Category currentCategory = Category.valueOf(categoryNode.getAttribute(CATEGORY_NAME));
				ArrayList<Question> categoryQuestions = ALL_QUESTIONS.get(currentCategory);
				NodeList questionNodes = categoryNode.getElementsByTagName(QUESTION);
				for(int questionIdx = 0; questionIdx < questionNodes.getLength(); questionIdx++){
					Element questionNode = (Element)questionNodes.item(questionIdx);
					Question question = new Question(questionNode.getAttribute(QUESTION_TEXT));
					NodeList responseNodes = categoryNode.getElementsByTagName(RESPONSE);
					for(int responseIdx = 0; responseIdx < questionNodes.getLength(); responseIdx++){
						Element responseNode = (Element)responseNodes.item(responseIdx);
						question.choices[responseIdx] = responseNode.getTextContent();
					}
					categoryQuestions.add(question);
				}
			}
			System.out.println("Random");
				
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}