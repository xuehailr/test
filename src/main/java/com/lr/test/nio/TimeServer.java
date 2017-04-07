package com.lr.test.nio;

/**
 * Created by Administrator on 2017/4/6.
 */
public class TimeServer {
    public static void main(String[] args) {
        MultiplexerTimeServer server = new MultiplexerTimeServer(8080);
        new Thread(server,"NIO-MultiplexerTimeServer-001").start();
    }
}
