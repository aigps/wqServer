package org.aigps.wq.join.client.handler;

import io.netty.channel.Channel;

import java.util.Arrays;

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
	//(state:1��λ�ɹ�;2:������;3:��λ��ʱ;4:��λʧ��)
	//(provider:gps;network;lbs)
	public void receive(Channel channel, String[] msg) {
		log.info("���ն�λ��Ϣ:" + Arrays.toString(msg));

		//ͨ��Ӧ��
		this.response(channel, msg[1], msg[0]);
		
		//�ߵ¶�λƫ��ת����ʵ��γ��
		offset(msg);
		
		//...
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
			log.error("��λת������", e);
		}
	}
}
