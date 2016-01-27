package tsauto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.nktin.flavix.CurrencyEvent;
import com.nktin.flavix.NewsEvent;
import com.ntkn.messages.evnveloped.IndicatorMessageEnvelope;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;




public class EmitObjects {

    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        //factory.setHost("localhost");
        factory.setUri("amqp://test:test@85.17.197.66:5672/vhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        sendCurrencyEventFrom9September(channel);
        //sendIndicatorMessageEnvelope(channel);
        
        channel.close();
        connection.close();
    }
    
    private static void sendCurrencyEventWithPipsAsTest(Channel channel) throws Exception {
    	LocalDateTime ldt = LocalDateTime.now(ZoneId.of("GMT"));
    	
    	 List<CurrencyEvent> list = new ArrayList<>();
    	
    	//20007
    	ldt = LocalDateTime.of(2015, 8, 21, 8, 30, 00);
    	CurrencyEvent currencyEvent = new CurrencyEvent("GBP",ldt);
    	currencyEvent.setPipsToTake(0.0001d);
		NewsEvent news = new NewsEvent(20007);
		news.addField(2, -2.3, false);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
    }
    
    private static void sendCurrencyEvent21August(Channel channel) throws Exception {
    	LocalDateTime ldt = LocalDateTime.now(ZoneId.of("GMT"));
    	
    	 List<CurrencyEvent> list = new ArrayList<>();
    	
    	//20007
    	ldt = LocalDateTime.of(2015, 8, 21, 8, 30, 00);
    	CurrencyEvent currencyEvent = new CurrencyEvent("GBP",ldt);
		NewsEvent news = new NewsEvent(20007);
		news.addField(2, -2.3, false);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
       //20021
    	ldt = LocalDateTime.of(2015, 8, 21, 12, 30, 00);
    	currencyEvent = new CurrencyEvent("CAD",ldt);
		news = new NewsEvent(40003);
		news.addField(2, 0.0d, true);
		currencyEvent.addNews(news);
		news = new NewsEvent(40017);
		news.addField(2, 0.6d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //serialization...
        Gson gson = new Gson();
        String message = gson.toJson(list);
        
        //message = cEvent.serializeAsJson();
        BuildCommand c = new BuildCommand();
        c.addArguments("type", "CurrencyEvent");
        
        System.out.println(" [x] Sent '" + message + "'");

        channel.basicPublish(EXCHANGE_NAME, "", c.buildAMQPBasicProperties(), message.getBytes());
    }
    
    private static void sendCurrencyEventFrom9September(Channel channel) throws Exception {
    	LocalDateTime ldt = LocalDateTime.now(ZoneId.of("GMT"));
    	
    	List<CurrencyEvent> list = new ArrayList<>();
    	
    	//20009
    	ldt = LocalDateTime.of(2015, 9, 9, 8, 30, 00);
    	CurrencyEvent currencyEvent = new CurrencyEvent("GBP",ldt);
		NewsEvent news = new NewsEvent(20009);
		news.addField(2, 0.20d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //40009
    	ldt = LocalDateTime.of(2015, 9, 9, 12, 30, 00);
    	currencyEvent = new CurrencyEvent("CAD",ldt);
		news = new NewsEvent(40009);
		news.addField(1, -4.7d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //40008
    	ldt = LocalDateTime.of(2015, 9, 9, 14, 00, 00);
    	currencyEvent = new CurrencyEvent("CAD",ldt);
		news = new NewsEvent(40008);
		news.addField(1, 0.5d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //134
    	ldt = LocalDateTime.of(2015, 9, 9, 14, 00, 00);
    	currencyEvent = new CurrencyEvent("USD",ldt);
		news = new NewsEvent(134);
		news.addField(1, 5300d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //20016
    	ldt = LocalDateTime.of(2015, 9, 10, 11, 00, 00);
    	currencyEvent = new CurrencyEvent("GBP",ldt);
		news = new NewsEvent(20016);
		news.addField(1, 0.5d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //40
    	ldt = LocalDateTime.of(2015, 9, 10, 12, 30, 00);
    	currencyEvent = new CurrencyEvent("USD",ldt);
		news = new NewsEvent(40);
		news.addField(1, 279000d, false);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //36
    	ldt = LocalDateTime.of(2015, 9, 11, 12, 30, 00);
    	currencyEvent = new CurrencyEvent("USD",ldt);
		news = new NewsEvent(36);
		news.addField(9, -0.1, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //40 & 55
    	ldt = LocalDateTime.of(2015, 9, 17, 12, 30, 00);
    	currencyEvent = new CurrencyEvent("USD",ldt);
		news = new NewsEvent(40);
		news.addField(1, 276000d, false);
		currencyEvent.addNews(news);
		news = new NewsEvent(55);
		news.addField(2, 1.15d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //133
    	ldt = LocalDateTime.of(2015, 9, 17, 18, 00, 00);
    	currencyEvent = new CurrencyEvent("USD",ldt);
		news = new NewsEvent(133);
		news.addField(2, 0.25, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //40003
    	ldt = LocalDateTime.of(2015, 9, 18, 12, 30, 00);
    	currencyEvent = new CurrencyEvent("CAD",ldt);
		news = new NewsEvent(40003);
		news.addField(3, 0.2, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
    	
	    //serialization...
	    Gson gson = new Gson();
	    String message = gson.toJson(list);
	    
	    //message = cEvent.serializeAsJson();
	    BuildCommand c = new BuildCommand();
	    c.addArguments("type", "CurrencyEvent");
	    
	    System.out.println(" [x] Sent '" + message + "'");
	
	    channel.basicPublish(EXCHANGE_NAME, "", c.buildAMQPBasicProperties(), message.getBytes());
	}
    
    private static void sendCurrencyEventFrom24August(Channel channel) throws Exception {
    	LocalDateTime ldt = LocalDateTime.now(ZoneId.of("GMT"));
    	
    	List<CurrencyEvent> list = new ArrayList<>();
    	
    	//30019
    	ldt = LocalDateTime.of(2015, 8, 25, 8, 00, 00);
    	CurrencyEvent currencyEvent = new CurrencyEvent("EUR",ldt);
		NewsEvent news = new NewsEvent(30019);
		news.addField(1, 107.6d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //52
    	ldt = LocalDateTime.of(2015, 8, 25, 14, 00, 00);
    	currencyEvent = new CurrencyEvent("USD",ldt);
		news = new NewsEvent(52);
		news.addField(1, 92.8d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //50
    	ldt = LocalDateTime.of(2015, 8, 26, 12, 30, 00);
    	currencyEvent = new CurrencyEvent("EUR",ldt);
		news = new NewsEvent(50);
		news.addField(3, 0.3d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //49
    	ldt = LocalDateTime.of(2015, 8, 27, 12, 30, 00);
    	currencyEvent = new CurrencyEvent("USD",ldt);
		news = new NewsEvent(49);
		news.addField(1, 3.2d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //40
    	ldt = LocalDateTime.of(2015, 8, 27, 12, 30, 00);
    	currencyEvent = new CurrencyEvent("USD",ldt);
		news = new NewsEvent(40);
		news.addField(1, 275000d, false);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //42
    	ldt = LocalDateTime.of(2015, 8, 27, 14, 00, 00);
    	currencyEvent = new CurrencyEvent("USD",ldt);
		news = new NewsEvent(42);
		news.addField(1, 1.3d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //20010
    	ldt = LocalDateTime.of(2015, 8, 28, 8, 30, 00);
    	currencyEvent = new CurrencyEvent("GBP",ldt);
		news = new NewsEvent(20010);
		news.addField(1, 0.7d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //57
    	ldt = LocalDateTime.of(2015, 8, 28, 12, 30, 00);
    	currencyEvent = new CurrencyEvent("USD",ldt);
		news = new NewsEvent(57);
		news.addField(2, 0.4d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //40007
    	ldt = LocalDateTime.of(2015, 8, 28, 12, 30, 00);
    	currencyEvent = new CurrencyEvent("CAD",ldt);
		news = new NewsEvent(40007);
		news.addField(3, 0d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //20027
    	ldt = LocalDateTime.of(2015, 8, 31, 9, 00, 00);
    	currencyEvent = new CurrencyEvent("EUR",ldt);
		news = new NewsEvent(20027);
		news.addField(1, 0.2d, true);
		currencyEvent.addNews(news);
		news = new NewsEvent(20027);
		news.addField(2, 1.0d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //118
    	ldt = LocalDateTime.of(2015, 8, 31, 13, 42, 00);
    	currencyEvent = new CurrencyEvent("EUR",ldt);
		news = new NewsEvent(118);
		news.addField(1, 54.70d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //20028
    	ldt = LocalDateTime.of(2015, 9, 1, 9, 00, 00);
    	currencyEvent = new CurrencyEvent("EUR",ldt);
		news = new NewsEvent(20028);
		news.addField(1, 11.10d, false);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //40005
    	ldt = LocalDateTime.of(2015, 9, 1, 12, 30, 00);
    	currencyEvent = new CurrencyEvent("CAD",ldt);
		news = new NewsEvent(40005);
		news.addField(1, -0.2d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //98
    	ldt = LocalDateTime.of(2015, 9, 1, 14, 00, 00);
    	currencyEvent = new CurrencyEvent("USD",ldt);
		news = new NewsEvent(98);
		news.addField(1, 52.7d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //121
    	ldt = LocalDateTime.of(2015, 9, 2, 12, 30, 00);
    	currencyEvent = new CurrencyEvent("USD",ldt);
		news = new NewsEvent(121);
		news.addField(1, 1.3d, false);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //47
    	ldt = LocalDateTime.of(2015, 9, 2, 14, 00, 00);
    	currencyEvent = new CurrencyEvent("USD",ldt);
		news = new NewsEvent(47);
		news.addField(1, 1.8d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //20029
    	ldt = LocalDateTime.of(2015, 9, 3, 9, 00, 00);
    	currencyEvent = new CurrencyEvent("EUR",ldt);
		news = new NewsEvent(20029);
		news.addField(1, -0.6d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //40
    	ldt = LocalDateTime.of(2015, 9, 3, 12, 30, 00);
    	currencyEvent = new CurrencyEvent("USD",ldt);
		news = new NewsEvent(40);
		news.addField(1, 273000, true);
		currencyEvent.addNews(news);
		news = new NewsEvent(39);
		news.addField(1, -43.8, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //99
    	ldt = LocalDateTime.of(2015, 9, 3, 14, 00, 00);
    	currencyEvent = new CurrencyEvent("USD", ldt);
		news = new NewsEvent(99);
		news.addField(1, 60.3d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //30011
    	ldt = LocalDateTime.of(2015, 9, 4, 6, 00, 00);
    	currencyEvent = new CurrencyEvent("EUR", ldt);
		news = new NewsEvent(30011);
		news.addField(1, 2d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //30011
    	ldt = LocalDateTime.of(2015, 9, 4, 6, 00, 00);
    	currencyEvent = new CurrencyEvent("EUR", ldt);
		news = new NewsEvent(30011);
		news.addField(1, 2d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //40011 & 40012
    	ldt = LocalDateTime.of(2015, 9, 4, 12, 30, 00);
    	currencyEvent = new CurrencyEvent("CAD", ldt);
		news = new NewsEvent(40011);
		news.addField(2, 6.8d, false);
		currencyEvent.addNews(news);
		news = new NewsEvent(40011);
		news.addField(1, 6.6d, true);
		currencyEvent.addNews(news);
		news = new NewsEvent(40012);
		news.addField(1, -0.1d, false);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
  
        
        //serialization...
        Gson gson = new Gson();
        String message = gson.toJson(list);
        
        //message = cEvent.serializeAsJson();
        BuildCommand c = new BuildCommand();
        c.addArguments("type", "CurrencyEvent");
        
        System.out.println(" [x] Sent '" + message + "'");

        channel.basicPublish(EXCHANGE_NAME, "", c.buildAMQPBasicProperties(), message.getBytes());
    }
    
    private static void sendCurrencyEvent20August(Channel channel) throws Exception {
    	LocalDateTime ldt = LocalDateTime.now(ZoneId.of("GMT"));
    	
    	 List<CurrencyEvent> list = new ArrayList<>();
    	
    	//20021
    	ldt = LocalDateTime.of(2015, 8, 20, 8, 30, 00);
    	CurrencyEvent currencyEvent = new CurrencyEvent("GBP",ldt);
		NewsEvent news = new NewsEvent(20021);
		news.addField(3, 0.4d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //20013
        ldt = LocalDateTime.of(2015, 8, 20, 10, 00, 00);
    	currencyEvent = new CurrencyEvent("GBP",ldt);
		news = new NewsEvent(20013);
		news.addField(1, -10.0d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //53
        ldt = LocalDateTime.of(2015, 8, 20, 14, 00, 00);
    	currencyEvent = new CurrencyEvent("USD",ldt);
		news = new NewsEvent(53);
		news.addField(1, 5.45d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        //40
        ldt = LocalDateTime.of(2015, 8, 20, 12, 30, 00);
    	currencyEvent = new CurrencyEvent("USD",ldt);
		news = new NewsEvent(40);
		news.addField(1, 272000d, false);
		currencyEvent.addNews(news);

        list.add(currencyEvent);
        
        //40018
        ldt = LocalDateTime.of(2015, 8, 20, 12, 30, 00);
    	currencyEvent = new CurrencyEvent("CAD",ldt);
		news = new NewsEvent(40018);
		news.addField(2, 1.0d, true);
		currencyEvent.addNews(news);
        list.add(currencyEvent);
        
        
        //serialization...
        Gson gson = new Gson();
        String message = gson.toJson(list);
        
        //message = cEvent.serializeAsJson();
        BuildCommand c = new BuildCommand();
        c.addArguments("type", "CurrencyEvent");
        
        System.out.println(" [x] Sent '" + message + "'");

        channel.basicPublish(EXCHANGE_NAME, "", c.buildAMQPBasicProperties(), message.getBytes());
    }
    
    private static void sendIndicatorMessageEnvelope(Channel channel) throws Exception {
    	IndicatorMessageEnvelope msg = new IndicatorMessageEnvelope(0,5,"2.85");
        List<IndicatorMessageEnvelope> lMsg = new ArrayList<>();
        lMsg.add(msg);
        
        Gson gson = new Gson();
        String message = gson.toJson(lMsg);
        
        //message = cEvent.serializeAsJson();
        BuildCommand c = new BuildCommand();
        c.addArguments("type", "IndicatorMessageEnvelope");
        
        System.out.println(" [x] Sent '" + message + "'");
        
        channel.basicPublish(EXCHANGE_NAME, "", c.buildAMQPBasicProperties(), message.getBytes());
    }
    
    //...
}