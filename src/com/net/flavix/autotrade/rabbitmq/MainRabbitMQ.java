package com.net.flavix.autotrade.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;



public class MainRabbitMQ {
	
	String userName = null;
	String password = null;
	String virtualHost = null;
	String hostName = null;
	int portNumber = 0;

	public MainRabbitMQ() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(userName);
		factory.setPassword(password);
		factory.setVirtualHost(virtualHost);
		factory.setHost(hostName);
		factory.setPort(portNumber);
		Connection conn = factory.newConnection();
	}
	
	  private static final String TASK_QUEUE_NAME = "task_queue";

	  public static void main(String[] argv) throws Exception {
		  
	  }

}
