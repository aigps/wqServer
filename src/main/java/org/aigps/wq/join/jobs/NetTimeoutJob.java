package org.aigps.wq.join.jobs;

import java.util.Map;

import org.aigps.wq.join.common.Cfg;
import org.aigps.wq.join.common.ChannelUtil;
import org.aigps.wq.join.common.Device;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NetTimeoutJob implements Runnable{
	private final static Log log = LogFactory.getLog(NetTimeoutJob.class);
	
	public void run() {
		offline(ChannelUtil.getClientDeviceMap());
		offline(ChannelUtil.getServerDeviceMap());
	}

	private void offline(Map<String, Device> map) {
		long now = System.currentTimeMillis();
		try {
			for(Device device : map.values()) {
				if(now - device.getLastTime() > Cfg.socketTimeout) {
					log.error("³¬Ê±ÏÂÏß£º"+device.getDeviceId());
					device.getChannel().close();
					map.remove(device.getDeviceId());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
