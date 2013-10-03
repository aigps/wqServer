
package org.aigps.wq.xmlmodel;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("posinfo")
public class Posinfo {

	//��λ���
	//0���ɹ�
	//1��������
	//2��GPS���ǳ�ʱ
	//3��GPS��λ��ʱ
	//4������GPS��λ 
	//5��GPS��λδ֪���� 
	//6����֧��MSA��λ 
	//7��������Ӧ��
	//8����������ʧ��
	//9��MSA��λ��ʱ
	private String positionresult;
	private String fixmode;			//��λ���� 0 MSA��1 Google��2 GPS��3 GPSOne��4 Hybrid
	private String fixtime;			//�õ���λ�����λʧ�ܵ�ʱ�䣬�ȵ�ǰʱ����8Сʱ
	private String latitudetype;	//γ�����ͣ�1-��γ��0-��γ
	private String latitude;		//γ�ȣ�����ΪС�����7λ
	private String longitudetype;	//�������ͣ�1-������0-����
	private String longitude;		//���ȣ�����ΪС�����7λ
	private String altitude;		//�߶ȣ�����ΪС�����1λ��û�����ݵĻ�Ϊ��
	private String direction;		//���򣬾���ΪС�����1λ��û�����ݵĻ�Ϊ��
	private String velocity;		//�ٶȣ�����ΪС�����1λ��û�����ݵĻ�Ϊ��
	private String precision;		//���ȣ�����ΪС�����1λ��û�����ݵĻ�Ϊ��
	
	//��������û�ã�ֻ��brew�ֻ�����
	private String rssi;
	private String wsysid;
	private String dwbaselatitudetype;
	private String dwbaselatitude;
	private String dwbaselongitudetype;
	private String dwbaselongitude;
	
	public String getPositionresult() {
		return positionresult;
	}
	public void setPositionresult(String positionresult) {
		this.positionresult = positionresult;
	}
	public String getFixmode() {
		return fixmode;
	}
	public void setFixmode(String fixmode) {
		this.fixmode = fixmode;
	}
	public String getFixtime() {
		return fixtime;
	}
	public void setFixtime(String fixtime) {
		this.fixtime = fixtime;
	}
	public String getLatitudetype() {
		return latitudetype;
	}
	public void setLatitudetype(String latitudetype) {
		this.latitudetype = latitudetype;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitudetype() {
		return longitudetype;
	}
	public void setLongitudetype(String longitudetype) {
		this.longitudetype = longitudetype;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getAltitude() {
		return altitude;
	}
	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getVelocity() {
		return velocity;
	}
	public void setVelocity(String velocity) {
		this.velocity = velocity;
	}
	public String getPrecision() {
		return precision;
	}
	public void setPrecision(String precision) {
		this.precision = precision;
	}
	public String getRssi() {
		return rssi;
	}
	public void setRssi(String rssi) {
		this.rssi = rssi;
	}
	public String getWsysid() {
		return wsysid;
	}
	public void setWsysid(String wsysid) {
		this.wsysid = wsysid;
	}
	public String getDwbaselatitudetype() {
		return dwbaselatitudetype;
	}
	public void setDwbaselatitudetype(String dwbaselatitudetype) {
		this.dwbaselatitudetype = dwbaselatitudetype;
	}
	public String getDwbaselatitude() {
		return dwbaselatitude;
	}
	public void setDwbaselatitude(String dwbaselatitude) {
		this.dwbaselatitude = dwbaselatitude;
	}
	public String getDwbaselongitudetype() {
		return dwbaselongitudetype;
	}
	public void setDwbaselongitudetype(String dwbaselongitudetype) {
		this.dwbaselongitudetype = dwbaselongitudetype;
	}
	public String getDwbaselongitude() {
		return dwbaselongitude;
	}
	public void setDwbaselongitude(String dwbaselongitude) {
		this.dwbaselongitude = dwbaselongitude;
	}
	
	
}

