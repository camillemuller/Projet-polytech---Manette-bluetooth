package org.mullerraillet.projetdev.Bluetooth;

public interface BluetoothListener {
	public void onConnect();
	public void onDisconnect();
	public void onReceived(String msg);

}
