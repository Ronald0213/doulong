package com.hotyi.hotyi.other.hotyiClass;

/**
 * Created by HOTYI on 2017/9/15.
 */

public class GroupInfo {
    String groupId;
    String appRYGoupId;
    String groupLogo;
    String groupName;
    String peopleNum;
    String pushMsg;
    String isManager;

    public GroupInfo(String groupId, String appRYGoupId, String groupLogo, String groupName, String peopleNum, String pushMsg, String isManager) {
        this.groupId = groupId;
        this.appRYGoupId = appRYGoupId;
        this.groupLogo = groupLogo;
        this.groupName = groupName;
        this.peopleNum = peopleNum;
        this.pushMsg = pushMsg;
        this.isManager = isManager;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAppRYGoupId() {
        return appRYGoupId;
    }

    public void setAppRYGoupId(String appRYGoupId) {
        this.appRYGoupId = appRYGoupId;
    }

    public String getGroupLogo() {
        return groupLogo;
    }

    public void setGroupLogo(String groupLogo) {
        this.groupLogo = groupLogo;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(String peopleNum) {
        this.peopleNum = peopleNum;
    }

    public String getPushMsg() {
        return pushMsg;
    }

    public void setPushMsg(String pushMsg) {
        this.pushMsg = pushMsg;
    }

    public String getIsManager() {
        return isManager;
    }

    public void setIsManager(String isManager) {
        this.isManager = isManager;
    }
}
