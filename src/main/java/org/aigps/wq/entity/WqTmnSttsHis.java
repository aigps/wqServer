package org.aigps.wq.entity;

import java.io.Serializable;

/**
 * 
* @Title���ֻ��ն˵�״̬��ʷ
* @Description������ϵͳ
*
* @author ccq
* @version 1.0
*
* Create Date��  2011-12-12����01:41:41
* Modified By��  <�޸�����������ƴ����д>
* Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
*
* Copyright��Copyright(C),1995-2011 ��IPC��09004804��
* Company������Ԫ��Ƽ����޹�˾
 */
public class WqTmnSttsHis implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7631795617183676903L;
	//Ա��ID
    private String staffId;
    //�ϱ�ʱ��
    private String rptTime;
    //״̬
    private String stts;
    //ָ��
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