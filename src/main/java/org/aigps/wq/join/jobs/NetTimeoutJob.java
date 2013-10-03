package org.aigps.wq.join.jobs;

import java.util.Collection;

import org.aigps.wq.join.common.Cfg;
import org.aigps.wq.join.common.ChannelUtil;
import org.aigps.wq.join.common.Device;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NetTimeoutJob implements Runnable{
	private final static Log log = LogFactory.getLog(NetTimeoutJob.class);
	
	public void run() {
		offline(ChannelUtil.getClientDeviceMap().values());
		offline(ChannelUtil.getServerDeviceMap().values());
	}

	private void offline(Collection<Device> devices) {
		long now = System.currentTimeMillis();
		try {
			for(Device device : devices) {
				if(now - device.getLastTime() > Cfg.socketTimeout) {
					log.error("超时下线："+device.getDeviceId());
					device.getChannel().close();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
