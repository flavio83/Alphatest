package com.net.flavix.autotrade.rabbitmq;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.net.flavix.dto.CountryEvent;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;


public class ReceiveObjects {
	
  private static final String EXCHANGE_NAME = "logs";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    //factory.setHost("localhost");
    factory.setUri("amqp://test:test@85.17.197.66:5672/vhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
    String queueName = channel.queueDeclare().getQueue();
    channel.queueBind(queueName, EXCHANGE_NAME, "");

    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope,
                                 AMQP.BasicProperties properties, byte[] body) throws IOException {
    	  
        String message = new String(body, "UTF-8");
        System.out.println(" [x] Received '" + message + "'");
        //CurrencyEvent cEvent = CurrencyEvent.deserializeAsJson(message);
        Gson gson = new Gson();
        ArrayList<CountryEvent> list = gson.fromJson(message, ArrayList.class);
        list.size();
      }
    };
    channel.basicConsume(queueName, true, consumer);
    
  }
}