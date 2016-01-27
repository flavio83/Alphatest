package tsauto.stats;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;




public class MainStat {
	
	TSAuto ts = TSPlay.load(null);

	public MainStat() throws Exception {		
	    SimpleDateFormat parseFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSSZ");
		Date from = parseFormat.parse("18/02/2015 12:00:00.000-0000");
		Date to = parseFormat.parse("20/02/2015 12:00:00.000-0000");
		List list = getHistory(Instrument.EURUSD, from, to);
		System.out.println(list.size());
	}
	
	public List<ITick> getHistory(Instrument ist, Date from, Date to) throws Exception {
		return ts.getHistoryTicks(Instrument.EURAUD, from.getTime(), to.getTime());
	}

	public static void main(String[] args) {
		try {
			new MainStat();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
