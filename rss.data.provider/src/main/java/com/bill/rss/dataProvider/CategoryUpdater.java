package com.bill.rss.dataProvider;

import com.bill.rss.domain.Category;

public interface CategoryUpdater {

    Category addCategory(Category category);

    Category saveCategory(Category category);

    Category deleteCategory(Category category);
}
