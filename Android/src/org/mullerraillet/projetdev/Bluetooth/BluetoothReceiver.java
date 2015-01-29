package org.mullerraillet.projetdev.Bluetooth;

import java.io.IOException;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.mullerraillet.projetdev.Application.ManetteBluetooth;


/**
 * Classe bluetooth permettant la gestion de la detection lors de la perte de
 * connexion Bluetooth
 * @author camillemuller
 *
 */
public class BluetoothReceiver  extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();

		ManetteBluetooth uneM = ((ManetteBluetooth) (context.getApplicationContext()));
		
		if(uneM.getModule().getSonListener() != null)
		{
			if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
					
					uneM.getModule().getSonListener().onConnect();
			}

			if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
			
				/*
				 * Fermeture des threads etc..
				 */
				try {
					uneM.getModule().closeBT();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				uneM.getModule().getSonListener().onDisconnect();
			}
		}

	}
}


