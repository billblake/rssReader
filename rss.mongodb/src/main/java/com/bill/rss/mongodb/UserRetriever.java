package com.bill.rss.mongodb;

import org.bson.types.ObjectId;

import com.bill.rss.dataProvider.UserProvider;
import com.bill.rss.domain.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

public class UserRetriever implements UserProvider {

	public boolean checkIfUserNameExists(String userName) {
		DB rssDb = MongoDBConnection.getDbConnection();
		DBCollection userCollection = rssDb.getCollection("user");
		
		DBObject userNameQuery = new BasicDBObject();
		userNameQuery.put("userName", userName);
		DBCursor queryResults = userCollection.find(userNameQuery);
		return queryResults.hasNext();
	}

	public void createNewUser(User user) {
		DB rssDb = MongoDBConnection.getDbConnection();
		DBCollection userCollection = rssDb.getCollection("user");
		
		BasicDBObject userDocument = new BasicDBObject();
    	userDocument.put("firstName", user.getFirstName());
    	userDocument.put("lastName", user.getLastName());
    	userDocument.put("userName", user.getUserName());
    	userDocument.put("password", user.getPassword());
		userCollection.insert(userDocument, WriteConcern.SAFE);
	}

}
