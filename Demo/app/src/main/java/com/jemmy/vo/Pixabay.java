package com.jemmy.vo;

import java.util.ArrayList;

public class Pixabay {

    Integer totalHits;
    ArrayList<PhotoItem> hits;
    Integer total;

    public Integer getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(Integer totalHits) {
        this.totalHits = totalHits;
    }

    public ArrayList<PhotoItem> getHits() {
        return hits;
    }

    public void setHits(ArrayList<PhotoItem> hits) {
        this.hits = hits;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}



