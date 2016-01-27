package tsauto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

import com.dukascopy.api.IContext;
import com.dukascopy.api.IEngine;
import com.dukascopy.api.IHistory;
import com.dukascopy.api.IOrder;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.system.ClientFactory;
import com.ntkn.messages.IndicatorMessage;




public class AlphaFlashStremer extends Thread {
	
	private List<AlphaNews> setNews = new CopyOnWriteArrayList<>();
	
	private BlockingQueue<IndicatorMessage> queue;
	private IContext context;
	//private Map<Integer,NewsEvent> map = null;

	public AlphaFlashStremer(TcpMsgReceiverAlphaFlashStream tcp, IContext context) {
		this.queue = tcp.getbQueue();
		this.context = context;
		//map = new ParseAllXMLs().getMap();
		new AlphaNewsDBFetcher(setNews).start();
	}
	
	public List<IOrder> getOrders() throws Exception {
		return context.getEngine().getOrders();
	}
	
	public Set<Integer> set = new HashSet<>();
	
	public void run() {
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("START");
		
		while(true)
			try {	
				IndicatorMessage msg = (IndicatorMessage)queue.take();
				Date d = new Date();
				System.out.println(d + " ms " + d.getTime() + " elaboro categoria n. " + msg.getHeader().getMessageCategoryId());
				//double aux = Double.valueOf(msg.getPayload().getIndicatorMessageFields().get(0).getfieldValue().toString());
				//String name = map.get(msg.getHeader().getMessageCategoryId()).getName();
				//System.out.println(msg.getHeader().getMessageCategoryId() + " " + IndicatorMessageType.values()[msg.getHeader().getMessageTypeId()] + " " + name);
		
				//Fri Mar 20 12:30:00 GMT 2015 40003 CA Consumer Price Index
				//Fri Mar 20 12:30:00 GMT 2015 40017 CA Retail Trade
				
				double cpi = Double.MAX_VALUE;
				double retailTrade = Double.MAX_VALUE;
				
				if(msg.getHeader().getMessageTypeId()==0
						&& msg.getHeader().getMessageCategoryId()==4003) {
					cpi = Double.valueOf(msg.getPayload().getIndicatorMessageFields().get(3).getfieldValue().toString());	
				}
				if(msg.getHeader().getMessageTypeId()==0
						&& msg.getHeader().getMessageCategoryId()==40017) {
					retailTrade = Double.valueOf(msg.getPayload().getIndicatorMessageFields().get(1).getfieldValue().toString());
				}
				
				if(cpi!=Double.MAX_VALUE && retailTrade!=Double.MAX_VALUE) {
					
					System.out.println("CPI: " + cpi + " RetailTrade: " + retailTrade);
					
					if(cpi>0.6 && retailTrade>-0.4) {
						//enter long
						enterPosition("EURCAD", false);
					}
					if(cpi<0.6 && retailTrade<-0.4) {
						//enter short
						enterPosition("EURCAD", true);
					}
					
				}
				
				/*
				for(AlphaNews anews : setNews) {
					if(msg.getHeader().getMessageTypeId()==0
							&& msg.getHeader().getMessageCategoryId()==anews.getCategoryID() 
								&& !set.contains(anews.getCategoryID())) {
						
						d = new Date();
						double v = Double.valueOf(msg.getPayload().getIndicatorMessageFields().get(0).getfieldValue().toString());
						System.out.println(d + " ms " + d.getTime() + " ELABORO CATEGORIA " + anews.getCategoryID() + " CON VALORE " + v);
						if(anews.getDescription().toLowerCase().contains("rate")) {
							//just for the national interest rate news
							if(v>=anews.getValue()) {
								enterPosition(anews.getCurrency(), anews.isEntryInLong());
							} else {
								enterPosition(anews.getCurrency(), !anews.isEntryInLong());
							}
						} else {
							if(v>anews.getValue()) {
								enterPosition(anews.getCurrency(), anews.isEntryInLong());
							} else {
								enterPosition(anews.getCurrency(), !anews.isEntryInLong());
							}
						}
						
						set.add(anews.getCategoryID());
					}
				}*/
				
				/*
				switch(msg.getHeader().getMessageCategoryId()) {
						
						case 30008:  //Final CPI y/y EUR
						//if(new Date().after(new Date(getActualTime()-2*60000)) 
						//		&& new Date().before(new Date(getActualTime()+2*60000))) {
							double v = Double.valueOf(msg.getPayload().getIndicatorMessageFields().get(0).getfieldValue().toString());
							if(v>-0.2d && msg.getHeader().getMessageTypeId()==0) {
								enterPosition("EURUSD", true);
							} else {
								enterPosition("EURUSD", false);
							}
						//}
						break;
						
						case 32:  //CPI m/m USD
							//if(new Date().after(new Date(getActualTime()-2*60000)) 
							//		&& new Date().before(new Date(getActualTime()+2*60000))) {
								v = Double.valueOf(msg.getPayload().getIndicatorMessageFields().get(0).getfieldValue().toString());
								if(v>-0.3d && msg.getHeader().getMessageTypeId()==0) {
									enterPosition("EURUSD", false);
								} else {
									enterPosition("EURUSD", true);
								}
							//}
							break;
							
						case 33:  //Capacity Utilization Rate USD
							//if(new Date().after(new Date(getActualTime()-2*60000)) 
							//		&& new Date().before(new Date(getActualTime()+2*60000))) {
								v = Double.valueOf(msg.getPayload().getIndicatorMessageFields().get(0).getfieldValue().toString());
								if(v>80.2d && msg.getHeader().getMessageTypeId()==0) {
									enterPosition("EURUSD", false);
								} else {
									enterPosition("EURUSD", true);
								}
							//}
							break;
				}*/
			} catch (java.lang.NumberFormatException nfe) {
				System.out.println(nfe.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	private void enterPosition(String cur, boolean better) {
		Date d = new Date();
		try {
			System.out.println("isConnected " + ClientFactory.getDefaultInstance().isConnected());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(d + " ms " + d.getTime() +  " PAIR: " + cur + " LONGP: " + better );
		context.executeTask(new OpenOrder3(cur,better,context.getHistory(),context.getEngine()));
	}
	
	private class OpenOrder3 implements Callable<Object> {

        double stopLossPips = 20;
		double takeProfitPips = 3;
		double slippage = 2;
		
		ITick tick = null;
		Instrument instrument = null;
		IEngine engine = null;
		boolean longp = false;

        OpenOrder3(String instrument, boolean longp, IHistory history, IEngine engine) {
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
	
	public void closeOrder(IOrder order) {
		context.executeTask(new CloseOrder(order));
	}
	
	public void setStopLoss(IOrder order) {
		context.executeTask(new SetStopLossOrder(order));
	}
	
	public class CloseOrder implements Callable<Object> {
		private IOrder order;
		CloseOrder(IOrder order) {
			this.order = order;
		}
		public Object call() throws Exception {
			order.close();
			return true;
		}
	}
	
	public class SetStopLossOrder implements Callable<Object> {
		private IOrder order;
		SetStopLossOrder(IOrder order) {
			this.order = order;
		}
		public Object call() throws Exception {
			if(order.isLong()) {
				System.out.println("SET STOP LOSS: " + order.getOpenPrice() + " " + order.isLong() + " " + (order.getOpenPrice()-plus(2)));
				order.setStopLossPrice(order.getOpenPrice()-plus(5), OfferSide.ASK);
			} else {
				System.out.println("SET STOP LOSS: " + order.getOpenPrice() + " " + order.isLong() + " " + (order.getOpenPrice()+plus(2)));
				order.setStopLossPrice(order.getOpenPrice()+plus(5), OfferSide.BID);
			}
			return true;
		}
	}
	
	public double plus(int n) {
		return EUR_ON_USD_CAD_GBP_CHF_tickSize.multiply(new BigDecimal(n)).doubleValue();
	}
	
	private BigDecimal EUR_ON_USD_CAD_GBP_CHF_tickSize = new BigDecimal("0.00001");
	
	private class OpenOrder2 implements Callable<Object> {

        double stopLossPips = 4;
		double takeProfitPips = 2;
		double slippage = 1;
		
		ITick tick = null;
		Instrument instrument = null;
		IEngine engine = null;
		boolean longp = false;

        OpenOrder2(String instrument, boolean longp, IHistory history, IEngine engine) {
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
        	
        		long goodTillTime = tick.getTime() + 14 * 1000; //4 secondi di buffer

				double price = (tick.getAsk() + 3 * instrument.getPipValue());
				double stopLossPrice = price - (tick.getAsk()-tick.getBid()) - stopLossPips * instrument.getPipValue();
				double takeProfitPrice = price + (tick.getAsk()-tick.getBid()) + takeProfitPips * instrument.getPipValue();
				if(longp) {
					System.out.println("ENTRY PRICE LONG: " + price + " stopLossPrice: " + stopLossPrice + " takeProfitPrice: " + takeProfitPrice);
					
					IOrder longOrder = engine.submitOrder(
							"LONG_DIRECT_" + String.valueOf(new Random().nextInt(Integer.MAX_VALUE)), // label
							instrument, // instrument
							IEngine.OrderCommand.BUYSTOP_BYBID, // ordercommand
							1, // amount
							price, // price
							slippage, //Double.NaN,
							stopLossPrice, //stopLossPrice, 
							takeProfitPrice);
							//goodTillTime);
					System.out.println("LONG TRY ENTER " + instrument.name());
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
									IEngine.OrderCommand.SELLSTOP_BYASK, 
									1,
									price, 
									slippage, // Double.NaN, 
									stopLossPrice, //stopLossPrice, 
									takeProfitPrice);
									//goodTillTime);
					System.out.println("SHORT TRY ENTER " + instrument.name());
					System.out.println(stopLossPrice);
					System.out.println(tick.getAsk());
					System.out.println(takeProfitPrice);
				
				}
	
				//createdOrders.put("SHORT", shortOrder.getLabel());
            
            return true;
        }
    }
	
	private class OpenOrder implements Callable<Object> {

        double stopLossPips = 10;
		double takeProfitPips = 4;
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
        		long goodTillTime = tick.getTime() + 14 * 1000; //4 secondi di buffer

				double price = (tick.getAsk() + 3 * instrument.getPipValue());
				double stopLossPrice = price - (tick.getAsk()-tick.getBid()) - stopLossPips * instrument.getPipValue();
				double takeProfitPrice = price + (tick.getAsk()-tick.getBid()) + takeProfitPips * instrument.getPipValue();
				if(longp) {
				System.out.println("ENTRY PRICE LONG: " + price + " stopLossPrice: " + stopLossPrice + " takeProfitPrice: " + takeProfitPrice);
				
				IOrder longOrder = engine.submitOrder(
						"LONG_DIRECT_" + String.valueOf(new Random().nextInt(Integer.MAX_VALUE)), // label
						instrument, // instrument
						IEngine.OrderCommand.BUYSTOP_BYBID, // ordercommand
						10, // amount
						price, // price
						slippage, //Double.NaN,
						stopLossPrice, //stopLossPrice, 
						takeProfitPrice, 
						goodTillTime);
				System.out.println("LONG TRY ENTER " + instrument.name());
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
								IEngine.OrderCommand.SELLSTOP_BYASK, 
								10,
								price, 
								slippage, // Double.NaN, 
								stopLossPrice, //stopLossPrice, 
								takeProfitPrice,
								goodTillTime);
				System.out.println("SHORT TRY ENTER " + instrument.name());
				System.out.println(stopLossPrice);
				System.out.println(tick.getAsk());
				System.out.println(takeProfitPrice);
				
				}
	
				//createdOrders.put("SHORT", shortOrder.getLabel());
            
            return true;
        }
    }

}
