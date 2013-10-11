package org.aigps.wq.mq;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
@Component
@DependsOn("vmMqFactory")
public class WqMqConsumer implements MessageListener{
	
    private transient Connection connection;
    private transient Session session;
    public  transient Destination destination;
    public transient MessageConsumer messageConsumer;
    
	public WqMqConsumer()throws Exception {
    	connection = VmMqFactory.getFactory().createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        if(destination==null){
    		destination = session.createTopic("WQ.SEND");
    	}
    	messageConsumer = session.createConsumer(destination);
    	messageConsumer.setMessageListener(this);
	}
	
	@PostConstruct
	public void init()throws Exception{
		
	}

	public void onMessage(Message msg) {
		
		
	}

}
