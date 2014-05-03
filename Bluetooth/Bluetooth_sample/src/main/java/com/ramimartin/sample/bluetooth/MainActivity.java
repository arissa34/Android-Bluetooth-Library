package com.ramimartin.sample.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ramimartin.bluetooth.activity.BluetoothActivity;

import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends BluetoothActivity {

    @InjectView(R.id.log_txt)
    TextView mLogTxt;
    @InjectView(R.id.scan)
    Button mScanBtn;
    @InjectView(R.id.send)
    Button mSendBtn;
    @InjectView(R.id.client)
    Button mClientBtn;
    @InjectView(R.id.serveur)
    Button mServeurBtn;
    @InjectView(R.id.communication)
    EditText mEditText;
    @InjectView(R.id.discovery)
    Button mDiscovery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public UUID myUUID() {
        return UUID.fromString("c47914a1-c8a2-45f0-a900-bc686b9328ab");
    }

    @OnClick(R.id.discovery)
    public void discovery() {
        setTimeDiscoverable(200);
        startDiscovery();
    }

    @OnClick(R.id.serveur)
    public void serveur() {
        setLogText("===> Start Serveur ...");
        createServeur();
    }

    @OnClick(R.id.client)
    public void client() {
        if (!TextUtils.isEmpty(mEditText.getText().toString())) {
            setLogText("===> Start Client connexion on device : " + mEditText.getText().toString());
            createClient(mEditText.getText().toString());
        }
    }

    @OnClick(R.id.scan)
    public void scan() {
        setLogText("===> Start Scanning devices ...");
        scanAllBluetoothDevice();
    }

    @OnClick(R.id.send)
    public void send() {
        sendMessage(mEditText.getText().toString());
        setLogText("===> Send : " + mEditText.getText().toString());
    }

    @Override
    public void onBluetoothStartDiscovery() {
        mScanBtn.setEnabled(true);
        setLogText("===> Start discovering !");
        mServeurBtn.setEnabled(true);
    }

    @Override
    public void onBluetoothDeviceFound(BluetoothDevice device) {
        setLogText("===> Device detected : " + device.getAddress());
        mEditText.setText(device.getAddress());
        mClientBtn.setEnabled(true);
    }

    @Override
    public void onClientConnectionSuccess() {
        setLogText("===> Client Connexion success !");
        mEditText.setText("");
        mSendBtn.setEnabled(true);
    }

    @Override
    public void onClientConnectionFail() {
        setLogText("===> Client Connexion fail !");
        mClientBtn.setEnabled(false);
    }

    @Override
    public void onServeurConnectionSuccess() {
        setLogText("===> Serveur Connexion success !");
        mEditText.setText("");
        mSendBtn.setEnabled(true);
    }

    @Override
    public void onServeurConnectionFail() {
        setLogText("===> Serveur Connexion fail !");
    }

    @Override
    public void onBluetoothCommunicator(String messageReceive) {
        setLogText("===> receive msg : " + messageReceive);
    }

    @Override
    public void onBluetoothNotAviable() {
        setLogText("===> Bluetooth not aviable on this device");
        mDiscovery.setEnabled(false);
        mClientBtn.setEnabled(false);
        mSendBtn.setEnabled(false);
        mScanBtn.setEnabled(false);
        mServeurBtn.setEnabled(false);
    }

    public void setLogText(String text) {
        mLogTxt.setText(mLogTxt.getText() + "\n" + text);
    }

}
