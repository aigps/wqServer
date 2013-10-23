package org.aigps.wq.join.client.handler;

import io.netty.channel.Channel;

import java.util.Arrays;

import org.aigps.wq.mq.MqMsg;
import org.aigps.wq.mq.server.WqMqServer;
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
		log.info("接收激活信息:" + Arrays.toString(msg));
		try {
			MqMsg mqMsg = new MqMsg(msg[2], "WQ", Integer.parseInt(msg[1]), "CmdActive");
			mqMsg.addDataProperty("imsi", msg[2]);
			mqMsg.addDataProperty("phone", msg[3]);
			WqMqServer.addMsg(mqMsg);
			//..
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	
	//msg: [water,ip,port]
	public void send(String imsi, String[] msg) {
		log.info("下发激活指令:" + imsi + ":" + Arrays.toString(msg));
		super.send(imsi, CMD, msg);
	}
}
