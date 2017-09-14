package com.hotyi.hotyi.other.hotyiClass;

/**
 * Created by HOTYI on 2017/9/14.
 */

public class SystemMsgInfo {
    private String amId;
    private String msgType;
    private String msgStatus;
    private String logo;
    private String content;
    private String name;
    private String applyTime;
    private String account;
    private String guildId;

    public SystemMsgInfo(String amId, String msgType, String msgStatus, String logo, String content, String name, String applyTime, String account, String guildId) {
        this.amId = amId;
        this.msgType = msgType;
        this.msgStatus = msgStatus;
        this.logo = logo;
        this.content = content;
        this.name = name;
        this.applyTime = applyTime;
        this.account = account;
        this.guildId = guildId;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getAmId() {
        return amId;
    }

    public void setAmId(String amId) {
        this.amId = amId;
    }

    public String getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(String msgStatus) {
        this.msgStatus = msgStatus;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }
}
