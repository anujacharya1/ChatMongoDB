package com.anuj.monsub;

import android.os.AsyncTask;
import android.util.Log;

import com.anuj.chatmongodb.ClientObject;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.Bytes;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * Created by anujacharya on 3/11/16.
 */
public class MonSubImpl<T> implements MonSub<T> {


    private static String SESS1;
    private static String SESS2;

    private  static String DATABASE;
    private static String COLLECTION_NAME;
    MongoClient mongoClient; //singleton
    DBCollection coll;
    private MonSubNotification monSubNotification;

    @Override
    public MonSub register(String sess1, String sess2, String database, String URI) {
        if(sess1==null || sess2==null || database==null || URI==null
                || sess1.isEmpty() || sess2.isEmpty() || database.isEmpty() || URI.isEmpty()){
            throw MonSubException.build(1, "check register parameter");
        }
        SESS1 = sess1;
        SESS2 = sess2;
        DATABASE = database;
        COLLECTION_NAME = SESS1+"_"+SESS2;
        //         MongoClientURI uri = new MongoClientURI( "mongodb://anuj:anuj@ds011298.mlab.com:11298/heroku_3c1g35n4");

        MonSubClientFactory monSubClient = MonSubClientFactory.getSingletonFactory(URI);

        // this will be always singleton
        mongoClient = monSubClient.getMongoClientURI(URI);

        return this;
    }


    private class CollectionAsycTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            boolean collectionExists = mongoClient.getDB(DATABASE).collectionExists(COLLECTION_NAME);
            if (collectionExists == false) {
                Log.i("INFO", "collection does not exist going to create one");
                DBObject options = BasicDBObjectBuilder.start().add("capped", true).add("size", 2000000000l).get();
                coll = mongoClient.getDB(DATABASE).createCollection(COLLECTION_NAME, options);
            }
            else{
                coll = mongoClient.getDB(DATABASE).getCollection(COLLECTION_NAME);
            }

            return collectionExists;
        }

        @Override
        protected void onPostExecute(Boolean collectionExists) {
            if (collectionExists == false) {
                // going to insert the BEGIN text as without this cursor will throw error
                // you need to have one element
                Log.i("INFO", "collection created collectionName=" + coll.getName());
                return;
            }
            else{
                Log.i("INFO", "collection already exist collectionName=" + coll.getName());
                startTheCursonOnCollection();
            }
        }
    }

    private void startTheCursonOnCollection(){

        final DBCursor cur = coll.find().sort(BasicDBObjectBuilder.start("$natural", 1).get())
                .addOption(Bytes.QUERYOPTION_TAILABLE | Bytes.QUERYOPTION_AWAITDATA);

        System.out.println("== open cursor ==");

        Runnable task = new Runnable() {
            @Override
            public void run() {
                System.out.println("Waiting for events");
                while (cur.hasNext()) {
                    DBObject obj = cur.next();
                    new PollingAsyncTask<T>().execute(obj);
                }
            }
        };
        new Thread(task).start();
    }
    private class PollingAsyncTask<T> extends AsyncTask<Object, Void, DBObject> {

        protected DBObject doInBackground(Object... dbObjects) {
            Log.i("INFO", "PollingAsyncTask#####################");
            System.out.println(dbObjects[0]);
            DBObject dbObject = (DBObject)dbObjects[0];
            return dbObject;
        }

        protected void onPostExecute(DBObject dbObject) {
            // notify the client about the message
            // client must implement this

            Object session1Obj = dbObject.get(SESS1);
            Object session2Obj = dbObject.get(SESS2);

            if(session1Obj!=null){
                Log.i("INFO", "PollingAsyncTask sess1Obj");
//                ClientObject sess1Result = MongoSubUtil.convertJSONToPojo(session1Obj.toString());
                monSubNotification.msgFromSess1(session1Obj.toString());
            }
            else if (session2Obj!=null){
                Log.i("INFO", "PollingAsyncTask sess2Obj");
                monSubNotification.msgFromSess2(session2Obj.toString());
            }

            Log.e("ERROR", "PollingAsyncTask does not return any message");

        }
    }

    /**
     *
     * String[0] text
     * String[1] do polling Y/N (String type)
     *
     */
    private class InsertAsyncTask<T> extends AsyncTask<T, Void, Void>{

        @Override
        protected Void doInBackground(T... params) {
            BasicDBObject document = new BasicDBObject();
            document.put(SESS1, params[0]);
            coll.insert(document);
            Log.i("INFO", "insert complete");

            // do the polling on the collection
            startTheCursonOnCollection();

            return null;
        }
    }

    @Override
    public boolean check(String sess1, String sess2) {
        return mongoClient.getDB(DATABASE).collectionExists(sess1+"_"+sess2);
    }

    @Override
    public void send(T msg) {
        if(coll == null){
            // if at this point the coll is null there is something went wrong
            Log.e("ERROR", "SOMETHING WRONG");
            return;
        }

        // insert in background
        // send "N" because by this time the curson will be already on
        // TODO: Investigate for any corner cases
        new InsertAsyncTask().execute(msg);
    }



    @Override
    public void open(MonSubNotification monSubNotification) {
        // this will create collection if not exist
        // and start the back ground thread
        setMonSubNotification(monSubNotification);
        new CollectionAsycTask().execute();
    }

    @Override
    public void close() {
        // TODO: Kill all the connection and threads
    }

    public void setMonSubNotification(MonSubNotification monSubNotification){
        this.monSubNotification = monSubNotification;
    }
}
