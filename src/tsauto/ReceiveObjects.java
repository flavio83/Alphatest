package tsauto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nktin.flavix.CurrencyEvent;
import com.ntkn.messages.evnveloped.IndicatorMessageEnvelope;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;




public class ReceiveObjects {

	private DukasConsole console = null;
	private static final String EXCHANGE_NAME = "logs";

	public ReceiveObjects(DukasConsole console) throws Exception {
		this.console = console;
		ConnectionFactory factory = new ConnectionFactory();
		// factory.setHost("localhost");
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
					AMQP.BasicProperties properties, byte[] body)
					throws IOException {

				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + message + "'");
				
				Gson gson = new Gson();
				
				Map<String,Object> map = properties.getHeaders();
				String type = map.get("type").toString();
				if("CurrencyEvent".equalsIgnoreCase(type)) {
					ArrayList<CurrencyEvent> list = gson.fromJson(message, 
							new TypeToken<ArrayList<CurrencyEvent>>(){}.getType());
					console.updateList(list);
				} else if("IndicatorMessageEnvelope".equalsIgnoreCase(type)) {
					ArrayList<IndicatorMessageEnvelope> list = gson.fromJson(message, 
							new TypeToken<ArrayList<IndicatorMessageEnvelope>>(){}.getType());
					for(IndicatorMessageEnvelope ime : list) {
						console.onEvent(ime);
					}
				}
			}
		};
		channel.basicQos(0);
		channel.basicConsume(queueName, true, consumer);
	}
}