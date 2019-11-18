package gy.android.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.appcompat.widget.AppCompatImageView;

import gy.android.util.LogUtil;
import gy.android.util.ScreenUtil;

public class FitScreenImageView extends AppCompatImageView {

    private FitScreenImageView view;
    private Matrix matrix;

    private int screenWidth;
    private int screenHeight;

    private int imageWidth;
    private int imageHeight;

    public FitScreenImageView(Context context) {
        this(context, null);
    }

    public FitScreenImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitScreenImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        view = this;
        matrix = new Matrix();

        screenWidth = ScreenUtil.getScreenWidth(getContext())
                - ScreenUtil.getStatusBarHeight(getContext());
        screenHeight = ScreenUtil.getScreenHeight(getContext(), false)
                - 2 * ScreenUtil.getStatusBarHeight(getContext());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }

            int imageWidth = drawable.getIntrinsicWidth();
            int imageHeight = drawable.getIntrinsicHeight();
            if (imageHeight == view.imageHeight && imageWidth == view.imageWidth) {
                return;
            }

            view.imageWidth = imageWidth;
            view.imageHeight = imageHeight;

            float imageRatio = imageHeight * 1.0f / imageWidth;

            int layoutWidth = getWidth();
            int layoutHeight = getHeight();

            int dstWidth = imageWidth;
            int dstHeight = imageHeight;

            if (screenHeight < screenWidth) {
                if (imageHeight > screenHeight) {
                    dstHeight = screenHeight;
                    dstWidth = (int) (dstHeight / imageRatio);
                }
            } else {
                if (imageWidth > screenWidth) {
                    dstWidth = screenWidth;
                    dstHeight = (int) (dstHeight * imageRatio);
                }
            }

            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = dstWidth;
            layoutParams.height = dstHeight;
            setLayoutParams(layoutParams);

            float scale = dstWidth * 1.0f / imageWidth;
            matrix.postScale(scale, scale);
            setImageMatrix(matrix);
            removeBorderAndTranslationCenter();

            LogUtil.v(view, "onGlobalLayout screen size(" + screenWidth + "," + screenHeight + ") " +
                    "image size(" + imageWidth + "," + imageHeight + ")" +
                    "layout size(" + layoutWidth + "," + layoutHeight + ")");
        }
    };

    //获取图片宽高以及左右上下边界
    private RectF getMatrixRectF() {

        Drawable drawable = getDrawable();
        if (drawable == null) {
            return null;
        }
        RectF rectF = new RectF(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        Matrix matrix = getImageMatrix();
        matrix.mapRect(rectF);

        return rectF;
    }

    //消除控件边界和把图片移动到中间
    private void removeBorderAndTranslationCenter() {
        RectF rectF = getMatrixRectF();
        if (rectF == null)
            return;

        int width = getWidth();
        int height = getHeight();
        float widthF = rectF.width();
        float heightF = rectF.height();
        float left = rectF.left;
        float right = rectF.right;
        float top = rectF.top;
        float bottom = rectF.bottom;
        float translationX = 0.0f, translationY = 0.0f;

        if (left > 0) {
            //左边有边界
            if (widthF > width) {
                //图片宽度大于控件宽度，移动到左边贴边
                translationX = -left;
            } else {
                //图片宽度小于控件宽度，移动到中间
                translationX = width * 1.0f / 2f - (widthF * 1.0f / 2f + left);
            }
        } else if (right < width) {
            //右边有边界
            if (widthF > width) {
                //图片宽度大于控件宽度，移动到右边贴边
                translationX = width - right;
            } else {
                //图片宽度小于控件宽度，移动到中间
                translationX = width * 1.0f / 2f - (widthF * 1.0f / 2f + left);
            }
        }

        if (top > 0) {
            //顶部有边界
            if (heightF > height) {
                //图片高度大于控件高度，去除顶部边界
                translationY = -top;
            } else {
                //图片高度小于控件宽度，移动到中间
                translationY = height * 1.0f / 2f - (top + heightF * 1.0f / 2f);
            }
        } else if (bottom < height) {
            //底部有边界
            if (heightF > height) {
                //图片高度大于控件高度，去除顶部边界
                translationY = height - bottom;
            } else {
                //图片高度小于控件宽度，移动到中间
                translationY = height * 1.0f / 2f - (top + heightF * 1.0f / 2f);
            }
        }

        matrix.postTranslate(translationX, translationY);
        setImageMatrix(matrix);
    }
}
