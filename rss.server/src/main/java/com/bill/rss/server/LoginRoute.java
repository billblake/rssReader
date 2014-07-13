package com.bill.rss.server;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.UserProvider;
import com.bill.rss.domain.User;
import com.bill.rss.mongodb.UserRetriever;

public class LoginRoute extends BaseRoute {

    private final ObjectMapper mapper = new ObjectMapper();

    private final UserProvider userProvider = new UserRetriever();

    protected LoginRoute(String path) {
        super(path);
    }

    @Override
    public Object handle(Request request, Response response) {
        User user = extractUserDetailsFromRequest(request);
        validateUserInput(user);
        encryptPassword(user);
        user = validateUser(user);
        createCookies(response, user);
        addUserDetailsToSession(request, user);
        return "{}";
    }


    private User extractUserDetailsFromRequest(Request request) {
        User user = null;
        try {
            user = mapper.readValue(request.body(), User.class);
        } catch (Exception e) {
            halt(401, "Invalid Input");
        }
        return user;
    }


    private void validateUserInput(User user) {
        if (StringUtils.isBlank(user.getUserName()) || StringUtils.isBlank(user.getPassword())) {
            halt(401, "Missing username/password");
        }
    }


    private void encryptPassword(User user) {
        user.setPassword(SecurityUtils.encrypt(user.getPassword()));
    }


    private User validateUser(User user) {
        try {
            user = userProvider.validateUser(user);
        } catch (Exception e) {
            halt(401, "Invalid username/password");
        }
        return user;
    }


    private void createCookies(Response response, User user) {
        response.cookie("loggedIn", "true", 10800);
        response.cookie("user", user.getFirstName() + " " + user.getLastName(), 10800);
    }


    private void addUserDetailsToSession(Request request, User user) {
        request.session().attribute("user", user);
    }
}
