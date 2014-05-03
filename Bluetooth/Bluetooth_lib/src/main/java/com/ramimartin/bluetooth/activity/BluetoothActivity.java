package com.ramimartin.bluetooth.activity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import com.ramimartin.bluetooth.BluetoothManager;
import com.ramimartin.bluetooth.bus.BluetoothCommunicator;
import com.ramimartin.bluetooth.bus.BondedDevice;
import com.ramimartin.bluetooth.bus.ClientConnectionFail;
import com.ramimartin.bluetooth.bus.ClientConnectionSuccess;
import com.ramimartin.bluetooth.bus.ServeurConnectionFail;
import com.ramimartin.bluetooth.bus.ServeurConnectionSuccess;

import java.util.UUID;

import de.greenrobot.event.EventBus;

/**
 * Created by Rami MARTIN on 13/04/2014.
 */
public abstract class BluetoothActivity extends Activity {

    protected BluetoothManager mBluetoothManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothManager = new BluetoothManager(this);
        checkBluetoothAviability();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this))
        EventBus.getDefault().register(this);
        mBluetoothManager.setUUID(myUUID());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mBluetoothManager.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothManager.REQUEST_DISCOVERABLE_CODE) {
            if (resultCode == BluetoothManager.BLUETOOTH_REQUEST_REFUSED) {
            } else if (resultCode == BluetoothManager.BLUETOOTH_REQUEST_ACCEPTED) {
                onBluetoothStartDiscovery();
            } else {
            }
        }
    }

    public void checkBluetoothAviability(){
        if(!mBluetoothManager.checkBluetoothAviability()){
            onBluetoothNotAviable();
        }
    }

    public void setTimeDiscoverable(int timeInSec){
        mBluetoothManager.setTimeDiscoverable(timeInSec);
    }

    public void startDiscovery(){
        mBluetoothManager.startDiscovery();
    }

    public void scanAllBluetoothDevice(){
        mBluetoothManager.scanAllBluetoothDevice();
    }

    public void createServeur(){
        mBluetoothManager.createServeur();
    }

    public void createClient(String addressMac){
        mBluetoothManager.createClient(addressMac);
    }

    public void sendMessage(String message){
        mBluetoothManager.sendMessage(message);
    }

    public abstract UUID myUUID();
    public abstract void onBluetoothDeviceFound(BluetoothDevice device);
    public abstract void onClientConnectionSuccess();
    public abstract void onClientConnectionFail();
    public abstract void onServeurConnectionSuccess();
    public abstract void onServeurConnectionFail();
    public abstract void onBluetoothStartDiscovery();
    public abstract void onBluetoothCommunicator(String messageReceive);
    public abstract void onBluetoothNotAviable();

    public void onEventMainThread(BluetoothDevice device){
        onBluetoothDeviceFound(device);
    }

    public void onEventMainThread(ClientConnectionSuccess event){
        mBluetoothManager.isConnected = true;
        onClientConnectionSuccess();
    }

    public void onEventMainThread(ClientConnectionFail event){
        mBluetoothManager.isConnected = false;
        onClientConnectionFail();
    }

    public void onEventMainThread(ServeurConnectionSuccess event){
        mBluetoothManager.isConnected = true;
        onServeurConnectionSuccess();
    }

    public void onEventMainThread(ServeurConnectionFail event){
        mBluetoothManager.isConnected = false;
        onServeurConnectionFail();
    }

    public void onEventMainThread(BluetoothCommunicator event){
        onBluetoothCommunicator(event.mMessageReceive);
    }

    public void onEventMainThread(BondedDevice event){
        //mBluetoothManager.sendMessage("BondedDevice");
    }

}
