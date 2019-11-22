package gy.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.IOException;
import java.util.UUID;

import gy.android.util.LogUtil;

public class BluetoothController {

    private Context context;
    private BluetoothAdapter adapter;
    private OnBluetoothStateChangeListener onBluetoothStateChangeListener;
    private BluetoothDiscovery discovery;

    public BluetoothController(Context context) {
        this.context = context;
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            LogUtil.e(BluetoothController.this, "Bluetooth isn`t available!");
        } else {
            registerBluetoothChangeReceiver();
            discovery = new BluetoothDiscovery(context, adapter);
        }
    }

    public void release() {
        if (adapter != null) {
            discovery.release();
            unregisterBluetoothChangeReceiver();
        }
    }

    public void setOnBluetoothStateChangeListener(OnBluetoothStateChangeListener listener) {
        this.onBluetoothStateChangeListener = listener;
    }

    public BluetoothAdapter getAdapter() {
        return adapter;
    }

    public BluetoothDiscovery getDiscovery() {
        return discovery;
    }

    public BluetoothSocket connect(String address, String uuid) {
        BluetoothDevice device = adapter.getRemoteDevice(address);
        BluetoothSocket socket = null;
        try {
            socket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
            if (socket != null && !socket.isConnected()) {
                socket.connect();
            }
        } catch (IOException e) {
            LogUtil.e(BluetoothController.this, "connect bluetooth " + address + "failed");
            try {
                socket.close();
            } catch (IOException ex) {
                LogUtil.e(BluetoothController.this, "close bluetooth socket failed");
            }
        }
        return socket;
    }

    private void registerBluetoothChangeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(receiver, filter);
    }

    private void unregisterBluetoothChangeReceiver() {
        context.unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.e(context, "StateChangeReceiver onReceive");
            if (intent == null || intent.getAction() == null) {
                return;
            }
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    if (onBluetoothStateChangeListener != null) {
                        onBluetoothStateChangeListener.onBluetoothStateChange(state);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public interface OnBluetoothStateChangeListener {
        void onBluetoothStateChange(int state);
    }
}
