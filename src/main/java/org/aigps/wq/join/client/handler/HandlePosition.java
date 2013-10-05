package org.aigps.wq.join.client.handler;

import io.netty.channel.Channel;

import java.util.Arrays;

import org.aigps.wq.entity.GisPosition;
import org.aigps.wq.service.GpsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.map.mapabc.MapAbcUtil;
import org.gps.map.model.MapLocation;

public class HandlePosition extends IHandler{
	private static final Log log = LogFactory.getLog(HandlePosition.class);
	
	public static String CMD = "50";
	private static IHandler handler = new HandlePosition();
	
	public static IHandler getInstance(){
		return handler;
	}

	//[cmd,water,imsi,state,time,lng,lat,height,accuracy,speed,direction,provider]
	//(state:1定位成功;2:最近结果;3:定位超时;4:定位失败)
	//(provider:gps;network;lbs)
	public void receive(Channel channel, String[] msg) {
		log.info("接收定位信息:" + Arrays.toString(msg));

		//通用应答
		this.response(channel, msg[1], msg[0]);
		try {
			//高德定位偏移转成真实经纬度
			offset(msg);
			GisPosition gps = new GisPosition(msg);
			GpsService.receiveGpsInfo(gps);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	public void send(String imsi, String[] msg) {
	}

	
	private void offset(String[] msg) {
		if(msg.length < 12) {
			return;
		}
		String provider = msg[11], lng = msg[5], lat = msg[6];
		if(!"lbs".equals(provider) || StringUtils.isBlank(lng) || StringUtils.isBlank(lat)){
			return;
		}
		try {
			MapLocation fake = new MapLocation(Double.parseDouble(lng),Double.parseDouble(lat));
			MapLocation real = MapAbcUtil.getRealGps(fake);
			
			msg[5] = String.valueOf(real.getLongtitude());
			msg[6] = String.valueOf(real.getLatitude());
		} catch (Exception e) {
			log.error("定位转换出错", e);
		}
	}
}
