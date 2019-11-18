package gy.android.camera;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import gy.android.ui.view.AutoFitTextureView;
import gy.android.util.ImageUtil;
import gy.android.util.LogUtil;

public class Camera2Controller {
    private static final String TAG = "Camera2Controller";

    private Activity activity;
    private AutoFitTextureView textureView;

    public Camera2Controller(Activity activity, AutoFitTextureView textureView) {
        this.activity = activity;
        this.textureView = textureView;
    }

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore cameraLock = new Semaphore(1);
    /**
     * Orientation of the camera sensor
     */
    private int sensorOrientation;

    private CameraDevice device;

    private Camera2Listener listener;

    public void setListener(Camera2Listener listener) {
        this.listener = listener;
    }

    /**
     * @param lensFace
     * @see CameraCharacteristics#LENS_FACING
     */
    @SuppressWarnings("MissingPermission")
    public void openCamera(int lensFace) {
        LogUtil.i(TAG, "openCamera: ");
        try {
            CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
            String[] cameras = manager.getCameraIdList();
            if (cameras.length > 0) {
                for (String camera : cameras) {
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(camera);
                    Integer cameraLensFace = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (cameraLensFace == null) {
                        switch (Integer.parseInt(camera)) {
                            case 1:
                                cameraLensFace = CameraMetadata.LENS_FACING_FRONT;
                                break;
                            case 0:
                            default:
                                cameraLensFace = CameraMetadata.LENS_FACING_BACK;
                                break;
                        }
                    }
                    if (cameraLensFace == lensFace) {
                        Integer integer = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                        sensorOrientation = integer == null ? 90 : integer;

                        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                        if (map == null) {
                            continue;
                        }
                        previewSizes = map.getOutputSizes(SurfaceTexture.class);
                        pictureSizes = map.getOutputSizes(ImageFormat.JPEG);
                        videoSizes = map.getOutputSizes(MediaRecorder.class);

                        previewSize = choosePreviewSize(previewSizes, textureView.getWidth(), textureView.getHeight(), null);
                        int orientation = activity.getResources().getConfiguration().orientation;
                        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
                        } else {
                            textureView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
                        }

                        configureTransform(textureView, previewSize, textureView.getWidth(), textureView.getHeight());

                        Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                        isFlashSupported = available == null ? false : available;

                        try {
                            if (!cameraLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                                throw new RuntimeException("Time out waiting to lock camera opening.");
                            }
                            manager.openCamera(camera, stateCallback, handler);
                        } catch (InterruptedException e) {
                            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
                        }
                        return;
                    }
                }
            } else {
                LogUtil.e(TAG, "openCamera: camera size is empty!");
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void closeCamera() {
        LogUtil.i(TAG, "closeCamera: ");
        if (isRecord) {
            stopRecord();
        }

        stopPreview();

        if (listener != null) {
            listener.onConnectStateChange(false);
        }

        if (null != device) {
            device.close();
            device = null;
        }
    }

    private CaptureRequest.Builder previewBuilder;
    private boolean isPreview = false;

    public void startPreview() {
        LogUtil.i(TAG, "startPreview: device " + device);
        if (device == null) {
            return;
        }
        stopPreview();

        List<Surface> surfaces = new ArrayList<>();

        SurfaceTexture texture = textureView.getSurfaceTexture();
        previewSize = choosePreviewSize(previewSizes, textureView.getWidth(), textureView.getHeight(), null);
        LogUtil.i(TAG, "startPreview: previewSize " + previewSize.toString());
        texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface surface = new Surface(texture);
        surfaces.add(surface);

        pictureSize = chooseVideoSize(pictureSizes);
        takePictureImageReader = ImageReader.newInstance(pictureSize.getWidth(), pictureSize.getHeight(), ImageFormat.JPEG, 2);
        takePictureImageReader.setOnImageAvailableListener(takePictureImageReaderListener, handler);

        Surface takePictureSurface = takePictureImageReader.getSurface();
        surfaces.add(takePictureSurface);

        try {
            previewBuilder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewBuilder.addTarget(surface);

            device.createCaptureSession(surfaces, previewStateCallback, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void stopPreview() {
        closeCameraSession();
        if (takePictureImageReader != null) {
            takePictureImageReader.close();
            takePictureImageReader = null;
        }
    }

    public void takePicture(String filePath) {
        // This is how to tell the camera to lock focus.
        pictureFilePath = filePath;
        try {
            CaptureRequest.Builder captureRequestBuilder = device.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            captureRequestBuilder.addTarget(takePictureImageReader.getSurface());
            // 自动对焦
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 自动闪光
            if (isFlashSupported) {
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            }
            // 获取手机方向
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            // 根据设备方向计算设置照片的方向
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, getPictureOrientation(rotation));
            //拍照
            CaptureRequest mCaptureRequest = captureRequestBuilder.build();
            previewSession.stopRepeating();
            previewSession.capture(mCaptureRequest, null, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CaptureRequest.Builder recordBuilder;
    private ImageReader recordImageReader;
    private boolean isRecord = false;

    public boolean isRecord() {
        return isRecord;
    }

    public void startRecord(String filePath) throws IOException {
        startRecord(filePath, false);
    }

    public void startRecord(String filePath, boolean capture) throws IOException {
        LogUtil.i(TAG, "startRecord: capture " + capture);
        stopPreview();
        initMediaRecorder(filePath);

        try {
            recordBuilder = device.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);

            List<Surface> surfaces = new ArrayList<>();

            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            previewSize = choosePreviewSize(previewSizes, videoSize.getWidth(), videoSize.getHeight(), videoSize);
            LogUtil.i(TAG, "startRecord: previewSize " + previewSize.toString());
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

            Surface surface = new Surface(texture);
            surfaces.add(surface);
            recordBuilder.addTarget(surface);

            Surface recordSurface = mediaRecorder.getSurface();
            surfaces.add(recordSurface);
            recordBuilder.addTarget(recordSurface);

            if (capture) {
                recordImageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.YUV_420_888, 2);
                recordImageReader.setOnImageAvailableListener(recordImageReaderListener, handler);
                Surface imageReaderSurface = recordImageReader.getSurface();
                surfaces.add(imageReaderSurface);
                recordBuilder.addTarget(imageReaderSurface);
            }

            device.createCaptureSession(surfaces, recordStateCallback, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        LogUtil.i(TAG, "stopRecord: ");
        isRecord = false;
        takePhotoState = TAKE_PHOTO_AFTER;
        if (recordImageReader != null) {
            recordImageReader.close();
            recordImageReader = null;
        }
        if (listener != null) {
            listener.onRecordStateChange(false);
        }
        mediaRecorder.stop();
        mediaRecorder.reset();
        releaseMediaRecorder();

        if (listener != null) {
            listener.onRecord(recordFilePath);
            recordFilePath = null;
        }

        startPreview();
    }

    private static final int TAKE_PHOTO_BEFORE = 0;
    private static final int TAKE_PHOTO_DOING = 1;
    private static final int TAKE_PHOTO_AFTER = 2;
    private int takePhotoState = TAKE_PHOTO_AFTER;
    private String recordCaptureFilePath;

    public void captureWhenRecord(String file) {
        if (takePhotoState == TAKE_PHOTO_AFTER) {
            recordCaptureFilePath = file;
            takePhotoState = TAKE_PHOTO_BEFORE;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private HandlerThread handlerThread;
    private Handler handler;

    public void startBackgroundThread() {
        handlerThread = new HandlerThread("camera2");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    public void stopBackgroundThread() {
        try {
            handlerThread.quitSafely();
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Size previewSize;
    private Size pictureSize;
    private Size videoSize;

    public void setPreviewSize(Size previewSize) {
        this.previewSize = previewSize;
    }

    public void setPictureSize(Size pictureSize) {
        this.pictureSize = pictureSize;
    }

    public void setVideoSize(Size videoSize) {
        this.videoSize = videoSize;
    }

    private Size[] previewSizes;
    private Size[] pictureSizes;
    private Size[] videoSizes;

    public Size[] getPreviewSizes() {
        return previewSizes;
    }

    public Size[] getPictureSizes() {
        return pictureSizes;
    }

    public Size[] getVideoSizes() {
        return videoSizes;
    }

    private final int RATIO_WIDTH = 16;
    private final int RATIO_HEIGHT = 9;

    private Size choosePreviewSize(Size[] sizes, int width, int height, Size aspectRatio) {
        for (Size size : sizes) {
            LogUtil.i(TAG, "choosePreviewSize: item size " + size.toString());
        }

        int w = RATIO_WIDTH;
        int h = RATIO_HEIGHT;
        if (aspectRatio != null) {
            w = aspectRatio.getWidth();
            h = aspectRatio.getHeight();
        }

        List<Size> enoughSizes = new ArrayList<>();
        for (Size size : sizes) {
            if (size.getWidth() == (size.getHeight() * w / h)
                    && size.getWidth() >= width && size.getHeight() >= height) {
                enoughSizes.add(size);
            }
        }

        LogUtil.i(TAG, "choosePreviewSize: enoughSizes.size " + enoughSizes.size());
        if (enoughSizes.size() > 0) {
            return Collections.min(enoughSizes, new CompareSizeByArea());
        } else {
            return sizes[0];
        }
    }

    class CompareSizeByArea implements Comparator<Size> {
        @Override
        public int compare(Size one, Size two) {
            return Long.signum((long) one.getWidth() * one.getHeight() - (long) two.getWidth() * two.getHeight());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Configures the necessary {@link Matrix} transformation to `mTextureView`.
     * This method should not to be called until the camera preview size is determined in
     * openCamera, or until the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(TextureView preview, Size previewSize, int viewWidth, int viewHeight) {
        if (null == preview || null == previewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / previewSize.getHeight(),
                    (float) viewWidth / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        preview.setTransform(matrix);
    }

    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraLock.release();
            device = camera;
            if (listener != null) {
                listener.onConnectStateChange(true);
            }
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraLock.release();
            camera.close();
            if (listener != null) {
                listener.onConnectStateChange(false);
            }
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraLock.release();
            camera.close();
            if (listener != null) {
                listener.onConnectStateChange(false);
            }
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private CameraCaptureSession previewSession;
    private CameraCaptureSession.StateCallback previewStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            previewSession = session;
            updatePreviewSession(previewBuilder, previewSession);
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            session.close();
            previewSession = null;
        }
    };

    private void closeCameraSession() {
        closeCameraSession(previewSession);
        closeCameraSession(recordSession);
        isPreview = false;
        if (listener != null) {
            listener.onPreviewStateChange(false);
        }
    }

    private void closeCameraSession(CameraCaptureSession session) {
        if (session != null) {
            session.close();
            session = null;
        }
    }

    private void updatePreviewSession(CaptureRequest.Builder builder, CameraCaptureSession session) {
        if (device == null) {
            return;
        }
        try {
            // Auto focus should be continuous for camera preview.
            builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // Flash is automatically enabled when necessary.
            if (isFlashSupported) {
                builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            }
            session.setRepeatingRequest(builder.build(), previewCaptureCallback, handler);
            isPreview = true;
            if (listener != null) {
                listener.onPreviewStateChange(true);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // { take picture

    private String pictureFilePath;

    private boolean isFlashSupported = false;

    private CameraCaptureSession.CaptureCallback previewCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }
    };

    private ImageReader takePictureImageReader;

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener takePictureImageReaderListener = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            LogUtil.i(TAG, "onImageAvailable: takePictureImageReaderListener");
            Image image = reader.acquireNextImage();
            if (image == null) {
                startPreview();
                return;
            }
            if (TextUtils.isEmpty(pictureFilePath)) {
                image.close();
                startPreview();
                return;
            }
            File file = new File(pictureFilePath);
            pictureFilePath = null;
            handler.post(new PictureImageSaver(image, file));
        }
    };

    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    private class PictureImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        private final Image image;
        /**
         * The file we save the image into.
         */
        private final File file;

        private ReentrantLock lock = new ReentrantLock();

        PictureImageSaver(Image image, File file) {
            this.image = image;
            this.file = file;
        }

        @Override
        public void run() {
            lock.lock();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(bytes);
                outputStream.flush();
                outputStream.close();
                if (listener != null) {
                    listener.onTakePhoto(file.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                image.close();
                startPreview();
            }
        }

    }
    // } take picture

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    private MediaRecorder mediaRecorder;
    private String recordFilePath;

    private void initMediaRecorder(String filePath) throws IOException {
        mediaRecorder = new MediaRecorder();
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (!TextUtils.isEmpty(filePath)) {
            recordFilePath = filePath;
            mediaRecorder.setOutputFile(recordFilePath);
        } else {
            throw new RuntimeException("The video file path is empty!");
        }
        mediaRecorder.setVideoEncodingBitRate(10000000);
        mediaRecorder.setVideoFrameRate(25);
        if (videoSize == null) {
            videoSize = chooseVideoSize(videoSizes);
        }
        LogUtil.i(TAG, "initMediaRecorder: videoSize " + videoSize.toString());
        mediaRecorder.setVideoSize(videoSize.getWidth(), videoSize.getHeight());
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (sensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                mediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                mediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
                break;
        }
        mediaRecorder.prepare();
    }

    private void releaseMediaRecorder() {
        if (null != mediaRecorder) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private Size chooseVideoSize(Size[] sizes) {
        for (Size size : sizes) {
            LogUtil.i(TAG, "chooseVideoSize: size " + size.toString());
        }
        for (Size size : sizes) {
            if (size.getWidth() == size.getHeight() * RATIO_WIDTH / RATIO_HEIGHT
                    && size.getWidth() >= 720 && size.getWidth() <= 1920) {
                return size;
            }
        }
        return sizes[sizes.length - 1];
    }

    private String getVideoFilePath(Context context) {
        final File dir = context.getExternalFilesDir(null);
        return (dir == null ? "" : (dir.getAbsolutePath() + "/"))
                + System.currentTimeMillis() + ".mp4";
    }

    private CameraCaptureSession recordSession;
    private CameraCaptureSession.StateCallback recordStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            recordSession = session;
            updateRecordSession(recordBuilder, recordSession);

            activity.runOnUiThread(() -> {
                mediaRecorder.start();
                isRecord = true;
                if (listener != null) {
                    listener.onRecordStateChange(true);
                }
            });
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            session.close();
            recordSession = null;
        }
    };

    private void updateRecordSession(CaptureRequest.Builder builder, CameraCaptureSession session) {
        if (device == null) {
            return;
        }
        try {
            builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            session.setRepeatingRequest(builder.build(), previewCaptureCallback, handler);
            isPreview = true;
            if (listener != null) {
                listener.onPreviewStateChange(true);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private OnImageAvailableListenerImpl recordImageReaderListener = new OnImageAvailableListenerImpl();

    private class OnImageAvailableListenerImpl implements ImageReader.OnImageAvailableListener {
        private byte[] y;
        private byte[] u;
        private byte[] v;
        private int stride;
        private int format;
        private ReentrantLock lock = new ReentrantLock();

        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireLatestImage();
            LogUtil.i(TAG, "onImageAvailable: acquireLatestImage");
            if (image == null) {
                return;
            }
            if (takePhotoState == TAKE_PHOTO_BEFORE) {
                takePhotoState = TAKE_PHOTO_DOING;
                LogUtil.i(TAG, "onImageAvailable: TAKE_PHOTO_DOING");
                // Y:U:V == 4:2:2
                if (image.getFormat() == ImageFormat.YUV_420_888
                        || image.getFormat() == ImageFormat.YUV_422_888) {
                    Image.Plane[] planes = image.getPlanes();
                    // 加锁确保y、u、v来源于同一个Image
                    lock.lock();
                    // 重复使用同一批byte数组，减少gc频率
                    if (y == null) {
                        y = new byte[planes[0].getBuffer().limit() - planes[0].getBuffer().position()];
                        u = new byte[planes[1].getBuffer().limit() - planes[1].getBuffer().position()];
                        v = new byte[planes[2].getBuffer().limit() - planes[2].getBuffer().position()];
                    }

                    if (image.getPlanes()[0].getBuffer().remaining() == y.length) {
                        planes[0].getBuffer().get(y);
                        planes[1].getBuffer().get(u);
                        planes[2].getBuffer().get(v);

                        stride = planes[0].getRowStride();
                        format = image.getFormat();

                        handler.post(() -> {
                            onRecordCaptureCallback(y, u, v, stride, previewSize.getWidth(), previewSize.getHeight(), format);
                            takePhotoState = TAKE_PHOTO_AFTER;
                        });
                    }
                    lock.unlock();
                }
            }
            image.close();
        }

        private byte[] nv21;

        private void onRecordCaptureCallback(byte[] y, byte[] u, byte[] v, int stride, int width, int height, int format) {
            switch (format) {
                case ImageFormat.YUV_420_888:
                    nv21 = ImageUtil.yuv420ToNV21(y, u, v, stride, height);
                    break;
                case ImageFormat.YUV_422_888:
                    nv21 = ImageUtil.yuv422ToNV21(y, u, v, stride, height);
                    break;
                default:
                    nv21 = null;
                    break;
            }
            if (nv21 == null) {
                return;
            }

            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            Bitmap bitmap = ImageUtil.jpegBytesToBitmap(ImageUtil.nv21ToJPEG(nv21, width, height), getPictureOrientation(rotation), false);

            if (TextUtils.isEmpty(recordCaptureFilePath)) {
                recordCaptureFilePath = getPictureFilePath(activity);
            }

            try {
                File file = new File(recordCaptureFilePath);
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            LogUtil.i(TAG, "RecordImageSaver run: picture " + recordCaptureFilePath);
            if (listener != null) {
                listener.onRecordCapture(recordCaptureFilePath);
            }
        }
    }

    private int getPictureOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (DEFAULT_ORIENTATIONS.get(rotation) + sensorOrientation + 270) % 360;
    }

    private String getPictureFilePath(Context context) {
        final File dir = context.getExternalFilesDir(null);
        return (dir == null ? "" : (dir.getAbsolutePath() + "/"))
                + System.currentTimeMillis() + ".jpg";
    }
}
