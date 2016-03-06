package com.anuj.chatmongodb;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.Bytes;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    /*
        Risk: On client username/password is exposed
                https://github.com/matfur92/mongo-java-driver used for SSL from client to heroku

        Scenario where session 1 started chatting with session 2
        Database Name : heroku_73gl73n7
        Collection Name: seesion1_session2 (This is because the capped collection does not have
                                            delete document inside it)

        start storing the records in this key




     */

    private static String SESS1 = "sess1";
    private static String SESS2 = "sess2";

    private static String collectionName = SESS1+"_"+SESS2;

    List<String> items = new ArrayList<>();
    ArrayAdapter<String> itemsAdapter;
    EditText etMsg;
    Button btnSend;
    DBCollection coll;
    MongoClient mongoClient;

    private static final String MONGO_HOST_IP = "ec2-52-8-69-36.us-west-1.compute.amazonaws.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MongoClientURI uri = new MongoClientURI( "mongodb://anuj:anuj@ds019058.mlab.com:19058/heroku_73gl73n7");
        mongoClient = new MongoClient(uri);

        //this will create the collection if not exist
        // and start the backgroung polling thread to keep on polling new entries
        new CollectionAsycTask().execute();

        etMsg = (EditText) findViewById(R.id.etMsg);
        btnSend = (Button) findViewById(R.id.btnSend);

        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

        ListView listView = (ListView) findViewById(R.id.lvMsg);
        listView.setAdapter(itemsAdapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert in mongo
                insertInMongo();
            }
        });
    }

    private void insertInMongo(){
        String txt = etMsg.getText().toString();

        if(coll == null){
            // if at this point the coll is null there is something went wrong
            Log.e("ERROR", "SOMETHING WRONG");
            return;
        }

        // insert in background
        // send "N" because by this time the curson will be already on
        // TODO: Investigate for any corner cases
        new InsertAsyncTask().execute(txt, "N");
    }

    /**
     *
     * String[0] text
     * String[1] do polling Y/N
     *
     */
    private class InsertAsyncTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            BasicDBObject document = new BasicDBObject();
            document.put(SESS1, params[0]);
            coll.insert(document);
            Log.i("INFO", "insert complete");

            if(params[1]!=null && params[1].equalsIgnoreCase("Y")){
                // do the polling as the collection was not existing and this was called by
                // CollectionAsycTask post process flow
                startTheCursonOnCollection();
            }

            return null;
        }
    }

    private class CollectionAsycTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {

            boolean collectionExists = mongoClient.getDB("heroku_73gl73n7").collectionExists(collectionName);
            if (collectionExists == false) {
                Log.i("INFO", "collection does not exist going to create one");
                DBObject options = BasicDBObjectBuilder.start().add("capped", true).add("size", 2000000000l).get();
                coll = mongoClient.getDB("heroku_73gl73n7").createCollection(collectionName, options);
            }
            else{
                coll = mongoClient.getDB("heroku_73gl73n7").getCollection(collectionName);
            }

            return collectionExists;
        }

        @Override
        protected void onPostExecute(Boolean collectionExists) {
            if (collectionExists == false) {
                // going to insert the BEGIN text as without this cursor will throw error
                // you need to have one element
                Log.i("INFO", "collection created collectionName=" + coll.getName());
                new InsertAsyncTask().execute("LETS THE FUN START", "Y");
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
                    new PollingAsyncTask().execute(obj);
                }
            }
        };
        new Thread(task).start();
    }
    private class PollingAsyncTask extends AsyncTask<DBObject, Void, String> {

        protected String doInBackground(DBObject... dbObjects) {
                Log.i("INFO", "PollingAsyncTask#####################");
                System.out.println(dbObjects[0]);
                String msg = (String) dbObjects[0].get("sess1");
                return msg;
        }

        protected void onPostExecute(String result) {
            // This method is executed in the UIThread
            // with access to the result of the long running task
//            chatScreen.setText(result);
            // Hide the progress bar
            itemsAdapter.add(result);
            itemsAdapter.notifyDataSetChanged();
        }
    }

}
