package gy.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

public class ImageUtil {

    private static final String TAG = "ImageUtil";

    /**
     * 使用鲁班压缩图片
     *
     * @param context
     * @param oldFile
     * @param newFile
     * @param listener
     */
    public static void lubanCompress(Context context, File oldFile, final File newFile, OnCompressListener listener) {
        Luban.with(context)
                .load(oldFile)
                .setTargetDir(newFile.getParentFile().getAbsolutePath())
                .ignoreBy(250) // 250K以下文件不压缩
                .putGear(3) // 压缩等级3
                .setCompressListener(listener)
                .setRenameListener(new OnRenameListener() {
                    @Override
                    public String rename(String filePath) {
                        return newFile.getName();
                    }
                })
                .filter(new CompressionPredicate() { // 压缩条件，检查文件名
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .launch();
    }

    /**
     * 获取指定文件大小(单位：字节)
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) {
        if (file == null) {
            return 0;
        }
        long size = 0;
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                size = fis.available();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return size;
    }

    public static byte[] imageToJpegBytes(Image image) {
        byte data[] = null;
        switch (image.getFormat()) {
            case ImageFormat.JPEG:
                ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
                data = new byte[byteBuffer.capacity()];
                byteBuffer.get(data);
                break;
            case ImageFormat.YUV_420_888:
            case ImageFormat.YUV_422_888:
                data = nv21ToJPEG(yuvToNV21(image.getPlanes(), image.getHeight(), image.getFormat()), image.getWidth(), image.getHeight());
                break;
            case ImageFormat.NV21:
                break;
        }
        return data;
    }

    public static byte[] yuvToNV21(Image.Plane[] planes, int height, int format) {
        byte[] y, u, v;
        y = new byte[planes[0].getBuffer().limit() - planes[0].getBuffer().position()];
        u = new byte[planes[1].getBuffer().limit() - planes[1].getBuffer().position()];
        v = new byte[planes[2].getBuffer().limit() - planes[2].getBuffer().position()];
        if (planes[0].getBuffer().remaining() == y.length) {
            planes[0].getBuffer().get(y);
            planes[1].getBuffer().get(u);
            planes[2].getBuffer().get(v);
        }
        switch (format) {
            case ImageFormat.YUV_420_888:
                return yuv420ToNV21(y, u, v, planes[0].getRowStride(), height);
            case ImageFormat.YUV_422_888:
                return yuv422ToNV21(y, u, v, planes[0].getRowStride(), height);
        }
        return null;
    }

    public static byte[] yuvToNV21(Image.Plane[] planes, int width, int height, int format) {
        byte[] yuv = null;
        switch (format) {
            case ImageFormat.YUV_420_888:
                yuv = new byte[width * height * 3 / 2];
                break;
            case ImageFormat.YUV_422_888:
                yuv = new byte[width * height * 2];
                break;
            default:
                break;
        }
        if (yuv != null) {
            // Y通道，对应planes[0]
            // Y size = width * height
            // yBuffer.remaining() = width * height;
            // pixelStride = 1
            ByteBuffer yBuffer = planes[0].getBuffer();
            int yLen = width * height;
            yBuffer.get(yuv, 0, yLen);
            // U通道，对应planes[1]
            // U size = width * height / 4;
            // uBuffer.remaining() = width * height / 2;
            // pixelStride = 2
            ByteBuffer uBuffer = planes[1].getBuffer();
            int pixelStride = planes[1].getPixelStride(); // pixelStride = 2
            for (int i = 0; i < uBuffer.remaining(); i += pixelStride) {
                yuv[yLen++] = uBuffer.get(i);
            }
            // V通道，对应planes[2]
            // V size = width * height / 4;
            // vBuffer.remaining() = width * height / 2;
            // pixelStride = 2
            ByteBuffer vBuffer = planes[2].getBuffer();
            pixelStride = planes[2].getPixelStride(); // pixelStride = 2
            for (int i = 0; i < vBuffer.remaining(); i += pixelStride) {
                yuv[yLen++] = vBuffer.get(i);
            }
        }
        return null;
    }

    /**
     * 将Y:U:V == 4:2:2的数据转换为nv21
     * (u.length == v.length) && (y.length / 2 > u.length) && (y.length / 2 ≈ u.length)
     *
     * @param y      Y 数据
     * @param u      U 数据
     * @param v      V 数据
     * @param stride 步长
     * @param height 图像高度
     * @return NV21 byte array
     */
    public static byte[] yuv422ToNV21(byte[] y, byte[] u, byte[] v, int stride, int height) {
        byte[] nv21 = new byte[stride * height * 3 / 2];
        System.arraycopy(y, 0, nv21, 0, y.length);
        // 注意，若length值为 y.length * 3 / 2 会有数组越界的风险，需使用真实数据长度计算
        int length = y.length + u.length / 2 + v.length / 2;
        int uIndex = 0, vIndex = 0;
        for (int i = stride * height; i < length; i += 2) {
            nv21[i] = v[vIndex];
            nv21[i + 1] = u[uIndex];
            vIndex += 2;
            uIndex += 2;
        }
        return nv21;
    }

    /**
     * 将Y:U:V == 4:1:1的数据转换为nv21
     * (u.length == v.length) && (y.length / 2 > u.length) && (y.length / 2 ≈ u.length)
     *
     * @param y      Y 数据
     * @param u      U 数据
     * @param v      V 数据
     * @param stride 步长
     * @param height 图像高度
     * @return NV21 byte array
     */
    public static byte[] yuv420ToNV21(byte[] y, byte[] u, byte[] v, int stride, int height) {
        byte[] nv21 = new byte[stride * height * 3 / 2];
        System.arraycopy(y, 0, nv21, 0, y.length);
        // 注意，若length值为 y.length * 3 / 2 会有数组越界的风险，需使用真实数据长度计算
        int length = y.length + u.length / 2 + v.length / 2;
        int uIndex = 0, vIndex = 0;
        for (int i = stride * height; i < length; i += 2) {
            nv21[i] = v[vIndex];
            nv21[i + 1] = u[uIndex];
            vIndex += 2;
            uIndex += 2;
        }
        return nv21;
    }

    /**
     * 将NV21（Yuv420sp） 转换为 JPEG
     *
     * @param nv21   nv21 数据
     * @param width  图像宽
     * @param height 图像高
     * @return JPEG byte array
     */
    public static byte[] nv21ToJPEG(byte[] nv21, int width, int height) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        YuvImage image = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        image.compressToJpeg(new Rect(0, 0, width, height), 100, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * 将JPEG 转换为 Bitmap
     *
     * @param jpeg        jpeg数据
     * @param orientation 旋转角度
     * @param isMirror    是否镜像
     * @return 图像bitmap
     */
    public static Bitmap jpegBytesToBitmap(byte[] jpeg, int orientation, boolean isMirror) {
        Matrix matrix = new Matrix();
        // 预览相对于原数据可能有旋转
        matrix.setRotate(orientation);
        if (isMirror) {
            // 对于前置数据，镜像处理；若手动设置镜像预览，则镜像处理；若都有，则不需要镜像处理
            matrix.setScale(-1, 1);
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
