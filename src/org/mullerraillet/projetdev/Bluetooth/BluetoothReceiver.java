package org.mullerraillet.projetdev.Bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.mullerraillet.projetdev.Application.ManetteBluetooth;

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
					uneM.getModule().getSonListener().onDisconnect();
			}
		}

	}
}


