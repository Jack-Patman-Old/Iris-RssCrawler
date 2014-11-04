package com.iris.rssreader.feed;

import java.util.UUID;

public class RssFeed
{
	private UUID id;
	private String category; 
	private String feedUrl; 
	private String dateFormat;
	

	public UUID getId()
	{
		return id;
	}
	
	public void setId(UUID id)
	{
		this.id = id;
	}
	
	public String getCategory()
	{
		return category;
	}
	
	public void setCategory(String category)
	{
		this.category = category;
	}
	
	public String getFeedUrl()
	{
		return feedUrl;
	}
	
	public void setFeedUrl(String feedUrl)
	{
		this.feedUrl = feedUrl;
	}
	
	public String getDateFormat()
	{
		return dateFormat;
	}
	
	public void setDateFormat(String dateFormat)
	{
		this.dateFormat = dateFormat;
	} 
}
