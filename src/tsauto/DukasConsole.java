package tsauto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dukascopy.api.IContext;
import com.dukascopy.api.IEngine;
import com.dukascopy.api.IHistory;
import com.dukascopy.api.IOrder;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.nktin.flavix.CurrencyEvent;
import com.ntkn.flavix.enums.Currency;
import com.ntkn.flavix.enums.Pair;
import com.ntkn.messages.IndicatorMessage;
import com.ntkn.messages.evnveloped.IndicatorMessageEnvelope;




public class DukasConsole {
	
	
	private final Logger logger = LoggerFactory.getLogger(DukasConsole.class);
	
	private Timer time = null;
	private DukasConsoleStatus status = DukasConsoleStatus.OUT_OF_TIME;
	private List<CurrencyEvent> eventList = new CopyOnWriteArrayList<>();
	
	private IContext context = null;
	
	double balance = 0d;


	public DukasConsole(IContext context) {
		balance = context.getAccount().getBalance();
		this.context = context;
	}
	
	protected synchronized void setInWaiting(CurrencyEvent c) {
		if(DukasConsoleStatus.OUT_OF_TIME.equals(status)) {
			logger.info("turned in WAITING status for the currencyEvent " + c);
			status = DukasConsoleStatus.LISTENING;
		}
	}
	
	protected synchronized void setOutOfTime(CurrencyEvent c) {
		if(DukasConsoleStatus.LISTENING.equals(status)) {
			logger.info("turned in OUT_OF_TIME status for the currencyEvent " + c);
			status = DukasConsoleStatus.OUT_OF_TIME;
		}
	}
	
	public synchronized void updateList(CurrencyEvent... cEvent) {
		updateList(Arrays.asList(cEvent));
	}
	
	public synchronized void updateList(List<CurrencyEvent> cEvent) {
		logger.info("received CurrencyEvent list of " + cEvent.size() + " elements");
		if(time!=null) {
			time.cancel();
		}
		time = new Timer();
		eventList.clear();
		eventList.addAll(cEvent);
		for(CurrencyEvent event : cEvent) {
			if(event.getDate().isBefore(LocalDateTime.now(ZoneId.of("GMT")).minusMinutes(3))) {
				logger.info("the event has not been added since it set in the past: " + event);
			} else {
				time.schedule(new DukasConsoleTask(this, event, true), toDate(event.getDate().minusMinutes(3)));
				time.schedule(new DukasConsoleTask(this, event, false), toDate(event.getDate().plusMinutes(3)));
				logger.info("added event: " + event);
			}
		}
	}
	
	private Date toDate(LocalDateTime ldt) {
		Instant instant = ldt.atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}
	
	public void onEvent(IndicatorMessage msg) {
		onEvent(new IndicatorMessageEnvelope(msg));
	}
	
	public void onEvent(IndicatorMessageEnvelope msg) {
		if(DukasConsoleStatus.LISTENING.equals(status)) {
			for(CurrencyEvent event : eventList) {
	    		LocalDateTime tradeDate = event.getDate();
	    		if(LocalDateTime.now().isAfter(tradeDate.minusMinutes(2)) 
	    				&& LocalDateTime.now().isBefore(tradeDate.plusMinutes(2))) {
	    			logger.info("evaluate IndicatorMessageEnvelope " + msg);
	        		event.onEvent(msg);
		        	if(event.isPassed()) {
		        		//entry pushing order to TSAuto's Engine
		        		//enterPosition(event.getCurrency(), event.isLong());
		        		logger.info("call open position " + event.getCurrency() + " " + event.isLong());
		        		openPostion(event.getCurrency(), event.isLong(), event.getPips());
		        		logger.info("back open position " + event.getCurrency() + " " + event.isLong());
		        	}
	    		}
	    	}
		}
	}
	
	private static Map<String,List<String>> map = new HashMap<>();
	
	// EUR,USD,JPY,GBP,CHF
	
