package com.sectong.serviceimpl;


import com.sectong.service.PayService;
import com.sectong.service.RESTClient;
import com.sectong.util.alipayutil.AlipayConfig;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2016/11/14.
 */

@Service
public class PayServiceImpl implements PayService {

    private static Logger logger = Logger.getLogger(PayServiceImpl.class);
    private RESTClient rest = new RESTClient();

    @Override
    public String pay_zfb(JSONObject reportMap) {

        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", AlipayConfig.service);
        sParaTemp.put("seller_id", AlipayConfig.seller_id);
        sParaTemp.put("payment_type", AlipayConfig.payment_type);
        sParaTemp.put("return_url", AlipayConfig.return_url);
        sParaTemp.put("anti_phishing_key", AlipayConfig.anti_phishing_key);
        sParaTemp.put("exter_invoke_ip", AlipayConfig.exter_invoke_ip);
        sParaTemp.put("out_trade_no", "zfbljwj" + rest.order_id()); //商户订单号，商户网站订单系统中唯一订单
        sParaTemp.put("subject", reportMap.get("subject").toString()); //订单名称，必填
        sParaTemp.put("total_fee", reportMap.get("total_fee").toString()); //付款金额，必填
        sParaTemp.put("body", reportMap.get("body").toString()); //商品描述，可空
        //其他业务参数根据在线开发文档，添加参数.文档地址:https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.O9yorI&treeId=62&articleId=103740&docType=1
        //如sParaTemp.put("参数名","参数值");
        //建立请求
//        String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
        // 输出到页面就可以了
        return rest.public_zfb(sParaTemp);
    }


    @Override
    public String refund_zfb(JSONObject reportMap) {

        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", AlipayConfig.refund_service);
        sParaTemp.put("seller_user_id", AlipayConfig.seller_user_id);
        sParaTemp.put("refund_date", AlipayConfig.refund_date);
        //批次号，必填，格式：当天日期[8位]+序列号[3至24位]，如：201603081000001
        sParaTemp.put("batch_no", rest.pay_date() + "refund" + rest.order_id());
        //退款笔数，必填，参数detail_data的值中，“#”字符出现的数量加1，最大支持1000笔（即“#”字符出现的数量999个）
        sParaTemp.put("batch_num", reportMap.get("WIDbatch_num").toString());
        //退款详细数据，必填，格式（支付宝交易号^退款金额^备注），多笔请用#隔开
        sParaTemp.put("detail_data", reportMap.get("WIDdetail_data").toString());
        //建立请求
//        String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
        return rest.public_zfb(sParaTemp);
    }

    @Override
    public String payment_zfb(JSONObject reportMap) {
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", AlipayConfig.payment_service);
        sParaTemp.put("email", AlipayConfig.zfb_email);
        sParaTemp.put("account_name", AlipayConfig.zfb_account_name);
        //付款当天日期 必填，格式：年[4位]月[2位]日[2位]，如：20100801
        sParaTemp.put("pay_date", rest.pay_date());
        //批次号 必填，格式：当天日期[8位]+序列号[3至16位]，如：201008010000001
        sParaTemp.put("batch_no", rest.pay_date() + rest.order_id());
        //付款总金额 必填，即参数detail_data的值中所有金额的总和
        sParaTemp.put("batch_fee", reportMap.get("batch_fee").toString());
        //付款笔数 必填，即参数detail_data的值中，“|”字符出现的数量加1，最大支持1000笔（即“|”字符出现的数量999个）
        sParaTemp.put("batch_num", reportMap.get("batch_num").toString());
        //付款详细数据 必填，格式：流水号1^收款方帐号1^真实姓名^付款金额1^备注说明1|流水号2^收款方帐号2^真实姓名^付款金额2^备注说明2....
        sParaTemp.put("detail_data", "payment" + rest.order_id() + "^" + reportMap.get("detail_data").toString());
        //建立请求
//        String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
        return rest.public_zfb(sParaTemp);
    }


}
