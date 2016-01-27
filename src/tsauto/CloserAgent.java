package tsauto;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.dukascopy.api.IOrder;



public class CloserAgent extends Thread {
	
	private long timeoutCloseOrders = 20*1000;
	private long timeoutSetStopLoss = 15*1000;
	private AlphaFlashStremer alpha;

	public CloserAgent(AlphaFlashStremer alpha) {
		this.alpha = alpha;
	}
	
	public List<String> ids = new LinkedList<String>();
	
	public void run() {
		while(true) {
			try {
				List<IOrder> orders = alpha.getOrders();
				//System.out.println("close orders " + orders);
				for(IOrder order : orders) {
					if(order.getState()==com.dukascopy.api.IOrder.State.FILLED) {
						long actualTime = Calendar.getInstance().getTime().getTime();
						if(order.getFillTime()+timeoutCloseOrders<=actualTime) {
							alpha.closeOrder(order);
							ids.add(order.getId());
							Thread.sleep(600);
						} else if(order.getFillTime()+timeoutSetStopLoss<=actualTime) {
							if(order.getStopLossPrice()==0) {
								alpha.setStopLoss(order);
								Thread.sleep(600);
							}
						}
					}
				}
				Thread.sleep(10);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
