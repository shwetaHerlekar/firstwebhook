package com.example;

import java.io.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceContext;
import ai.api.AIServiceContextBuilder;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;



// [START example]
@SuppressWarnings("serial")
public class MyServiceServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    /*PrintWriter out = resp.getWriter();
    out.println("Hello Servlet!!");*/
    
	AIConfiguration aiConfig = new AIConfiguration(config.getInitParameter(PARAM_API_AI_KEY));
	AIDataService aiDataService = new AIDataService(aiConfig);
	
	AIResponse aiResponse = request(request.getParameter("query"), request.getSession());
	response.setContentType("text/plain");
    response.getWriter().append(aiResponse.getResult().getFulfillment().getSpeech());
	
  }
  
  public AIResponse request(String query, HttpSession session) throws AIServiceException {
    return request(new AIRequest(query),
        (session != null) ? AIServiceContextBuilder.buildFromSessionId(session.getId()) : null);
	}
  
}