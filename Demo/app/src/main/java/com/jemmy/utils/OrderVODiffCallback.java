package com.jemmy.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.jemmy.vo.OrderVO;

public class OrderVODiffCallback extends DiffUtil.ItemCallback<OrderVO>{

    @Override
    public boolean areItemsTheSame(@NonNull OrderVO oldItem, @NonNull OrderVO newItem) {
        return oldItem == newItem;
    }

    @Override
    public boolean areContentsTheSame(@NonNull OrderVO oldItem, @NonNull OrderVO newItem) {
        return oldItem.getOrderNo().equals(newItem.getOrderNo());
    }
}
