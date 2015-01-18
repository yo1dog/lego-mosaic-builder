package net.awesomebox.legoMosaicBuilder.web.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/CustomErrorPage")
public class CustomErrorPage extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		showErrorPage(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		showErrorPage(request, response);
	}
	
	private static void showErrorPage(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Throwable error       = (Throwable)request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
		String    servletName = (String)   request.getAttribute(RequestDispatcher.ERROR_SERVLET_NAME);
		Integer   statusCode  = (Integer)  request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		String    requestUri  = (String)   request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
		
		statusCode = statusCode == null? 0 : statusCode.intValue();
		
		response.setStatus(statusCode);
		response.setContentType("text/plain");
		
		PrintWriter writer = response.getWriter();
		writer.println(
				"An error has occured.\n" +
				"\nServlet: " + servletName + 
				"\nURI: "     + requestUri +
				"\nStack Trace:\n");
		
		if (error == null)
			writer.println("null");
		else
			error.printStackTrace(writer);
		
		response.flushBuffer();
	}
}
