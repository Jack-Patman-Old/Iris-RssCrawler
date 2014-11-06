package com.iris.rssreader.main;

import java.util.ArrayList;
import java.util.List;

import com.iris.rssreader.db.DbAccessor;
import com.iris.rssreader.feed.Article;
import com.iris.rssreader.feed.RssFeed;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RssReader
{
	/**
	 * When main method is invoked, will load RSS feed details from a database
	 * extract the latest articles and archive them.
	 */
	public static void main(String[] args)
	{
		DbAccessor db = new DbAccessor(); 
		List<RssFeed> feeds = db.LoadFeeds();
		List<Article> articles = ExtractArticles(feeds);
		db.WriteArticlesToDb(articles);
	}

	// Unchecked cast - should be safe based on api specification
	@SuppressWarnings("unchecked")
	private static List<Article> ExtractArticles(List<RssFeed> feeds)
	{
		/* Extract articles from a given Rss feed, generate list of articles 
		ready to write to database */
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
							date, uri, null));
				}
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
		}

		return articles;
	}
}
