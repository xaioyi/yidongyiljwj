package com.sectong.controller;

import com.sectong.authorization.annotation.Authorization;
import com.sectong.domain.UserLocation;
import com.sectong.message.Message;
import com.sectong.repository.LocationRepository;
import com.sectong.service.MessageService;
import com.sectong.service.RESTClient;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/v2/mongo", name = "mongodb服务API")
public class AaUserController {

    private static Logger logger = Logger.getLogger(AaUserController.class);

    private LocationRepository locationRepository;
    private MessageService messageService;
    private RESTClient restClient;


    private Message message = new Message();

    @Autowired
    public AaUserController(LocationRepository locationRepository, RESTClient restClient, MessageService messageService) {
        this.locationRepository = locationRepository;
        this.restClient = restClient;
        this.messageService = messageService;
    }


    @RequestMapping(value = "/locations", method = RequestMethod.POST)
    @ApiOperation(value = "实现定位", notes = "根据传入的信息定位地址信息，并存入mongodb返回数据")
    public ResponseEntity<Message> test(@RequestParam JSONObject reportMap, @RequestParam String telephone) throws Exception {
        UserLocation aauser = null;
        try {
            aauser = messageService.SaveAa(reportMap, telephone);
        } catch (Exception e) {
            message.setMsg(0, e.toString());
        }
        if (aauser != null) message.setMsg(1, "Creating a successful.");
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }

    @RequestMapping(value = "/getlocations", method = RequestMethod.GET)
    @ApiOperation(value = "获取定位信息", notes = "从mongodb按照查询定位数据，按天查询")
    public ResponseEntity<Message> get_data_msg(@ApiParam(value = "请求时间") @RequestParam String msg_data, @ApiParam(value = "身份标识（电话号）") @RequestParam String telephone) {

        String result = messageService.getLocation(msg_data, telephone);
        if (result.indexOf("user") != -1)
            message.setMsg(0, "The query fails.", result);
        else message.setMsg(1, "The query is successful.", result);
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }


    @RequestMapping(value = "/demo1", method = RequestMethod.GET)
    @ApiOperation(value = "测试定位（Longitude，Latitude）", notes = "根据输入的X，Y获取对应的地址，并返回")
    @Authorization
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header"),
//    })
    public ResponseEntity<Message> test1(@RequestParam String Longitude, @RequestParam String Latitude) throws Exception {
        int page = 0;//页数 从零开始
        int pageSize = 2;
        Pageable pageable = new PageRequest(page, pageSize);
        Page<UserLocation> allCourseRequest = locationRepository.findAll(pageable);
        message.setMsg(1, "create success", restClient.location_xy(Longitude, Latitude));
//        message.setMsg(1, "create success", allCourseRequest);
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }

    @RequestMapping(value = "/backup", method = RequestMethod.GET)
    @ApiOperation(value = "拉取备份", notes = "拉取数据备份至mongodb")
    public ResponseEntity<Message> backup_msg_col(HttpServletRequest request) throws Exception {
        restClient.MSG_IMG_ADDRESS = request.getServletContext().getRealPath("/") + restClient.MSG_IMG_ADDRESS;
        messageService.backup_msg();
        if (restClient.BACKUP_MSG_ID.equals(restClient.format_yesterday()))
            message.setMsg(1, "backup message success");
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }

    @RequestMapping(value = "/msg", method = RequestMethod.GET)
    @ApiOperation(value = "查看数据", notes = "从mongodb查询数据")
    public ResponseEntity<Message> get_msg(@ApiParam(value = "请求的月份", defaultValue = "-1") @RequestParam String id_msg_date, @ApiParam(value = "组标识") @RequestParam String group_msg) {

        if (!group_msg.equals(restClient.MSG_GROUP_ID) || restClient.format_date_str(id_msg_date))
            restClient.clear_constant();
        restClient.MSG_GROUP_ID = group_msg;
        if (restClient.MSG_MONTH_TIME == null) restClient.format_month();
        JSONArray jss = messageService.get_msg(id_msg_date, restClient.MSG_MONTH_TIME, group_msg);
        if (jss.optJSONObject(restClient.NUMBER_ITEM - 2) == null && !restClient.format_date_str(id_msg_date))
            restClient.month_msg();
//        System.out.println(restClient.MSG_MONTH_TIME + "**************************" + restClient.NUMBER_ID);
        message.setMsg(1, "The query is successful.", jss.toString());
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }

}
