package org.aigps.wq.join.common;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
* @Title��Զ�̻Ự�ն˻���
* @Description��<������>
*
* @author ccq
* @version 1.0
*
* Create Date��  2012-10-23����10:00:52
* Modified By��  <�޸����������ƴ����д>
* Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
*
* Copyright��Copyright(C),1995-2012 ��IPC��09004804��
* Company�������е��Ƽ����޹�˾
 */
public class ChannelUtil {
	public final static Log log = LogFactory.getLog(ChannelUtil.class);

	// <�ն˺�,Device>
	private static Map<String, ClientDevice> deviceMap = new ConcurrentHashMap<String, ClientDevice>();
	
	/**
	 * ���ø���Զ���ն˻Ự��������û���򴴽��ն˻Ự
	 * @param deviceId
	 * @param tcpChannel
	 * @throws Exception
	 */
	public static void setChannel(String deviceId,Channel channel)throws Exception{
		ClientDevice device = deviceMap.get(deviceId);
		if (device != null) {
			device.setChannel(channel);
			device.setLastTime(System.currentTimeMillis());
		} else {
			deviceMap.put(deviceId, new ClientDevice(deviceId, channel));
		}
	}

	/**
	 * �������
	 */
	public static void sendMsg(String deviceId, String msg) throws Exception{
		ClientDevice device = deviceMap.get(deviceId);
		if(device != null){
			device.addMsg(msg);
		}
	}
	
	/**
	 * �������ͨ���������ն˺�
	 * @param tcpChannel
	 * @return
	 * @throws Exception
	 */
	public static String getDeviceId(Channel channel)throws Exception{
		for(ClientDevice device : deviceMap.values()){
			if(channel == device.getChannel()){
				return device.getDeviceId();
			}
		}
		return null;
	}
	
	public static void removeDevice(Channel channel)throws Exception{
		for(ClientDevice device : deviceMap.values()){
			if(channel == device.getChannel()){
				deviceMap.remove(device.getDeviceId());
			}
		}
	}
	
	/**
	 * ��ȡ�����ն˻Ự����
	 * @return
	 */
	public static Map<String, ClientDevice> getDeviceMap(){
		return deviceMap;
	}
	

}
