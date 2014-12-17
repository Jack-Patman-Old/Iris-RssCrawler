package com.iris.rssreader.db.properties;

import java.io.IOException;
import java.util.Properties;

public class PropertiesAccessor
{
	/**
	 * Loads the config.properties file containing Database access information.
	 *
	 * @return Standard property object containing all information within the config.properties file
	 */
	public Properties GetProperties()
	{
		Properties prop = new Properties();

		try
		{
			try
			{
				prop.load(PropertiesAccessor.class.getClassLoader().getResourceAsStream("config.properties"));
				return prop;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return prop;
	}
}
