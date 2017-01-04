package com.sectong.util;

/**
 * Created by admin on 2016/12/7.
 */
public class SradeState {

    public static String SUCCESS = "支付成功";
    public static String REFUND = "转入退款";
    public static String NOTPAY = "未支付";
    public static String CLOSED = "已关闭";
    public static String REVOKED = "已撤销（刷卡支付）";
    public static String USERPAYING = "用户支付中";
    public static String PAYERROR = "支付失败(其他原因，如银行返回失败)";
}
