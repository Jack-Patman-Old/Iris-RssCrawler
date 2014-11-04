package com.iris.rssreader.main;
import java.sql.*;
import java.util.List;
import java.util.Properties;

import com.iris.rsseader.db.properties.PropertiesAccessor;
import com.iris.rssreader.feed.RssFeed;

public class RssReader
{
	Properties prop;

	/**
	 * When main method is invoked, will load RSS feed details from a database
	 * extract the latest articles and archive them.
	 */
	public static void main(String[] args)
	{
		List<RssFeed> feeds = LoadFeeds();
		// Load rss feeds from db
		// Read rss feeds into list
		// Write articles to db
	}


	private static List<RssFeed> LoadFeeds()
	{
		try
		{
			Class.forName("org.postgresql.Driver");
			Connection dbConnection = connectToDb();
		} 
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	private static Connection connectToDb()
	{
		Connection conn = null;
		PropertiesAccessor accessor = new PropertiesAccessor(); 
		Properties prop = accessor.GetProperties();
		
		try
		{
			conn = DriverManager.getConnection(prop.getProperty("conn"), prop);
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return conn;
	}

}
