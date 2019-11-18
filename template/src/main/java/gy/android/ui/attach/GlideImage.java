package gy.android.ui.attach;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;

import gy.android.R;
import gy.android.util.LogUtil;

public class GlideImage {

    public static void loadImage(final ImageView view, String url) {
        loadImage(view, url, null);
    }

    public static void loadImage(final ImageView view, String url, RequestOptions options) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        // 如果是本地文件
        RequestBuilder builder;
        if (url.startsWith("/storage/")) {
            File file = new File(url);
            builder = Glide.with(view.getContext()).load(file);
        } else {
            Uri uri = Uri.parse(url);
            builder = Glide.with(view.getContext()).load(uri);
        }
        if (options == null) {
            options = new RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE);
        }
        builder.apply(options).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition transition) {
                view.setImageDrawable(resource);
            }
        });
    }

    /**
     * @param object Activity, Fragment, Context, View
     * @param view
     * @param url
     */
    public static void loadImage(Object object, ImageView view, String url) {
        loadImage(object, view, url, null);
    }

    public static void loadImage(Object object, ImageView view, String url, DiskCacheStrategy strategy) {
        loadImage(object, view, url, strategy, false, ImageView.ScaleType.MATRIX);
    }

    public static void loadImage(Object object, ImageView view, String url, boolean originalSize) {
        loadImage(object, view, url, null, originalSize, ImageView.ScaleType.MATRIX);
    }

    public static void loadImage(Object object, ImageView view, String url, DiskCacheStrategy strategy, boolean originalSize) {
        loadImage(object, view, url, strategy, originalSize, ImageView.ScaleType.MATRIX);
    }

    public static void loadImage(Object object, ImageView view, String url, DiskCacheStrategy strategy, boolean originalSize, ImageView.ScaleType scaleType) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_error)
                .dontAnimate()
                .dontTransform();
        if (originalSize) {
            requestOptions.override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        }
        if (strategy == null) {
            requestOptions.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        } else {
            requestOptions.diskCacheStrategy(strategy);
        }
        switch (scaleType) {
            case FIT_CENTER:
                requestOptions.fitCenter();
                break;
            case CENTER_CROP:
                requestOptions.centerCrop();
                break;
            case CENTER_INSIDE:
                requestOptions.centerInside();
                break;
            default:
                break;
        }
        if (object instanceof Activity) {
            Glide.with((Activity) object).load(url)
                    .apply(requestOptions).into(view);
        } else if (object instanceof Context) {
            Glide.with((Context) object).load(url)
                    .apply(requestOptions).into(view);
        } else if (object instanceof Fragment) {
            Glide.with((Fragment) object).load(url)
                    .apply(requestOptions).into(view);
        } else if (object instanceof View) {
            Glide.with((View) object).load(url)
                    .apply(requestOptions).into(view);
        } else {
            LogUtil.v(GlideImage.class, "loadImage with unknown object!");
        }
    }
}
