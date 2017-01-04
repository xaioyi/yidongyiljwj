package com.sectong.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sectong.domain.UserMessages;
import com.sectong.util.HttpUtil;
import com.sectong.util.PayCommonUtil;
import com.sectong.util.PayConfigUtil;
import com.sectong.util.XMLUtil;
import com.sectong.util.alipayutil.AlipayConfig;
import com.sectong.util.alipayutil.AlipaySubmit;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by admin on 2016/11/16.
 */

@Component
public class RESTClient {
    private static Logger logger = Logger.getLogger(RESTClient.class);

    private RestTemplate template = new RestTemplate();

    //每次滑屏规定请求的条数
    public static final int NUMBER_ITEM = 3;
    // 每次请求后的指针位置
    public static int NUMBER_ID = -100;
    // 消息月数标识
    public static String MSG_MONTH_TIME = null;
    // 组信息 组id
    public static String MSG_GROUP_ID = null;
    // img 保存地址
    public static String MSG_IMG_ADDRESS = "WEB-INF/classes/public/backups/";
    // 本地服务器图片地址
    public final static String MY_MSG_IMG_ADDRESS = "http://139.196.219.204:8081/yidongyiljwj/backups/";
    // 证书地址
//    public final static String MY_CERTFICATE_ADDRESS = "G:/apiclient_cert.p12";
    public final static String MY_CERTFICATE_ADDRESS = "/ljwjpay/apiclient_cert.p12";
    // 执行备份的日期 （昨天）
    public static String BACKUP_MSG_ID = "";
    // odoo 发起支付着 账户
    public static String ACCOUNT_ID = "";
    // 拉去备份数据 url
    public final static String BACKUP_URL = "https://report.im.jpush.cn/v2/messages?count=1000&";

    // 测试个人
//    private final static String ljwjbasic = "Basic NDVhYmJjMGIyOTlkODNhY2FhMzNhYzNhOjdjYzA5ZTIyZTM0NDJkOGVjMjhlYTdhOQ==";
    // 公司申请的
    private final static String ljwjbasic = "Basic YmI5N2NjZWNkOTA1YTNmZWQ1NGU2NjM0OjlhOGVlNTc4OTZhYzQxZGM5NDA1YzM5ZQ==";

    private final static String url1 = "http://restapi.amap.com/v3/geocode/regeo?key=20b03eb082ad8beee9d3764cc19a8fa9&location=";

