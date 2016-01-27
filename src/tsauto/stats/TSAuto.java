package tsauto.stats;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IDataService;
import com.dukascopy.api.IEngine;
import com.dukascopy.api.IHistory;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IOrder;
import com.dukascopy.api.IOrder.State;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.Period;




public class TSAuto implements IStrategy {

	private IEngine engine = null;
    private IConsole console = null;
    private IDataService dataService = null;
    private IHistory history = null;
    
    public String currency;
    public boolean longpos;
    public AtomicBoolean enter = new AtomicBoolean();

    public void onStart(IContext context) throws JFException {
        engine = context.getEngine();
        dataService = context.getDataService();
        history = context.getHistory();
        console = context.getConsole();
        console.getOut().println("Started"); 
    }
    
    public List<ITick> getHistoryTicks(Instrument inst, long from, long to) throws Exception {
    	return history.getTicks(inst, from, to);
    }

	@Override
	public void onTick(Instrument instrument, ITick tick) throws JFException {

	}
	
	@Override
	public void onBar(Instrument instrument, Period period, IBar askBar,
			IBar bidBar) throws JFException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(IMessage message) throws JFException {
		if(message.getType()==IMessage.Type.ORDER_FILL_OK 
				&& (message.getOrder().getState()==State.FILLED)) {
			IOrder order = message.getOrder();
			String label = order.getLabel();
			if(label!=null) {
				double pips = Double.parseDouble(order.getLabel());
				order.close(order.getAmount(),addUnit(order.getOpenPrice(),order.isLong(),pips),0);
			} else {
				order.close(order.getAmount(),addUnit(order.getOpenPrice(),order.isLong()),0);
			}
		}
	}
	
	public double addUnit(double dvalue, boolean longpos) {
		String sdvalue = String.valueOf(dvalue);
		BigDecimal value = new BigDecimal("1.00000005")
				.round(new MathContext(new BigDecimal(sdvalue).scale()+1,RoundingMode.CEILING))
				.subtract(new BigDecimal("1"));
    	if(longpos)
    		return new BigDecimal(sdvalue).add(value).doubleValue();
    	else
    		return new BigDecimal(sdvalue).subtract(value).doubleValue();
	}
	
	public double addUnit(double dvalue, boolean longpos, double pips) {
		String sdvalue = String.valueOf(dvalue);
		BigDecimal value = BigDecimal.valueOf(pips);
    	if(longpos)
    		return new BigDecimal(sdvalue).add(value).doubleValue();
    	else
    		return new BigDecimal(sdvalue).subtract(value).doubleValue();
	}

	@Override
	public void onAccount(IAccount account) throws JFException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStop() throws JFException {
		// TODO Auto-generated method stub
		
	}

	public String getCurrency() {
		return currency;
	}

	public boolean isLongpos() {
		return longpos;
	}

	public boolean tryEnter() {
		return enter.compareAndSet(false,true);
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setLongpos(boolean longpos) {
		this.longpos = longpos;
	}

	public void setEnter(boolean enter) {
		this.enter.set(enter);
	}

}
