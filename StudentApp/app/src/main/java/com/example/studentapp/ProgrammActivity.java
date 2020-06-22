package com.example.studentapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.studentapp.my.connection.TestManyConnectionsThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class ProgrammActivity extends AppCompatActivity {
    private static final int MAX_TRY_CONNECT_TO_PHONE = 10;
    private static final int TIMEOUT_FOR_CONNECT_TO_PHONE_MS = 500;

    private WifiManager wifi;
    private List<ScanResult> wifiPoints;

    private Button scanWifiBtn;
    private ListView wifiPointsList;
    private TextView resultSend;
    private ProgressDialog progressDialog;
    private Activity pa;

    private String SSID = null;
    private String pass = null;
    private String type = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programm);
        pa = this;
        // запрашиваем разрешение на использование гео данных
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},1);

        scanWifiBtn = findViewById(R.id.scanWiFiBtn);
        wifiPointsList = findViewById(R.id.listOfPoints);
        resultSend = findViewById(R.id.result_tv);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // сканируем wifi сети по нажатию на кнопку
        scanWifiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(ProgrammActivity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                progressDialog.setCancelable(false);
                LoadWifiPoints loadWifiPoints = new LoadWifiPoints();
                loadWifiPoints.execute();
            }
        });

        // обработчик нажатия на элемент спика
        wifiPointsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                SSID = wifiPointsList.getItemAtPosition(position).toString().split("---")[0];
                type = wifiPointsList.getItemAtPosition(position).toString().split("---")[1];

                final AlertDialog.Builder builder = new AlertDialog.Builder(ProgrammActivity.this);
                final EditText input = new EditText(ProgrammActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setTitle("Set password");
                builder.setMessage("Set password for this network!");
                builder.setView(input);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        pass = input.getText().toString().trim();
                        // подключение к выбранной сети
                        if (!pass.equals("")) {
                            // поделючение и отправка данных для одного потока
                            if (connectToWiFiPoint(SSID, pass, type)) {
                                SharedPreferences sPref = getSharedPreferences("StudentAppSP", MODE_PRIVATE);
                                String phone = sPref.getString("login", "");
                                SendPhoneToMainDevice loadSendData = new SendPhoneToMainDevice(phone);
                                loadSendData.execute();
                            }
                            // тестирование подключения и отправки данным на множестве потоков
                            //ExecutorService executor = Executors.newFixedThreadPool(20);
                            /*for (int i = 0; i < 50; i++) {
                                //executor.submit(new TestConnectionsThread(pa, SSID, pass, type, i));
                                new Thread(new TestManyConnectionsThread(pa, i)).start();
                            }*/
                            SSID = null;
                            pass = null;
                        } else {
                            resultSend.setText("Put password!");
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog ad = builder.create();
                ad.show();
            }
        });
    }

    private void enableWifi() {
        wifi.setWifiEnabled(true);
    }

    private void disableWifi() {
        wifi.setWifiEnabled(false);
    }

    private void scanNetPoints() {
        wifi.startScan();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wifiPoints = wifi.getScanResults();
    }

    private String getScanResult() {
        String points = "";
        for (int i = 0; i < wifiPoints.size(); i++) {
            points += wifiPoints.get(i).SSID;
            points += "---";
            points += wifiPoints.get(i).capabilities;
            points += " ";
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, points.split(" "));
        wifiPointsList.setAdapter(adapter);
        return points;
    }
    // подключение к точки доступа
    public boolean connectToWiFiPoint(String ssid, String pass, String type) {
        disableWifi();
        enableWifi();
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + ssid + "\"";
        if (type.contains("WEP")) {
            conf.wepKeys[0] = "\"" + pass + "\"";
            conf.wepTxKeyIndex = 0;
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        } else if (type.contains("WPA")) {
            conf.preSharedKey = "\"" + pass + "\"";
        } else if (type.contains("OPEN")) {
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        // подключение к сети
        int netId = wifi.addNetwork(conf);
        return wifi.enableNetwork(netId, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class LoadWifiPoints extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            scanNetPoints();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getScanResult();
            progressDialog.dismiss();
        }
    }

    // класс для отправки логина на устройство организатора
    private class SendPhoneToMainDevice extends AsyncTask<Void, Void, Void> {
        private String message;
        private String response;
        private int timeOut;
        private int tryConnectCount;

        SendPhoneToMainDevice(String message) {
            this.message = message;
            this.response = "";
            timeOut = 50;
            tryConnectCount = 0;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int serverPort = 8080;
            String serverAdress = "192.168.43.1";   // адрес телефона (точки доступа) организатора в лок. сети (всегда одинаков)
            int timeout = (int) (Math.random() * timeOut);
            while (!response.equals("true") && tryConnectCount <= MAX_TRY_CONNECT_TO_PHONE) {
                try {
                    tryConnectCount++;
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(serverAdress, serverPort), TIMEOUT_FOR_CONNECT_TO_PHONE_MS);
                    PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
                    // отправка организатору
                    toServer.println(message);
                    BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    response = fromServer.readLine();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (response.equals("true")) {
                                resultSend.append("Вы отметились!");
                            } else {
                                resultSend.append(response);
                            }
                        }
                    });
                    fromServer.close();
                    toServer.close();
                    socket.close();
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
                        System.out.println("Turn off the client by timeout");
                    }
                }
                try {
                    Thread.sleep(timeout);
                    timeout = timeout * 2;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (tryConnectCount > MAX_TRY_CONNECT_TO_PHONE) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultSend.append("Не могу подключиться! Попробуйте еще раз!");
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }
}