	//currencies crosses
	static {
		map.put("EUR", Arrays.asList("EUR_USD","EUR_JPY","EUR_CHF","EUR_GBP","EUR_CAD","EUR_AUD","EUR_NZD"));
		map.put("JPY", Arrays.asList("EUR_JPY","USD_JPY","GBP_JPY","CHF_JPY","CAD_JPY","AUD_JPY","NZD_JPY"));
		map.put("GBP", Arrays.asList("EUR_GBP","GBP_JPY","GBP_CHF","GBP_AUD","GBP_CAD","GBP_AUD","GBP_NZD"));
		map.put("USD", Arrays.asList("EUR_USD","USD_JPY","GBP_USD","USD_CAD","USD_AUD","USD_NZD","USD_CHF"));
		map.put("CHF", Arrays.asList("EUR_CHF","JPY_CHF","GBP_CHF","USD_CHF","USD_CHF","CHF_NZD","CHF_AUD"));
	}
	
	public static void main(String[] args) {
		System.out.println(selectPairs("EUR","GBP","JPY","CAD","USD"));
	}
	
	private static List<String> selectPairs(String currency, String... currenciesAtSameTime) {
		List<String> aux = new ArrayList<>(map.get(currency));
		for(String cry : currenciesAtSameTime) {
			if(!(cry.compareToIgnoreCase(currency)==0)) {
				for(int i=0;i<aux.size();i++) {
					if(aux.get(i).contains(cry)) {
						aux.remove(i);
					}
				}
 			}
		}
		return aux;
	}
	
	private static String[] groupCurrencyAtThisTime(List<CurrencyEvent> events, LocalDateTime ldt) {
		List<String> aux = new ArrayList<>();
		for(CurrencyEvent event : events) {
			if(event.getDate().equals(ldt)) {
				aux.add(event.getCurrency());
			}
		}
		return aux.toArray(new String[]{});
	}
	
	private boolean longp = false;
	private Pair pair = null;
	
	private void enterPosition(Pair pair, boolean better, double pipsToTake) {
		this.pair = pair;
		longp = better;
		LocalDateTime now = LocalDateTime.now();
		logger.info("ENTER IN " + (better?"LONG":"SHORT") + " POSITION FOR PAIR " + pair + " ON DATE " + now + " ");
		//....
		context.executeTask(new QuickOpenOrder(pair.name(),better,pipsToTake,context.getHistory(),context.getEngine()));
	}
	
	/*
	 * JPY
	 * EUR
	 * CHF
	 * AUD
	 * NZD
	 * USD 
	 * CAD
	 */
	
	private void openPostion(String scur, boolean betterForTheCurrency, double pipsToTake) {
		Currency cur = Currency.valueOf(scur);
		if(Currency.JPY.equals(cur)) {
			enterPosition(Pair.AUDJPY, !betterForTheCurrency,pipsToTake);
		} else if(Currency.EUR.equals(cur)) {
			enterPosition(Pair.EURUSD, betterForTheCurrency,pipsToTake);
		} else if(Currency.CHF.equals(cur)) {
			//enterPosition(Pair.CHFJPY,better);
		} else if(Currency.AUD.equals(cur)) {
			enterPosition(Pair.AUDJPY, betterForTheCurrency,pipsToTake);
		} else if(Currency.NZD.equals(cur)) {
			enterPosition(Pair.NZDUSD, betterForTheCurrency,pipsToTake);
		} else if(Currency.USD.equals(cur)) {
			enterPosition(Pair.EURUSD, !betterForTheCurrency,pipsToTake);
		} else if(Currency.CAD.equals(cur)) {
			enterPosition(Pair.EURCAD, !betterForTheCurrency,pipsToTake);
		} else if(Currency.GBP.equals(cur)) {
			enterPosition(Pair.EURGBP, !betterForTheCurrency,pipsToTake);
		} else {
			return;
		}
	}

	public DukasConsoleStatus getStatus() {
		return status;
	}

	public void setStatus(DukasConsoleStatus status) {
		this.status = status;
	}

	public boolean isLong() {
		return longp;
	}

	public void setLongp(boolean longp) {
		this.longp = longp;
	}

	public Pair getPair() {
		return pair;
	}

	public void setPair(Pair pair) {
		this.pair = pair;
	}
	
	public class DukasConsoleTask extends TimerTask {
		
		boolean active = false;
		CurrencyEvent event = null;
		DukasConsole console = null;
		
		DukasConsoleTask(DukasConsole console, CurrencyEvent c, boolean active) {
			this.console = console;
			this.event = c;
			this.active = active;
		}
		
		public void run() {
			if(active) {
				console.setInWaiting(event);
			} else {
				console.setOutOfTime(event);
			}
		}
	}
	
	//currency,equitypercent,
	
	public class QuickOpenOrder implements Callable<Object> {

