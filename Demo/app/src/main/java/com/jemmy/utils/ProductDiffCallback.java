package com.jemmy.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.jemmy.vo.Product;

public class ProductDiffCallback extends DiffUtil.ItemCallback<Product>{

    @Override
    public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
        return oldItem == newItem;
    }

    @Override
    public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
        return oldItem.getId().equals(newItem.getId());
    }
}
