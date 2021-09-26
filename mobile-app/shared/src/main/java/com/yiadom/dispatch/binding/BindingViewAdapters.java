package com.yiadom.dispatch.binding;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.Target;
import com.yiadom.dispatch.R;

public class BindingViewAdapters {

    @SuppressLint("CheckResult")
    @BindingAdapter({"app:image_url", "app:circular"})
    public static void loadImage(ImageView view, @Nullable String url, boolean circular) {
        if (url != null && !url.isEmpty()) {
            var builder = Glide.with(view)
                    .asBitmap()
                    .load(url)
                    .override(Target.SIZE_ORIGINAL)
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .centerCrop()
                    .centerCrop();

            // crop image to have circular outline
            if (circular) builder
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .fallback(R.drawable.avatar)
                    .circleCrop();

            builder.into(view);
        }
    }

    /*@SuppressLint("CheckResult")
    @BindingAdapter({"app:drawable_res", "app:rounded"})
    public static void loadImageFromDrawable(ImageView view, Drawable drawable, boolean rounded) {

        var builder = Glide.with(view).asDrawable()
                .load(drawable)
                .override(Target.SIZE_ORIGINAL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .centerCrop();

        // crop image to have circular outline
        if (rounded) builder
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .fallback(R.drawable.avatar)
                .circleCrop();

        builder.into(view);
    }*/
}
