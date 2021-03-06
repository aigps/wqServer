package org.aigps.wq.entity;

import java.io.Serializable;

/**
 * 
* @Title：手机终端的状态历史
* @Description：外勤系统
*
* @author ccq
* @version 1.0
*
* Create Date：  2011-12-12下午01:41:41
* Modified By：  <修改人中文名或拼音缩写>
* Modified Date：<修改日期，格式:YYYY-MM-DD>
*
* Copyright：Copyright(C),1995-2011 浙IPC备09004804号
* Company：杭州元码科技有限公司
 */
public class WqTmnSttsHis implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7631795617183676903L;
	//员工ID
    private String staffId;
    //上报时间
    private String rptTime;
    //状态
    private String stts;
    //指令
    private String cmd;

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getRptTime() {
        return rptTime;
    }

    public void setRptTime(String rptTime) {
        this.rptTime = rptTime;
    }

    public String getStts() {
        return stts;
    }

    public void setStts(String stts) {
        this.stts = stts;
    }

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
}