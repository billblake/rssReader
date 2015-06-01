package com.bill.rss.domain;

import java.util.ArrayList;
import java.util.List;

public class Category {

	private String categoryId;
	private String username;
	private String name;
	private String totalCount;
	private String unReadCount;
	private List<Feed> feeds;


	public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(String unReadCount) {
        this.unReadCount = unReadCount;
    }


	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Feed> getFeeds() {
		return feeds;
	}

	public void setFeeds(List<Feed> feeds) {
		this.feeds = feeds;
	}

	public void addFeed(Feed feed) {
		if (feed != null) {
			if (this.feeds == null) {
				this.feeds = new ArrayList<Feed>();
			}
			this.feeds.add(feed);
		}
	}
}
