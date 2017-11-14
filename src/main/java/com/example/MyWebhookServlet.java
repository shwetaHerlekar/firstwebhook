package com.example;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

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
			if (!parameter.get("startDate").equals("")) {
				log.info("start date");
				outParameter.put("startDate", parameter.get("startDate").toString());
			}
			if (!parameter.get("endDate").equals("")) {
				log.info("endDate");
				outParameter.put("endDate", parameter.get("endDate").toString());
			}
			if (!parameter.get("endDate").equals("") && !parameter.get("startDate").equals("")) {
				days =  getDays(parameter.get("startDate").toString(), parameter.get("endDate").toString());
				outParameter.put("noOfDays", String.valueOf(days));
				//fetch no of days
			}
			AIEvent followupEvent = new AIEvent("simple_leave_event");
			followupEvent.setData(outParameter);
			log.info("rerouting to event : evt trg");
			output.setFollowupEvent(followupEvent);
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
	
	private int getDays(String startDate , String endDate) {
		
		// TODO Auto-generated method stub
		int days = 0;

		return 0;
	}
}