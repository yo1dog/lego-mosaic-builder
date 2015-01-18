package net.awesomebox.legoMosaicBuilder.web.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.awesomebox.legoMosaicBuilder.lego.LegoConnector;

public class Startup implements ServletContextListener
{
	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		System.out.println("Starting in context listener...");
		
		Config.init();
		LegoConnector.init();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce)
	{
	}
}
