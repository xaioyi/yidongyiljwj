package com.sectong.controller;

import com.sectong.domain.PayMessage;
import com.sectong.message.Message;
import com.sectong.service.MessageService;
import com.sectong.service.PayService;
import com.sectong.service.RESTClient;
import com.sectong.util.PayCommonUtil;
import com.sectong.util.PayConfigUtil;
import com.sectong.util.XMLUtil;
import com.sectong.util.alipayutil.AlipayNotify;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by admin on 2016/12/1.
 */

@RestController
@RequestMapping(value = "/ljwjpay", name = "mongodb服务API")
public class DemoController {

    private static Logger logger = Logger.getLogger(DemoController.class);

    private RESTClient restClient;
    private MessageService messageService;
    private PayService payService;
    private Message message = new Message();

    @Autowired
    public DemoController(RESTClient restClient, MessageService messageService, PayService payService) {
        this.restClient = restClient;
        this.messageService = messageService;
        this.payService = payService;
    }

    @RequestMapping(value = "/pay", method = RequestMethod.GET)
    @ApiOperation(value = "提供生成二维码", notes = "根据请求生成二维码并返回给请求者")
    public ResponseEntity<Message> pay_two(@RequestParam String account_id, @RequestParam String price, @RequestParam String notice) throws Exception {
        restClient.ACCOUNT_ID = account_id;
        List<String> str = null;
        try {
            str = restClient.weixin_pay(price, notice);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (str != null) message.setMsg(1, "Creating a successful.", str);
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }


    @RequestMapping(value = "/Notify", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "回吊函数", notes = "付款成功执行此方法")
    public String pay_two_mydemo(HttpServletRequest request) throws Exception {

        String restlt = null;
        InputStream inputStream;
        StringBuffer sb = new StringBuffer();
        inputStream = request.getInputStream();
        String s;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        while ((s = in.readLine()) != null) {
            sb.append(s);
        }
        in.close();
        inputStream.close();
        //解析xml成map
        Map<String, String> m = new HashMap<String, String>();
        try {
            m = XMLUtil.doXMLParse(sb.toString());
        } catch (JDOMException e) {
            e.printStackTrace();
        }
        //过滤空 设置 TreeMap
        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
        Iterator it = m.keySet().iterator();
        while (it.hasNext()) {
            System.out.println();
            String parameter = (String) it.next();
            String parameterValue = m.get(parameter);
            String v = "";
            if (null != parameterValue) v = parameterValue.trim();
            packageParams.put(parameter, v);
        }
        // 账号信息
        String key = PayConfigUtil.API_KEY; // key
        logger.info(packageParams);
        //判断签名是否正确
        if (PayCommonUtil.isTenpaySign("UTF-8", packageParams, key)) {
            //------------------------------
            //处理业务开始
            //------------------------------
            String resXml = "";
            if ("SUCCESS".equals((String) packageParams.get("result_code"))) {
                // 这里是支付成功
                //////////执行自己的业务逻辑////////////////
                String mch_id = (String) packageParams.get("mch_id");
                String openid = (String) packageParams.get("openid");
                String is_subscribe = (String) packageParams.get("is_subscribe");//是否关注公众号 Y/N
                String out_trade_no = (String) packageParams.get("out_trade_no");
                String total_fee = (String) packageParams.get("total_fee");
                logger.info("mch_id:" + mch_id);
                logger.info("openid:" + openid);
                logger.info("is_subscribe:" + is_subscribe);
                logger.info("out_trade_no:" + out_trade_no);
                logger.info("total_fee:" + total_fee);

                List<PayMessage> rest = messageService.get_wx_openid(restClient.ACCOUNT_ID);

                if (rest.size() <= 0) {
                    // TODO 保存用户 opendid 信息到数据库
                    PayMessage payMessage = new PayMessage(out_trade_no, openid, restClient.ACCOUNT_ID);
                    messageService.savepay(payMessage);
                    logger.info("****************:" + payMessage);
                }
                restClient.ACCOUNT_ID = "";

                logger.info("支付成功+123456");
                //通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
                resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                        + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
            } else {
                logger.info("支付失败,错误信息：" + packageParams.get("err_code"));
                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                        + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            }
            //------------------------------
            //处理业务完毕
            //------------------------------
            restlt = resXml;
        } else {
            logger.info("通知签名验证失败");
        }
        logger.info("jieshu");
        return restlt;
    }


    @RequestMapping(value = "/selectpay", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询订单信息", notes = "根据订单号查询订单支付状态与订单详情")
    public ResponseEntity<Message> pay_select1(@RequestParam String orderid) throws Exception {
        // 查询订单
        Map result = restClient.select_order_status(orderid, "select");
        String statu2 = result.get("trade_state").toString();
        if (result.get("result_code").equals("SUCCESS")) {
            String statu1 = "";
            if (!result.get("trade_state").equals("SUCCESS"))
                statu1 = result.get("trade_state_desc").toString();
            message.setMsg(1, statu1, result.get("trade_state"));
        } else
            message.setMsg(0, result.get("err_code_des").toString(), result.get("err_code"));
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }

    @RequestMapping(value = "/closepay", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "关闭支付交易", notes = "根据订单号关闭订单")
    public ResponseEntity<Message> pay_close_order(@RequestParam String orderid) throws Exception {
        // 关闭订单
        Map result = restClient.select_order_status(orderid, "close");
        if (result.get("result_code").equals("FAIL"))
            message.setMsg(0, result.get("err_code_des").toString(), result.get("err_code"));
        else message.setMsg(1, "", result.get("result_code"));
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }


    @RequestMapping(value = "/refund", method = RequestMethod.GET)
    @ApiOperation(value = "退款", notes = "根据输入的商品单号与金额回退资金")
    public ResponseEntity<Message> pay_select(@RequestParam String orderid, @RequestParam String price) throws Exception {
        List result_refund = restClient.order_refund(orderid, price);
        int result_refund_id = 0;
        if (result_refund.get(0).equals("SUCCESS")) result_refund_id = 1;
        message.setMsg(result_refund_id, result_refund.get(0).toString(), result_refund.get(1));
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }

    @RequestMapping(value = "/payment", method = RequestMethod.GET)
    @ApiOperation(value = "企业付款", notes = "根据输入的opendid与金额付款到微信零钱")
    public ResponseEntity<Message> pay_payment(@RequestParam String account_id, @RequestParam String price, @RequestParam String note) throws Exception {
        String opendid = "";
        List<PayMessage> res = messageService.get_wx_openid(account_id);
        if (res.size() <= 0) {
            message.setMsg(0, "", "此用户没有预留，提现目标微信");
            return new ResponseEntity<Message>(message, HttpStatus.OK);
        }
        opendid = res.get(0).getOpenid();
        List result_payment = restClient.enterprise_payment(opendid, price, note);
        int result_id = 0;
        if (result_payment.get(0).equals("SUCCESS")) result_id = 1;
        message.setMsg(result_id, result_payment.get(0).toString(), result_payment.get(1));
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }


    @RequestMapping(value = "/payzfb", method = RequestMethod.GET)
    @ApiOperation(value = "通过支付宝付款", notes = "输入相关参数然后充值到个人账户（商城钱包）")
    public ResponseEntity<Message> zfb_pay(@RequestParam JSONObject reportMap) throws Exception {
        String resuly = payService.pay_zfb(reportMap);
        if (resuly != null) message.setMsg(1, "请求发起成功", resuly);
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }


    @RequestMapping(value = "/refundzfb", method = RequestMethod.GET)
    @ApiOperation(value = "通过支付宝退款", notes = "输入订单相关信息向用户退款")
    public ResponseEntity<Message> zfb_refund(@RequestParam JSONObject reportMap) throws Exception {
        String resuly = payService.refund_zfb(reportMap);
        if (resuly != null) message.setMsg(1, "请求发起成功", resuly);
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }


    @RequestMapping(value = "/paymentzfb", method = RequestMethod.GET)
    @ApiOperation(value = "通过支付宝转账", notes = "输入用户相关信息转账给N个客户")
    public ResponseEntity<Message> payment_zfb(@RequestParam JSONObject reportMap) throws Exception {
        String resuly = payService.payment_zfb(reportMap);
        if (resuly != null) message.setMsg(1, "请求发起成功", resuly);
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }


    @RequestMapping(value = "/Notifyzfb", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "支付宝回吊函数", notes = "付款成功执行此方法，异步通知支付宝")
    public String Notifyzfb(HttpServletRequest request) throws Exception {
        String resu = "";
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
        //商户订单号

//        String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
//        //支付宝交易号
//        String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
        //交易状态
        String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

        if (AlipayNotify.verify(params)) {//验证成功
            //////////////////////////////////////////////////////////////////////////////////////////
            //请在这里加上商户的业务逻辑程序代码

            //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——

            if (trade_status.equals("TRADE_FINISHED")) {
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
            } else if (trade_status.equals("TRADE_SUCCESS")) {
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                //如果有做过处理，不执行商户的业务程序
                //注意：
                //付款完成后，支付宝系统发送该交易状态通知
            }
            //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
//            out.print("success");    //请不要修改或删除
            resu = "success";
            //////////////////////////////////////////////////////////////////////////////////////////
        } else {//验证失败
//            out.print("fail");
            resu = "fail";
        }
        return resu;
    }


}
