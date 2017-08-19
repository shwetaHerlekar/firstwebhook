package com.example;

import java.io.IOException;
import java.io.PrintWriter;

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
    PrintWriter out = resp.getWriter();
    JSONObject obj = new JSONObject();
    obj.put("displayText", "500 Rs.");
    obj.put("speech", "500 Rs.");
    out.println(obj);
    
  }

}