package com.iris.rssreader.feed;

import java.util.Date;

public class Article
{
	private int feedId;
	private String headline;
	private String description;
	private Date publicationDate;
	private String url;

    //TODO : Base Category needs to be extracted from articles and read into Db when we get to the Categorization stage.
	public Article(int feedId, String headline, String description, Date publicationDate, String url, String baseCategory)
	{
		this.feedId = feedId;
		this.headline = headline.replace("'", "''");
		this.description = description.replace("'", "''");
		this.publicationDate = publicationDate;
		this.url = url; 
	}

	public int getFeedId()
	{
		return feedId;
	}
	public String getHeadline()
	{
		return headline;
	}
	public String getDescription()
	{
		return description;
	}
	public Date getPublicationDate()
	{
		return publicationDate;
	}
	public String getUrl()	{ return url; }

}

