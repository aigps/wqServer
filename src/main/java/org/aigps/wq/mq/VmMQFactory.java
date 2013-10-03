package org.aigps.wq.mq;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

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
public class VmMQFactory {
	private static BrokerService brokerService;

    private static transient ConnectionFactory factory;
    
    public static String url;
    
	public static String getUrl() {
		return url;
	}

	public  void setUrl(String url) {
		VmMQFactory.url = url;
	}
    /**
	 * 
	 */
	public VmMQFactory(String url)throws Exception {
		setUrl(url);
		brokerService = new BrokerService();
        //configure the broker to use the Memory Store
		brokerService.setPersistent(false);
		brokerService.getSystemUsage().getMemoryUsage().setLimit(10*1024*1024);
		brokerService.setBrokerName("gpsAdapter");  
        //Add a transport connector
		brokerService.addConnector(getUrl());
        //now start the broker
		brokerService.start();
    	factory = new ActiveMQConnectionFactory("vm://gpsAdapter?jms.useAsyncSend=true");
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
		VmMQFactory.brokerService = brokerService;
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
		VmMQFactory.factory = factory;
	}
	
	

}
