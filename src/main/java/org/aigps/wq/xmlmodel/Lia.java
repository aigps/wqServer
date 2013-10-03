
package org.aigps.wq.xmlmodel;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("lia")
public class Lia {

	private String app_id;			//Ӧ�ñ�ʶ����Ϊ�ֻ��ź�9λ
	private String req_id;			//�����ʶ����Ϊ�ֻ���ǰ3λ+2λ��λ����
	private Msids msids;			//�ֻ�IMSI
	private List<Posinfo> posinfos;	//��λ��Ϣ
	private String cellids;			//��վIDS
	private Picture picture;		//��Ƭ
	private String userdata;		//��������
	
	public String getApp_id() {
		return app_id;
	}
	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}
	public String getReq_id() {
		return req_id;
	}
	public void setReq_id(String req_id) {
		this.req_id = req_id;
	}
	public Msids getMsids() {
		return msids;
	}
	public void setMsids(Msids msids) {
		this.msids = msids;
	}
	public List<Posinfo> getPosinfos() {
		return posinfos;
	}
	public void setPosinfos(List<Posinfo> posinfos) {
		this.posinfos = posinfos;
	}
	public Picture getPicture() {
		return picture;
	}
	public void setPicture(Picture picture) {
		this.picture = picture;
	}
	public String getCellids() {
		return cellids;
	}
	public void setCellids(String cellids) {
		this.cellids = cellids;
	}
	public String getUserdata() {
		return userdata;
	}
	public void setUserdata(String userdata) {
		this.userdata = userdata;
	}
	
	
	
}

