package com.lr.test.aio;

/**
 * Created by Administrator on 2017/4/8.
 */
public class TimeServer {
    public static void main(String[] args) {
        AsyncTimeServerHandler asyncTimeServerHandler = new AsyncTimeServerHandler(8080);
        new Thread(asyncTimeServerHandler,"aio time server thread ").start();
    }
}
