package com.anuj.monsub;

/**
 * Created by anujacharya on 3/13/16.
 */
public interface MonSubNotification<T> {
    void msgFromSess1(T message);
    void msgFromSess2(T message);

}
