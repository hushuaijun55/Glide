package com.hsj.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

/**
 * Create by hsj55
 * 2019/11/21
 */
public class ImageLoader {

    public static void displayImage(Context context, int res, ImageView imageView) {
        Glide.with(context).load(res).into(imageView);
    }

    public static void displayImage(Context context, int res, int placeholder, ImageView imageView) {
        Glide.with(context).load(res).placeholder(placeholder).into(imageView);
    }

    public static void displayImage(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    public static void displayImage(Context context, String url, ImageView imageView, int width, int height) {
        Glide.with(context).load(url).override(width, height).centerCrop().into(imageView);
    }

    public static void displayImage(Context context, String url, int placeholder, ImageView imageView) {
        Glide.with(context).load(url).placeholder(placeholder).into(imageView);
    }

    public static void displayImage(Context context, String url, int placeholder, int errorRes, ImageView imageView) {
        Glide.with(context).load(url).placeholder(placeholder).error(errorRes).into(imageView);
    }

    public static void displayImage(Context context, String url, int placeholder, final ImageLoadCallback callback) {
        Glide.with(context).asBitmap().load(url).placeholder(placeholder).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                HLogger.d("onResourceReady");
                callback.onResourceReady(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }
}
