package com.example;

import java.text.ParseException;
import java.util.HashMap;
import java.util.logging.Logger;

import com.google.gson.JsonElement;

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
	
	private Fulfillment queryLeave(Fulfillment output, HashMap<String, JsonElement> parameter) throws ParseException {
		String message = "Your birthday is coming..Do you want to go out??";
		output.setDisplayText(message);
		output.setSpeech(message);
		return output;
	}
}