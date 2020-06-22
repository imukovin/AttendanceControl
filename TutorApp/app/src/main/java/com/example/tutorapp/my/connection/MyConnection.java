package com.example.tutorapp.my.connection;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import io.netty.util.CharsetUtil;

public class MyConnection extends AsyncTask<String, Integer, String> {
    private String serverAddress;
    private int port;
    private JSONObject message;
    private String response = "Ne polucheno";
    public boolean isAnswer = false;

    public MyConnection(String serverAddress, int port, JSONObject message) {
        this.serverAddress = serverAddress;
        this.port = port;
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(serverAddress, port));
            // Write buffer to socket
            ByteBuffer buf = ByteBuffer.allocate(10000);
            buf.put((byte) 0x0);
            buf.put((message.toString() + "\r\n").getBytes(CharsetUtil.UTF_8));
            buf.flip();
            while (buf.hasRemaining()) {
                socketChannel.write(buf);
            }
            // Read
            buf = ByteBuffer.allocate(10000);
            buf.flip();
            buf.clear();
            while (true) {
                int n = socketChannel.read(buf);
                if (n < 0) {
                    break;
                }
                response = new String(buf.array());
                buf.clear();
            }
            socketChannel.close();
            isAnswer = true;
            System.out.println("Channel closed!  " + response);
        } catch (IOException e) {
            e.printStackTrace();
            isAnswer = false;
            response = "Сервер не отвечает! Попробуйте позже!";
        }
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        // здесь обработка результата из предыдущего метода
    }
}
