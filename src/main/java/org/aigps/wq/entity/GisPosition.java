package org.aigps.wq.entity;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.gps.util.common.DateUtil;
public class GisPosition implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3877679755390311347L;
	//�ն˺�
	private String tmnKey;
	//
	private String tmnAlias;
	//��λʱ��yyyyMMddHHmmss
	private String rptTime;
	//����
	private double lon;
	//γ��
	private double lat;
	//�ٶ�
	private double speed;
	//����
	private double dire;
	//����
	private double altitude;
	//gsm�ź�
	private int gsmSign;
	//�����ź�
	private int satlSign;
	//��λ����0 MSA��1 Google��2 GPS��3 GPSOne��4 Hybrid��5������6GPS��������ģʽ
	private String gpsType;
	//�������� 00 ��ͨ��λ,01����,02����,03����, 97��Ƭ,98��ǩ,99��ǩ
	private String trigType;
	//��������
	private String locDesc;
		//����
	private int precision ;
	//����ʱ��yyyyMMddHHmmss
	private String serverTime;
	//����������
	private String zCode;
	//��ѯ��ʼʱ��yyyyMMddHHmmss
	private String queryStartTime;
	//��ѯ����ʱ��yyyyMMddHHmmss
	private String queryEndTime;
	//ƫ�ƾ���
	private double lonOff;
	//ƫ��γ��
	private double latOff;
	//��λʱ��yyyy-MM-dd HH:mm:ss
	private transient String rptStrTime;
	
	
	
	
	
	public String getRptStrTime()throws Exception {
		if(StringUtils.isBlank(rptStrTime)){
			rptStrTime = DateUtil.converDateFormat(rptTime, DateUtil.YMDHMS, DateUtil.YMD_HMS);
		}
		return rptStrTime;
	}
	public void setRptStrTime(String rptStrTime) {
		this.rptStrTime = rptStrTime;
	}
	//��վID  ��ʱ��֧��
//	private String cellId;
	public String getzCode() {
		return zCode;
	}
	public void setzCode(String zCode) {
		this.zCode = zCode;
	}
	public String getServerTime() {
		return serverTime;
	}
	public void setServerTime(String serverTime) {
		this.serverTime = serverTime;
	}
	public String getTmnAlias() {
		return tmnAlias;
	}
	public void setTmnAlias(String tmnAlias) {
		this.tmnAlias = tmnAlias;
	}
	public int getPrecision() {
		return precision;
	}
	public void setPrecision(int precision) {
		this.precision = precision;
	}
	public String getTmnKey() {
		return tmnKey;
	}
	public void setTmnKey(String tmnKey) {
		this.tmnKey = tmnKey;
	}
	
	public String getRptTime() {
		return rptTime;
	}
	public void setRptTime(String rptTime) {
		this.rptTime = rptTime;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public double getDire() {
		return dire;
	}
	public void setDire(double dire) {
		this.dire = dire;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	public int getGsmSign() {
		return gsmSign;
	}
	public void setGsmSign(int gsmSign) {
		this.gsmSign = gsmSign;
	}
	public int getSatlSign() {
		return satlSign;
	}
	public void setSatlSign(int satlSign) {
		this.satlSign = satlSign;
	}
	
	public String getGpsType() {
		return gpsType;
	}
	public void setGpsType(String gpsType) {
		this.gpsType = gpsType;
	}
	public String getTrigType() {
		return trigType;
	}
	public void setTrigType(String trigType) {
		this.trigType = trigType;
	}
	public String getLocDesc() {
		return locDesc;
	}
	public void setLocDesc(String locDesc) {
		this.locDesc = locDesc;
	}
	public String getQueryStartTime() {
		return queryStartTime;
	}
	public void setQueryStartTime(String queryStartTime) {
		this.queryStartTime = queryStartTime;
	}
	public String getQueryEndTime() {
		return queryEndTime;
	}
	public void setQueryEndTime(String queryEndTime) {
		this.queryEndTime = queryEndTime;
	}
	public double getLonOff() {
		return lonOff;
	}
	public void setLonOff(double lonOff) {
		this.lonOff = lonOff;
	}
	public double getLatOff() {
		return latOff;
	}
	public void setLatOff(double latOff) {
		this.latOff = latOff;
	}

//	public String getCellId() {
//		return cellId;
//	}
//	public void setCellId(String cellId) {
//		this.cellId = cellId;
//	}
	
	
}
