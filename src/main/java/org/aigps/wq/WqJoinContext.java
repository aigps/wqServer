package org.aigps.wq;

import org.aigps.wq.join.JoinServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Administrator
 *
 */
public class WqJoinContext {
	private static final Log log = LogFactory.getLog(WqJoinContext.class);
	
	

	public static ApplicationContext context ;
	
	
	public static <T> T getBean(String beanName,Class<T> clazz)throws Exception{
		return context.getBean(beanName, clazz);
	}
	
	public static void main(String[] args) {
		try {
			
			context = new ClassPathXmlApplicationContext(new String[]{"wqJoinContext.xml"});
			WqConfig wqConfig = getBean("wqConfig", WqConfig.class);
			DcGpsCache dcGpsCache = getBean("dcGpsCache", DcGpsCache.class);
			dcGpsCache.init();
			
			JoinServer.startup();
			log.error("  start wq join  success!");
			while(true){
				Thread.sleep(30*1000);
			}
		} catch (Throwable e) {
			log.error(e.getMessage(),e);
		}
	}

}
