package com.anuj.chatmongodb;

import com.mongodb.BasicDBObject;

/**
 * Created by anujacharya on 3/13/16.
 */
public class ClientObject extends BasicDBObject {


    public ClientObject() {
    }

    public String getMsg() {
        return (String)super.get("msg");
    }

    public void setMsg(String msg) {
        super.put("msg", msg);
    }
}
