package com.bill.rss.mongodb;

import java.net.UnknownHostException;
import java.util.Map;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;
import com.mongodb.WriteConcern;


public class MongoDBConnection {

    private static final String MONGODB_USERNAME = "MONGODB_USERNAME";
    private static final String MONGODB_PASSWORD = "MONGODB_PASSWORD";
    private static final String MONGODB_HOSTNAME = "MONGODB_HOSTNAME";
    private static final String MONGODB_PORT = "MONGODB_PORT";
    private static final String MONGODB_DB_NAME = "MONGODB_DB_NAME";
    private static final String MONGODB_CONNECTION_STRING = "mongodb://%s:%s@%s:%s/%s";

	private static DB dbConnection;


	public static DB getDbConnection() {
		if (dbConnection != null) {
			return dbConnection;
		}
		return createNewDbConnection();
	}


	static void setDbConnection(DB db) {
	    dbConnection = db;
    }

	private static DB createNewDbConnection() {
	    String dbUser = readEnvironmentVariable(MONGODB_USERNAME);
        String dbpassword = readEnvironmentVariable(MONGODB_PASSWORD);
        String dbHostname = readEnvironmentVariable(MONGODB_HOSTNAME);
        String dbPort = readEnvironmentVariable(MONGODB_PORT);
        String dbName = readEnvironmentVariable(MONGODB_DB_NAME);

        String uriString = String.format(MONGODB_CONNECTION_STRING, dbUser, dbpassword, dbHostname, dbPort, dbName);
	    MongoURI uri = new MongoURI(uriString);
    	try {
    	    Mongo conn = uri.connect();
//  	          conn = new Mongo("localhost", 27017);
            WriteConcern w = new WriteConcern( 1, 2000 );
            conn.setWriteConcern( w );
            return conn.getDB(dbName);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
	}


    private static String readEnvironmentVariable(String environmentVariableName) {
        Map<String, String> env = System.getenv();
        String environmentVariable = env.get(environmentVariableName);
        if (environmentVariable == null) {
            environmentVariable = "";
        }
        return environmentVariable;
    }
}
