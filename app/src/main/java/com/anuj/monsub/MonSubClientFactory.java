package com.anuj.monsub;

/**
 * Created by anujacharya on 3/11/16.
 */

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * This is wrapper around the MongoClientURI
 * Singleton
 */
public class MonSubClientFactory {

    static private MonSubClientFactory singletonFactory;

    private MongoClient mongoClient;

    private MonSubClientFactory(){
    }

    private MonSubClientFactory(String uri) {
        MongoClientURI mongoClientURI =  new MongoClientURI(uri);
        mongoClient = new MongoClient(mongoClientURI);

    }

    /**
     * Singleton Getter
     */
    public static synchronized MonSubClientFactory getSingletonFactory(String uri) {
        if (singletonFactory == null) {
            singletonFactory = new MonSubClientFactory(uri);
        }
        return singletonFactory;
    }

    public MongoClient getMongoClientURI(String uri) {
        if (this.mongoClient == null){
            synchronized(this) {
                if ( this.mongoClient == null ) {
                    MongoClientURI mongoClientURI = new MongoClientURI(uri);
                    mongoClient = new MongoClient(mongoClientURI);

                }
            }
        }
        return this.mongoClient;
    }
}
