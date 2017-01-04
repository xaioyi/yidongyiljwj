package com.sectong.demo;

import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by admin on 2016/11/18.
 */
public class jiguang {
    public static String partner = "2088421331410953";

    // 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
    public static String seller_user_id = partner;

    // MD5密钥，安全检验码，由数字和字母组成的32位字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
    public static String key = "jao24fggv7zeihus2twqhnw4gw4o0e0v";


    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://139.196.219.204:8081/weixin02_war/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://139.196.219.204:8081/weixin02_war/return_url.jsp";

    // 签名方式
    public static String sign_type = "MD5";

    // 调试用，创建TXT日志文件夹路径，见AlipayCore.java类中的logResult(String sWord)打印方法。
    public static String log_path = "G:\\log\\";

    // 字符编码格式 目前支持 gbk 或 utf-8
    public static String input_charset = "utf-8";

    // 调用的接口名，无需修改
    public static String service = "refund_fastpay_by_platform_pwd";

    public static JSONObject parse_msg_body(String data) {
        String body_data = data.replace('/', '-').replace('=', ':');
        JSONObject json_body_data = new JSONObject(body_data);
        json_body_data.put("media_id", json_body_data.get("media_id").toString().replace('-', '/'));
        return json_body_data;
    }


    public static String getDateFormatter() {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }


    public static String buildRequestMysign(Map<String, String> sPara) {
        String prestr = createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String mysign = "";
        mysign = MD51.sign(prestr, "jao24fggv7zeihus2twqhnw4gw4o0e0v", "utf-8");
        return mysign;
    }


    public static String getMessageAll() {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        HttpEntity<String> formEntity = new HttpEntity<String>(headers);

        //参数使用MAP传递
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", service);
        sParaTemp.put("partner", partner);
        sParaTemp.put("_input_charset", input_charset);
        sParaTemp.put("notify_url", notify_url);
        sParaTemp.put("seller_user_id", seller_user_id);
        sParaTemp.put("refund_date", getDateFormatter());
        sParaTemp.put("batch_no", "201603081000001");
        sParaTemp.put("batch_num", "1");
        sParaTemp.put("detail_data", "2016122621001004330284992882^0.01^退款测试");


        sParaTemp.put("sign", buildRequestMysign(sParaTemp));
        sParaTemp.put("sign_type", "MD5");


        String message2 = restTemplate.getForObject("https://mapi.alipay.com/gateway.do?_input_charset=utf-8", String.class, sParaTemp);

//        ResponseEntity<Map> result = restTemplate.exchange(ljwjurl, HttpMethod.GET, formEntity, Map.class);

        System.out.println(message2);


        return "";
    }


    public static void main(String[] args) {



        getMessageAll();

        Map map = new HashMap();
        map.put("a", 11);
        map.put("b", 12);
        map.put("c", 13);
        String asd = "";

        asd = map.get("d") == null ? map.get("a").toString() : map.get("b").toString();

        System.out.println(asd);


    }


    public static String stampToDate(String str) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        long ltt = new Long(str);
        Date date = new Date(ltt);

        String res = simpleDateFormat.format(date);
        return res;
    }


    private static Gson gson = new Gson();

//    public static void main(String[] args) {
//
//        String aasd = "-1";
//        System.out.println(aasd.matches("[0-2]0[1-2][0-9]-[0-1][0-9]"));
//
//        if (1 > 2 || 2 > 1) System.out.println(111);
//
//
//        System.out.println(stampToDate("1477449724629"));
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//
//        String res = simpleDateFormat.format(new Date()).substring(0, 7);
//
//        System.out.println(res);
//        String sssss = null;
//
//        String[] asdfg = res.split("-");
//
//        int asd = Integer.parseInt(asdfg[1]);
//        if (asd - 1 == 0) {
//            sssss = (Integer.parseInt(asdfg[0]) - 1) + "-" + 12;
//            System.out.println(sssss);
//
//        } else {
//            if (asd - 1 < 10) {
//                sssss = asdfg[0] + "-0" + (asd - 5);
//            }
//            sssss = asdfg[0] + "-" + (asd - 1);
//            System.out.println(sssss);
//        }
//
//
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//            Date date = sdf.parse("2008-08-08");
//
//            System.out.println(simpleDateFormat.format(date));
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//
////        int iias = 100;
////
////        for (int ii = 0; ii < 2; ii++) {
////
////            for (int i = 0; i < 5; i++) {
////                System.out.println(iias - i);
////                if (i == 4) {
////                    iias = iias - 1 - i;
////                    System.out.println(iias + "***************");
////                }
////            }
////        }
//
//
////        JSONObject aass = new JSONObject();
////        JSONArray jsonArray = aass.optJSONArray("2016-111");
////        JSONObject jsonobject2 = jsonArray.optJSONObject(0);
////        String text = jsonobject2.optString("text");
////        System.out.println(text);
//
//
//    }

}
