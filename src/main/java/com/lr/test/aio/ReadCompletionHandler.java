package com.lr.test.aio;

import org.apache.coyote.http11.Constants;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by Administrator on 2017/4/8.
 */
public class ReadCompletionHandler implements CompletionHandler<Integer,ByteBuffer> {
    private AsynchronousSocketChannel asynchronousSocketChannel;
    public ReadCompletionHandler(AsynchronousSocketChannel channel){
        this.asynchronousSocketChannel = channel;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);
        try {
            String req = new String(body,"UTF-8");
            System.out.println("the time server receive order : "+req);
            doWrite(System.currentTimeMillis()+"");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void doWrite(String response){
        final ByteBuffer headerBuffer = ByteBuffer.allocate(10000);

       /* if(response!=null && response.trim().length()>0){
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer);
        }*/
        this.writeHeadLine(headerBuffer);
        this.writeHeader(headerBuffer,"Content-Length",response.getBytes().length+"");
//        this.writeHeader(headerBuffer,"Content-Length",15+"");
        this.writeHeader(headerBuffer,"Content-Type","text/html; charset=utf-8");

        headerBuffer.put((byte)13).put((byte)10);
        headerBuffer.put(response.getBytes());
        headerBuffer.flip();
        asynchronousSocketChannel.write(headerBuffer, headerBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if(headerBuffer.hasRemaining()){
                    asynchronousSocketChannel.write(headerBuffer,headerBuffer,this);
                }else{
//                    try {
//                        asynchronousSocketChannel.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                exc.printStackTrace();
                try {
                    asynchronousSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void writeHeader(ByteBuffer byteBuffer , String name,String value){
        byteBuffer.put(name.getBytes());
        byteBuffer.put((byte)58).put((byte)32);
        byteBuffer.put(value.getBytes());
        byteBuffer.put((byte)13).put((byte)10);
    }
    private void writeHeadLine(ByteBuffer headerBuffer){
        headerBuffer.put(Constants.HTTP_11_BYTES);
        headerBuffer.put((byte)32);
        headerBuffer.put(Constants._200_BYTES);
        headerBuffer.put((byte)32);
        headerBuffer.put("ok".getBytes());
        headerBuffer.put((byte) 13).put((byte)10);
    }
    private void doWrite(String s,String s2){
        if(s!=null && s.trim().length() > 0){
            byte[] bytes = s.getBytes();
            final ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
            byteBuffer.put(bytes);
            byteBuffer.flip();
            asynchronousSocketChannel.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    if(byteBuffer.hasRemaining()){
                        asynchronousSocketChannel.write(byteBuffer,byteBuffer,this);
                    }else {
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                    try {
                        asynchronousSocketChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        exc.printStackTrace();
        try {
            asynchronousSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
