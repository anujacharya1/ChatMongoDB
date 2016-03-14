package com.anuj.monsub;

import com.anuj.chatmongodb.ClientObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by anujacharya on 3/13/16.
 */
public class MongoSubUtil{

    public static <T> T convertJSONToPojo(String json){

        Type type = new TypeToken<T>(){}.getType();
        return new Gson().fromJson(json, type);
    }

//    public static ClientObject convertJSONToPojo(String json){
//        Type type = new TypeToken< ClientObject >(){}.getType();
//        return new Gson().fromJson(json, type);
//    }
}
