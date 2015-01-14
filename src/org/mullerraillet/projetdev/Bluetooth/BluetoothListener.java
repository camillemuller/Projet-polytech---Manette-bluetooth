package org.mullerraillet.projetdev.Bluetooth;


/**
 * Classe interface bluetooth permettant la gestion des events
 * @author camillemuller
 *
 */
public interface BluetoothListener {
	public void onConnect();
	public void onDisconnect();
	public void onReceived(String msg);

}
