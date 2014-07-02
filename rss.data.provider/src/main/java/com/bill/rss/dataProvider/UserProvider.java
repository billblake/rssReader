package com.bill.rss.dataProvider;

import com.bill.rss.domain.User;

public interface UserProvider {

	boolean checkIfUserNameExists(String username);
	
	void createNewUser(User user);
}
