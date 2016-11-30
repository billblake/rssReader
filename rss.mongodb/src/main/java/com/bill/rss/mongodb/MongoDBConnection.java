package com.bill.rss.mongodb;

import java.util.Map;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

import static java.util.Arrays.asList;


public class MongoDBConnection {

    private static final String MONGODB_USERNAME = "MONGODB_USERNAME";
    private static final String MONGODB_PASSWORD = "MONGODB_PASSWORD";
    private static final String MONGODB_HOSTNAME = "MONGODB_HOSTNAME";
    private static final String MONGODB_PORT = "MONGODB_PORT";
    private static final String MONGODB_DB_NAME = "MONGODB_DB_NAME";


    private static MongoClient mongoClient;

    public static DB getDbConnection() {
        return getMongoClient().getDB(readEnvironmentVariable(MONGODB_DB_NAME));
    }


    static void setMongoClient(MongoClient _mongoClient) {
        mongoClient = _mongoClient;
    }


    private static MongoClient getMongoClient() {
        if (mongoClient != null) {
            return mongoClient;
        }
        String dbUser = readEnvironmentVariable(MONGODB_USERNAME);
        char[] dbpassword = readEnvironmentVariable(MONGODB_PASSWORD).toCharArray();
        String dbHostname = readEnvironmentVariable(MONGODB_HOSTNAME);
        int dbPort = Integer.parseInt(readEnvironmentVariable(MONGODB_PORT));
        String dbName = readEnvironmentVariable(MONGODB_DB_NAME);
        MongoCredential credential = MongoCredential.createCredential(dbUser, dbName, dbpassword);
        ServerAddress serverAddress = new ServerAddress(dbHostname, dbPort);
        MongoClient mongoClient = new MongoClient(serverAddress, asList(credential));
        WriteConcern writeConcern = new WriteConcern( 1, 2000 );
        mongoClient.setWriteConcern(writeConcern);
        return mongoClient;
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
