package org.aigps.wq.mq.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aigps.wq.mq.MqCmdC;
import org.aigps.wq.mq.MqMsg;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class WqMqClientImpl extends WqMqClient {
	private static final Log log = LogFactory.getLog(WqMqClientImpl.class);
	
	@Override
	public void OnMqMsq(MqMsg mqMsg) {
		if(mqMsg == null){
			return;
		}
		try {
			//指令
			List<String> cmdList = mqMsg.getCmdList();
			if(cmdList == null || cmdList.size() == 0){
				return;
			}
			if(MqCmdC.MSG_GPS.equals(cmdList.get(0))){//定位数据
				if(cmdList.size() ==1 ){
					return;
				}
				if(MqCmdC.GPS_RPT.equals(cmdList.get(1))){//实时上报
					
					
				}
			}
		} catch (Throwable e) {
			log.error(e.getMessage(),e);
		}
	}
}
