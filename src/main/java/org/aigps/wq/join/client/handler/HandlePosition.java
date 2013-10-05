package org.aigps.wq.join.client.handler;

import io.netty.channel.Channel;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HandlePosition extends IHandler{
	private static final Log log = LogFactory.getLog(HandlePosition.class);
	
	public static String CMD = "50";
	private static IHandler handler = new HandlePosition();
	
	public static IHandler getInstance(){
		return handler;
	}

	//[cmd,water,imsi,state,time,lng,lat,height,accuracy,speed,direction,provider]
	public void receive(Channel channel, String[] msg) {
		log.info("���ն�λ��Ϣ:" + Arrays.toString(msg));

		//ͨ��Ӧ��
		this.response(channel, msg[1], msg[0]);
		
		//...
	}

	public void send(String imsi, String[] msg) {
	}

}
