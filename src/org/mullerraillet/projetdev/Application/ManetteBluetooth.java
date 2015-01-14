package org.mullerraillet.projetdev.Application;

import org.mullerraillet.projetdev.Bluetooth.Bluetooth;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ManetteBluetooth extends Application {
	private Bluetooth module;
	

	
	/**
	 * Création de l'activité et du module bluetooth lors du démarrage de l'application
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		/* Create and setup the model with saved values */
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		createBluetooth( prefs.getString("moduleBluetooth", "0"));
	}

	
	/**
	 * Création du module bluetooth sauf si pas de module choisi 
	 * @param unModule
	 */
	public void createBluetooth(String unModule)
	{
		if(unModule != "")
		this.module = new Bluetooth(this.getApplicationContext(),unModule);
	}

	/**
	 * Getter module Bluetooth
	 * @return
	 */
	public Bluetooth getModule() {
		return module;
	}


	/**
	 * Setter module Bluetooth
	 * @param module
	 */
	public void setModule(Bluetooth module) {
		this.module = module;
	}
}
