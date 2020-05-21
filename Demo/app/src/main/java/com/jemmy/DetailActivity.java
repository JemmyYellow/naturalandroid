package com.jemmy;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jemmy.vo.PhotoItem;

import io.supercharge.shimmerlayout.ShimmerLayout;
import uk.co.senab.photoview.PhotoView;

public class DetailActivity extends AppCompatActivity {

    private ShimmerLayout shimmerLayoutDetail;
    private PhotoView photoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        photoView = findViewById(R.id.photoView);
        shimmerLayoutDetail =findViewById(R.id.shimmerLayoutDetail);
        shimmerLayoutDetail.setShimmerAngle(0);
        shimmerLayoutDetail.setShimmerColor(0x55FFFFFF);
        shimmerLayoutDetail.startShimmerAnimation();

        PhotoItem photoItem = (PhotoItem) getIntent().getBundleExtra("PhotoData").getSerializable("Photo");
        Glide.with(this).load(photoItem.getLargeUrl()).placeholder(R.drawable.ic_photo_gray_24dp)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if(shimmerLayoutDetail != null){
                            shimmerLayoutDetail.stopShimmerAnimation();
                        }
                        return false;
                    }
                })
                .into(photoView);
    }

}
