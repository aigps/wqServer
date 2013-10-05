package org.aigps.wq.join.client.handler;

import io.netty.channel.Channel;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HandleActive extends IHandler{
	private static final Log log = LogFactory.getLog(HandleActive.class);
	
	public static String CMD = "10";
	private static IHandler handler = new HandleActive();
	
	public static IHandler getInstance(){
		return handler;
	}

	//[cmd,water,imsi,phone]
	public void receive(Channel channel, String[] msg) {
		log.info("���ռ�����Ϣ:" + Arrays.toString(msg));
		
		//..
	}
	
	//msg: [water,ip,port]
	public void send(String imsi, String[] msg) {
		log.info("�·�����ָ��:" + imsi + ":" + Arrays.toString(msg));
		super.send(imsi, CMD, msg);
	}
}
