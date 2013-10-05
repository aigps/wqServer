package org.aigps.wq.join.client.handler;

import io.netty.channel.Channel;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HandleSign extends IHandler{
	private static final Log log = LogFactory.getLog(HandleSign.class);
	
	public static String CMD = "60";
	private static IHandler handler = new HandleSign();
	
	public static IHandler getInstance(){
		return handler;
	}

	//[cmd,water,imsi,state,time] (state 0:ǩ�� 1:ǩ��)
	public void receive(Channel channel, String[] msg) {
		log.info("����ǩ����Ϣ:" + Arrays.toString(msg));
		//ͨ��Ӧ��ظ�
		this.response(channel, msg[1], msg[0]);
		
		//.....
	}

	public void send(String imsi, String[] msg) {
	}

}
