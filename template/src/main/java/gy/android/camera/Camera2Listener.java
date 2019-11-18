package gy.android.camera;

public interface Camera2Listener {

    void onTakePhoto(String path);

    void onRecord(String path);

    void onRecordCapture(String path);

    void onConnectStateChange(boolean connect);

    void onPreviewStateChange(boolean preview);

    void onRecordStateChange(boolean record);
}
