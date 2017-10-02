package com.test.conn.config;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.test.conn.consumer.QpidAmqpMessageHandler;

@Configuration
public class ApacheQpidConfig {
	
	@Bean
	public Context initContext() throws NamingException{
		Context ctx = new InitialContext();
		return ctx;
	}
	
	@Bean
	@Primary
	public ConnectionFactory cachingConnectionFactory(ConnectionFactory connectionFactory) throws NamingException, JMSException{
		
		ConnectionFactory factory = new CachingConnectionFactory(connectionFactory);
		return factory;
	}
	
	@Bean
	public ConnectionFactory connectionFactory(Context initContext) throws NamingException, JMSException{
		ConnectionFactory factory = (ConnectionFactory)initContext.lookup("qpidFactoryLookup");
		return factory;
	}

	@Bean
	public QpidAmqpMessageHandler nmAmqpMsgHandler(ConnectionFactory cachingConnectionFactory){
		return new QpidAmqpMessageHandler();
	}
	
	@Bean
	public DefaultMessageListenerContainer qpidAmqpMsgListenerContainer(ConnectionFactory cachingConnectionFactory, QpidAmqpMessageHandler nmAmqpMsgHandler, Destination activeMqAmqpQ){
		DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
		
		dmlc.setConnectionFactory(cachingConnectionFactory);
		dmlc.setMessageListener(nmAmqpMsgHandler);
		dmlc.setConcurrency("2-10");
		dmlc.setDestination(activeMqAmqpQ);
		return dmlc;
	}
	
	@Bean
	public Destination activeMqAmqpQ(Context initContext) throws NamingException{
		Destination activeMqAmqpQ = (Destination)initContext.lookup("nmQueueLookup");
		return activeMqAmqpQ;
	}
}
