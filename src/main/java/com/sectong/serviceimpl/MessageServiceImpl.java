package com.sectong.serviceimpl;


import com.sectong.domain.PayMessage;
import com.sectong.domain.UserLocation;
import com.sectong.domain.UserMessages;
import com.sectong.repository.LocationRepository;
import com.sectong.repository.MessageRepository;
import com.sectong.repository.PayRepository;
import com.sectong.service.MessageService;
import com.sectong.service.RESTClient;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/11/14.
 */

@Service
public class MessageServiceImpl implements MessageService {


    private static Logger logger = Logger.getLogger(MessageServiceImpl.class);

    private MessageRepository messageRepository;
    private PayRepository payRepository;
    private LocationRepository locationRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, PayRepository payRepository, LocationRepository locationRepository) {
        this.messageRepository = messageRepository;
        this.payRepository = payRepository;
        this.locationRepository = locationRepository;
    }

    private RESTClient rest = new RESTClient();


    @Override
    public UserLocation SaveAa(JSONObject reportMap, String telephone) {
        UserLocation location_id = null;
//        JSONObject reportMap = new JSONObject(reportJson);

//        logger.info("*******************" + reportMap.get("mytype").toString() + "*******************");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String location_time = simpleDateFormat.format(new Date());
        String myaddress = rest.location_xy(reportMap.get("Longitude").toString(), reportMap.get("Latitude").toString());
        reportMap.put("myaddress", myaddress);
        JSONObject location_msg = new JSONObject();
        JSONArray ja01 = new JSONArray();
        List<UserLocation> usermsg = locationRepository.findByTelephone(telephone);
        if (usermsg.size() == 0) {
            System.out.println("新的用户");
            ja01.put(reportMap);
            location_msg.put(location_time, ja01);
            UserLocation s = new UserLocation(telephone, location_msg.toString());
            location_id = locationRepository.save(s);
        } else {
            System.out.println("追加定位");
            UserLocation location_data = usermsg.get(0);
            JSONObject jsono = new JSONObject(location_data.getLocations());
            JSONArray jsona = jsono.optJSONArray(location_time);
            if (jsona == null) {
                System.out.println("新的一天");
                ja01.put(reportMap);
                jsono.put(location_time, ja01);
            } else {
                System.out.println("追加定位");
                jsona.put(reportMap);
            }
            location_data.setLocations(jsono.toString());
            location_id = locationRepository.save(location_data);
        }
        return location_id;
    }

    @Override
    public String getLocation(String location_time, String telephone) {
        List<UserLocation> usermsg = locationRepository.findByTelephone(telephone);
        JSONArray jsona = null;
        if (usermsg.size() > 0) {
            UserLocation location_data = usermsg.get(0);
            JSONObject jsono = new JSONObject(location_data.getLocations());
            jsona = jsono.optJSONArray(location_time);
            if (jsona == null)
                return "The user in the specified date no location information.";
        } else return "This user does not exist positioning information.";
        return jsona.toString();
    }

    @Override
    public String backup_msg() {
        int number_id = 0;
        String fdate = rest.format_yesterday();
        List message_list = new ArrayList();
        if (!rest.BACKUP_MSG_ID.equals(fdate)) {
            ResponseEntity<Map> result = rest.getMessageAll(rest.BACKUP_URL + "begin_time=" + fdate + " 00:00:00&end_time=" + fdate + " 23:59:59");
            message_list = (List) result.getBody().get("messages");
            number_id = message_list.size();
            if (message_list.size() >= 0) rest.BACKUP_MSG_ID = fdate;
        }
        for (int i = 0; i < number_id; i++) {
            UserMessages messqge_data = new UserMessages((Map) message_list.get(i));
            JSONObject msg_body = rest.parse_msg_body(messqge_data, messqge_data.getMsgbody());
            List<UserMessages> usermsg = messageRepository.findByTargetid(messqge_data.getTargetid());//还需要考虑数据库里面Targetid是否单人
            if (usermsg.size() == 1) {
                System.out.println("已经存在，追加数据");
                if (messqge_data.getTargettype().equals("group")) {
                    UserMessages msg_append = usermsg.get(0);
                    JSONObject msg_data = new JSONObject(msg_append.getMsgdata());
                    JSONArray db_msg_data = msg_data.optJSONArray(messqge_data.getMsgctime().substring(0, 7));
                    if (db_msg_data != null) {
                        System.out.println("月份数据已存在");
                        db_msg_data.put(rest.format_db_msg(messqge_data, msg_body));
                    } else {
                        System.out.println("月份数据不存在");
                        JSONArray db_msg_list = new JSONArray();
                        db_msg_list.put(rest.format_db_msg(messqge_data, msg_body));
                        msg_data.put(messqge_data.getMsgctime().substring(0, 7), db_msg_list);
                    }
                    msg_append.setMsgdata(msg_data.toString());
                    messageRepository.save(msg_append);
                } else {
                    System.out.println("单人聊天，追加信息");
                }
            } else if (usermsg.size() == 2) {
                System.out.println("已经有了，单人与组的的备份信息，需要处理");
            } else {
                System.out.println("不存在，新建数据");
                if (messqge_data.getTargettype().equals("group")) {
                    JSONObject db_msg_month_id = new JSONObject();
                    JSONArray db_msg_list = new JSONArray();
                    db_msg_list.put(rest.format_db_msg(messqge_data, msg_body));
                    db_msg_month_id.put(messqge_data.getMsgctime().substring(0, 7), db_msg_list);
                    messqge_data.setMsgdata(db_msg_month_id.toString());
                    messageRepository.save(messqge_data);
                } else {
                    System.out.println("我是对单人聊天，单人聊天数据Targetid不能与组相同");
                }
            }
        }
        System.out.println("结束");
        // 清空常量
        rest.clear_constant();
        return null;
    }

    @Override
    public JSONArray get_msg(String id_msg_date, String res, String group_msg) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String res = simpleDateFormat.format(new Date()).substring(0, 7);
        // 方法验证id_msg_date输入格式是否正确
        if (rest.format_date_str(id_msg_date)) res = id_msg_date;
        JSONArray array_data1 = new JSONArray();
        // 可以优化 每个组只查一次
        List<UserMessages> usermsg = messageRepository.findByTargetid(group_msg);
        if (usermsg.size() == 1 && usermsg.get(0).getTargettype().equals("group")) {
            JSONObject json_data = new JSONObject(usermsg.get(0).getMsgdata());
            if (json_data.opt(res) == null) {
                System.out.println("本月没有数据");
                // 等同于有数据但 不满足条数
                array_data1.put("没有数据");
            } else {
                if (rest.format_date_str(id_msg_date))
                    array_data1 = json_data.optJSONArray(res);
                else array_data1 = rest.hua_msg(json_data, res);
            }
        }
        if (rest.format_date_str(id_msg_date)) rest.clear_constant();
        return array_data1;
    }

    @Override
    public List<PayMessage> get_wx_openid(String userid) {
        List<PayMessage> pay_list = payRepository.findByOdoouserid(userid);
        return pay_list;
    }

    @Override
    public PayMessage savepay(PayMessage payMessage) {
        return payRepository.save(payMessage);
    }

}
