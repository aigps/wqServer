package org.aigps.wq.mq;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Title��<�����>
 * @Description��<������>
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date��  2012-12-5����11:01:08
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company������Ԫ��Ƽ����޹�˾
 */
@Component
public class VmMqFactory {
	private static BrokerService brokerService;

    private static transient ConnectionFactory factory;
    
    public static String url;
    
	public static String getUrl() {
		return url;
	}
	@Value("${mq.msg.url}")
	public  void setUrl(String url) {
		VmMqFactory.url = url;
	}
    /**
	 * 
	 */
	public VmMqFactory() {
	}
	
	@PostConstruct
	public void init()throws Exception {
		brokerService = new BrokerService();
        //configure the broker to use the Memory Store
		brokerService.setPersistent(false);
		brokerService.getSystemUsage().getMemoryUsage().setLimit(10*1024*1024);
		brokerService.setBrokerName("WqMsg");  
        //Add a transport connector
		brokerService.addConnector(getUrl());
        //now start the broker
		brokerService.start();
    	factory = new ActiveMQConnectionFactory("vm://WqMsg?jms.useAsyncSend=true");
	}

	/**
	 * @return the brokerService
	 */
	public static BrokerService getBrokerService() {
		return brokerService;
	}

	/**
	 * @param brokerService the brokerService to set
	 */
	public static void setBrokerService(BrokerService brokerService) {
		VmMqFactory.brokerService = brokerService;
	}

	/**
	 * @return the factory
	 */
	public static ConnectionFactory getFactory() {
		return factory;
	}

	/**
	 * @param factory the factory to set
	 */
	public static void setFactory(ConnectionFactory factory) {
		VmMqFactory.factory = factory;
	}
	
	

}
