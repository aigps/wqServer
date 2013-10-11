package org.aigps.wq.service;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.entity.GisPosition;
import org.aigps.wq.mq.MqMsg;
import org.aigps.wq.mq.WqMqProducer;
import org.aigps.wq.util.DistrictUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.map.mapabc.MapAbcUtil;
import org.gps.map.model.MapLocation;
import org.gps.util.common.DateUtil;
import org.gps.util.common.LonLatUtil;


public class GpsService {
	private static final Log log = LogFactory.getLog(GpsService.class);
	public static boolean correctGpsTime(String maxFutureTime,GisPosition gisPos)throws Exception{
		boolean retFlag = true;
		String gpsTime = gisPos.getRptTime();
		//定位时间超过系统时间
		if(maxFutureTime.compareTo(gpsTime)<=0){
			retFlag = false;
			if(log.isErrorEnabled()){
				log.error("终端:"+gisPos.getTmnKey()+"上报了未来时间-->"+gpsTime+"定位, 丢弃!");
			}
		}
		return retFlag;
	}
	
	
	/**
	 * 解析定位数据
	 * @param tmnCode
	 * @param ymGpsModel
	 * @throws Exception
	 */
	public static void receiveGpsInfo(GisPosition gisPos)
			throws Exception {
		//如果存在终端与业务ID的对照,设置对照
		if(DcGpsCache.getTmnSysIdMap().containsKey(gisPos.getTmnKey())){
			gisPos.setTmnAlias(DcGpsCache.getTmnSysIdMap().get(gisPos.getTmnKey()));
		}
		String sysTime = DateUtil.sysNumDateTime;
		String nextHour = DateUtil.sysNumNextHour;
		//最近定位
		GisPosition preGps = DcGpsCache.getLastGps(gisPos.getTmnKey());
		if(preGps!=null){
			if(StringUtils.isNotBlank(preGps.getRptTime()) && preGps.getRptTime().equalsIgnoreCase(gisPos.getRptTime())){
				log.warn(gisPos.getTmnKey()+" 上报重复定位信息:"+gisPos.getRptTime());
				return;
			}
		}

		//如果是正确时间或者能纠正的时间，接收定位
		if(! correctGpsTime(nextHour, gisPos)){
			return;
		}
		//设置接收时间
		gisPos.setServerTime(sysTime);
		//如果是有效定位
		if(LonLatUtil.isValidLocation(gisPos.getLon(), gisPos.getLat())){
			//行政区域
			String zCode = DistrictUtil.getCityZcode(gisPos.getLon(), gisPos.getLat());
			gisPos.setzCode(zCode);
		}else{//无效定位，取最近有效定位
			if(preGps!=null){//已经有最近的有效定位
				/**
				 * 暂时不采用有效定位
				 */
				gisPos.setLon(preGps.getLon());
				gisPos.setLat(preGps.getLat());
				gisPos.setzCode(preGps.getzCode());
			}
		}
		MapLocation offGps = MapAbcUtil.getFakeGps(new MapLocation(gisPos.getLon(),gisPos.getLat()));
		gisPos.setLonOff(offGps.getLongtitude());
		gisPos.setLatOff(offGps.getLatitude());
		
		//更新行政区域
		DcGpsCache.updateDcRgAreaHis(preGps,gisPos);
		//更新最后定位
		DcGpsCache.updateLastGps(gisPos.getTmnKey(), gisPos);
		
		//增量定位信息
		DcGpsCache.gpsAddCache.add(gisPos);
		
		MqMsg mqMsg = new MqMsg(gisPos.getTmnKey(), "WQ", 0, "GPS","RPT");
		mqMsg.setData(gisPos);
		WqMqProducer.addMsg(mqMsg);
	}
}
