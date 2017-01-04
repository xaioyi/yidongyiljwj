package com.sectong.service;

import org.json.JSONObject;

public interface PayService {

    String pay_zfb(JSONObject reportMap);


    String refund_zfb(JSONObject reportMap);


    String payment_zfb(JSONObject reportMap);

}
