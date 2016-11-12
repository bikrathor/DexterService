package com.ccec.dexterservice.entities;

import java.util.List;
import java.util.Map;

public class RequestRow {
    private Map<String, Object> requestMap;
    private Map<String, Object> itemMap;

    public RequestRow(Map<String, Object> requestMap, Map<String, Object> itemMap) {
        this.requestMap = requestMap;
        this.itemMap = itemMap;
    }

    public Map<String, Object> getRequestMap() {
        return requestMap;
    }

    public void setRequestMap(Map<String, Object> requestMap) {
        this.requestMap = requestMap;
    }

    public Map<String, Object> getItemMap() {
        return itemMap;
    }

    public void setItemMap(Map<String, Object> itemMap) {
        this.itemMap = itemMap;
    }
}