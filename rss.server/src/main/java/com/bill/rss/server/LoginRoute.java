package com.bill.rss.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.bill.rss.dataProvider.UserProvider;
import com.bill.rss.domain.User;
import com.bill.rss.mongodb.UserRetriever;

import spark.Request;
import spark.Response;
import spark.Route;

public class LoginRoute extends Route {

    private ObjectMapper mapper = new ObjectMapper();

    private UserProvider userProvider = new UserRetriever();

    protected LoginRoute(String path) {
        super(path);
    }

    @Override
    public Object handle(Request request, Response response) {
        User user = null;
        try {
            user = mapper.readValue(request.body(), User.class);
        } catch (Exception e) {
            halt(401, "Invalid Input");
        }

        if (StringUtils.isBlank(user.getUserName()) || StringUtils.isBlank(user.getPassword())) {
            halt(401, "Missing username/password");
        }
        user.setPassword(EncryptionUtils.encrypt(user.getPassword()));

        try {
            user = userProvider.validateUser(user);
        } catch (Exception e) {
            halt(401, "Invalid username/password");
        }

        response.cookie("loggedIn", "true", 10800);
        response.cookie("user", user.getFirstName() + " " + user.getLastName(), 10800);

        request.session().attribute("user", user);

        return request.body();
    }

}
