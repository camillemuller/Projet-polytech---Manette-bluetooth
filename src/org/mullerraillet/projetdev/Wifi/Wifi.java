package org.mullerraillet.projetdev.Wifi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import android.app.Activity;


public class Wifi extends Activity {

	public ServerSocketChannel sonSocket;
	public Selector selector;
	public String sonIP = "192.168.1.1";
	public int sonPort =  80;
	public PrintWriter sonOut;
	public BufferedReader sonIn;


	
	public Wifi()
	{
		
	}


	public void sendData(String unMsg)
	{
		this.sonOut.print(unMsg);
	}
	
	
	
	private Thread readingThread = new ListeningThread();
	private boolean running;
	private boolean close;
	  private BufferedReader messenger;
	  private SocketChannel socket;

	 /**
	  * Listening thread - reads messages in a separate thread so the application does not get blocked.
	  */
	 private class ListeningThread extends Thread {

	public void run() {
	   running = true;
	   try {
	    while(!close) listen();
	    messenger.close();
	   }
	   catch(ConnectException ce) {
	   // doNotifyConnectionFailed(ce);
	   }
	   catch(Exception e) {
//	    e.printStackTrace();
	    try {
			messenger.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	   }
	   running = false;
	  }
	 }

	 /**
	  * Connects to host and port.
	  * @param host Host to connect to.
	  * @param port Port of the host machine to connect to.
	  */
	 public void connect(String host, int port) {
	  try {
	   socket = SocketChannel.open();
	   socket.configureBlocking(false);
	   socket.register(this.selector, SelectionKey.OP_CONNECT);
	   socket.connect(new InetSocketAddress(host, port));
	  }
	  catch(IOException e) {
	 //  this.doNotifyConnectionFailed(e);
	  }
	 }

	 /**
	  * Waits for an event to happen, processes it and then returns.
	  * @throws IOException when something goes wrong.
	  */
	 protected void listen() throws IOException {
	  // see if there are any new things going on
	  this.selector.select();
	  // process events
	  Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
	  while(iter.hasNext()) {
	   SelectionKey key = iter.next();
	   iter.remove();
	   // check validity
	   if(key.isValid()) {
	    // if connectable...
	    if(key.isConnectable()) {
	     // ...establish connection, make messenger, and notify everyone
	     SocketChannel client = (SocketChannel)key.channel();
	     // now this is tricky, registering for OP_READ earlier causes the selector not to wait for incoming bytes, which results in 100% cpu usage very, very fast
	     if(client!=null && client.finishConnect()) {
	      client.register(this.selector, SelectionKey.OP_READ);
	     }
	    }
	    // if readable, tell messenger to read bytes
	    else if(key.isReadable() && (SocketChannel)key.channel()== socket) {
	    	String line;
	    	if((line=messenger.readLine()) != null)
	    	{
	    		if(!line.endsWith("\n")) 
	    		{
	    			
	    		}
	    	}
	    }
	   }
	  }
	 }

	 /**
	  * Starts the client.
	  */
	 public void start() {
	  // start a reading thread
	  if(!this.running) {
	   this.readingThread = new ListeningThread();
	   this.readingThread.start();
	  }
	 }

	 /**
	  * Tells the client to close at nearest possible moment.
	  */
	 public void close() {
	  this.close = true;
	 }
	
	

	
}
