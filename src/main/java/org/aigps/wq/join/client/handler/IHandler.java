package org.aigps.wq.join.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import org.aigps.wq.join.common.ChannelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class IHandler {
	private static final Log log = LogFactory.getLog(IHandler.class);

	//���յ��ֻ���������Ϣ
	public abstract void receive(Channel channel, String[] msg);

	//���ֻ�������Ϣ
	//msg:[water...]
	public abstract void send(String imsi, String[] msg);
	
	//���ֻ�������Ϣ
	//msg:[water...]
	protected void send(String imsi, String cmd, String[] msg){
		String msgs = "&"+cmd+"|"+StringUtils.join(msg,"|")+"&";
		try {
			ChannelUtil.sendClientMsg(imsi, msgs);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	//��ͨ��Ӧ��ظ�
	protected void response(Channel channel, String water, String cmd) {
		String msg = "&00|"+water+"|"+cmd+"&";
		log.error("�ظ�ͨ��Ӧ��" + msg);
		try {
			ByteBuf buf = channel.alloc().buffer();
			buf.writeBytes(msg.getBytes());
			channel.writeAndFlush(buf);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
