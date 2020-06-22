package com.example.tutorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tutorapp.helpers.Helper;
import com.example.tutorapp.helpers.TheApplication;
import com.example.tutorapp.helpers.Translit;
import com.example.tutorapp.my.connection.MyConnection;
import com.example.tutorapp.my.hotspot.ClientScanResult;
import com.example.tutorapp.my.hotspot.FinishScanListener;
import com.example.tutorapp.my.hotspot.WifiApManager;
import com.example.tutorapp.my.jsonparsers.SendCameUsersJson;
import com.example.tutorapp.my.objects.Event;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SubjectActivity extends AppCompatActivity {
    public final String SERVER_ADDRESS = Helper.getConfigValue(TheApplication.getInstance().getApplicationContext(),"server_address");
    public final String SERVER_PORT = Helper.getConfigValue(TheApplication.getInstance().getApplicationContext(),"server_port");

    public TextView infoHotspot;
    private TextView listRV;
    private Button onHotSpot;
    private Button offHotSpot;

    private Event event;
    private WifiApManager wifiApManager;
    private WifiConfiguration wifiConf;
    private Thread connectionThread = null;
    private ArrayList<String> connectedStudents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        event = (Event) getIntent().getSerializableExtra("event");

        listRV = findViewById(R.id.list_rv);
        TextView eventName = findViewById(R.id.event_name_tv);
        eventName.setText(event.getName());
        infoHotspot = findViewById(R.id.hotspotInfoTextView);
        listRV.setMovementMethod(new ScrollingMovementMethod());

        onHotSpot = findViewById(R.id.onHotSpot);
        offHotSpot = findViewById(R.id.offHotSpot);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            onHotSpot.setText("Start server");
            offHotSpot.setText("Stop server");
        }
        offHotSpot.setVisibility(View.INVISIBLE);

        // настройка конфигурации точки доступа
        wifiApManager = new WifiApManager(this);
        wifiConf = new WifiConfiguration();
        wifiConf.SSID = Translit.cyr2lat(event.getName()).replace(" ", "-"); // name hotspot
        wifiConf.preSharedKey = "12345678"; // password hotspot
        wifiConf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wifiConf.allowedKeyManagement.set(4);

        onHotSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                    // установка конфигурации и включение точки доступа
                    wifiApManager.setWifiApConfiguration(wifiConf);
                    if (wifiApManager.setWifiApEnabled(wifiConf, true));
                    String result = Boolean.toString(wifiApManager.isWifiApEnabled());
                    infoHotspot.setText(result);
                }
                onHotSpot.setVisibility(View.INVISIBLE);
                offHotSpot.setVisibility(View.VISIBLE);
                try {
                    TimeUnit.SECONDS.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startServerSocket();
            }
        });

        offHotSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                    // выключение точки доступа
                    wifiApManager.setWifiApEnabled(wifiConf, false);
                    String result = Boolean.toString(wifiApManager.isWifiApEnabled());
                    infoHotspot.setText(result);
                } else {
                    infoHotspot.setText("Прием данных окончен!");
                }
                connectionThread.interrupt();
                onHotSpot.setVisibility(View.VISIBLE);
                offHotSpot.setVisibility(View.INVISIBLE);
            }
        });
        findViewById(R.id.send_client_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sendCameUsers();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        wifiApManager.showWritePermissionSettings(false);
    }

    // метод для вывода подключенных клиентов к точки доступа
    private void scan() {
        wifiApManager.getClientList(false, new FinishScanListener() {

            @Override
            public void onFinishScan(final ArrayList<ClientScanResult> clients) {

                infoHotspot.setText("WifiApState: " + wifiApManager.getWifiApState() + "\n\n");
                infoHotspot.append("Clients: \n");
                for (ClientScanResult clientScanResult : clients) {
                    System.out.println("####################\n");
                    System.out.println("IpAddr: " + clientScanResult.getIpAddr() + "\n");
                    System.out.println("Device: " + clientScanResult.getDevice() + "\n");
                    System.out.println("HWAddr: " + clientScanResult.getHWAddr() + "\n");
                    System.out.println("isReachable: " + clientScanResult.isReachable() + "\n");
                }
            }
        });
    }


    private void startServerSocket() {
        connectionThread = new Thread(new ConnectionThread());
        connectionThread.start();
    }
    // запуск ServerSocket для приема логина от участинка
    class ConnectionThread implements Runnable {
        private String dataFromClient;
        private boolean isConnection = true;

        @Override
        public void run() {
            Socket server;
            ServerSocket serverSocket;
            try{
                int serverPort = 8080;
                serverSocket = new ServerSocket(serverPort);
                serverSocket.setSoTimeout(120000); // если за 120 сек не поступило запроса, сокет закрывается
                while (true) {
                    server = serverSocket.accept();
                    BufferedReader fromClient = new BufferedReader(
                            new InputStreamReader(server.getInputStream()));
                    dataFromClient = fromClient.readLine(); // полученный логин от клиента
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // вывод на экран
                            listRV.append("\n\t" + dataFromClient);
                            // добавление в коллекцию пришедших участников
                            connectedStudents.add(dataFromClient);
                        }
                    });
                    PrintWriter toClient = new PrintWriter(
                            server.getOutputStream(), true);
                    toClient.println("true");
                    server.close();
                    fromClient.close();
                    toClient.close();
                    TimeUnit.MILLISECONDS.sleep(300);
                }
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
                    infoHotspot.setText("Прием данных окончен!");
                    isConnection = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onHotSpot.setVisibility(View.VISIBLE);
                            offHotSpot.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void setConnection(boolean connection) {
            isConnection = connection;
        }
    }

    // отправка списка пришедших участников на сервер
    private void sendCameUsers() throws JSONException {
        SendCameUsersJson sendCameUsersJson = new SendCameUsersJson(connectedStudents, event);
        JSONObject request = sendCameUsersJson.build();
        MyConnection connection = new MyConnection(SERVER_ADDRESS, Integer.parseInt(SERVER_PORT), request);
        connection.execute();

        try {
            connection.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (connection.isAnswer) {
            boolean isLogin = sendCameUsersJson.parseAndGetResult(connection.getResponse());
            if (isLogin) {
                infoHotspot.setText("Список отправлен!");
            }
        }
    }
}
