package com.jemmy.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jemmy.DetailActivity;
import com.jemmy.R;
import com.jemmy.vo.Product;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class HomeAdapter extends ListAdapter<Product, HomeAdapter.MyViewHolder> {

    private ItemClickListener itemClickListener;

    public interface ItemClickListener {
        public void onClick(int position);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        itemClickListener = listener;
    }

    public HomeAdapter(@NonNull DiffUtil.ItemCallback<Product> diffCallback) {
        super(diffCallback);
    }

    public HomeAdapter(@NonNull AsyncDifferConfig<Product> config) {
        super(config);
    }

    /**
     * Adapter onCreateViewHolder
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_cell, parent, false);
        final MyViewHolder holder = new MyViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击进入详情
                Bundle bundle = new Bundle();
//                PhotoItem item = getItem(holder.getAdapterPosition());
                Product item = getItem(holder.getAdapterPosition());
                bundle.putSerializable("Product", item);
                Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
                intent.putExtra("ProductData", bundle);
                holder.itemView.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        ShimmerLayout shimmerLayout = holder.shimmerLayout;
        shimmerLayout.setShimmerColor(0x55FFFFFF);
        shimmerLayout.setShimmerAngle(0);
        shimmerLayout.startShimmerAnimation();

//        int myHeight = holder.productName.getLayoutParams().height + holder.productPrice.getLayoutParams().height;
//        holder.imageView.getLayoutParams().height = myHeight;

        Glide.with(holder.itemView).load(getItem(position).getMainImage()).placeholder(R.drawable.ic_photo_gray_24dp)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (holder.shimmerLayout != null) {
                            holder.shimmerLayout.stopShimmerAnimation();
                        }
                        return false;
                    }
                })
                .into(holder.imageView);

        holder.productName.setText(getItem(position).getName());
        holder.productPrice.setText(String.valueOf(getItem(position).getPrice()));
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private ShimmerLayout shimmerLayout;
        private CardView cardView;
        private TextView productName, productPrice;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photo);
            shimmerLayout = itemView.findViewById(R.id.shimmerLayoutCell);
            cardView = itemView.findViewById(R.id.cartCardView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
        }
    }
}
