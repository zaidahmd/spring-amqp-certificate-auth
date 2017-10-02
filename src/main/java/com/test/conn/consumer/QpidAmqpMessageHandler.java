package com.test.conn.consumer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.test.conn.producer.QpidProducer;

@Component
@Scope("prototype")
public class QpidAmqpMessageHandler implements MessageListener {

	@Autowired
	QpidProducer producer;
	
	@Override
	public void onMessage(Message message) {
		File file = new File("D:\\nm-messages.txt");
		FileOutputStream fos  = null;
		try {
			fos = new FileOutputStream(file, true);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			String recvdMessage = ((TextMessage)message).getText();
			System.out.println("Message Received = " + recvdMessage.substring(0,200));
			fos.write((recvdMessage+"\n").getBytes());
		} catch (Exception e) {
			try {
				fos.write(("Received nessage of NON-TEXTMSG Type = " + message.toString()).getBytes());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} 
		try {
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
