package com.bill.rss.server;

import static com.bill.rss.server.ViewConstants.JSON_RESPONSE_TYPE;

import org.codehaus.jackson.map.ObjectMapper;

import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.UserProvider;
import com.bill.rss.domain.User;
import com.bill.rss.mongodb.UserRetriever;

public class UserRegistrationRoute extends BaseRoute {

	private final UserProvider userProvider = new UserRetriever();

	private final ObjectMapper mapper = new ObjectMapper();

	protected UserRegistrationRoute(String path) {
		super(path);
	}

	@Override
	public Object handle(Request request, Response response) {
		User user = parseInput(request);
		checkIfUserNameExists(user);
		createNewUser(user);
		response.type(JSON_RESPONSE_TYPE);
		return "{}";
	}

	private User parseInput(Request request) {
		User user = new User();
		try {
			user = mapper.readValue(request.body() , User.class);
		} catch (Exception e) {
			halt(400 , "Invalid Input");
		}
		return user;
	}

	private void checkIfUserNameExists(User user) {
		if (userProvider.checkIfUserNameExists(user.getUserName())) {
			halt(403, "Username already exists");
		}
	}

	private void createNewUser(User user) {
		try {
			userProvider.createNewUser(user);
		}
		catch (Exception e) {
			halt(500, "Internal Error");
		}
	}
}
