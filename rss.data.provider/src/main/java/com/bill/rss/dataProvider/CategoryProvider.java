package com.bill.rss.dataProvider;

import java.util.List;

import com.bill.rss.domain.Category;

public interface CategoryProvider {
	
	 List<Category> retrieveCategories();

}
