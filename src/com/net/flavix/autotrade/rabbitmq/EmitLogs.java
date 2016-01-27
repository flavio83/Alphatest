package com.net.flavix.autotrade.rabbitmq;

import java.io.IOException;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;




public class EmitLogs {

    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv)
                  throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        //factory.setHost("localhost");
        factory.setUri("amqp://test:test@85.17.197.66:5672/vhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        String message = "message"; //getMessage(argv);

        channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        channel.close();
        connection.close();
    }
    //...
}