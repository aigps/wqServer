
package org.aigps.wq.xmlmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("msids")
public class Msids {
	private String msid;			//�ֻ�IMSI��
	private String msid_type;		//�̶�ֵ2
	private String new_msid;		//���ֻ�IMSI��
	private String new_msid_type;	//�¹̶�ֵ2
	
	public String getMsid() {
		return msid;
	}
	public void setMsid(String msid) {
		this.msid = msid;
	}
	public String getMsid_type() {
		return msid_type;
	}
	public void setMsid_type(String msid_type) {
		this.msid_type = msid_type;
	}
	public String getNew_msid() {
		return new_msid;
	}
	public void setNew_msid(String new_msid) {
		this.new_msid = new_msid;
	}
	public String getNew_msid_type() {
		return new_msid_type;
	}
	public void setNew_msid_type(String new_msid_type) {
		this.new_msid_type = new_msid_type;
	}
	
}

