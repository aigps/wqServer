package org.aigps.wq.join.client.handler;

import io.netty.channel.Channel;

import java.util.Arrays;

import org.aigps.wq.mq.MqMsg;
import org.aigps.wq.mq.WqJoinMqService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HandleResponse extends IHandler{
	private static final Log log = LogFactory.getLog(HandleResponse.class);
	
	public static String CMD = "00";
	private static IHandler handler = new HandleResponse();
	
	public static IHandler getInstance(){
		return handler;
	}

	//[CMD,water,imsi,cmd]
	public void receive(Channel channel, String[] msg) {
		log.info("接收通用应答:" + Arrays.toString(msg));
		String tmnKey = msg[2];
		String rspCmd = msg[3];
		try {
			MqMsg mqMsg = new MqMsg(tmnKey, "IMSI", 0, "CMD_RSP",rspCmd);
			mqMsg.addDataProperty("respResult", "0");
			WqJoinMqService.addMsg(mqMsg);
			//...
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	public void send(String imsi, String[] msg) {
	}
	
}
