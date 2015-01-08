package org.mullerraillet.projetdev.Bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothReceiver  extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();

		if(Bluetooth.sonListener != null)
		{
			if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
				Bluetooth.sonListener.onConnect();
			}

			if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
				Bluetooth.sonListener.onDisconnect();
			}
		}

	}
}


