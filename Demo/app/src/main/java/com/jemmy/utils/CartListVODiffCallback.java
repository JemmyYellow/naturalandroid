package com.jemmy.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.jemmy.vo.CartProductVO;
import com.jemmy.vo.Product;

public class CartListVODiffCallback extends DiffUtil.ItemCallback<CartProductVO>{

    @Override
    public boolean areItemsTheSame(@NonNull CartProductVO oldItem, @NonNull CartProductVO newItem) {
        return oldItem == newItem;
    }

    @Override
    public boolean areContentsTheSame(@NonNull CartProductVO oldItem, @NonNull CartProductVO newItem) {
        return oldItem.getId().equals(newItem.getId());
    }
}
