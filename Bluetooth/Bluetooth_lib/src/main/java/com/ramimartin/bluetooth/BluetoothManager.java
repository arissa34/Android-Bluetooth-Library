package com.ramimartin.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.ramimartin.bluetooth.bus.BondedDevice;

import java.util.UUID;

import de.greenrobot.event.EventBus;

/**
 * Created by Rami MARTIN on 13/04/2014.
 */
public class BluetoothManager extends BroadcastReceiver {

    public enum TypeBluetooth{
        Client,
        Server;
    }

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static final int REQUEST_DISCOVERABLE_CODE = 114;

    public static int BLUETOOTH_REQUEST_ACCEPTED;
    public static final int BLUETOOTH_REQUEST_REFUSED = 0; // NE PAS MODIFIER LA VALEUR

    public static final int BLUETOOTH_TIME_DICOVERY_60_SEC = 60;
    public static final int BLUETOOTH_TIME_DICOVERY_120_SEC = 120;
    public static final int BLUETOOTH_TIME_DICOVERY_300_SEC = 300;

    private Activity mActivity;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothClient mBluetoothClient;
    private BluetoothServer mBluetoothServer;

    private TypeBluetooth mType;
    private int mTimeDiscoverable;
    public boolean isConnected;
    private boolean mBluetoothIsEnableOnStart;

    public BluetoothManager(){

    }

    public BluetoothManager(Activity activity) {
        mActivity = activity;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothIsEnableOnStart = mBluetoothAdapter.isEnabled();
        isConnected = false;
        setTimeDiscoverable(BLUETOOTH_TIME_DICOVERY_300_SEC);
    }

    public void setTimeDiscoverable(int timeInSec){
        mTimeDiscoverable = timeInSec;
        BLUETOOTH_REQUEST_ACCEPTED = mTimeDiscoverable;
    }

    public boolean checkBluetoothAviability(){
        if (mBluetoothAdapter == null) {
            return false;
        }else{
            return true;
        }
    }

    public void cancelDiscovery(){
        if(mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    public boolean isDiscovering(){
        return mBluetoothAdapter.isDiscovering();
    }

    public void startDiscovery() {
        if (mBluetoothAdapter == null) {
            return;
        } else {
            Log.e(""," ==> mBluetoothAdapter.isEnabled() : "+mBluetoothAdapter.isEnabled());
            Log.e(""," ==> mBluetoothAdapter.isDiscovering() : "+mBluetoothAdapter.isDiscovering());
            if (mBluetoothAdapter.isEnabled() && mBluetoothAdapter.isDiscovering()) {
                return;
            } else {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, mTimeDiscoverable);
                mActivity.startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_CODE);
            }
        }
    }

    public void scanAllBluetoothDevice() {
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mActivity.registerReceiver(this, intentFilter);
        mBluetoothAdapter.startDiscovery();
    }

    public void createClient(String addressMac) {
        mType = TypeBluetooth.Client;
        IntentFilter bondStateIntent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        mActivity.registerReceiver(this, bondStateIntent);
        mBluetoothClient = new BluetoothClient(mBluetoothAdapter, MY_UUID, addressMac);
        new Thread(mBluetoothClient).start();
    }

    public void createServeur(){
        mType = TypeBluetooth.Server;
        mBluetoothServer = new BluetoothServer(mBluetoothAdapter, MY_UUID);
        new Thread(mBluetoothServer).start();
        IntentFilter bondStateIntent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        mActivity.registerReceiver(this, bondStateIntent);
    }

    public void sendMessage(String message) {
        if(mType != null && isConnected){
            if(mType == TypeBluetooth.Server && mBluetoothServer != null){
                mBluetoothServer.write(message);
            }else if(mType == TypeBluetooth.Client && mBluetoothClient != null){
                mBluetoothClient.write(message);
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            EventBus.getDefault().post(device);
        }
        if(intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
            //Log.e("", "===> ACTION_BOND_STATE_CHANGED");
            int prevBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);
            int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
            if (prevBondState == BluetoothDevice.BOND_BONDING)
            {
                // check for both BONDED and NONE here because in some error cases the bonding fails and we need to fail gracefully.
                if (bondState == BluetoothDevice.BOND_BONDED || bondState == BluetoothDevice.BOND_NONE )
                {
                    //Log.e("", "===> BluetoothDevice.BOND_BONDED");
                    EventBus.getDefault().post(new BondedDevice());
                }
            }
        }
    }

    public void onDestroy(){
        try{
            mActivity.unregisterReceiver(this);
        }catch(Exception e){}

        cancelDiscovery();

        if(!mBluetoothIsEnableOnStart){
            mBluetoothAdapter.disable();
        }

        if(mType != null){
            if(mType == TypeBluetooth.Server && mBluetoothServer != null){
                mBluetoothServer.onDestroy();
            }else if(mType == TypeBluetooth.Client && mBluetoothClient != null){
                mBluetoothClient.onDestroy();
            }
        }
    }
}
