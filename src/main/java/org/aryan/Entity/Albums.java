package org.aryan.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Albums {
    private Album[] items;
    private Integer limit;
    private Integer offset;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Album[] getItems() {
        return items;
    }

    public Albums() {
    }

    public Albums(Album[] items, Integer limit, Integer offset) {
        this.items = items;
        this.limit = limit;
        this.offset = offset;
    }

    public void setItems(Album[] items) {
        this.items = items;
    }
}
