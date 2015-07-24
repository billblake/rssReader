package com.bill.rss.mongodb;

import java.util.Map;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;
import com.mongodb.WriteConcern;

public class MongoDBConnection {

    private static final String RSS_READER_DB = "reader";
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
	    String uriString = "";
	    try {
		    Mongo conn;
		    Map<String, String> env = System.getenv();
            String dbpassword = env.get("dbPassword");
		    uriString = "mongodb://billblake:" + dbpassword + "@kahana.mongohq.com:10060/reader";
		    MongoURI uri = new MongoURI(uriString);

	    	conn = uri.connect();
//	          conn = new Mongo("localhost", 27017);
	        WriteConcern w = new WriteConcern( 1, 2000 );
	        conn.setWriteConcern( w );
	        dbConnection = conn.getDB(RSS_READER_DB);
	    } catch (Exception e) {
	        throw new RuntimeException(uriString);
	    }
	    return dbConnection;
	}
}
