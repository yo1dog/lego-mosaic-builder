package net.awesomebox.legoMosaicBuilder.web.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Config
{
	public static final String DATA_DIRECTORY;
	
	// images
	public static final String[] IMAGE_VALID_TYPES;
	
	// other
	public static final boolean DEBUG;
	
	
	public static void init()
	{
		// calls static constructor
	}
	
	
	static
	{
		Properties properties = new Properties();
		
		// read the location of the property file
		InputStream dataDirectoryConfigInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("../../META-INF/datadir.conf"); // relative to webapps/legoMosaicBuilder/WEB-INF/classes/
		InputStreamReader is = new InputStreamReader(dataDirectoryConfigInputStream);
		BufferedReader br = new BufferedReader(is);
		
		String dataDirectory = null;
		
		try
		{
			dataDirectory = br.readLine();
			
			if (dataDirectory == null || dataDirectory.length() == 0)
				System.err.println("Data directory config file is empty.");
		}
		catch (IOException e)
		{
			System.err.println("Unable to read data directory config file.");
			e.printStackTrace();
		}
		
		if (dataDirectory != null)
		{
			String configFileLocation = dataDirectory + "config.properties";
			
			System.out.println("Data Directory: " + dataDirectory);
			System.out.println("Config File: " + configFileLocation);
			
			File configFile = new File(configFileLocation);
			
			if (!configFile.exists())
				System.err.println("Config file does not exist at " + configFileLocation);
			else
			{
				try
				{
					InputStream in = new FileInputStream(configFile);
					
					try
					{
						properties.load(in);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					
					try
					{
						in.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				catch (FileNotFoundException e)
				{
					System.err.println("Unable to read config file at " + configFileLocation);
					e.printStackTrace();
				}
			}
		}
		
        
		DATA_DIRECTORY = dataDirectory;
        
		// images
		IMAGE_VALID_TYPES = getPropertyArray(properties, "validImageTypes", ",",
				new String[] {
					"png",
					"jpg"
				});
		
		// debug mode
		DEBUG = getProperty(properties, "debug", false);
		if (DEBUG)
			System.out.println("DEBUG MODE");
	}
	
	
	private static boolean getProperty(Properties properties, String propertyName, boolean defaultValue)
	{
		String strValue = properties.getProperty(propertyName);
		
		if (strValue == null)
			return defaultValue;
		
		return strValue.toLowerCase().equals("true");
	}
	
	private static String[] getPropertyArray(Properties properties, String propertyName, String seperator, String[] defaultValue)
	{
		String strValue = properties.getProperty(propertyName);
		
		if (strValue == null)
			return defaultValue;
		
		List<String> items = Arrays.asList(strValue.split(seperator));
		for (int i = 0; i < items.size(); ++i)
		{
			if (items.get(i).trim().length() == 0)
			{
				items.remove(i);
				--i;
			}
		}
		
		return (String[])items.toArray();
	}
}
