package com.lr.test.nio;

import org.apache.coyote.http11.Constants;
import org.apache.tomcat.util.buf.MessageBytes;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Administrator on 2017/4/6.
 */
public class MultiplexerTimeServer implements Runnable{
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private volatile boolean stop;
    public MultiplexerTimeServer(int port){
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port),1024);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("server start in port "+port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
    @Override
    public void run() {
        while (!stop){
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey key = null;
                while (iterator.hasNext()){
                    key = iterator.next();
                    iterator.remove();
                    handleInput(key);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void stop(){
        this.stop = true;
    }
    private void handleInput(SelectionKey key) throws IOException {
        if(key.isValid()){
            if(key.isAcceptable()){
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(selector,SelectionKey.OP_READ);
            }
            if (key.isReadable()){
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if(readBytes>0){
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes,"UTF-8");
                    System.out.println(body);
                    doWrite(sc,System.currentTimeMillis()+"");
                }
            }
        }
    }
    private void doWrite(SocketChannel channel,String response) throws IOException {
        ByteBuffer headerBuffer = ByteBuffer.allocate(10000);
        headerBuffer.put(Constants.HTTP_11_BYTES);
        headerBuffer.put((byte)32);
        headerBuffer.put(Constants._200_BYTES);
        headerBuffer.put((byte)32);
        headerBuffer.put("ok".getBytes());
        headerBuffer.put((byte) 13).put((byte)10);
       /* if(response!=null && response.trim().length()>0){
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer);
        }*/
        headerBuffer.put("Content-Length".getBytes());
        headerBuffer.put((byte)58).put((byte)32);
        headerBuffer.put(String.valueOf(response.getBytes().length).getBytes());
        headerBuffer.put((byte)13).put((byte)10);
        headerBuffer.put(response.getBytes());
        headerBuffer.put((byte) 13).put((byte)10);
        headerBuffer.flip();
        channel.write(headerBuffer);
        channel.socket().close();
    }
}
