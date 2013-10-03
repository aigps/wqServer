
package org.aigps.wq.xmlmodel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ans")
public class LtaModel {
	protected static final Log log = LogFactory.getLog(LtaModel.class);

	private Lta lta;

	public Lta getLta() {
		return lta;
	}

	public void setLta(Lta lta) {
		this.lta = lta;
	}

	//����Ϣƴװ��Ԫ��Э���������
	public String convertToYmData(){
		if(lta == null){
			return null;
		}
		return "0|"+getCmdType()+"|"+getYmResult();
	}
	
	//��ȡ�ֻ�IMSI��
	public String getMsid(){
		try{
			return lta.getMsids().getMsid();
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return "";
		}
	}
	
	//��ȡ�ֻ���
	public String getPhone(){
		String appId = lta.getApp_id();//Ӧ�ñ�ʶ
		String reqId = lta.getReq_id();//�����ʶ
		try{
			//��װ�ֻ��� reqId���ֻ���ǰ3λ+2λ��λ����   appId���ֻ��ź�9λ
			if(StringUtils.isNotBlank(appId) && StringUtils.isNotBlank(reqId)){
				String phone = reqId.substring(0,3)+appId;
				return NumberUtils.isNumber(phone) ? phone : null;
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}
		return null;
	}
	
	//��ȡ��Ӧ������
	public String getCmdType(){
		String reqType = lta.getReq_id();//��λ����  01���ζ�λ;02���λ;03���ڶ�λ
		if(reqType!=null && reqType.length()>3){
			reqType = reqType.substring(3);
		}
		if ("01".equals(reqType)) {//���ζ�λ
			return "LCSNow";
		} else if ("02".equals(reqType)) {//���λ
			return "ActiveLCS";
		} else if ("03".equals(reqType)) {//���ڶ�λ
			return "TimingInterval";
		} else if ("04".equals(reqType)) {//ʧ�λ
			return "CancelActiveLCS";
		}
		log.error("============��reqType :"+reqType);
		return "LCSNow";
	}

	//���ֻ���ִ�н����ת����Ԫ���ִ�н��
	public String getYmResult(){
		String result = lta.getResult();
		//0:�ӳٶ�λ���óɹ� 
		//1:ȡ����λ�ɹ� 
		//3:������λ���óɹ� 
		//10:���δ˴� 
		//11:��������� 
		//14:δ�ҵ�Ҫȡ���Ķ�λ����
		if("0,1,3,10,11,14,".indexOf(result+",") != -1){
			return "0";//�ɹ�
		}
		//4:���Ÿ�ʽ����
		if("4".equals(result)){
			return "10";//ָ���ʽ����
		}
		//8:�ܾ��˴� 9:���������
		if("8".equals(result)||"9".equals(result)){
			return "12";//ָ��ܾ�
		}
		//12:��Ȩ����Ӧ
		if("12".equals(result)){
			return "13";//ָ��δ������
		}
		log.error("============��LTA RESULT :"+result);
		return "0";//�ɹ�
	}
}

