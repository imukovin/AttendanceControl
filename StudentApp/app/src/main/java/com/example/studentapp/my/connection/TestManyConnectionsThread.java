package com.example.studentapp.my.connection;

import android.app.Activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class TestManyConnectionsThread implements Runnable {
    Activity programActivity;
    private volatile int numOfThread;
    private int tryConnectCount;
    private int timeOut;

    public TestManyConnectionsThread(Activity programActivity, int numOfThread) {
        this.programActivity = programActivity;
        this.numOfThread = numOfThread;
        timeOut = 1000;
    }

    @Override
    public void run() {
        int serverPort = 8080;
        String serverAdress = "192.168.43.1";
        String serverAnswer = "";
        int timeout = (int) (Math.random() * timeOut);
        while (!serverAnswer.equals("true")) {
            try {
                tryConnectCount++;
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(serverAdress, serverPort), 10);
                PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
                // отправка на сервер
                String mess = "Thread" + numOfThread + " TryConnectCount: " + tryConnectCount;
                toServer.println(mess);
                System.out.println("=========> " + mess);
                BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                serverAnswer = fromServer.readLine();
                //System.out.println("=========> " + serverAnswer);
                fromServer.close();
                toServer.close();
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            } catch (IOException e) {
                try {
                    if (e instanceof SocketTimeoutException) {
                        throw new SocketTimeoutException();
                    } else {
                        e.printStackTrace();
                    }
                } catch (SocketTimeoutException ste) {
                    System.out.println("Turn off the client by timeout " + numOfThread);
                }
            }
            try {
                Thread.sleep(timeout);
                timeout = timeout * 2;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*if (tryConnectCount > 10) {
                System.out.println("Thread" + numOfThread + " - can't connect! Try: " + tryConnectCount + " LastTimeout: " + timeout);
            }*/
        }
    }
}
