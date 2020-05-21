package com.jemmy.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.jemmy.vo.PhotoItem;

public class DiffCallback extends DiffUtil.ItemCallback<PhotoItem>{

    @Override
    public boolean areItemsTheSame(@NonNull PhotoItem oldItem, @NonNull PhotoItem newItem) {
        return oldItem == newItem;
    }

    @Override
    public boolean areContentsTheSame(@NonNull PhotoItem oldItem, @NonNull PhotoItem newItem) {
        Integer oldid = oldItem.getPhotoid();
        Integer newid = newItem.getPhotoid();
        return oldid.equals(newid);
    }
}
