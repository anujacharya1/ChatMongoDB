package com.anuj.monsub;

/**
 * Created by anujacharya on 3/10/16.
 */
public interface MonSub {

    /**
     * This will register the connection
     * @param sess1 session1 this can be sender or receiver
     * @param sess2 session2 this can be sender or receiver
     * @param database database name of mongo
     * @return
     */
    MonSub register(String sess1, String sess2, String database, String URI);

    /**
     * Check to see if the session alread exist
     * @param sess1
     * @param sess2
     * @return
     */
    boolean check(String sess1, String sess2);

    /**
     * Send the message
     * @param msg
     */
    void send(String msg);

    /**
     *
     * @param monSubNotification this is required to receive the notification back
     */
    void open(MonSubImpl.MonSubNotification monSubNotification);


    void close();
}
