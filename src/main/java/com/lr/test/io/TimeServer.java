package com.lr.test.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Administrator on 2017/4/6.
 */
public class TimeServer {
    public static void main(String[] args) throws IOException {
        ServerSocket server = null;
        try {
            server = new ServerSocket(8080);
            while (true){
                Socket socket = server.accept();
                new Thread(new TimeServerHander(socket)).start();
            }
        }finally {
            if(server!=null){
                server.close();
                server = null;
            }
        }
    }
}
