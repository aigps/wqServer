package org.aigps.wq.join.client.handler;

import io.netty.channel.Channel;

import java.util.Arrays;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.WqJoinContext;
import org.aigps.wq.entity.WqTmnSttsHis;
import org.aigps.wq.ibatis.IbatisUpdateJob;
import org.aigps.wq.mq.MqMsg;
import org.aigps.wq.mq.server.WqMqServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HandleStateNet extends IHandler{
	private static final Log log = LogFactory.getLog(HandleStateNet.class);
	
	public static String CMD = "71";
	private static IHandler handler = new HandleStateNet();
	
	public static IHandler getInstance(){
		return handler;
	}

	//[cmd,water,imsi,state,time] (state 0:关网络 1:开网络)
	public void receive(Channel channel, String[] msg) {
		log.info("接收开关网络信息:" + Arrays.toString(msg));
		//通用应答回复
		this.response(channel, msg[1], msg[0]);
		try {
			String tmnKey = msg[2];
			String status = msg[3];
			String rptTime = msg[4];
			String staffId = DcGpsCache.getTmnSysIdMap().get(tmnKey);
			if(StringUtils.isBlank(staffId)){
				staffId = tmnKey;
			}
			WqTmnSttsHis wqTmnSttsHis = new WqTmnSttsHis();
			wqTmnSttsHis.setStaffId(staffId);
			wqTmnSttsHis.setCmd(CMD);
			wqTmnSttsHis.setRptTime(rptTime);
			wqTmnSttsHis.setStts(status);
			
			MqMsg mqMsg = new MqMsg(tmnKey, "IMSI", 0, "CMD","UploadStatus");
			mqMsg.setData(wqTmnSttsHis);
			WqMqServer.addMsg(mqMsg);
			
			IbatisUpdateJob ibatisUpdateJob = WqJoinContext.getBean("ibatisUpdateJob", IbatisUpdateJob.class);
			ibatisUpdateJob.addExeSql("WQ_TMN_STTS_HIS.insert", wqTmnSttsHis);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	public void send(String imsi, String[] msg) {
	}

}
