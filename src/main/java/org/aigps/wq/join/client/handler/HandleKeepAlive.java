package org.aigps.wq.join.client.handler;

import io.netty.channel.Channel;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class HandleKeepAlive extends IHandler{
	private static final Log log = LogFactory.getLog(HandleKeepAlive.class);

	public static String CMD = "30";
	private static IHandler handler = new HandleKeepAlive();
	
	public static IHandler getInstance(){
		return handler;
	}

	//[cmd,water,imsi]
	public void receive(Channel channel, String[] msg) {
		log.info("��������ָ��:" + Arrays.toString(msg));
		response(channel, msg[1]);
	}

	//�����ظ���������ͨ��Ӧ��ظ�
	private void response(Channel channel, String water) {
		byte[] msg = ("&"+CMD+"|"+water+"&").getBytes();
		channel.writeAndFlush(msg);
	}
	
	public void send(String imis, String[] msg) {
	}
	
}