    public void format_month() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        MSG_MONTH_TIME = simpleDateFormat.format(new Date()).substring(0, 7);
    }

    public void clear_constant() {
        MSG_GROUP_ID = null;
        MSG_MONTH_TIME = null;
        NUMBER_ID = -100;
        MSG_IMG_ADDRESS = "WEB-INF/classes/public/backups/";
    }

    public Boolean format_date_str(String id_msg_date) {
        return id_msg_date.matches("[0-2]0[1-2][0-9]-[0-1][0-9]");
    }


    public void month_msg() {
        String[] asdfg = MSG_MONTH_TIME.split("-");
        if ((Integer.parseInt(asdfg[1]) - 1) == 0) {
            MSG_MONTH_TIME = (Integer.parseInt(asdfg[0]) - 1) + "-" + 12;
        } else {
            if ((Integer.parseInt(asdfg[1]) - 1) < 10)
                MSG_MONTH_TIME = asdfg[0] + "-0" + (Integer.parseInt(asdfg[1]) - 1);
            else
                MSG_MONTH_TIME = asdfg[0] + "-" + (Integer.parseInt(asdfg[1]) - 1);
        }
        NUMBER_ID = -100;
    }

    public String format_yesterday() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fdate = simpleDateFormat.format(new Date());
        int day = Integer.parseInt(fdate.substring(fdate.length() - 2));
        fdate = fdate.replace(day + "", (day - 1) + "");
        return fdate;
    }

    public String location_xy(String Longitude, String Latitude) {

        String result = template.getForObject(url1 + Longitude + "," + Latitude, String.class);
        Gson gson = new Gson();
        Map maps = gson.fromJson(result, Map.class);
        System.out.println(maps.get("regeocode"));
        JsonObject user1 = (JsonObject) gson.toJsonTree(maps.get("regeocode"));
//        String res=user1.get("formatted_address").toString();

        return user1.get("formatted_address").toString();
    }

    public static JSONObject parse_msg_body(UserMessages messqge_data, String data) {
        String body_data = data.replace('/', '-').replace('=', ':');
        JSONObject json_body_data = new JSONObject(body_data);
        if (!messqge_data.getMsgtype().equals("text"))
            json_body_data.put("media_id", json_body_data.get("media_id").toString().replace('-', '/'));
        return json_body_data;
    }


    public static JSONObject format_db_msg(UserMessages messqge_data, JSONObject msg_body) {

        JSONObject db_messages_month = new JSONObject();
        db_messages_month.put("form_user", messqge_data.getFromid());
        db_messages_month.put("msg_time", messqge_data.getMsgctime());
        if (messqge_data.getMsgtype().equals("image") || messqge_data.getMsgtype().equals("voice")) {//media_id
            //判断是否 img 改变url 保存图片到本地
            ResponseEntity<Map> result = getMessageAll("https://api.im.jpush.cn/v1/resource?mediaId=" + msg_body.get("media_id").toString());
            String turn_url = result.getBody().get("url").toString();
//            String sdfgh = request.getServletContext().getRealPath("/") + "WEB-INF/classes/public/backups/" + "ok111.jpg";
            String media_name = messqge_data.getFromname() + String.valueOf(System.currentTimeMillis());
            if (msg_body.get("media_id").toString().indexOf("image") == -1) {
                media_name = "voice/" + media_name + ".mp3";
            } else {
                media_name = "img/" + media_name + ".jpg";
            }
            download_url(turn_url, MSG_IMG_ADDRESS + media_name);

//            http://139.196.219.204:8081/yidongyiljwj/backups/img/182050525791480317640409.jpg

            db_messages_month.put("msg", MY_MSG_IMG_ADDRESS + media_name);

        } else if (messqge_data.getMsgtype().equals("text")) {//text
            db_messages_month.put("msg", msg_body.get("text"));
        }
        return db_messages_month;
    }


    public static JSONArray hua_msg(JSONObject json_data, String res) {

        JSONArray array_data1 = new JSONArray();
        JSONArray array_data = json_data.optJSONArray(res);
        if (NUMBER_ID == -100) NUMBER_ID = array_data.length();
        for (int i = 1; i < NUMBER_ITEM; i++) {
            array_data1.put(array_data.optJSONObject(NUMBER_ID - i));
            if (i == (NUMBER_ITEM - 1)) NUMBER_ID = NUMBER_ID - i;
        }
        return array_data1;
    }


    public static ResponseEntity<Map> getMessageAll(String ljwjurl) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", ljwjbasic);
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
//        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> formEntity = new HttpEntity<String>(headers);
        ResponseEntity<Map> result = restTemplate.exchange(ljwjurl, HttpMethod.GET, formEntity, Map.class);
        return result;
    }


    public static void download_url(String url_url, String path) {
        URL website = null;
        try {
            website = new URL(url_url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(path);//例如：test.txt
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static SortedMap<Object, Object> pay_entity() {

        // 参数：appid mch_id nonce_str

        // 账号信息
        String appid = PayConfigUtil.APP_ID;  // appid
        //String appsecret = PayConfigUtil.APP_SECRET; // appsecret
        String mch_id = PayConfigUtil.MCH_ID; // 商业号

        String currTime = PayCommonUtil.getCurrTime();
        String strTime = currTime.substring(8, currTime.length());
        String strRandom = PayCommonUtil.buildRandom(4) + "";
        String nonce_str = strTime + strRandom;// 随机数

        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
        packageParams.put("appid", appid);
        packageParams.put("mch_id", mch_id);
        packageParams.put("nonce_str", nonce_str);


        return packageParams;
    }

    public List<String> weixin_pay(String price, String notice) throws Exception {
//        // 账号信息
//        String appid = PayConfigUtil.APP_ID;  // appid
//        //String appsecret = PayConfigUtil.APP_SECRET; // appsecret
//        String mch_id = PayConfigUtil.MCH_ID; // 商业号
//        String key = PayConfigUtil.API_KEY; // key
//
//        String currTime = PayCommonUtil.getCurrTime();
//        String strTime = currTime.substring(8, currTime.length());
//        String strRandom = PayCommonUtil.buildRandom(4) + "";
//        String nonce_str = strTime + strRandom;
//
//        String order_price = "1"; // 价格   注意：价格的单位是分
//        String body = "测试单";   // 商品名称
//        String out_trade_no = order_id(); // 订单号
//
//        // 获取发起电脑 ip
//        String spbill_create_ip = PayConfigUtil.CREATE_IP;
//        // 回调接口
//        String notify_url = PayConfigUtil.NOTIFY_URL;
//        String trade_type = "NATIVE";
//
//        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
//        packageParams.put("appid", appid);
//        packageParams.put("mch_id", mch_id);
//        packageParams.put("nonce_str", nonce_str);
//        packageParams.put("body", body);
//        packageParams.put("out_trade_no", out_trade_no);
//        packageParams.put("total_fee", order_price);
//        packageParams.put("spbill_create_ip", spbill_create_ip);
//        packageParams.put("notify_url", notify_url);
//        packageParams.put("trade_type", trade_type);
//
//        String sign = PayCommonUtil.createSign("UTF-8", packageParams, key);
//        packageParams.put("sign", sign);

        String key = PayConfigUtil.API_KEY; // key
        String order_price = price; // 价格   注意：价格的单位是分
//        String body = notice;   // 商品名称
        String out_trade_no = order_id(); // 订单号
        // 获取发起电脑 ip
        String spbill_create_ip = PayConfigUtil.CREATE_IP;
        // 回调接口
        String notify_url = PayConfigUtil.NOTIFY_URL;
        String trade_type = "NATIVE";

        SortedMap<Object, Object> packageParams = pay_entity();
        packageParams.put("body", notice);
        packageParams.put("out_trade_no", out_trade_no);
        packageParams.put("total_fee", order_price);
        packageParams.put("spbill_create_ip", spbill_create_ip);
        packageParams.put("notify_url", notify_url);
        packageParams.put("trade_type", trade_type);

        String sign = PayCommonUtil.createSign("UTF-8", packageParams, key);
        packageParams.put("sign", sign);


        String requestXML = PayCommonUtil.getRequestXml(packageParams);
        System.out.println(requestXML);

        String resXml = HttpUtil.postData(PayConfigUtil.UFDODER_URL, requestXML);


        Map map = XMLUtil.doXMLParse(resXml);
        //String return_code = (String) map.get("return_code");
        //String prepay_id = (String) map.get("prepay_id");//微信生成的预支付订单ID
        String urlCode = (String) map.get("code_url");

        List<String> list = new ArrayList<String>();
        list.add(urlCode);
        list.add(out_trade_no);
        return list;
    }

    public static String order_id() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
        String time = df.format(date);
        System.out.println(time);
        return time;
    }

    public static String pay_date() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String time = df.format(date);
        return time;
    }


    public Map select_order_status(String out_trade_no, String st_se) throws Exception {

        String key = PayConfigUtil.API_KEY; // key
        SortedMap<Object, Object> packageParams = pay_entity();
        packageParams.put("out_trade_no", out_trade_no);

        String sign = PayCommonUtil.createSign("UTF-8", packageParams, key);
        packageParams.put("sign", sign);

        String requestXML = PayCommonUtil.getRequestXml(packageParams);
        System.out.println(requestXML);
        String url = PayConfigUtil.SELECT_URL;

        if (st_se.equals("close")) url = PayConfigUtil.CLOSE_URL;

        String resXml = HttpUtil.postData(url, requestXML);
        Map result = new HashMap();
        Map map = XMLUtil.doXMLParse(resXml);
        String return_code = (String) map.get("return_code");
        String trade_state = (String) map.get("trade_state");
        String trade_state_desc = (String) map.get("trade_state_desc");
        if (return_code.equals("SUCCESS")) result = map;

        return result;
    }


    public List enterprise_payment(String opendid, String many, String note) throws Exception {

        // 企业付款
        // TODO 优化代码
        String appid = PayConfigUtil.APP_ID;  // appid
        String mch_id = PayConfigUtil.MCH_ID; // 商业号
        String currTime = PayCommonUtil.getCurrTime();
        String strTime = currTime.substring(8, currTime.length());
        String strRandom = PayCommonUtil.buildRandom(4) + "";
        String nonce_str = strTime + strRandom;// 随机数
        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();

        packageParams.put("mch_appid", appid);
        packageParams.put("mchid", mch_id);
        packageParams.put("nonce_str", nonce_str);
        packageParams.put("partner_trade_no", "wxljwj" + order_id());// 商户订单号
        packageParams.put("openid", opendid);//
        packageParams.put("check_name", "NO_CHECK");//
        packageParams.put("amount", many);// 企业付款金额，单位为分
        packageParams.put("desc", note);// 企业付款操作说明信息。必填。
        packageParams.put("spbill_create_ip", PayConfigUtil.CREATE_IP);// 调用接口的机器Ip地址
        String key = PayConfigUtil.API_KEY; // key
        String sign = PayCommonUtil.createSign("UTF-8", packageParams, key);
        packageParams.put("sign", sign);
        String requestXML = PayCommonUtil.getRequestXml(packageParams);

        Map map = get_certificate(PayConfigUtil.PAY_USER_URL, requestXML);
        System.out.println(map.get("result_code").toString() + map.get("return_msg").toString());

//        if (map.get("return_code").equals("SUCCESS") || map.get("result_code").equals("SUCCESS")) {
//            result.add(map.get("result_code").toString());
//            result.add(map.get("return_msg").toString());
//        }// 交易是否成功
//        else {
//            result.add(map.get("result_code"));
//            result.add(map.get("return_msg"));
//        }
        List result = new ArrayList();
        result.add(map.get("result_code"));
        result.add(map.get("return_msg"));
        return result;
    }

    // 加载证书
    public static Map get_certificate(String URl, String requestXML) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        File filr = new File(MY_CERTFICATE_ADDRESS);
        Map map = new HashMap();
        //TODO 更改输出方式
        if (!filr.isFile()) System.out.println("证书错误");
        FileInputStream instream = new FileInputStream(filr);//放退款证书的路径
        try {
            keyStore.load(instream, "1407453802".toCharArray());
        } finally {
            instream.close();
        }
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, "1407453802".toCharArray()).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[]{"TLSv1"},
                null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        try {
            HttpPost httpPost = new HttpPost(URl);//企业付款接口
            StringEntity reqEntity = new StringEntity(requestXML, "UTF-8");
            // 设置类型
            reqEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.addHeader("Content-Type", "text/xml");
            httpPost.setEntity(reqEntity);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            System.out.println(response);
            try {
                org.apache.http.HttpEntity entity = response.getEntity();
                String resXml = EntityUtils.toString(entity, "UTF-8");
                map = XMLUtil.doXMLParse(resXml);
            } finally {
                response.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            httpclient.close();
        }
        return map;
    }


    public List order_refund(String out_trade_no, String refund_fee) throws Exception {

//         退款
        SortedMap<Object, Object> packageParams = pay_entity();
        packageParams.put("out_trade_no", out_trade_no);//商品订单号
        packageParams.put("out_refund_no", "No.ljwj" + order_id());// 商户退款单号
        //  订单总金额查询
        Map map1 = select_order_status(out_trade_no, "select");
        packageParams.put("total_fee", map1.get("total_fee"));//订单总金额，单位为分
        packageParams.put("refund_fee", refund_fee);//退款总金额，订单总金额，单位为分
        packageParams.put("op_user_id", "1407453802");//操作员帐号, 默认为商户号

        String key = PayConfigUtil.API_KEY; // key
        String sign = PayCommonUtil.createSign("UTF-8", packageParams, key);
        packageParams.put("sign", sign);
        String requestXML = PayCommonUtil.getRequestXml(packageParams);
        //TODO 代码优化
        Map map = get_certificate(PayConfigUtil.REFUND_URL, requestXML);
        System.out.println(map.get("result_code") + map.get("return_msg").toString());
//        String result1 = null;
//        String return_code = (String) map.get("return_code"); // 命令是否成功
//        if (return_code.equals("SUCCESS")) result1 = (String) map.get("result_code");// 交易是否成功
//        else result1 = map.get("return_msg").toString();
        List result = new ArrayList();
        String resuut = map.get("err_code") == null ? map.get("return_msg").toString() : map.get("err_code_des").toString();
        result.add(map.get("result_code"));
        result.add(resuut);
        return result;
    }

    //为支付宝访问提供支持
    public String public_zfb(Map<String, String> sParaTemp) {
        sParaTemp.put("partner", AlipayConfig.partner);
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
        sParaTemp.put("notify_url", AlipayConfig.notify_url);
        //建立请求
        String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
        return sHtmlText;
    }


    //下面实例demo
    public String addUser(String user) {
        return template.postForObject("url" + "add.do?user={user}", null, String.class, user);
    }

    public String editUser(String user) {
        template.put("url" + "edit.do?user={user}", null, user);
        return user;
    }



    public String removeUser(String id) {
        template.delete("url" + "/remove/{id}.do", id);
        return id;
    }

}
