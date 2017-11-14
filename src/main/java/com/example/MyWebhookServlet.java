package com.example;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import ai.api.model.AIEvent;
import ai.api.model.AIOutputContext;
import ai.api.model.Fulfillment;
import ai.api.web.AIWebhookServlet;

// [START example]
@SuppressWarnings("serial")
public class MyWebhookServlet extends AIWebhookServlet {
	private static final Logger log = Logger.getLogger(MyWebhookServlet.class.getName());

	@Override
	protected void doWebhook(AIWebhookRequest input, Fulfillment output) {
		log.info("webhook call");
		String action = input.getResult().getAction();
		HashMap<String, JsonElement> parameter = input.getResult().getParameters();
		
		try{
			switch (action) {
				case "query_leave" :
					log.info("in query leave case");
					output = queryLeave(output, parameter);
					break;
				/*case "simple_leave" :
					log.info("in simple leave case");
					output = simpleLeave(output, parameter);
					break;*/
			}
		}catch(Exception e){
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private Fulfillment queryLeave(Fulfillment output, HashMap<String, JsonElement> parameter) throws ParseException {
		log.info("inside queryLeave");
		HashMap<String, Integer> holidayData = new HashMap<>( Data.getHolidays());
		log.info("holiday "+ holidayData.toString());
		String message ="";
		int balance = holidayData.get("leave_balance");
		log.info("bal :"+balance);
		int days = 0;
		HashMap<String, JsonElement> outParameters = new HashMap<String, JsonElement>();
		
		
		if (parameter.containsKey("startDate") && parameter.containsKey("endDate")) {
			if (!parameter.get("startDate").getAsString().equals("")) {
				log.info("start date");
				JsonElement startDate = new JsonPrimitive(parameter.get("startDate").getAsString());
				outParameters.put("startDate", startDate);
			}
			if (!parameter.get("endDate").getAsString().equals("")) {
				log.info("endDate");
				JsonElement endDate = new JsonPrimitive(parameter.get("endDate").getAsString());
				outParameters.put("endDate", endDate);
			}
			if (!parameter.get("endDate").getAsString().equals("") && !parameter.get("startDate").getAsString().equals("")) {
				days =  getDays(parameter.get("startDate").getAsString(), parameter.get("endDate").getAsString());
				JsonElement noOfDays = new JsonPrimitive(days);
				outParameters.put("noOfDays", noOfDays);
				if(days<=balance)
				{
					message = "You have sufficient leave_balance("+balance+"). You can apply leave.";
					log.info("msg :"+message);
				}
				else
				{
					message = "You have only "+balance+" leaves remaining.You will need DP approval to apply these leaves.";
					log.info("msg :"+message);
				}
				AIOutputContext contextOut = new AIOutputContext();
				contextOut.setLifespan(2);
				contextOut.setName("SimpleLeave");
				output.setContextOut(contextOut);
				output.setDisplayText(message);
				output.setSpeech(message);
			}
		}
		if (parameter.containsKey("noOfDays") && !parameter.get("noOfDays").equals("")) {
			log.info("no of days");
			
		}
		if (parameter.containsKey("event") && !parameter.get("event").equals("")) {
			log.info("event");
		}
		
		return output;
	}
	
	private int getDays(String startDate , String endDate) throws ParseException{
		
		// TODO Auto-generated method stub
		Date start = new SimpleDateFormat("yyyy-mm-dd").parse(startDate);  
		Date end = new SimpleDateFormat("yyyy-mm-dd").parse(endDate);
		
		int days = (int)Math.round((end.getTime() - start.getTime()) / (double) 86400000);

		log.info("days :"+days);
		return days;
	}
}