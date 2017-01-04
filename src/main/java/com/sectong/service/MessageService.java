package com.sectong.service;

import com.sectong.domain.PayMessage;
import com.sectong.domain.UserLocation;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public interface MessageService {

    UserLocation SaveAa(JSONObject reportMap, String telephone);

    String getLocation(String location_time, String telephone);

    String backup_msg();

    JSONArray get_msg(String id_msg_date, String id_msg, String group_msg);

    List<PayMessage> get_wx_openid(String userid);

    PayMessage savepay(PayMessage payMessage);

}
