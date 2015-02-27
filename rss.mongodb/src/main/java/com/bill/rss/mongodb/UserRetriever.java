package com.bill.rss.mongodb;

import com.bill.rss.dataProvider.UserProvider;
import com.bill.rss.domain.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;

public class UserRetriever implements UserProvider {

    private static final String USER_COLLECTION = "user";
    private static final String PASSWORD_FIELD = "password";
    private static final String USER_NAME_FIELD = "username";
    private static final String LAST_NAME_FIELD = "lastName";
    private static final String FIRST_NAME_FIELD = "firstName";

    public boolean checkIfUserNameExists(String userName) {
        DB rssDb = MongoDBConnection.getDbConnection();
        DBCollection userCollection = rssDb.getCollection(USER_COLLECTION);

        DBObject userNameQuery = new BasicDBObject();
        userNameQuery.put(USER_NAME_FIELD, userName);
        DBCursor queryResults = userCollection.find(userNameQuery);
        return queryResults.hasNext();
    }

    public void createNewUser(User user) {
        DB rssDb = MongoDBConnection.getDbConnection();
        DBCollection userCollection = rssDb.getCollection(USER_COLLECTION);

        BasicDBObject userDocument = new BasicDBObject();
        userDocument.put(FIRST_NAME_FIELD, user.getFirstName());
        userDocument.put(LAST_NAME_FIELD, user.getLastName());
        userDocument.put(USER_NAME_FIELD, user.getUserName());
        userDocument.put(PASSWORD_FIELD, user.getPassword());
        userCollection.insert(userDocument, WriteConcern.SAFE);
    }

    public User validateUser(User user) {
        DB rssDb = MongoDBConnection.getDbConnection();
        DBCollection userCollection = rssDb.getCollection(USER_COLLECTION);

        DBObject userNameQuery = new BasicDBObject();
        userNameQuery.put(USER_NAME_FIELD, user.getUserName());
        userNameQuery.put(PASSWORD_FIELD, user.getPassword());
        DBObject queryResults = userCollection.findOne(userNameQuery);

        if (queryResults == null) {
            throw new RuntimeException("Invalid username/password");
        }

        user.setFirstName(queryResults.get(FIRST_NAME_FIELD).toString());
        user.setLastName(queryResults.get(LAST_NAME_FIELD).toString());

        return user;
    }

}
