package org.aigps.wq.join.common;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
* @Title：远程会话终端缓存
* @Description：<类描述>
*
* @author ccq
* @version 1.0
*
* Create Date：  2012-10-23上午10:00:52
* Modified By：  <修改人中文名或拼音缩写>
* Modified Date：<修改日期，格式:YYYY-MM-DD>
*
* Copyright：Copyright(C),1995-2012 浙IPC备09004804号
* Company：杭州中导科技有限公司
 */
public class ChannelUtil {
	public final static Log log = LogFactory.getLog(ChannelUtil.class);

	// <终端号,Device>
	private static Map<String, Device> clientDeviceMap = new ConcurrentHashMap<String, Device>();

	// <终端号,Device>
	private static Map<String, Device> serverDeviceMap = new ConcurrentHashMap<String, Device>();
	
	/**
	 * 设置更新远程终端会话，如果缓存中没有则创建终端会话
	 * @param deviceId
	 * @param tcpChannel
	 * @throws Exception
	 */
	public static void setClientChannel(String deviceId,Channel channel)throws Exception{
		setChannel(clientDeviceMap, deviceId, channel);
	}

	/**
	 * 设置更新远程终端会话，如果缓存中没有则创建终端会话
	 * @param deviceId
	 * @param tcpChannel
	 * @throws Exception
	 */
	public static void setServerChannel(String deviceId,Channel channel)throws Exception{
		setChannel(serverDeviceMap, deviceId, channel);
	}

	private static void setChannel( Map<String, Device> map, String deviceId,Channel channel)throws Exception{
		Device device = map.get(deviceId);
		if (device != null) {
			device.setChannel(channel);
			device.setLastTime(System.currentTimeMillis());
		} else {
			map.put(deviceId, new Device(deviceId, channel));
		}
	}

	public static void sendClientMsg(String deviceId, String msg) throws Exception{
		Device device = clientDeviceMap.get(deviceId);
		if(device != null){
			device.addMsg(msg);
		}
	}

	public static void sendServerMsg(String deviceId, String msg) throws Exception{
		Device device = serverDeviceMap.get(deviceId);
		if(device != null){
			device.addMsg(msg);
		}
	}
	
	public static void removeClientDevice(Channel channel)throws Exception{
		for(Device device : clientDeviceMap.values()){
			if(channel == device.getChannel()){
				clientDeviceMap.remove(device.getDeviceId());
			}
		}
	}

	public static void removeServerDevice(Channel channel)throws Exception{
		for(Device device : serverDeviceMap.values()){
			if(channel == device.getChannel()){
				serverDeviceMap.remove(device.getDeviceId());
			}
		}
	}
	
	public static Map<String, Device> getClientDeviceMap(){
		return clientDeviceMap;
	}

	public static Map<String, Device> getServerDeviceMap(){
		return serverDeviceMap;
	}
	

}
