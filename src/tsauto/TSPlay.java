package tsauto;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.system.ClientFactory;
import com.dukascopy.api.system.IClient;
import com.dukascopy.api.system.ISystemListener;

/**
 * This small program demonstrates how to initialize Dukascopy client and start a strategy
 */
public class TSPlay {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(TSAuto.class);

    //an.ricchiedi@outlook.it
    //url of the DEMO jnlp
    
   // private static String jnlpUrl = "https://www.dukascopy.com/client/demo/jclient/jforex.jnlp";
    private static String jnlpUrl = "https://www.dukascopy.com/client/live/jclient/jforex.jnlp";
    //user name
    
    private static String userName = "Calnimp40EU";
   // private static String userName = "flavio831EU";
    //private static String userName = "flavEU";
    //password
    //private static String password = "er983456";
    private static String password = "Er983456";
    
    //live account
    /*
      	Calnimp40EU
		Er983456
		pincode: 8359
     */
    

    public static void main(String[] args) throws Exception {
        //get the instance of the IClient interface
        final IClient client = ClientFactory.getDefaultInstance();
        //set the listener that will receive system events
        client.setSystemListener(new ISystemListener() {
            private int lightReconnects = 3;

        	@Override
        	public void onStart(long processId) {
                LOGGER.info("Strategy started: " + processId);
        	}

			@Override
			public void onStop(long processId) {
                LOGGER.info("Strategy stopped: " + processId);
                if (client.getStartedStrategies().size() == 0) {
                    System.exit(0);
                }
			}

			@Override
			public void onConnect() {
                LOGGER.info("Connected");
                lightReconnects = 3;
			}

			@Override
			public void onDisconnect() {
                LOGGER.warn("Disconnected");
                if (lightReconnects > 0) {
                    client.reconnect();
                    --lightReconnects;
                } else {
                    try {
                        //sleep for 10 seconds before attempting to reconnect
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        //ignore
                    }
                    try {
                        client.connect(jnlpUrl, userName, password);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
			}
		});

        LOGGER.info("Connecting...");
        //connect to the server using jnlp, user name and password
        client.connect(jnlpUrl, userName, password);

        //wait for it to connect
        int i = 10; //wait max ten seconds
        while (i > 0 && !client.isConnected()) {
            Thread.sleep(1000);
            i--;
        }
        if (!client.isConnected()) {
            LOGGER.error("Failed to connect Dukascopy servers");
            System.exit(1);
        }
        
        //args
        String instrument = null;
        Long fromtime = null;
        Long totime = null;
        if(args.length>0) {
        	instrument = args[0];
            fromtime = Long.parseLong(args[1]);
            totime = Long.parseLong(args[2]);
        }
      
        //subscribe to the instruments
        Set<Instrument> instruments = new HashSet<Instrument>();
        instruments.add(Instrument.EURUSD);
        instruments.add(Instrument.EURGBP);
        /*
        instruments.addAll(client.getAvailableInstruments());
        System.out.println(instruments.size());
        instruments.remove(Instrument.valueOf("BRENTCMDUSD"));
        instruments.remove(Instrument.valueOf("FRAIDXEUR"));
        instruments.remove(Instrument.valueOf("JPNIDXJPY"));
        instruments.remove(Instrument.valueOf("DEUIDXEUR"));
        instruments.remove(Instrument.valueOf("USATECHIDXUSD"));
        instruments.remove(Instrument.valueOf("USA500IDXUSD"));
        instruments.remove(Instrument.valueOf("CHEIDXCHF"));
        instruments.remove(Instrument.valueOf("GBRIDXGBP"));
        instruments.remove(Instrument.valueOf("USA30IDXUSD"));
        System.out.println(instruments.size());
        */
        
        LOGGER.info("Subscribing instruments...");
        System.out.println(client.getAvailableInstruments());
        //client.setSubscribedInstruments(client.getAvailableInstruments());
        client.setSubscribedInstruments(client.getAvailableInstruments());
        
        //NewsFilter newsFilter = new NewsFilter();
        //all today's news
        //newsFilter.setTimeFrame(NewsFilter.TimeFrame.TODAY);
        //client.addNewsFilter(newsFilter);
        
        
        //workaround for LoadNumberOfCandlesAction for JForex-API versions > 2.6.64
        Thread.sleep(5000);
        
        System.out.println("--->" + Instrument.EURGBP.getPipValue());
        
        //start the strategy
        LOGGER.info("Starting strategy2");
        //client.startStrategy(new MA_Play_GETTICKS(instrument,fromtime,totime));
        
        TSAuto tsa = new TSAuto();
        client.startStrategy(tsa);
    }
   
}

