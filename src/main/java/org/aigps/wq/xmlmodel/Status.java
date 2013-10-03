
package org.aigps.wq.xmlmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("status")
public class Status {

	//30:��������ģʽ
	//31:�رշ���ģʽ
	//32:������������
	//33:������������
	//34:����GPS��λ
	//35:����GPS��λ
	//36:����
	//37:�ػ�
	//38:��������
	//39:��ص�
	//40:����SIM��
	//98:����ǩ��
	//99:����ǩ��
	private String result;	
	private String time;	//ʱ��
	private Msids msids;	//�ֻ�IMSI

	public void setTime(String time) {
		this.time = time;
	}
	public String getTime() {
		return time;
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

