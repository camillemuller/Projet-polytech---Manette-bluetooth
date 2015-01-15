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
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * Classe gérant la connection Bluetooth
 * Permet de recevoir & envoyer des données vers un apparail choisit
 * @author camillemuller
 *
 */
public class Bluetooth extends Activity 	  
{
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket mmSocket;
	private static BluetoothDevice mmDevice;
	private OutputStream mmOutputStream;
	private InputStream mmInputStream;
	private Thread workerThread,beginThread;
	private byte[] readBuffer;
	private int readBufferPosition;
	private volatile boolean stopWorker = true;
	private String lesData;
	private Context mainContext;
	private BluetoothListener sonListener;





	private  BluetoothReceiver sonBluetoothReceiver;
	private static final String SSProfile = "00001101-0000-1000-8000-00805f9b34fb";
	private String DeviceName;



	public void setOnBluetoothListener(BluetoothListener unListener){
		sonListener= unListener;
	}

	String getLesData()
	{
		return this.lesData;
	}

	public void setLesData(String desData)
	{
		this.lesData = desData;
	}

	/**
	 * Default constructor
	 */
	public Bluetooth()
	{
	}

	/**
	 * Constructeur permettant d'avoir le context et de choisir un module
	 * @param mainContext
	 * @param module
	 */
	public Bluetooth(Context mainContext, String module)
	{
		this.mainContext = mainContext;
		this.DeviceName = module;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	public BluetoothListener getSonListener() {
		return sonListener;
	}

	public void setSonListener(BluetoothListener sonListener) {
		this.sonListener = sonListener;
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



	public List<String> getListpairedDevices()
	{
		List<String> paraidDevices = new ArrayList<String>();

		try
		{
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
			if(pairedDevices.size() > 0)
			{
				for(BluetoothDevice device : pairedDevices)
				{
					paraidDevices.add(device.getName());
				}
			}
		}
		catch(NullPointerException e)
		{

		}
		return paraidDevices;

	}


	/**
	 * Permet d'initialisé la connexion bluetooth
	 * @return
	 * @throws IOException
	 */
	public boolean findBT() throws IOException
	{

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
				if(device.getName().equals(DeviceName))
				{
					mmDevice = device;
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	/**
	 * Permet d'ouvrir une connexion Bluetooth
	 * findBT() doit être utiliser au préalable
	 * @throws IOException
	 */
	public void openBT() throws IOException
	{


		if(mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON)
		{
			UUID uuid = UUID.fromString(SSProfile); //Standard SerialPortService ID

			mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
			/** ATTENTION IMPORTANT
			 * Creating new connections to remote Bluetooth devices should not be attempted while device discovery is in progress. 
			 * Device discovery is a heavyweight procedure on the Bluetooth adapter  and will significantly slow a device connection. 
			 * Use cancelDiscovery() to cancel an ongoing discovery.  
			 * Discovery is not managed by the Activity, but is run as a system service,
			 * so an application should always call cancelDiscovery() even if it did not directly request a discovery, 
			 *  just to be sure.
			 */
			mBluetoothAdapter.cancelDiscovery();
			mmSocket.connect();
			mmOutputStream = mmSocket.getOutputStream();
			mmInputStream = mmSocket.getInputStream();



			beginListenForData();
		}

	}


	/**
	 * Création du thread de reception des données bluetooth
	 */
	public void beginListenForData()
	{
		final Handler handler = new Handler();
		final byte delimiter = 10; //This is the ASCII code for a newline character

		stopWorker = false;
		readBufferPosition = 0;
		readBuffer = new byte[1024];
		workerThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while(!Thread.currentThread().isInterrupted() && !stopWorker )
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
										@Override
										public void run()
										{											
											sonListener.onReceived(data);
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

	/**
	 * Fonction d'envoie des données vers l'appareil bluetooth
	 * @param msg
	 * @throws IOException
	 */
	public void sendData(String msg) throws IOException
	{
		if(goodForSend())
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
	}

	/**
	 * Permet de fermer la connexion Bluetooth
	 * @throws IOException
	 */
	public void closeBT() throws IOException
	{
		stopWorker = true;

		mmOutputStream.close();
		mmInputStream.close();
		mmSocket.close();

	}

	private boolean goodForSend()
	{
		if(!stopWorker)
		{
			return true;
		}else
			return false;
	}

}