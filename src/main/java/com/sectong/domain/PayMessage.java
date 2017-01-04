package com.sectong.domain;

import org.springframework.data.annotation.Id;

/**
 * Created by admin on 2016/12/6.
 */
public class PayMessage {

    @Id
    private String id;

    private String orderid;
    private String openid;
    private String odoouserid;

    public PayMessage() {
    }

    public PayMessage(String orderid, String openid, String odoouserid) {
        this.orderid = orderid;
        this.openid = openid;
        this.odoouserid = odoouserid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getIssubscribe() {
        return odoouserid;
    }

    public void setIssubscribe(String odoouserid) {
        this.odoouserid = odoouserid;
    }
}
