package com.sectong.util.alipayutil;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.4
 *修改日期：2016-03-08
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
    public static String partner = "2088421331410953";

    //付款账号 必填
    public static String zfb_email = "ljwj@lejuwanjia.com";

    //付款账户名 必填 个人支付宝账号是真实姓名公司支付宝账号是公司名称
    public static String zfb_account_name = "深圳市乐居万家电商服务有限公司";

    // 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号 用于退款操作
    public static String seller_user_id = partner;

    // 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
    public static String seller_id = partner;

    // MD5密钥，安全检验码，由数字和字母组成的32位字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
    public static String key = "jao24fggv7zeihus2twqhnw4gw4o0e0v";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
//    public static String notify_url = "http://139.196.219.204:8081/weixin02_war/notify_url.jsp";
    public static String notify_url = "http://139.196.219.204:8081/yidongyiljwj/ljwjpay/Notifyzfb";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
//    public static String return_url = "http://139.196.219.204:8081/weixin02_war/return_url.jsp";
    public static String return_url = "http://139.196.219.204:8081/yidongyiljwj/demo/text";

    // 签名方式
    public static String sign_type = "MD5";

    // 调试用，创建TXT日志文件夹路径，见AlipayCore.java类中的logResult(String sWord)打印方法。
//    public static String log_path = "G:\\log\\";
    public static String log_path = "/jdkdemo/log/";

    // 字符编码格式 目前支持 gbk 或 utf-8
    public static String input_charset = "utf-8";

    // 支付类型 ，无需修改
    public static String payment_type = "1";

    // 调用的接口名，无需修改
    public static String service = "create_direct_pay_by_user";

    // 调用的退款接口名，无需修改
    public static String refund_service = "refund_fastpay_by_platform_pwd";

    // 调用的转账接口名，无需修改
    public static String payment_service = "batch_trans_notify";


    //退款日期 时间格式 yyyy-MM-dd HH:mm:ss 用于退款
    public static String refund_date = UtilDate.getDateFormatter();


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

//↓↓↓↓↓↓↓↓↓↓ 请在这里配置防钓鱼信息，如果没开通防钓鱼功能，为空即可 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 防钓鱼时间戳  若要使用请调用类文件submit中的query_timestamp函数
    public static String anti_phishing_key = "";

    // 客户端的IP地址 非局域网的外网IP地址，如：221.0.0.1
    public static String exter_invoke_ip = "139.196.219.204";

//↑↑↑↑↑↑↑↑↑↑请在这里配置防钓鱼信息，如果没开通防钓鱼功能，为空即可 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

}

