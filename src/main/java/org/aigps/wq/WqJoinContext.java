package org.aigps.wq;

import org.aigps.wq.join.JoinServer;
import org.aigps.wq.join.WqJoinHttpService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.netty4.server.HttpServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Title��<�����>
 * @Description��<������>
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date��  2012-3-2����10:58:38
 * Modified By��  <�޸����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company������Ԫ��Ƽ����޹�˾
 */
public class WqJoinContext {
	private static final Log log = LogFactory.getLog(WqJoinContext.class);
	
	

	public static ApplicationContext context ;
	
	
	public static <T> T getBean(String beanName,Class<T> clazz)throws Exception{
		return context.getBean(beanName, clazz);
	}
	
	public static void main(String[] args) {
		try {
			JoinServer.startup();
			
			context = new ClassPathXmlApplicationContext(new String[]{"wqJoinContext.xml"});
			WqConfig wqConfig = getBean("wqConfig", WqConfig.class);
			log.error("  start wq join  success!");
			while(true){
				Thread.sleep(30*1000);
			}
		} catch (Throwable e) {
			log.error(e.getMessage(),e);
		}
	}

}
