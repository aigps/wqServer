package org.aigps.wq.mq;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 消息指令常量
 * @author Administrator
 *
 */
public class MqCmdC {
	private static final Log log = LogFactory.getLog(MqCmdC.class);
	//一级指令，数据同步
	public static final String MSG_SYN="SYN";
	//二级指令，同步员工区域
	public static final String SYN_STAFF_REGIONS ="STAFF_REGIONS";
	//二级指令，同步员工规则
	public static final String SYN_STAFF_RULES = "STAFF_RULES";
	
	
	//一级指令，定位
	public static final String MSG_GPS="GPS";
	//上报定位
	public static final String GPS_RPT="RPT";
	
	
	// 一级指令，指令
	public static final String MSG_CMD ="CMD";
	
	
	
		
}
