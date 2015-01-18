package net.awesomebox.legoMosaicBuilder.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.awesomebox.legoMosaicBuilder.lego.LegoShape;

import com.google.gson.Gson;


@WebServlet("/test")
public final class Test extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setStatus(200);
		response.setContentType("application/json");
		
		Gson gson = new Gson();
		response.getWriter().print(gson.toJson(LegoShape._1x1.getBricks()));
	}
}
