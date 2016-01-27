package tsauto;

/**
 * All of the information (including source code, content and artwork) are copyright. No part of this message 
 * or any included attachment may be reproduced, stored in a retrieval system, transmitted, broadcast or published by any means 
 * (optical, magnetic, electronic, mechanical or otherwise) without the prior written permission of Need to Know News. 
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND 
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, 
 * ARE HEREBY EXCLUDED. "Need to Know News, LLC" AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL "Need to Know News" OR 
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL
 * OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO
 *  USE THIS SOFTWARE, EVEN IF "Need To Know News" HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *  
 *  Need to Know News, Copyright 2008
 * 
 */

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.io.FilenameUtils;
import org.springframework.security.crypto.codec.Base64;

import com.dukascopy.api.IContext;
import com.google.gson.Gson;
import com.ntkn.messages.HexDump;
import com.ntkn.messages.IndicatorMessage;
import com.ntkn.messages.IndicatorMessageHeader;
import com.ntkn.messages.IndicatorMessagePayload;

/**
 * TcpMsgReceiver is a simple example demonstrating connect, login, and receiving of 
 * indicator data from a Lightning Bolt v5.x data feed.
 *  
 * @author jwalton
 *
 */
public class TcpMsgReceiverAlphaFlashStream extends Thread
{
    private Socket xferSocket = null;  
    private DataOutputStream dataOutStream = null;
    private DataInputStream dataInStream = null;    
    private SimpleDateFormat dateFormatter = null;    
    
    private static final String DATE_FORMAT = "HH:mm:ss.SSS";
    
    static BufferedWriter bw = null;

