package org.aigps.wq.join.common;

import io.netty.channel.Channel;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @Title��Զ�̻Ự�ն�
 * @Description��<������>
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date��  2012-10-23����09:02:59
 * Modified By��  <�޸����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company�������е��Ƽ����޹�˾
 */
public class ClientDevice {
	private static final Log log = LogFactory.getLog(ClientDevice.class);
	
	private String deviceId;
	private Channel channel;
	private Long lastTime;
	private AtomicBoolean isSending = new AtomicBoolean(false);
	private Queue<String> msgs = new ConcurrentLinkedQueue<String>();
	
	public ClientDevice(String deviceId,Channel channel) {
		this.deviceId = deviceId;
		this.channel = channel;
		this.lastTime = System.currentTimeMillis();
	}
	
	public void addMsg(String msg)throws Exception{
		msgs.add(msg);
	}
	
	/**
	 * ���͸�Զ���ն�
	 * @throws Exception
	 */
	public void sendMsg(){
		if(!msgs.isEmpty() && isSending.compareAndSet(false, true)){
			try {
				while(!msgs.isEmpty() && channel!=null && channel.isWritable()){
					channel.writeAndFlush(msgs.poll());
				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			} finally {
				isSending.set(false);
			}
		}
	}
	
	public boolean isSendingMsg(){
		return isSending.get();
	}
	
	public boolean msgIsEmpty(){
		return msgs.isEmpty();
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Long getLastTime() {
		return lastTime;
	}

	public void setLastTime(Long lastTime) {
		this.lastTime = lastTime;
	}

}
