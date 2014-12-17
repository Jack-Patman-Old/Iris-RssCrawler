package com.iris.rssreader.feed;

import java.net.MalformedURLException;
import java.net.URL;

public class RssFeed
{
	private int id;
	private int outletId;
	private String category; 
	private URL feedUrl; 
	private String dateFormat;
	
	public RssFeed(int id, int outletId, String category, String feedUrl, String dateFormat) throws MalformedURLException
	{
		this.id = id; 
		this.outletId = outletId; 
		this.category = category; 
		this.feedUrl = new URL(feedUrl);
		this.dateFormat = dateFormat; 
	}

	public int getId()
	{
		return id;
	}
	public int getOutletId()
	{
		return outletId; 
	}
	public String getCategory()
	{
		return category;
	}
	public URL getFeedUrl()
	{
		return feedUrl;
	}
	public String getDateFormat()
	{
		return dateFormat;
	}
	
}
