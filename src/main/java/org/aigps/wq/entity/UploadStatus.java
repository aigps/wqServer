package org.aigps.wq.entity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UploadStatus {
	private static final Log log = LogFactory.getLog(UploadStatus.class);
	
    private String staffId;

    private String phone;
	//�ϱ�ʱ��
	private String rptTime;
	//�ϱ�״̬
	private String status;
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getRptTime() {
		return rptTime;
	}
	public void setRptTime(String rptTime) {
		this.rptTime = rptTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
