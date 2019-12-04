package gy.android.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import gy.android.util.LogUtil;

public class Camera2Manager {

    private CameraManager manager;
    private CameraCharacteristics characteristics;

    public Camera2Manager(Context context) {
        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    /**
     * @param lensFace
     * @see CameraCharacteristics#LENS_FACING
     */
    @SuppressWarnings("MissingPermission")
    public boolean openCamera(int lensFace) {
        startHandlerThread();
        try {
            String[] cameraIds = manager.getCameraIdList();
            if (cameraIds.length > 0) {
                for (String cameraId : cameraIds) {
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                    // TODO 不支持Camera2的直接使用Camera1的默认配置
                    Integer cameraLensFace = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (cameraLensFace == null) {
                        switch (Integer.parseInt(cameraId)) {
                            case 1:
                                cameraLensFace = CameraMetadata.LENS_FACING_FRONT;
                                break;
                            case 0:
                            default:
                                cameraLensFace = CameraMetadata.LENS_FACING_BACK;
                                break;
                        }
                    }
                    // TODO 寻找对应的Camera
                    if (cameraLensFace == lensFace) {
                        this.characteristics = characteristics;
                        try {
                            if (!deviceSemaphore.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                                throw new RuntimeException("Time out waiting to lock camera opening.");
                            }
                            manager.openCamera(cameraId, deviceStateCallback, handler);
                            return true;
                        } catch (InterruptedException e) {
                            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
                        }
                    }
                }
            } else {
                LogUtil.e(manager, "openCamera: camera size is empty!");
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean closeCamera() {
        closeCaptureSession();
        closeCameraDevice();
        closeCaptureImageReader();
        quitHandlerThread();
        return false;
    }

    private ImageReader captureImageReader;
    private ImageReader.OnImageAvailableListener picturePreviewReaderListener = imageReader -> {
        // TODO 拍照
    };

    private CaptureRequest.Builder picturePreviewBuilder;

    // { CameraCaptureSession 拍照和录像共用
    private CameraCaptureSession captureSession;
    private CameraCaptureSession.StateCallback captureSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
            captureSession = cameraCaptureSession;
            updatePreview(cameraCaptureSession, device, picturePreviewBuilder);
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
            captureSession = null;
            LogUtil.e(manager, "captureSessionStateCallback onConfigureFailed");
        }
    };
    // } CameraCaptureSession 拍照和录像共用

    /**
     * Update the camera preview. {@link #startPreview(CameraDevice, TextureView, int, int)} ()} needs to be called in advance.
     */
    private void updatePreview(CameraCaptureSession session, CameraDevice device, CaptureRequest.Builder builder) {
        if (null == device || session == null) {
            return;
        }
        try {
            setUpCaptureRequestBuilder(builder);
            HandlerThread thread = new HandlerThread("PicturePreview");
            thread.start();
            session.setRepeatingRequest(builder.build(), null, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }

    public void startPreview(CameraDevice device, TextureView textureView) {
        startPreview(device, textureView, 0, 0);
    }

    public void startPreview(CameraDevice device, TextureView textureView, int picWidth, int picHeight) {
        LogUtil.i(manager, "startPreview: device:" + device + " texture:" + textureView);
        if (device == null) {
            return;
        }

        closeCaptureSession();

        List<Surface> surfaces = new ArrayList<>();

        // Preview
        SurfaceTexture texture = textureView.getSurfaceTexture();
        previewSize = findBestPreviewSize(textureView.getWidth(), textureView.getHeight());
        LogUtil.i(manager, "startPreview: previewSize " + previewSize.toString());
        texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface surface = new Surface(texture);
        surfaces.add(surface);

        // Picture
        pictureSize = findBestPictureSize(picWidth, picHeight);
        captureImageReader = ImageReader.newInstance(pictureSize.getWidth(), pictureSize.getHeight(), ImageFormat.JPEG, 2);
        captureImageReader.setOnImageAvailableListener(picturePreviewReaderListener, handler);
        Surface takePictureSurface = captureImageReader.getSurface();
        surfaces.add(takePictureSurface);

        try {
            picturePreviewBuilder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            picturePreviewBuilder.addTarget(surface);

            device.createCaptureSession(surfaces, captureSessionStateCallback, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭捕获会话
     */
    public void closeCaptureSession() {
        if (captureSession != null) {
            captureSession.close();
            captureSession = null;
        }
    }

    public void closeCameraDevice() {
        if (device != null) {
            device.close();
            device = null;
        }
    }

    public void closeCaptureImageReader() {
        if (captureImageReader != null) {
            captureImageReader.close();
            captureImageReader = null;
        }
    }

    // { Handler
    private HandlerThread handlerThread;
    private Handler handler;

    private void startHandlerThread() {
        handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    private void quitHandlerThread() {
        try {
            handlerThread.quitSafely();
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    // } Handler

    // { CameraDevice
    private CameraDevice device;
    private Semaphore deviceSemaphore = new Semaphore(1);
    private CameraDevice.StateCallback deviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            deviceSemaphore.release();
            device = camera;
            //TODO 创建预览界面
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            deviceSemaphore.release();
            camera.close();
            device = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int i) {
            deviceSemaphore.release();
            camera.close();
            device = null;
        }
    };
    // } CameraDevice

    // { Size
    private static final Size DEFAULT_SIZE = new Size(1280, 720); // 720P
    private Size previewSize;
    private Size pictureSize;
    private Size videoSize;
    private Size[] previewSizes = null;
    private Size[] pictureSizes = null;
    private Size[] videoSizes = null;

    private void initAllSizes() {
        if (previewSizes != null && pictureSizes != null && videoSizes != null) {
            return;
        }
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map == null) {
            return;
        }
        previewSizes = map.getOutputSizes(SurfaceTexture.class);
        pictureSizes = map.getOutputSizes(ImageFormat.JPEG);
        videoSizes = map.getOutputSizes(MediaRecorder.class);
    }

    private void unInitAllSizes() {
        previewSizes = null;
        pictureSizes = null;
        videoSizes = null;

        previewSize = null;
        pictureSize = null;
        videoSize = null;
    }

    public Size findBestPreviewSize() {
        return findBestSize(DEFAULT_SIZE.getWidth(), DEFAULT_SIZE.getHeight());
    }

    public Size findBestPreviewSize(int width, int height) {
        return findBestSize(width, height);
    }

    public Size findBestPictureSize() {
        return findBestSize(DEFAULT_SIZE.getWidth(), DEFAULT_SIZE.getHeight());
    }

    public Size findBestPictureSize(int width, int height) {
        return findBestSize(width, height);
    }

    public Size findBestVideoSize() {
        return findBestSize(DEFAULT_SIZE.getWidth(), DEFAULT_SIZE.getHeight());
    }

    public Size findBestVideoSize(int width, int height) {
        return findBestSize(width, height);
    }

    public Size findBestSize(int width, int height) {
        if (previewSizes != null && previewSizes.length > 0) {
            Size preview = previewSizes[0];
            int product = width * height;
            for (Size size : previewSizes) {
                // TODO 预览宽高比例完全匹配，且宽高大于或等于匹配宽高
                if (size.getWidth() == size.getHeight() * width / height) {
                    if (size.getWidth() >= width && Math.abs(size.getWidth() - width) < Math.abs(preview.getWidth() - width)) {
                        preview = size;
                        continue;
                    }
                }
                // TODO 宽高比不同时，面积匹配最佳
                int previewProduct = preview.getWidth() * preview.getHeight();
                int sizeProduct = size.getWidth() * size.getHeight();
                if (sizeProduct >= product && Math.abs(sizeProduct - product) < Math.abs(previewProduct - product)) {
                    preview = size;
                }
            }
            return preview;
        }
        return null;
    }
    // } Size
}
