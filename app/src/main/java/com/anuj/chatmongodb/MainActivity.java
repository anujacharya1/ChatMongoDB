package com.anuj.chatmongodb;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.anuj.monsub.MonSub;
import com.anuj.monsub.MonSubImpl;
import com.anuj.monsub.MonSubNotification;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

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
    private  static String DATABASE = "heroku_3c1g35n4";
    private static String MONGODB_URI = "mongodb://anuj:anuj@ds011298.mlab.com:11298/heroku_3c1g35n4";
    ArrayList<ClientObject> clientObjects = new ArrayList<>();
    private ChatAdapter chatAdapter;
    EditText etMsg;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MonSub<ClientObject> monSub = new MonSubImpl<>();
        monSub.register(SESS1, SESS2, DATABASE, MONGODB_URI);

        etMsg = (EditText) findViewById(R.id.etMsg);
        btnSend = (Button) findViewById(R.id.btnSend);

        chatAdapter = new ChatAdapter(this, clientObjects);

//        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
//
        ListView listView = (ListView) findViewById(R.id.lvMsg);
        listView.setAdapter(chatAdapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert in mongo
                String txt = etMsg.getText().toString();
                ClientObject clientObject = new ClientObject();
                clientObject.setMsg(txt);
                monSub.send(clientObject);
            }
        });

        monSub.open(new MonSubNotification<String>() {
            @Override
            public void msgFromSess1(String message) {
                // handle messaged from sess1
                Log.i("INFO", "msgFromSess1 " + message);

                ClientObject clientObject = new Gson().fromJson(message, ClientObject.class);
                chatAdapter.add(clientObject);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void msgFromSess2(String message) {
                // handle messaged from sess2
                Log.i("INFO", "msgFromSess2 "+message);
                ClientObject clientObject = new Gson().fromJson(message, ClientObject.class);
                chatAdapter.add(clientObject);
                chatAdapter.notifyDataSetChanged();
            }
        });
    }
}
