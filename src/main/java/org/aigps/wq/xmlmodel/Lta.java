
package org.aigps.wq.xmlmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("lta")
public class Lta {

	private String app_id;//Ӧ�ñ�ʶ����Ϊ�ֻ��ź�9λ
	private String req_id;//�����ʶ����Ϊ�ֻ���ǰ3λ+2λ��λ����
	//0:�ӳٶ�λ���óɹ�
	//1:ȡ����λ�ɹ�
	//3:������λ���óɹ�
	//4:���Ÿ�ʽ����
	//8:�ܾ��˴�
	//9:�������
	//10:���δ˴�
	//11:���������
	//12:��Ȩ����Ӧ
	//14:δ�ҵ�Ҫȡ���Ķ�λ����
	private String result;
	private Msids msids;	//�ֻ�IMSI
	
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
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Msids getMsids() {
		return msids;
	}
	public void setMsids(Msids msids) {
		this.msids = msids;
	}
	
	
}

