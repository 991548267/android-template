package gy.android.template;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;

import java.util.List;

import gy.android.ui.BaseActivity;
import gy.android.util.LogUtil;
import gy.android.util.PermissionUtil;

public class EntryActivity extends BaseActivity {

    private String[] permissions;

    @Override
    public void initData(@Nullable Bundle bundle) {
        permissions = new String[]{
                Manifest.permission.CAMERA};
    }

    @Override
    public void initContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        PermissionUtil.withContext(activity).permission(permissions)
                .rationale(shouldRequest -> PermissionUtil.showRationaleDialog(activity, shouldRequest))
                .callback(new PermissionUtil.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        LogUtil.v(activity, "onGranted:" + JSON.toJSONString(permissionsGranted));
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                        LogUtil.v(activity, "onDenied:" + JSON.toJSONString(permissionsDenied));
                        LogUtil.v(activity, "onDenied forever:" + JSON.toJSONString(permissionsDeniedForever));
                        if (!permissionsDeniedForever.isEmpty()) {
                            PermissionUtil.showOpenAppSettingDialog(activity, () -> activity.finish());
                            return;
                        }
                        activity.finish();
                    }
                }).request();
    }
}
