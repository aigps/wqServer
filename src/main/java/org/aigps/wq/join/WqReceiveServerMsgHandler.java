package org.aigps.wq.join;

import java.util.Observable;
import java.util.Observer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;

/**
 * @Title锟斤拷<锟斤拷锟斤拷锟�
 * @Description锟斤拷<锟斤拷锟斤拷锟斤拷>
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date锟斤拷  2012-3-2锟斤拷锟斤拷11:17:12
 * Modified By锟斤拷  <锟睫革拷锟斤拷锟斤拷锟斤拷锟斤拷锟狡达拷锟斤拷锟叫�
 * Modified Date锟斤拷<锟睫革拷锟斤拷锟节ｏ拷锟斤拷式:YYYY-MM-DD>
 *
 * Copyright锟斤拷Copyright(C),1995-2011 锟斤拷IPC锟斤拷09004804锟斤拷
 * Company锟斤拷锟斤拷锟斤拷元锟斤拷萍锟斤拷锟斤拷薰锟剿� */
public class WqReceiveServerMsgHandler implements Observer {
	private static final Log log = LogFactory.getLog(WqReceiveServerMsgHandler.class);

	public void update(Observable obs, Object args) {
			try {
				String cmdType = "";
				String phone = "";
				String[] params = null;
				String mobileType = params[0];
				String classId = "";
				
				if("LCSNow".equalsIgnoreCase(cmdType)){//锟斤拷锟轿讹拷位
//					SmsClient.sendSms(classId, phone, "01", "1", params[1], "0", "#0",params[2]);
				}
				else if("ActiveLCS".equalsIgnoreCase(cmdType)){//锟斤拷位锟斤拷锟斤拷
					int secondInterval = Integer.parseInt(params[1]);//锟斤拷位锟斤拷锟�					
					String startTime = params[2];//锟斤拷始时锟斤拷
					String endTime = params[3];//锟斤拷锟斤拷时锟斤拷
					String fixModel = params[4];//锟斤拷位锟斤拷锟斤拷
					String workWeekDays = params[5];//锟较帮拷锟斤拷锟斤拷
					String smsSender = params[6];//锟斤拷锟�					
					long diffTime = DateUtil.getBetweenTime(startTime, endTime, "HHmmss");
					if(diffTime <= 0){
						diffTime = 24*60*60*1000 + diffTime;
					}
					long gpsCount = diffTime/1000/secondInterval;
					
					String otherParams = "#" + startTime + "#" + secondInterval + "#" + gpsCount + "#1";
					if("hxylSmsSender".equals(smsSender)){
						otherParams += "#" + endTime + "#" + workWeekDays + "#";
					}
//					SmsClient.sendSms(classId, phone, "02", "3", fixModel, "0", otherParams,smsSender);
				}
				else if("CancelActiveLCS".equalsIgnoreCase(cmdType)){//锟斤拷位取锟斤拷锟�//					SmsClient.sendSms(classId, phone, "04", "4", null, null, null,params[1]);
				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
	}
}
