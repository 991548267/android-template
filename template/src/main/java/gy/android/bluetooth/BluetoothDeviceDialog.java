package gy.android.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gy.android.R;
import gy.android.ui.adapter.BaseRecyclerViewAdapter;
import gy.android.util.PermissionUtil;

public class BluetoothDeviceDialog extends AppCompatDialog {

    private BluetoothDiscovery discovery;
    private OnBluetoothDeviceSelectListener onBluetoothDeviceSelectListener;

    public BluetoothDeviceDialog(Context context, BluetoothAdapter adapter) {
        super(context);
        discovery = new BluetoothDiscovery(context, adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
        initEvent();
    }

    private List<BTDevice> rv_bluetooth_device_list = new ArrayList<>();
    private BTDeviceAdapter rv_bluetooth_device_adapter;
    private DividerItemDecoration rv_bluetooth_device_divider;

    private void initData() {
        rv_bluetooth_device_list.clear();
        rv_bluetooth_device_adapter = new BTDeviceAdapter(getContext(), rv_bluetooth_device_list);
        rv_bluetooth_device_divider = new DividerItemDecoration(getContext(), LinearLayout.VERTICAL);
        rv_bluetooth_device_divider.setDrawable(getContext().getDrawable(R.drawable.shape_line_stroke_gray));
    }

    private ContentLoadingProgressBar progress_bluetooth_device;
    private RecyclerView rv_bluetooth_device;
    private AppCompatImageButton btn_close;
    private AppCompatButton btn_bluetooth_start_discovery;
    private AppCompatButton btn_bluetooth_stop_discovery;

    private void initView() {
        setContentView(R.layout.dialog_bluetooth_device);
        progress_bluetooth_device = findViewById(R.id.progress_bluetooth_device);
        btn_close = findViewById(R.id.btn_close);
        btn_bluetooth_start_discovery = findViewById(R.id.btn_bluetooth_start_discovery);
        btn_bluetooth_stop_discovery = findViewById(R.id.btn_bluetooth_stop_discovery);
        rv_bluetooth_device = findViewById(R.id.rv_bluetooth_device);
        if (rv_bluetooth_device == null) {
            dismiss();
        }
        rv_bluetooth_device.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_bluetooth_device.addItemDecoration(rv_bluetooth_device_divider);
        rv_bluetooth_device.setAdapter(rv_bluetooth_device_adapter);
    }

    private void initEvent() {
        onStartClick();

        btn_close.setOnClickListener(view -> onCloseClick());
        btn_bluetooth_start_discovery.setOnClickListener(view -> onStartClick());
        btn_bluetooth_stop_discovery.setOnClickListener(view -> onStopClick());
        rv_bluetooth_device_adapter.setOnItemClickListener(position -> onBTDeviceItemClick(position));

        discovery.setOnBluetoothDeviceChangeListener(new BluetoothDiscovery.OnBluetoothDeviceChangeListener() {

            @Override
            public void onDiscoveryStarted() {
                progress_bluetooth_device.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDiscoveryFinished(Map<String, BluetoothDevice> map) {
                progress_bluetooth_device.setVisibility(View.GONE);
            }

            @Override
            public void onDeviceFound(BluetoothDevice device) {
                BTDevice btDevice = new BTDevice(device.getName(), device.getAddress(), device.getBondState() == BluetoothDevice.BOND_BONDED);
                rv_bluetooth_device_list.add(btDevice);
                rv_bluetooth_device_adapter.notifyDataSetChanged();
            }
        });
    }

    private void onCloseClick() {
        if (isShowing()) {
            dismiss();
        }
    }

    private void onStartClick() {
        progress_bluetooth_device.setVisibility(View.VISIBLE);
        discovery.requestPermission(new PermissionUtil.FullCallback() {
            @Override
            public void onGranted(List<String> permissionsGranted) {
                discovery.startDiscovery();
            }

            @Override
            public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                if (!permissionsDeniedForever.isEmpty()) {
                    PermissionUtil.showOpenAppSettingDialog(getContext(), () -> {
                    });
                }
                dismiss();
            }
        });
    }

    private void onStopClick() {
        progress_bluetooth_device.setVisibility(View.GONE);
        discovery.cancelDiscovery();
    }

    private void onBTDeviceItemClick(int position) {
        if (onBluetoothDeviceSelectListener != null) {
            onBluetoothDeviceSelectListener.onDeviceSelect(rv_bluetooth_device_list.get(position).address);
        }
        dismiss();
    }

    public void setOnBluetoothDeviceSelectListener(OnBluetoothDeviceSelectListener onBluetoothDeviceSelectListener) {
        this.onBluetoothDeviceSelectListener = onBluetoothDeviceSelectListener;
    }

    @Override
    public void dismiss() {
        discovery.release();
        super.dismiss();
    }

    class BTDeviceAdapter extends BaseRecyclerViewAdapter<BTDevice> {
        private OnItemClickListener onItemClickListener;

        public BTDeviceAdapter(Context context, List<BTDevice> list) {
            super(context, list);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(inflaterFrom(parent, R.layout.item_bluetooth_device));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (!TextUtils.isEmpty(list.get(position).name)) {
                ((ViewHolder) holder).tv_bluetooth_name.setText(list.get(position).name);
            } else {
                ((ViewHolder) holder).tv_bluetooth_name.setText("Unknown Device");
            }
            ((ViewHolder) holder).tv_bluetooth_address.setText(list.get(position).address);
            ((ViewHolder) holder).tv_bluetooth_pair.setVisibility(list.get(position).isPair ? View.VISIBLE : View.GONE);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private AppCompatTextView tv_bluetooth_name;
            private AppCompatTextView tv_bluetooth_address;
            private AppCompatTextView tv_bluetooth_pair;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_bluetooth_name = itemView.findViewById(R.id.tv_bluetooth_name);
                tv_bluetooth_address = itemView.findViewById(R.id.tv_bluetooth_address);
                tv_bluetooth_pair = itemView.findViewById(R.id.tv_bluetooth_pair);
                itemView.setOnClickListener(view -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition());
                    }
                });
            }
        }
    }

    interface OnItemClickListener {
        void onItemClick(int position);
    }

    class BTDevice {
        private String name;
        private String address;
        private boolean isPair;

        public BTDevice(String name, String address, boolean isPair) {
            this.name = name;
            this.address = address;
            this.isPair = isPair;
        }
    }

    public interface OnBluetoothDeviceSelectListener {
        void onDeviceSelect(String address);
    }
}
