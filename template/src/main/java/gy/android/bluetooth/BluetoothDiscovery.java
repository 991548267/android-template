package gy.android.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gy.android.util.LogUtil;
import gy.android.util.PermissionUtil;

public class BluetoothDiscovery {

    private Context context;
    private BluetoothAdapter adapter;

    private Map<String, BluetoothDevice> deviceMap = new HashMap<>();
    private OnBluetoothDeviceChangeListener onBluetoothDeviceChangeListener;

    private BroadcastReceiver bluetoothDeviceChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }
            LogUtil.v(BluetoothDiscovery.this, "bluetoothDeviceChangeReceiver " + intent.getAction());
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    deviceMap.clear();
                    if (onBluetoothDeviceChangeListener != null) {
                        onBluetoothDeviceChangeListener.onDiscoveryStarted();
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    if (onBluetoothDeviceChangeListener != null) {
                        onBluetoothDeviceChangeListener.onDiscoveryFinished(deviceMap);
                    }
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device == null) {
                        return;
                    }
                    deviceMap.put(device.getAddress(), device);
                    if (onBluetoothDeviceChangeListener != null) {
                        onBluetoothDeviceChangeListener.onDeviceFound(device);
                    }
                    break;
            }
        }
    };

    public BluetoothDiscovery(Context context, BluetoothAdapter adapter) {
        this.context = context;
        this.adapter = adapter;

        // { 监听蓝牙设备搜索结果
        IntentFilter filter1 = new IntentFilter(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter filter2 = new IntentFilter(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(bluetoothDeviceChangeReceiver, filter1);
        context.registerReceiver(bluetoothDeviceChangeReceiver, filter2);
        context.registerReceiver(bluetoothDeviceChangeReceiver, filter3);
        // } 监听蓝牙设备搜索结果
    }

    public void release() {
        cancelDiscovery();
        context.unregisterReceiver(bluetoothDeviceChangeReceiver);
        deviceMap.clear();
    }

    public void requestPermission(PermissionUtil.FullCallback fullCallback) {
        PermissionUtil.withContext(context).permission(Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                .rationale(shouldRequest -> PermissionUtil.showRationaleDialog(context, shouldRequest))
                .callback(fullCallback).request();
    }

    public boolean startDiscovery() {
        LogUtil.v(BluetoothDiscovery.this, "startDiscovery");
        if (PermissionUtil.isGranted(context, Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            if (adapter != null) {
                if (!adapter.isDiscovering()) {
                    adapter.startDiscovery();
                    return true;
                }
            }
        }
        return false;
    }

    public void cancelDiscovery() {
        LogUtil.v(BluetoothDiscovery.this, "cancelDiscovery");
        if (adapter == null) {
            return;
        }
        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }
    }

    public void setOnBluetoothDeviceChangeListener(OnBluetoothDeviceChangeListener onBluetoothDeviceChangeListener) {
        this.onBluetoothDeviceChangeListener = onBluetoothDeviceChangeListener;
    }

    public interface OnBluetoothDeviceChangeListener {
        void onDiscoveryStarted();

        void onDiscoveryFinished(Map<String, BluetoothDevice> map);

        void onDeviceFound(BluetoothDevice device);
    }
}
