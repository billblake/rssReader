package com.bill.rss.mongodb;

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
		Mongo conn;
		String uriString = "mongodb://billblake:bill6551@kahana.mongohq.com:10060/reader";
		MongoURI uri = new MongoURI(uriString);
	    try {
	    	conn = uri.connect();
//	      conn = new Mongo("localhost", 27017);
	    } catch (Exception e) {
	      throw new RuntimeException(e);
	    }

	    WriteConcern w = new WriteConcern( 1, 2000 );
	    conn.setWriteConcern( w );

	    dbConnection = conn.getDB(RSS_READER_DB);
	    return dbConnection;
	}
}
