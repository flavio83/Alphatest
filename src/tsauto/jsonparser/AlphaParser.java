package tsauto.jsonparser;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TimeZone;

import org.apache.axis.encoding.Base64;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.nktin.protocol.tailored.NewsEvent;
import com.nktin.protocol.tailored.ParseAllXMLs;
import com.ntkn.messages.IndicatorMessage;
import com.ntkn.messages.IndicatorMessageHeader;
import com.ntkn.messages.IndicatorMessagePayload;



public class AlphaParser {

	String filename = "C:\\Users\\flavio\\Desktop\\alphaflashdatalog\\alphatestLIVE1421403652732.txt";

	Gson gson = new Gson();
	
	 private static final String DATE_FORMAT = "E dd-MM-yyyy HH:mm:ss.SSS";
	 
	 ParseAllXMLs db = new ParseAllXMLs();
	 
	 PrintWriter writer = null;

	public AlphaParser() {
		SimpleDateFormat dateFormatter =  new SimpleDateFormat(DATE_FORMAT);
		dateFormatter.setTimeZone(TimeZone.getDefault());   
        try {
        	writer = new PrintWriter("C:\\Users\\flavio\\Desktop\\alphaflashdatalog\\logUS_Pending_Home_Sales.txt", "UTF-8");
              Scanner content = new Scanner(new File(filename)).useDelimiter("\\r\\n");
              while(content.hasNext()) {
            	  String text = content.next();
            	  LinkedTreeMap news = gson.fromJson(text,LinkedTreeMap.class);
            	  Date date = dateFormatter.parse((String)news.get("date"));
                  byte[] headerBytes = Base64.decode((String)news.get("HeaderBytes"));
                  byte[] payloadBytes = Base64.decode((String)news.get("PayloadBytes"));
                  byte[] crcBytes = Base64.decode((String)news.get("CrcBytes"));
                  IndicatorMessageHeader header = new IndicatorMessageHeader(headerBytes);
                  IndicatorMessagePayload payload = new IndicatorMessagePayload(payloadBytes);
                  IndicatorMessage msg = new IndicatorMessage(header, payload, crcBytes);
                  if(header.getMessageTypeId()==0) {
                	  NewsEvent event = db.getMap().get(header.getMessageCategoryId());
                	  //if(event!=null && "US_Pending_Home_Sales".compareToIgnoreCase(event.getName())==0) {
	                	  writer.write(date + " TypeID " + header.getMessageTypeId());
	                	  writer.write("\r\n");
	                	  writer.write(event==null ? "null" : event.toString());
	                	  writer.write("\r\n");
	                	  writer.write(payload.getIndicatorMessageFields().toString());
	                	  writer.write("\r\n");
	                	  writer.flush();
                	  //}
                  }
              }
              writer.close();
              content.close();
        } catch(Exception e) {
              e.printStackTrace();
        }
    }

	public static void main(String[] args) {
		new AlphaParser();
	}
	
	class AlphaNews extends HashMap<String,String> {
		
	}

}