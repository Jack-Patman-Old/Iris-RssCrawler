package com.iris.rssreader.main;

import java.net.MalformedURLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.iris.rsseader.db.properties.PropertiesAccessor;
import com.iris.rssreader.feed.Article;
import com.iris.rssreader.feed.RssFeed;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RssReader
{
	Properties prop;
	static Connection dbConnection;

	/**
	 * When main method is invoked, will load RSS feed details from a database
	 * extract the latest articles and archive them.
	 */
	public static void main(String[] args)
	{
		List<RssFeed> feeds = LoadFeeds();
		List<Article> articles = ExtractArticles(feeds);
		WriteArticlesToDb(articles);
	}

	private static void WriteArticlesToDb(List<Article> articles)
	{
		for (Article article : articles)
		{

			Statement statement;
			String sql = null;
			int urlId = 0;

			try
			{
				statement = dbConnection.createStatement();
				sql = "INSERT INTO \"UnprocessedArticleUrls\" (\"Url\")"
						+ "VALUES ('" + article.getUrl().toString() + "')";
				statement.execute(sql, Statement.RETURN_GENERATED_KEYS);
				ResultSet keyset = statement.getGeneratedKeys();
				if (keyset.next())
				{
					// Retrieve the auto generated key(s).
					urlId = keyset.getInt(1);
				}

				statement = dbConnection.createStatement();
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
			catch (Exception e)
			{
				System.out.println("Error encountered :" + e
						+ " executing statement :" + sql);
			}
		}
	}

	/**
	 * @param feeds
	 * @return
	 */
	// Unchecked cast - should be safe based on api specification
	@SuppressWarnings("unchecked")
	private static List<Article> ExtractArticles(List<RssFeed> feeds)
	{
		List<Article> articles = new ArrayList<Article>();

		for (RssFeed feed : feeds)
		{
			try
			{
				SyndFeedInput input = new SyndFeedInput();
				SyndFeed inputFeed = input.build(new XmlReader(feed
						.getFeedUrl()));
				for (SyndEntry entry : (List<SyndEntry>) inputFeed.getEntries())
				{
					String title = entry.getTitle();
					String description = entry.getDescription().getValue();
					java.util.Date date = entry.getPublishedDate();
					String uri = entry.getUri();
					articles.add(new Article(feed.getId(), title, description,
							date, uri));
				}
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
		}

		return articles;
	}

	private static List<RssFeed> LoadFeeds()
	{
		List<RssFeed> feeds = new ArrayList<RssFeed>();

		try
		{
			Class.forName("org.postgresql.Driver");
			dbConnection = connectToDb();
			Statement st = dbConnection.createStatement();
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
		catch (ClassNotFoundException | SQLException | MalformedURLException e)
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