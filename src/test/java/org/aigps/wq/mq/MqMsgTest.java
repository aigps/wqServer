package org.aigps.wq.mq;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class MqMsgTest {
	private static final Log log = LogFactory.getLog(MqMsgTest.class);
	@Test
	public void testJson(){
		MqMsg mqMsg = new MqMsg("id", "SL", 1, "cmd","subCmd");
		Map<String,Set<String>> map = new HashMap<String, Set<String>>();
		Set<String> regionSet = new HashSet<String>();
		regionSet.add("111");
		regionSet.add("222");
		map.put("staff",regionSet );
		mqMsg.setData(map);
		String mqMsgStr = JSON.toJSONString(mqMsg);
		MqMsg destMsg = JSON.parseObject(mqMsgStr, MqMsg.class);
		Map<String,Set<String>> destMap = (Map<String,Set<String>>)destMsg.getData();
		destMap = JSON.parseObject(destMsg.getData().toString(),new TypeReference<Map<String,Set<String>>>(){
			
		});
		for (String key : destMap.keySet()) {
			Set<String> set = destMap.get(key);
			log.info(key+"   value ->"+JSON.toJSONString(set));
		}
		log.info(destMap);
	}

}
