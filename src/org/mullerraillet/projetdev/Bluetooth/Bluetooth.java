package org.mullerraillet.projetdev.Bluetooth;

/**
 * Created by camillemuller on 13/05/2014.
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;



public class Bluetooth extends Activity 	  
{
	BluetoothAdapter mBluetoothAdapter;
	BluetoothSocket mmSocket;
	static BluetoothDevice mmDevice;
	OutputStream mmOutputStream;
	InputStream mmInputStream;
	Thread workerThread;
	byte[] readBuffer;
	int readBufferPosition;
	int counter;
	volatile boolean stopWorker;
	String lesData;
	Context mainContext;
	ProgressBar saProgressBar;
	static BluetoothListener sonListener;
	static BluetoothReceiver sonBluetoothReceiver;



	public void setOnBluetoothListener(BluetoothListener unListener){
		sonListener= unListener;
	}

	String getLesData()
	{
		return this.lesData;
	}

	void setLesData(String desData)
	{
		this.lesData = desData;
	}

	public Bluetooth(Context mainContext, ProgressBar uneProgressBar)
	{
		this.mainContext = mainContext;
		this.saProgressBar = uneProgressBar;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(sonBluetoothReceiver == null)
		{
			IntentFilter filter = new IntentFilter();
			filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
			filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED); 
			sonBluetoothReceiver = new BluetoothReceiver();
			LocalBroadcastManager.getInstance(this).registerReceiver(new BluetoothReceiver(), filter);
		}
	}



	public boolean findBT() throws IOException
	{
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		boolean flag= false;
		if(mBluetoothAdapter == null)
		{
			Toast.makeText(mainContext, "Le bluetooth n'est pas activé ", Toast.LENGTH_LONG).show();
			throw new IOException();
		}

		if(!mBluetoothAdapter.isEnabled())
		{
			Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBluetooth, 0);
		}

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if(pairedDevices.size() > 0)
		{
			for(BluetoothDevice device : pairedDevices)
			{
				if(device.getName().equals("HC-06"))
				{
					mmDevice = device;
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	public void openBT() throws IOException
	{
		UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID

		try
		{mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
		}
		catch (Exception e)
		{
			Toast.makeText(mainContext, "Votre module bluetooth n'est pas appareillé dans vos parametres bluetooth", Toast.LENGTH_LONG).show();
			throw new IOException();
		}
		mmSocket.connect();
		mmOutputStream = mmSocket.getOutputStream();
		mmInputStream = mmSocket.getInputStream();
		beginListenForData();

	}


	void beginListenForData()
	{
		final Handler handler = new Handler();
		final byte delimiter = 10; //This is the ASCII code for a newline character

		stopWorker = false;
		readBufferPosition = 0;
		readBuffer = new byte[1024];
		workerThread = new Thread(new Runnable()
		{
			public void run()
			{
				while(!Thread.currentThread().isInterrupted() && !stopWorker)
				{
					try
					{
						int bytesAvailable = mmInputStream.available();
						if(bytesAvailable > 0)
						{
							byte[] packetBytes = new byte[bytesAvailable];
							mmInputStream.read(packetBytes);
							for(int i=0;i<bytesAvailable;i++)
							{
								byte b = packetBytes[i];
								if(b == delimiter)
								{
									byte[] encodedBytes = new byte[readBufferPosition];
									System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
									final String data = new String(encodedBytes, "US-ASCII");
									readBufferPosition = 0;
									handler.post(new Runnable()
									{
										public void run()
										{
											try
											{
												saProgressBar.setProgress(Integer.parseInt( data.substring(data.indexOf(",")+1, data.indexOf("]"))));
												
											}catch(NumberFormatException e)
											{
												// DO nothing ( Si le nombre est pas le bon
											}catch(StringIndexOutOfBoundsException e)
											{
												//Do Nothing
											}
										}
									});
								}
								else
								{
									readBuffer[readBufferPosition++] = b;
								}
							}
						}
					}
					catch (IOException ex)
					{
						stopWorker = true;
					}
				}
			}
		});

		workerThread.start();
	}

	public void sendData(String msg) throws IOException
	{
		msg += "\n";
		try
		{
			mmOutputStream.write(msg.getBytes());
		}catch (Exception e)
		{
			Toast.makeText(mainContext, "Impossible de rafraichir l'apparail n'est pas syncroniser (ou plus)", Toast.LENGTH_LONG).show();
			throw new IOException();
		}

	}

	public void closeBT() throws IOException
	{
		stopWorker = true;
		mmOutputStream.close();
		mmInputStream.close();
		mmSocket.close();
	}

}