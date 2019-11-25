package gy.android.template;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

import gy.android.bluetooth.BluetoothController;
import gy.android.bluetooth.BluetoothDeviceDialog;
import gy.android.ui.BaseToolbarActivity;
import gy.android.util.LogUtil;
import gy.android.util.PermissionUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class BluetoothSearchActivity extends BaseToolbarActivity {

    private BluetoothController bluetoothController;
    private BluetoothSocket bluetoothSocket;

    @Override
    public int getContentViewId() {
        return R.layout.activity_bluetooth_search;
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        bluetoothController = new BluetoothController(activity);
    }

    @Override
    public void initView(View parent) {
        if (bluetoothController.getAdapter() == null) {
            activity.finish();
            return;
        }
    }

    @Override
    public void initEvent() {
        bluetoothController.setOnBluetoothStateChangeListener(state -> {
            switch (state) {
                case BluetoothAdapter.STATE_TURNING_ON:
                    LogUtil.e(activity, "onReceive---------STATE_TURNING_ON");
                    break;
                case BluetoothAdapter.STATE_ON:
                    LogUtil.e(activity, "onReceive---------STATE_ON");
                    BluetoothDeviceDialog dialog = new BluetoothDeviceDialog(activity, bluetoothController.getAdapter());
                    dialog.setOnBluetoothDeviceSelectListener(deviceSelectListener);
                    dialog.setCancelable(false);
                    dialog.show();
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    LogUtil.e(activity, "onReceive---------STATE_TURNING_OFF");
                    break;
                case BluetoothAdapter.STATE_OFF:
                    LogUtil.e(activity, "onReceive---------STATE_OFF");
                    break;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        PermissionUtil.withContext(activity)
                .permission(Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .rationale(shouldRequest -> PermissionUtil.showRationaleDialog(activity, shouldRequest))
                .callback(new PermissionUtil.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        if (bluetoothController.getAdapter().isEnabled()) {
                            BluetoothDeviceDialog dialog = new BluetoothDeviceDialog(activity, bluetoothController.getAdapter());
                            dialog.setOnBluetoothDeviceSelectListener(deviceSelectListener);
                            dialog.setCancelable(false);
                            dialog.show();
                        } else {
                            bluetoothController.getAdapter().enable();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                        if (!permissionsDeniedForever.isEmpty()) {
                            PermissionUtil.showOpenAppSettingDialog(activity, () -> activity.finish());
                            return;
                        }
                        activity.finish();
                    }
                }).request();
    }

    @Override
    protected void onDestroy() {
        bluetoothController.release();
        super.onDestroy();
    }

    private BluetoothDeviceDialog.OnBluetoothDeviceSelectListener deviceSelectListener = address -> {
        LogUtil.v(activity, "OnBluetoothDeviceSelectListener address:" + address);
        Observable.create(new ObservableOnSubscribe<BluetoothSocket>() {
            @Override
            public void subscribe(ObservableEmitter<BluetoothSocket> emitter) throws Exception {
                emitter.onNext(bluetoothController.connect(address, ""));
            }
        }).subscribe(new Observer<BluetoothSocket>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BluetoothSocket socket) {
                bluetoothSocket = socket;
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    };
}
