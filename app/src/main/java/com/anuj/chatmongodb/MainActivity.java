package com.anuj.chatmongodb;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.Bytes;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;


public class MainActivity extends AppCompatActivity {

    TextView chatScreen;

    private static final String MONGO_HOST_IP = "ec2-52-8-69-36.us-west-1.compute.amazonaws.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatScreen = (TextView) findViewById(R.id.tvMsg);
        pollMongo();
    }

    private void pollMongo(){
        MongoClientURI uri = new MongoClientURI( "mongodb://anuj:anuj@ds019058.mlab.com:19058/heroku_73gl73n7");
        MongoClient mongoClient = new MongoClient(uri);
        DBCollection coll = mongoClient.getDB("heroku_73gl73n7").getCollection("chat");

        final DBCursor cur = coll.find().sort(BasicDBObjectBuilder.start("$natural", 1).get())
                .addOption(Bytes.QUERYOPTION_TAILABLE | Bytes.QUERYOPTION_AWAITDATA);

        System.out.println("== open cursor ==");

        Runnable task = new Runnable() {
            @Override
            public void run() {
                System.out.println("Waiting for events");
                while (cur.hasNext()) {
                    DBObject obj = cur.next();
                    new MyAsyncTask().execute(obj);
                }
            }
        };
        new Thread(task).start();
    }

    private class MyAsyncTask extends AsyncTask<DBObject, Void, String> {

        protected String doInBackground(DBObject... dbObjects) {

                System.out.println(dbObjects[0]);
                String msg = (String) dbObjects[0].get("msg");
                return msg;

        }

        protected void onPostExecute(String result) {
            // This method is executed in the UIThread
            // with access to the result of the long running task
            chatScreen.setText(result);
            // Hide the progress bar
        }
    }

}
