package com.sectong.controller;

import com.sectong.service.PayService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by admin on 2016/12/1.
 */

@Controller
@RequestMapping(value = "/demo")
public class ajaxController {

    @Autowired
    private PayService payService;

    @RequestMapping(value = "/text", method = RequestMethod.GET)
    public ModelAndView pay_payment(HttpServletRequest request) throws Exception {


        String out_trade_no = new String(request.getParameter("sign").getBytes("ISO-8859-1"), "UTF-8");

        //支付宝交易号

        String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

        //交易状态
        String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
        ModelAndView mv = new ModelAndView();

        mv.addObject("out_trade_no", out_trade_no);
        mv.addObject("trade_no", trade_no);
        mv.addObject("trade_status", trade_status);
        mv.setViewName("/mypay1");
        return mv;
    }

    @RequestMapping(value = "/payzfb1", method = RequestMethod.GET)
    @ResponseBody
    public String zfb_pay1() throws Exception {
        String js1 = "{\"subject\" : \"圣诞测试\",\"total_fee\" : \"0.01\",\"body\":\"我就是测试\"}";
        JSONObject js = new JSONObject(js1);
        String resuly = payService.pay_zfb(js);
        return resuly;
    }

    @RequestMapping(value = "/refundzfb1", method = RequestMethod.GET)
    @ResponseBody
    public String zfb_refund() throws Exception {
        String js1 = "{\"WIDbatch_no\":\"20161227101936abc\",\"WIDbatch_num\":\"1\",\"WIDdetail_data\":\"2016122621001004330285758984^0.01^hellopay\"}";
        JSONObject js = new JSONObject(js1);
        String resuly = payService.refund_zfb(js);
        return resuly;
    }


    @RequestMapping(value = "/paymentzfb1", method = RequestMethod.GET)
    @ResponseBody
    public String payment_zfb1() throws Exception {
        String js1 = "{\"batch_fee\":\"0.07\",\"batch_num\":\"1\",\"detail_data\":\"13137408018^王亚飞^0.07^hellopay\"}";
        JSONObject js = new JSONObject(js1);
        String resuly = payService.payment_zfb(js);
        return resuly;
    }


}
