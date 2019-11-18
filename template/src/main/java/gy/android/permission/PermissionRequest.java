package gy.android.permission;

import android.content.Context;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;

public class PermissionRequest {
    public static void requestPermissions(Context context,
                                          Action<List<String>> granted,
                                          Action<List<String>> denied,
                                          String... permissions) {
        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(granted)
                .onDenied(denied)
                .start();
    }
}
