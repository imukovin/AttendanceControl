package com.example.tutorapp.my.hotspot;

import com.example.tutorapp.my.hotspot.ClientScanResult;

import java.util.ArrayList;

public interface FinishScanListener {
    /**
     * Interface called when the scan method finishes. Network operations should not execute on UI thread
     * @param clients
     */
    public void onFinishScan(ArrayList<ClientScanResult> clients);
}