		double slippage = 15;
		
		ITick tick = null;
		Instrument instrument = null;
		IEngine engine = null;
		boolean longp = false;
		double pipToTake = 0d;

		QuickOpenOrder(String instrument, boolean longp, double pipToTake, IHistory history, IEngine engine) {
        	this.longp = longp;
        	this.engine = engine;
        	this.pipToTake = pipToTake;
            this.instrument = Instrument.valueOf(instrument);
        }
                       
        public Object call() throws Exception {
        	if(longp) {
				IOrder longOrder = engine.submitOrder(
						"LONG_DIRECT_" + String.valueOf(new Random().nextInt(Integer.MAX_VALUE)), // label
						instrument, // instrument
						IEngine.OrderCommand.BUY, // ordercommand
						1);
				longOrder.setLabel(String.valueOf(pipToTake));
				//createdOrders.put("LONG", longOrder.getLabel());
			} else {
				IOrder shortOrder = engine.submitOrder(
						"SHORT_DIRECT_" + String.valueOf(new Random().nextInt(Integer.MAX_VALUE)), // label
						instrument, // instrument
						IEngine.OrderCommand.SELL, // ordercommand
						1);
				shortOrder.setLabel(String.valueOf(pipToTake));
			}
        	
        	return true;
        }
	}
	
	public class OpenOrder implements Callable<Object> {

        double stopLossPips = 20;
		double takeProfitPips = 3;
		double slippage = 2;
		
		ITick tick = null;
		Instrument instrument = null;
		IEngine engine = null;
		boolean longp = false;

        OpenOrder(String instrument, boolean longp, IHistory history, IEngine engine) {
        	this.longp = longp;
        	this.engine = engine;
            this.instrument = Instrument.valueOf(instrument);
            try {
				this.tick = history.getLastTick(this.instrument);
			} catch (JFException e) {
				e.printStackTrace();
			}
        }
                       
        public Object call() throws Exception {

				double price = (tick.getAsk() + 3 * instrument.getPipValue());
				double stopLossPrice = price - (tick.getAsk()-tick.getBid()) - stopLossPips * instrument.getPipValue();
				double takeProfitPrice = price + (tick.getAsk()-tick.getBid()) + takeProfitPips * instrument.getPipValue();
				if(longp) {
					System.out.println("ENTRY PRICE LONG: " + price + " stopLossPrice: " + stopLossPrice + " takeProfitPrice: " + takeProfitPrice);
					
					IOrder longOrder = engine.submitOrder(
							"LONG_DIRECT_" + String.valueOf(new Random().nextInt(Integer.MAX_VALUE)), // label
							instrument, // instrument
							IEngine.OrderCommand.BUY, // ordercommand
							1, // amount
							price, // price
							slippage, //Double.NaN,
							stopLossPrice, //stopLossPrice, 
							takeProfitPrice);
							//goodTillTime);
					System.out.println("LONG ENTER " + instrument.name());
					System.out.println(stopLossPrice);
					System.out.println(tick.getAsk());
					System.out.println(takeProfitPrice);
					
					//createdOrders.put("LONG", longOrder.getLabel());
				} else {
	
	            	price = (tick.getBid() - 3 * instrument.getPipValue());
					stopLossPrice = price + (tick.getAsk()-tick.getBid()) + stopLossPips * instrument.getPipValue();
					takeProfitPrice = price - (tick.getAsk()-tick.getBid()) - takeProfitPips * instrument.getPipValue();
					
					System.out.println("ENTRY PRICE SHORT: " + price + " stopLossPrice: " + stopLossPrice + " takeProfitPrice: " + takeProfitPrice);
		
					IOrder shortOrder = engine.submitOrder(
							"SHORT_DIRECT_" + String.valueOf(new Random().nextInt(Integer.MAX_VALUE)),
							instrument, 
							IEngine.OrderCommand.SELL, 
							1,
							price, 
							slippage, // Double.NaN, 
							stopLossPrice, //stopLossPrice, 
							takeProfitPrice);
							//goodTillTime);
					System.out.println("SHORT ENTER " + instrument.name());
					System.out.println(stopLossPrice);
					System.out.println(tick.getAsk());
					System.out.println(takeProfitPrice);
				
				}
	
				//createdOrders.put("SHORT", shortOrder.getLabel());
            
            return true;
        }
    }

}
