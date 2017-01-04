package com.sectong.domain;

import com.google.gson.JsonObject;
import org.springframework.data.annotation.Id;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by admin on 2016/11/19.
 */
public class UserMessages {

    @Id
    private String id;

    private String fromplatform;
    private String targetname;
    private String msgtype;
    private String version;
    private String targetid;
    private String fromappkey;
    private String fromname;
    private String fromid;
    private String createtime;
    private String fromtype;
    private String targetappkey;
    private String targettype;
    private String msgid;
    private String msgctime;
    private String msglevel;
    private String msgbody;
    private String msgdata;

    public UserMessages() {
    }

    public UserMessages(String fromplatform, String targetname, String msgtype, String version, String targetid, String fromappkey, String fromname, String fromid, String createtime, String fromtype, String targetappkey, String targettype, String msgid, String msgctime, String msglevel, String msgbody, String msgdata) {
        this.fromplatform = fromplatform;
        this.targetname = targetname;
        this.msgtype = msgtype;
        this.version = version;
        this.targetid = targetid;
        this.fromappkey = fromappkey;
        this.fromname = fromname;
        this.fromid = fromid;
        this.createtime = createtime;
        this.fromtype = fromtype;
        this.targetappkey = targetappkey;
        this.targettype = targettype;
        this.msgid = msgid;
        this.msgctime = msgctime;
        this.msglevel = msglevel;
        this.msgbody = msgbody;
        this.msgdata = msgdata;
    }

    public UserMessages(Map asdf) {
        this.fromplatform = asdf.get("from_platform").toString();
        this.targetname = asdf.get("target_name").toString();
        this.msgtype = asdf.get("msg_type").toString();
        this.version = asdf.get("version").toString();
        this.targetid = asdf.get("target_id").toString();
        this.fromappkey = asdf.get("from_appkey").toString();
        this.fromname = asdf.get("from_name").toString();
        this.fromid = asdf.get("from_id").toString();
        this.createtime = stampToDate(asdf.get("create_time").toString());
        this.fromtype = asdf.get("from_type").toString();
//        this.targetappkey = asdf.get("target_appkey").toString();
        this.targettype = asdf.get("target_type").toString();
        this.msgid = asdf.get("msgid").toString();
        this.msgctime = stampToDate(asdf.get("msg_ctime").toString());
        this.msglevel = asdf.get("msg_level").toString();
        this.msgbody = asdf.get("msg_body").toString();
    }


    public static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromplatform() {
        return fromplatform;
    }

    public void setFromplatform(String fromplatform) {
        this.fromplatform = fromplatform;
    }

    public String getTargetname() {
        return targetname;
    }

    public void setTargetname(String targetname) {
        this.targetname = targetname;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTargetid() {
        return targetid;
    }

    public void setTargetid(String targetid) {
        this.targetid = targetid;
    }

    public String getFromappkey() {
        return fromappkey;
    }

    public void setFromappkey(String fromappkey) {
        this.fromappkey = fromappkey;
    }

    public String getFromname() {
        return fromname;
    }

    public void setFromname(String fromname) {
        this.fromname = fromname;
    }

    public String getFromid() {
        return fromid;
    }

    public void setFromid(String fromid) {
        this.fromid = fromid;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getFromtype() {
        return fromtype;
    }

    public void setFromtype(String fromtype) {
        this.fromtype = fromtype;
    }

    public String getTargetappkey() {
        return targetappkey;
    }

    public void setTargetappkey(String targetappkey) {
        this.targetappkey = targetappkey;
    }

    public String getTargettype() {
        return targettype;
    }

    public void setTargettype(String targettype) {
        this.targettype = targettype;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getMsgctime() {
        return msgctime;
    }

    public void setMsgctime(String msgctime) {
        this.msgctime = msgctime;
    }

    public String getMsglevel() {
        return msglevel;
    }

    public void setMsglevel(String msglevel) {
        this.msglevel = msglevel;
    }

    public String getMsgbody() {
        return msgbody;
    }

    public void setMsgbody(String msgbody) {
        this.msgbody = msgbody;
    }

    public String getMsgdata() {
        return msgdata;
    }

    public void setMsgdata(String msgdata) {
        this.msgdata = msgdata;
    }
}
