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
	private PreparedStatement ps;

	/**
	 * The DbAccessor object maintains is an object that stores all database interaction.
	 * Constructing the DbAccessor object using the default constructor will search for
	 * a config.properties file from the classpath before search for the following values (by string key)
	 *
	 * <ul>
	 *     <li>connectingString: Connecting string to the intended database.</li>
	 *     <li>Username: Self explanatory, username for db login.</li>
	 *     <li>Pasword: Self explanatory, password for db login.</li>
	 * </ul>
	 */
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

	/**
	 * Takes a list of articles and writes them to the database as UnprocessedArticles
	 * for later processing by the rainbow module.
	 *
	 * @param  articles  List of articles gathered from RssFeeds.
	 */
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


	/**
	 * Returns a list of manually entered RssFeeds to search for articles.
	 *
	 * @return List of RssFeed objects that can be searched for news articles.
	 */
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

	/**
	 * Writes a single url to the database and returns the serial Id generated for that Url
	 *
	 * @param url The full url to write to the database
	 * @return The generated key for the Url entered into the database.
	 */
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


	/**
	 * Checks if the Url in question has already been processed, otherwise the article is assumed
	 * to not have yet been processed. Especially importing as we must trim down the amount of data
	 * to be processed at later stages.
	 *
	 * @param url The full url to check against existing urls.
	 * @return a boolean specifying if the url already exists or not on the Db.
	 */
	private boolean ArticleIsDuplicate(String url)
	{
		String sql = null;

		try
		{

			final String statement = "SELECT * FROM  \"ProcessedArticleUrls\" WHERE \"Url\" = ?";

			ps = conn.prepareStatement(statement);
			ps.setString(1, url);


			ResultSet rs = ps.executeQuery();

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
