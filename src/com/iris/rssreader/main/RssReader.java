package com.iris.rssreader.main;
import java.sql.*;
import java.util.ArrayList;
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
		List<RssFeed> feeds = new ArrayList<RssFeed>(); 
		
		try
		{
			Class.forName("org.postgresql.Driver");
			Connection dbConnection = connectToDb();
			Statement st = dbConnection.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM \"RSSFeeds\"");
			while (rs.next())
			{
			   int id = Integer.parseInt(rs.getString(1));
			   String feedCategory = rs.getString(2);
			   String feedUrl = rs.getString(3);
			   String dateFormat = rs.getString(4);
			   
			   feeds.add(new RssFeed(id, feedCategory, feedUrl, dateFormat));
			} 
			rs.close();
			st.close();
		} 
		catch (ClassNotFoundException | SQLException e)
		{
			e.printStackTrace();
		}	

		return feeds;
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
