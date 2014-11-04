package com.iris.rssreader.feed;

import java.util.UUID;

public class RssFeed
{
	private int id;
	private String category; 
	private String feedUrl; 
	private String dateFormat;
	
	public RssFeed(int id, String category, String feedUrl, String dateFormat)
	{
		this.id = id; 
		this.category = category; 
		this.feedUrl = feedUrl;
		this.dateFormat = dateFormat; 
	}

	public int getId()
	{
		return id;
	}

	public String getCategory()
	{
		return category;
	}

	public String getFeedUrl()
	{
		return feedUrl;
	}
	
	public String getDateFormat()
	{
		return dateFormat;
	}
	
}
