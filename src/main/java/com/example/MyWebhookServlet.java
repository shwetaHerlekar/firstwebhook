package com.example;

import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;

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
				case "simple_leave" :
					log.info("in simple leave case");
					output = simpleLeave(output, parameter);
					break;
				case "QueryLeave.QueryLeave-yes" :
					log.info("in QueryLeave.QueryLeave-yes case");
					output = eventOneLeave(output, parameter);
					break;
				case "QueryLeave.QueryLeave-yes.QueryLeave-yes-yes" :
					log.info("in QueryLeave.QueryLeave-yes.QueryLeave-yes-yes");
					output = applyOneLeave(output, parameter);
					break;
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
				contextOut.setParameters(outParameters);
				output.setContextOut(contextOut);
				output.setDisplayText(message);
				output.setSpeech(message);
			}
			else if (parameter.get("endDate").getAsString().equals("") && !parameter.get("startDate").getAsString().equals(""))
			{
				log.info("only start date is given");
			}
			else if ( !parameter.get("endDate").getAsString().equals("") && parameter.get("startDate").getAsString().equals(""))
			{
				log.info("only end date is given");
			}
			else if (parameter.get("endDate").getAsString().equals("") && parameter.get("startDate").getAsString().equals(""))
			{
				log.info("no date is given");
				message = Suggest();
				
				String event = "birthday";
				JsonElement noOfDays = new JsonPrimitive(event);
				outParameters.put("event", noOfDays);
				
				String bday = holidayData.get("birthday").toString();
				Date birthday = new SimpleDateFormat("dd/MM/yyyy").parse(bday); 
				
				JsonElement startDate = new JsonPrimitive(birthday.toString());
				outParameters.put("startDate", startDate);
				
				AIOutputContext contextOut = new AIOutputContext();
				contextOut.setLifespan(2);
				contextOut.setName("QueryLeave-followup");
				contextOut.setParameters(outParameters);
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
	
	@SuppressWarnings("unchecked")
	private Fulfillment eventOneLeave(Fulfillment output, HashMap<String, JsonElement> parameter) throws ParseException {
		String message = "Do you want to take off on "+parameter.get("event").getAsString();
		log.info(message);
		AIOutputContext contextOut = new AIOutputContext();
		contextOut.setLifespan(2);
		contextOut.setName("QueryLeave-yes-followup");
		contextOut.setParameters(parameter);
		output.setContextOut(contextOut);		
		output.setDisplayText(message);
		output.setSpeech(message);
		return output;
	}
	
	@SuppressWarnings("unchecked")
	private Fulfillment applyOneLeave(Fulfillment output, HashMap<String, JsonElement> parameter) throws ParseException {
		log.info("inside apply one leave");
		
		String message = "Please confirm your leave on "+parameter.get("startDate").getAsString()+".";
		JsonElement endDate = new JsonPrimitive(parameter.get("startDate").toString());
		parameter.put("endDate", endDate);
		int days = 1;
		JsonElement noOfDays = new JsonPrimitive(days);
		parameter.put("noOfDays", noOfDays);
		AIOutputContext contextOut = new AIOutputContext();
		contextOut.setLifespan(2);
		contextOut.setName("SimpleLeave-followup");
		contextOut.setParameters(parameter);
		output.setContextOut(contextOut);		
		output.setDisplayText(message);
		output.setSpeech(message);
		
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
	
	@SuppressWarnings("unchecked")
	private Fulfillment simpleLeave(Fulfillment output, HashMap<String, JsonElement> parameter) throws ParseException {
		log.info("in simple leave function");
		String message = "";
		
		
		if (parameter.containsKey("startDate") && parameter.containsKey("endDate")) {
			
			if (!parameter.get("endDate").getAsString().equals("") && !parameter.get("startDate").getAsString().equals("")) {
				message = "You are applying leave from "+parameter.get("startDate").getAsString()+" to "+parameter.get("endDate").getAsString()+". Please Confirm.";
				log.info(message);
			}
			else if (!parameter.get("startDate").getAsString().equals("") && !parameter.get("noOfDays").getAsString().equals("")){
				message = "You are applying "+parameter.get("noOfDays").getAsString()+" from "+parameter.get("startDate").getAsString()+". Please confirm.";
				log.info(message);
			}
		}
		
		output.setDisplayText(message);
		output.setSpeech(message);
		return output;
	}
	
	private boolean isEventWithinRange(Date testDate) throws ParseException {  
		String event_date="11/15/2017";
		Date today = new SimpleDateFormat("dd/MM/yyyy").parse(event_date);  
		event_date="31/01/2018";
		Date last = new SimpleDateFormat("dd/MM/yyyy").parse(event_date);  
		return testDate.before(today) && last.after(testDate);
	}
	
	@SuppressWarnings("deprecation")
	private String Suggest() throws ParseException
	{
		log.info("inside suggest");
		JSONObject holidayData = Data.getHolidays();
		String bday = holidayData.get("birthday").toString();
		Date birthday = new SimpleDateFormat("dd/MM/yyyy").parse(bday);
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
		String msg = "";
		
		if(isEventWithinRange(birthday))
		{
			msg = "Your birthday is coming on "+sdf.format(birthday)+". Want to go out??";
			log.info(msg);
		}
		
		return msg;
	}
}