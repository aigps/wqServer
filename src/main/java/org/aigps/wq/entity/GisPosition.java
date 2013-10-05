package org.aigps.wq.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.gps.util.common.DateUtil;
public class GisPosition implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3877679755390311347L;
	//终端号
	private String tmnKey;
	//
	private String tmnAlias;
	//定位时间yyyyMMddHHmmss
	private String rptTime;
	//经度
	private double lon;
	//纬度
	private double lat;
	//速度
	private double speed;
	//方向
	private double dire;
	//海拔
	private double altitude;
	//gsm信号
	private int gsmSign;
	//卫星信号
	private int satlSign;
	//定位类型0 MSA，1 Google，2 GPS，3 GPSOne，4 Hybrid，5北斗，6GPS北斗兼容模式
	private String gpsType;
	//地理描述
	private String locDesc;
		//精度
	private int precision ;
	//接收时间yyyyMMddHHmmss
	private String serverTime;
	//行政区域编号
	private String zCode;
	//查询开始时间yyyyMMddHHmmss
	private String queryStartTime;
	//查询结束时间yyyyMMddHHmmss
	private String queryEndTime;
	//偏移经度
	private double lonOff;
	//偏移纬度
	private double latOff;
	//定位时间yyyy-MM-dd HH:mm:ss
	private transient String rptStrTime;
	//(state:1定位成功;2:最近结果;3:定位超时;4:定位失败)
	private String state;
	
	
	public GisPosition() {
	}
	
	//[cmd,water,imsi,state,time,lng,lat,height,accuracy,speed,direction,provider]
	public GisPosition(String[] msg)throws Exception{
		this.setTmnKey(msg[2]);
		this.setRptTime(msg[4]);
		this.setState(msg[3]);
		if("4".equals(getState())){//不能定位，跳出来
			return;
		}
		this.setLon(Double.parseDouble(msg[5]));
		this.setLat(Double.parseDouble(msg[6]));
		this.setAltitude(Double.parseDouble(msg[7]));
		this.setPrecision(Integer.parseInt(msg[8]));
		this.setSpeed(Double.parseDouble(msg[9]));
		this.setDire(Double.parseDouble(msg[10]));
		this.setGpsType(msg[11]);
	}
	
	
	
	public String getRptStrTime()throws Exception {
		if(StringUtils.isBlank(rptStrTime)){
			rptStrTime = DateUtil.converDateFormat(rptTime, DateUtil.YMDHMS, DateUtil.YMD_HMS);
		}
		return rptStrTime;
	}
	public void setRptStrTime(String rptStrTime) {
		this.rptStrTime = rptStrTime;
	}
	//基站ID  暂时不支持
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	

//	public String getCellId() {
//		return cellId;
//	}
//	public void setCellId(String cellId) {
//		this.cellId = cellId;
//	}
	
	
}
