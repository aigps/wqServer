
package org.aigps.wq.xmlmodel;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ans")
public class StatusModel {
	protected static final Log log = LogFactory.getLog(StatusModel.class);
	
	private Status status;

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	// ����Ϣƴװ��Ԫ��Э���������
	public String convertToYmData() {
		if (status == null) {
			return null;
		}
		String time = status.getTime();
		if (!NumberUtils.isNumber(time)) {
			time = DateUtil.parseToNum(time);
		}
		return "0|UploadStatus|" + time + "|" + status.getResult();
	}

	public String getMsid(){
		try{
			return status.getMsids().getMsid();
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return "";
		}
	}
	
	public String getTime(){
		if (status == null) {
			return null;
		}
		String time = status.getTime();
		if (!NumberUtils.isNumber(time)) {
			time = DateUtil.parseToNum(time);
		}
		return time;
	}
	
	public String getResult(){
		if (status == null) {
			return null;
		}
		return status.getResult();
	}
	
	
}

