package com.example;

import java.io.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

// [START example]
@SuppressWarnings("serial")
public class MyWebhookServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PrintWriter out = resp.getWriter();
    out.println("Hello, world");
    
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    
	resp.setContentType("application/json");
	
	StringBuilder buffer = new StringBuilder();
    BufferedReader reader = request.getReader();
    String line;
    while ((line = reader.readLine()) != null) {
        buffer.append(line);
    }
    String data = buffer.toString();
	JSONObject reqJSON = (JSONObject) new JSONParser().parse(data);;
	
    PrintWriter out = resp.getWriter();
    JSONObject obj = new JSONObject();
    obj.put("displayText", "500 Rs.");
    obj.put("speech", "500 Rs."+String.valueOf(reqJSON.result.action));
    out.println(obj);
    
  }

}