	static {
		try {
			//bw = new BufferedWriter(new FileWriter(getUrlFile(), true));
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	private static String getUrlFile() {
		String aux= "LIVE2015_alphatest"+Calendar.getInstance().getTimeInMillis() + ".txt";
		StringBuffer buf = new StringBuffer();
		buf.append("datalog");
		buf.append(Calendar.getInstance().getTime());
		buf.append(".txt");
		aux = aux.trim();
		System.out.println("-------------> "+aux);
		String path = FilenameUtils.getPath(aux.trim());
		System.out.println("-------------> "+path);
		return aux;
	}
	
	static String[] args = new String[6];
	
	static {
		args[0] = "data-01.lon6.mni-news.com";
		args[1] = "30226";
		args[2] = "tonybannayi";
		args[3] = "cp93laoj";
		args[4] = "false";
		args[5] = "false";
	}
	
	/**
	 * Entry point for this application.
	 * 
	 * @param args
	 */
	public void startReadingStream() 
	{
		args = new String[6];
		args[0] = "data-01.lon6.mni-news.com";
		args[1] = "30226";
		args[2] = "tonybannayi";
		args[3] = "cp93laoj";
		args[4] = "false";
		args[5] = "false";
		if (args.length < 6)
		{
			System.out.println("Usage: TcpMsgReceiver <host> <port> <user> <pass> <showMessage> <useSSL>");
			System.out.println("       <showMessage> - true to show the complete message");
			System.out.println("       <useSSL> - true to use SSL connection");
			System.exit(1);
		}	
		
		int port = Integer.parseInt(args[1]);
		boolean show = Boolean.parseBoolean(args[4]);
		boolean useSSL = Boolean.parseBoolean(args[5]);
		
		System.out.println("Connecting to "+args[0]+":"+port);
		
		receive(args[0],port,args[2],args[3], show, useSSL);
	}
	
	private BlockingQueue<IndicatorMessage> bQueue = new ArrayBlockingQueue<IndicatorMessage>(1000);
	
	public BlockingQueue<IndicatorMessage> getbQueue() {
		return bQueue;
	}
	
	private IContext context = null;
	private DukasConsole console = null;

	/**
	 * Default constructor.
	 */
	public TcpMsgReceiverAlphaFlashStream(DukasConsole console) 
	{
		this.console = console;
		dateFormatter =  new java.text.SimpleDateFormat(DATE_FORMAT);
		dateFormatter.setTimeZone(TimeZone.getDefault());    	
	}
	
	public void run() {
		startReadingStream();
	}

	/**
	 * Start receiving indicator data using the supplied parameters.
	 * 
	 * @param host The host to connect to
	 * @param port The port to use
	 * @param user user name
	 * @param pass password
	 * @param show True to show message details
	 */
	public void receive(String host, int port, String user, String pass, boolean show, boolean useSSL)
	{
		try 
		{
		    // connect to the server
			xferSocket = createSocket(host, port, useSSL);
			
			// setup some socket option to keep our connection alive
			xferSocket.setTcpNoDelay(true);
			xferSocket.setKeepAlive(true);			
			
			// setup the streams for read/write
	        dataOutStream = new DataOutputStream(xferSocket.getOutputStream());
	        dataInStream = new DataInputStream(xferSocket.getInputStream());	        
	        
	        // try to authenticate
	        if (negotiateAuth(user,pass))
	        {	            
	            // we're in, start reading messages 
	        	int count = 0;
			Date timestamp = null;

	        	do
	        	{	        		 
		        	// Read the header
		        	byte[] headerBytes = new byte[IndicatorMessageHeader.HEADER_SIZE_IN_BYTES];		        		        
		        	dataInStream.readFully(headerBytes);
		        	IndicatorMessageHeader header = new IndicatorMessageHeader(headerBytes);
		        	
		        	// Read the payload       	
		        	byte[] payloadBytes = new byte[header.getMessageLength()-IndicatorMessageHeader.HEADER_SIZE_IN_BYTES-IndicatorMessageHeader.INT_SIZE_IN_BYTES];		        	
		        	dataInStream.readFully(payloadBytes);	        	
		        	IndicatorMessagePayload payload = new IndicatorMessagePayload(payloadBytes);

	        		// this is the CRC
		        	byte[] crcBytes = new byte[4];
		        	dataInStream.readFully(crcBytes);

		        	timestamp = new Date();
		        	
		        	// construct an object from the bytes
		        	IndicatorMessage msg = new IndicatorMessage(header, payload, crcBytes);
		        	   
		        	if(msg.getHeader().getMessageTypeId()==0) {
		        		console.onEvent(msg);
		        	}
		        	
		        	//System.out.println("queue size: " + bQueue.size());
		        	//if(msg.getHeader().getMessageTypeId()==0) {
		        	//bQueue.put(msg);
		        	//}
		        	count++;
		        	
		        	// show some info on console
		        	if (show || msg.getHeader().getMessageTypeId()==0)
		        	{
		        		System.out.println("\nReceived Indicator:"+msg.toString()+" at "+ dateFormatter.format(timestamp));
		        		System.out.println(msg.getPayload().toMultilineString());
		        		System.out.println(msg.getPayload().toFormattedString());
		        		
		        		System.out.println("Hex:"+HexDump.dumpToString(msg.getBytes()));
		        		
		        		AlphaNews news = new AlphaNews();
		        		news.put("indicator", msg.toString());
		        		news.put("date", dateFormatter.format(timestamp));
		        		news.put("timems", String.valueOf(timestamp.getTime()));
		        		news.put("toMultilineString", msg.getPayload().toMultilineString());
		        		news.put("toFormattedString", msg.getPayload().toFormattedString());
		        		news.put("hex", HexDump.dumpToString(msg.getBytes()));
		        		news.put("CrcBytes",  new String(Base64.encode(crcBytes)));
		        		news.put("HeaderBytes",  new String(Base64.encode(headerBytes)));
		        		news.put("PayloadBytes",  new String(Base64.encode(payloadBytes)));
		        		
		        		Gson gson = new Gson();
		        		String text = gson.toJson(news, AlphaNews.class);
		        		//System.out.println(text);
		        		try {
		        			//bw.write(text+"\r\n");
		        			//bw.flush();
		        		} catch (Exception e) {
		        			e.printStackTrace();
		        		}
		        	}
		        	else
		        	{
		        		//System.out.println("\nReceive #"+count+" Indicator TxmitId:"+msg.getTxmitId()+" CRC:"+msg.getCRC()+" at "+ dateFormatter.format(timestamp));
		        	}
		        	
	        	} while(true);
	        }
	        else
	        {
	        	System.out.println("Login failed.");
	        }	        	        
	    } 
		catch (Exception e) 
		{
			System.out.println("Unexpected exception occurred:"+e.getMessage());
			e.printStackTrace();
	    }		
		finally
		{
	        try { if (dataOutStream != null) dataOutStream.close(); } catch(Exception e){};	    
	        try { if (dataInStream != null) dataInStream.close(); } catch(Exception e){};
	        try { if (xferSocket != null) xferSocket.close();	} catch(Exception e){};
	        
	        System.out.println("Closing up shop.");
	        System.out.println("attemp again...");
	        
	        receive(args[0],port,args[2],args[3], show, useSSL);
		}
	}
	
	private Socket createSocket(String host, int port, boolean useSSL) 
		throws UnknownHostException, IOException
	{
		Socket socket = null;
		
		if (useSSL)
		{
		    socket = SSLSocketFactory.getDefault().createSocket(host, port);
		}
		else
		{
			socket = new Socket(host, port);
		}
		
		return socket;
	}

	/**
	 * Send the login to the Lightning Bolt v5.x server.
	 * 
	 * @param user username
	 * @param pass password
	 * @return boolean indicating status of authentication. 
	 * @throws IOException Socket can throw this exception
	 */
	private boolean negotiateAuth(String user, String pass) 
		throws IOException
	{
		boolean allowed = false;
		
		if (xferSocket != null && dataOutStream != null && dataInStream != null) 
		{
			xferSocket.getOutputStream().write( new String( "AUTH "+user+" "+pass+"\n\n" ).getBytes() );
			
			
			String responseLine;

			responseLine = dataInStream.readLine();			
            System.out.println("Server AUTH repsonse: " +  responseLine);
            if (responseLine.indexOf("OK") != -1) 
            {
            	allowed = true;
            	
                // next new line ???
    			responseLine = dataInStream.readLine();
    			
    			System.out.println("Connected and authorized, waiting for indicator messages.");    			
            }					
		}
		
		return allowed;
	}
	
	class AlphaNews extends HashMap<String,String> {
		
	}
	
}
