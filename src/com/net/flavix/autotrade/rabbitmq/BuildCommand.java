package com.net.flavix.autotrade.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP;



public class BuildCommand {
	
	private Map<String,Object> map = new HashMap<>();

	public BuildCommand() {
		
	}
	
	public void addArguments(String argkey, String value) {
		map.put(argkey, value);
	}
	
	public AMQP.BasicProperties buildAMQPBasicProperties() {
		AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
        		.headers(map)
        		.build();
		return properties;
	}

}
