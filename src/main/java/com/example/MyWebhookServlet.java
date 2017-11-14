package com.example;

import java.io.*;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

// [START example]
@SuppressWarnings("serial")
public class MyWebhookServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(MyWebhookServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PrintWriter out = resp.getWriter();
    out.println("Hello, world");
    
  }

  @SuppressWarnings("unchecked")
@Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    
	try
	{
	resp.setContentType("application/json");
	
	StringBuilder buffer = new StringBuilder();
    BufferedReader reader = req.getReader();
    String line;
    while ((line = reader.readLine()) != null) {
        buffer.append(line);
    }
    String data = buffer.toString();
	JSONParser parser = new JSONParser();
	JSONObject reqJSON = (JSONObject)parser.parse(data);
	JSONObject result = (JSONObject)reqJSON.get("result");
	String action1 = String.valueOf(result.get("action"));
	JSONObject parameters = (JSONObject)result.get("parameters");
	
	

	if(action1.equals("QUERY_LEAVE")){
	
	
		PrintWriter out = resp.getWriter();
		JSONObject obj = new JSONObject();
		
		JSONObject response = getHolidays();
		log.info("bday :"+response.get("birthday"));
		Calendar bday = (Calendar) response.get("birthday");
		
		obj.put("displayText", "Your birthday is coming on 21st November 2017. Want to go out??"+isEventWithinRange(bday));
		obj.put("speech", "Your birthday is coming on 21st November 2017. Want to go out??"+isEventWithinRange(bday));
		out.println(obj);
	}
	/*else if(action1.equals("calculate_pizza_bill")){
		
		String pizza = String.valueOf(parameters.get("pizza"));
		int pizzacnt = Integer.parseInt(String.valueOf(parameters.get("pizzaCount")));
	
		int bill = calculatePizzaBill(pizza,pizzacnt);
		PrintWriter out = resp.getWriter();
		JSONObject obj = new JSONObject();
		obj.put("displayText", "Your bill is "+String.valueOf(bill)+"Rupees. Thanks for visiting us.");
		obj.put("speech", "Your bill is "+String.valueOf(bill)+"Rupees. Thanks for visiting us.");
		out.println(obj);
	}*/
	
	
	}
	catch(Exception e){
		
	}
    
  }
  
  public JSONObject getHolidays(){
	  
	  int leaveBalance = 4;
		
		JSONObject responseObject = new JSONObject();
		
		Calendar bday = Calendar.getInstance();
		bday.set(2017, 11, 21);
		responseObject.put("birthday", bday.toString());
		
		JSONObject holidays = new JSONObject();
		Calendar event_date= Calendar.getInstance();
		event_date.set(2017, 12, 25);
		holidays.put(event_date.toString(), "christmas");
		

		event_date= Calendar.getInstance();
		event_date.set(2017, 12, 31);
		holidays.put(event_date.toString(), "newyear");
		
		responseObject.put("holidays", holidays);
		responseObject.put("leave_balance", leaveBalance);
		return responseObject;
	  
  }
  
  public boolean isEventWithinRange(Calendar testDate) {
		Calendar today = Calendar.getInstance();
		today.clear(Calendar.HOUR); today.clear(Calendar.MINUTE); today.clear(Calendar.SECOND);
		
		Calendar lastday = Calendar.getInstance();
		lastday.add(Calendar.MONTH, 3);
		
	    return testDate.after(today) && testDate.before(lastday);
	}
 
}