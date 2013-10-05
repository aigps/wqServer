package org.aigps.wq.join.client.handler;

import java.util.Arrays;

import io.netty.channel.Channel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class HandleCallRoll extends IHandler{
	private static final Log log = LogFactory.getLog(HandleCallRoll.class);

	public static String CMD = "40";
	private static IHandler handler = new HandleCallRoll();
	
	public static IHandler getInstance(){
		return handler;
	}

	//msg: [40,water,imsi,gps](gps: 0:��  1:��)
	public void receive(Channel channel, String[] msg) {
		log.info("���յ����ظ�ָ��:" + Arrays.toString(msg));
		
	}

	//msg: [water]
	public void send(String imsi, String[] msg) {
		log.info("���յ�����ָ��:" + imsi + ":" + Arrays.toString(msg));
		super.send(imsi, CMD, msg);
	}
}
