package com.iris.rssreader.db;

import java.net.MalformedURLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.iris.rssreader.db.properties.PropertiesAccessor;
import com.iris.rssreader.feed.Article;
import com.iris.rssreader.feed.RssFeed;

public class DbAccessor
{
	private Properties prop;
	private Connection conn;
	
	public DbAccessor()
	{
			PropertiesAccessor accessor = new PropertiesAccessor();
			prop = accessor.GetProperties();

			try
			{
				Class.forName("org.postgresql.Driver");
				conn = DriverManager.getConnection(prop.getProperty("connectionString"), prop.getProperty("username"), prop.getProperty("password"));
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
	}
	
	public void WriteArticlesToDb(List<Article> articles)
	{
		for (Article article : articles)
		{

			Statement statement;
			String sql = null;

			try
			{
				if (!ArticleIsDuplicate(article.getUrl()))
				{
					int urlId = WriteUrlToDb(article.getUrl());

					statement = conn.createStatement();
					sql = "INSERT INTO \"UnprocessedArticles\" (\"FeedId\",\"Headline\",\"Description\",\"PublicationDate\",\"UrlId\") "
							+ "VALUES ("
							+ Integer.toString(article.getFeedId())
							+ ","
							+ "'"
							+ article.getHeadline()
							+ "',"
							+ "'"
							+ article.getDescription()
							+ "',"
							+ "'"
							+ article.getPublicationDate() + "'," + urlId + ")";
					statement.executeUpdate(sql);
				}
			}
			catch (Exception e)
			{
				System.out.println("Error encountered :" + e
						+ " executing statement :" + sql);
			}
		}
	}
	
	public List<RssFeed> LoadFeeds()
	{
		List<RssFeed> feeds = new ArrayList<RssFeed>();

		try
		{
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM \"RSSFeeds\"");
			while (rs.next())
			{
				int id = Integer.parseInt(rs.getString(1));
				int outletId = Integer.parseInt(rs.getString(2));
				String feedCategory = rs.getString(3);
				String feedUrl = rs.getString(4);
				String dateFormat = rs.getString(5);

				feeds.add(new RssFeed(id, outletId, feedCategory, feedUrl,
						dateFormat));
			}
			rs.close();
			st.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}

		return feeds;
	}
	
	private int WriteUrlToDb(String url)
	{
		Statement statement;
		String sql = null;
		try
		{
			Timestamp dateProcessed = new Timestamp(System.currentTimeMillis());
			sql = "INSERT INTO \"UnprocessedArticleUrls\" (\"Url\", \"DateProcessed\") VALUES ('" + url + "', '" + dateProcessed + "' )";
			statement = conn.createStatement();


			statement.execute(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet keyset = statement.getGeneratedKeys();
			if (keyset.next())
			{
				// Return the generated index.
				return (keyset.getInt(1));
			}
		}
		catch (SQLException e)
		{
			System.out
					.println("Exception encountered writing url to database, "
							+ "statement was " + sql + " Exception was " + e);
		}

		return 0;
	}

	/* Checks if the url already exists in database to avoid duplicate
	 *  archived articles. Url used as same headline may be used by different 
	 *  news outlets */ 
	private boolean ArticleIsDuplicate(String url)
	{
		String sql = null;

		try
		{
			Statement statement = conn.createStatement();
			sql = "SELECT * FROM \"ProcessedArticleUrls\" "
				  + "WHERE \"Url\"= + url +";
			ResultSet rs = statement.executeQuery(sql);

			if (!rs.next())
			{
				return false;
			}
		}
		catch (SQLException e)
		{
			System.out.println("Query " + sql
					+ " failed, exception encountered was " + e);
		}

		return true;
	}
}
