package org.aigps.wq.entity;
/**
 * ������Ϣ��
 * @author Administrator
 *
 */
public class WqEmergency {
	//ID
    private String id;

    //Ա��ID
    private String staffId;

    //��Ϣ
    private String message;

    //��Ϣʱ��
    private String msgTime;

    //ת������
    private String sendTo;

    //��˾ID
    private String companyId;

    //��ע
    private String remark;


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getStaffId() {
        return staffId;
    }


    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public String getMsgTime() {
        return msgTime;
    }


    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }


    public String getSendTo() {
        return sendTo;
    }


    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }


    public String getCompanyId() {
        return companyId;
    }


    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}