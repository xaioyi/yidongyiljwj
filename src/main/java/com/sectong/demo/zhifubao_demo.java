package com.sectong.demo;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.sectong.util.PayConfigUtil;

import javax.servlet.ServletException;
import java.io.IOException;

import static com.alipay.api.AlipayConstants.CHARSET_GBK;

/**
 * Created by admin on 2016/12/14.
 */
public class zhifubao_demo {


    public static void main(String[] args) throws Exception {
        demo();
    }

    public static void demo() throws ServletException, IOException, AlipayApiException {


        String siyao = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+eSZ9B0vy6BXaKlUH8nwrnQkWIQ+VhdeihbABT1IPuCUr9Ru1qhw77lsuPiWMiJPyi5zTBAzWYKLEibauOb3VG+jxgAlWJUe9G+e00vqCgr+QmqqAFTzZ+BohAtaPHQFP8e1IjQiDNRfoicad/LQLiho/rJWW9cLe4VCh9O2zVQIDAQAB";


        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", PayConfigUtil.APP_ID_ZFB, PayConfigUtil.APP_PRIVATE_KEY, "json", CHARSET_GBK, PayConfigUtil.ALIPAY_PUBLIC_KEY);

        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
        alipayRequest.setReturnUrl("http://domain.com/CallBack/return_url.jsp");
        alipayRequest.setNotifyUrl("http://domain.com/CallBack/notify_url.jsp");//在公共参数中设置回跳和通知地址
        alipayRequest.setBizContent("{" +
                "    \"out_trade_no\":\"201503200101010022\"," +
                "    \"total_amount\":88.88," +
                "    \"subject\":\"Iphone6 16G\"," +
                "    \"seller_id\":\"2088123456789012\"," +
                "    \"product_code\":\"QUICK_WAP_PAY\"" +
                "  }");//填充业务参数

        AlipayTradeWapPayResponse response = alipayClient.execute(alipayRequest);

        if (response.isSuccess()) {
            //获取图片访问地址
//            String imageUrl = response.
            System.out.println(response);
        }


//// 实例化具体API对应的request类,类名称和接口名称对应，当前调用接口名称：alipay.offline.material.image.upload
//        AlipayOfflineMaterialImageUploadRequest request = new AlipayOfflineMaterialImageUploadRequest();
//
//        request.setImageName("test");
//        FileItem item = new FileItem("G:\\github1.jpg");
//        request.setImageType("JPG");
//        request.setImageContent(item);
////执行API请求
//        AlipayOfflineMaterialImageUploadResponse response1 = alipayClient.execute(request);
////调用成功，则处理业务逻辑
//        if (response1.isSuccess()) {
//            //获取图片访问地址
//            String imageUrl = response1.getImageUrl();
//            System.out.println(imageUrl);
//        }

    }


//    public void doPost(HttpServletRequest httpRequest,
//                       HttpServletResponse httpResponse) throws ServletException, IOException {
//        AlipayClient alipayClient = ... //获得初始化的AlipayClient
//        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
//        alipayRequest.setReturnUrl("http://domain.com/CallBack/return_url.jsp");
//        alipayRequest.setNotifyUrl("http://domain.com/CallBack/notify_url.jsp");//在公共参数中设置回跳和通知地址
//        alipayRequest.setBizContent("{" +
//                "    \"out_trade_no\":\"20150320010101002\"," +
//                "    \"total_amount\":88.88," +
//                "    \"subject\":\"Iphone6 16G\"," +
//                "    \"seller_id\":\"2088123456789012\"," +
//                "    \"product_code\":\"QUICK_WAP_PAY\"" +
//                "  }");//填充业务参数
//        String form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
//        httpResponse.setContentType("text/html;charset=" + AlipayServiceEnvConstants.CHARSET);
//        httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
//        httpResponse.getWriter().flush();
//    }
}
