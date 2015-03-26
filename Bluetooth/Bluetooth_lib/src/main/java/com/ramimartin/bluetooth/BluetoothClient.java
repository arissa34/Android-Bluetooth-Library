package com.ramimartin.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.ramimartin.bluetooth.bus.BluetoothCommunicator;
import com.ramimartin.bluetooth.bus.ClientConnectionFail;
import com.ramimartin.bluetooth.bus.ClientConnectionSuccess;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.greenrobot.event.EventBus;

/**
 * Created by Rami MARTIN on 13/04/2014.
 */
public class BluetoothClient implements Runnable {

    private boolean CONTINUE_READ_WRITE = true;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private UUID mUuid;
    private String mAdressMac;

    private BluetoothSocket mSocket;
    private BluetoothSocket fallbackSocket;
    private InputStream mInputStream;
    private OutputStreamWriter mOutputStreamWriter;

    private BluetoothConnector mBluetoothConnector;

    public BluetoothClient(BluetoothAdapter bluetoothAdapter, UUID uuid, String adressMac) {
        mBluetoothAdapter = bluetoothAdapter;
        mUuid = uuid;
        mAdressMac = adressMac;
    }

    @Override
    public void run() {

        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mAdressMac);
        List<UUID> uuidCandidates = new ArrayList<UUID>();
        uuidCandidates.add(mUuid);
        mBluetoothConnector = new BluetoothConnector(mBluetoothDevice, true, mBluetoothAdapter, uuidCandidates);

        try {
            mSocket = mBluetoothConnector.connect().getUnderlyingSocket();
        } catch (IOException e1) {
            EventBus.getDefault().post(new ClientConnectionFail());
            e1.printStackTrace();
        }

        if (mSocket == null) {
            Log.e("", "mSocket == Null");
            return;
        }

        try {

            mInputStream = mSocket.getInputStream();
            mOutputStreamWriter = new OutputStreamWriter(mSocket.getOutputStream());

            int bufferSize = 1024;
            int bytesRead = -1;
            byte[] buffer = new byte[bufferSize];

            EventBus.getDefault().post(new ClientConnectionSuccess());

            while (CONTINUE_READ_WRITE) {

                final StringBuilder sb = new StringBuilder();
                bytesRead = mInputStream.read(buffer);
                if (bytesRead != -1) {
                    String result = "";
                    while ((bytesRead == bufferSize) && (buffer[bufferSize] != 0)) {
                        result = result + new String(buffer, 0, bytesRead);
                        bytesRead = mInputStream.read(buffer);
                    }
                    result = result + new String(buffer, 0, bytesRead);
                    sb.append(result);
                }
                EventBus.getDefault().post(new BluetoothCommunicator(sb.toString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new ClientConnectionFail());
        }
    }

    public void write(String message) {
        try {
            mOutputStreamWriter.write(message);
            mOutputStreamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnexion() {
        if (mSocket != null) {
            try {
                mInputStream.close();
                mInputStream = null;
                mOutputStreamWriter.close();
                mOutputStreamWriter = null;
                mSocket.close();
                mSocket = null;
                mBluetoothConnector.close();
            } catch (Exception e) {
            }
            CONTINUE_READ_WRITE = false;
        }
    }
}
