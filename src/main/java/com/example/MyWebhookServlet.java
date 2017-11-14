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
		Map<String,String> outParameter = new HashMap<>();
		
		
		if (parameter.containsKey("startDate") && parameter.containsKey("endDate")) {
			if (!parameter.get("startDate").getAsString().equals("")) {
				log.info("start date");
				outParameter.put("startDate", parameter.get("startDate").toString());
			}
			if (!parameter.get("endDate").getAsString().equals("")) {
				log.info("endDate");
				outParameter.put("endDate", parameter.get("endDate").toString());
			}
			if (!parameter.get("endDate").getAsString().equals("") && !parameter.get("startDate").getAsString().equals("")) {
				days =  getDays(parameter.get("startDate").getAsString(), parameter.get("endDate").getAsString());
				outParameter.put("noOfDays", String.valueOf(days));
				AIEvent followupEvent = new AIEvent("simple_leave_event");
				followupEvent.setData(outParameter);
				log.info("rerouting to event : evt trg");
				output.setFollowupEvent(followupEvent);
				message = "You want to apply leave from "+parameter.get("startDate").toString()+" to "+parameter.get("endDate").toString();
				log.info("mesg :"+message);
				output.setDisplayText(message);
				output.setSpeech(message);
			}
		}
		if (parameter.containsKey("noOfDays") && !parameter.get("noOfDays").equals("")) {
			log.info("no of days");
			outParameter.put("noOfDays", parameter.get("noOfDays").toString());
			AIEvent followupEvent = new AIEvent("simple_leave_event");
			followupEvent.setData(outParameter);
			log.info("rerouting to event : evt trg");
			output.setFollowupEvent(followupEvent);
		}
		if (parameter.containsKey("event") && !parameter.get("event").equals("")) {
			outParameter.put("event", parameter.get("event").toString());
			AIEvent followupEvent = new AIEvent("simple_leave_event");
			followupEvent.setData(outParameter);
			log.info("rerouting to event : evt trg");
			output.setFollowupEvent(followupEvent);
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