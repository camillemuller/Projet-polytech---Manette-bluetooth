package org.mullerraillet.projetdev.Application;

import java.io.IOException;

import org.mullerraillet.projetdev.Bluetooth.Bluetooth;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

public class ManetteBluetooth extends Application {
	private Bluetooth module;

	private int mInterval = 1000; // 1 seconds z
	private Handler mHandler;


	/**
	 * Création de l'activité et du module bluetooth lors du démarrage de l'application
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		/* Create and setup the model with saved values */
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		createBluetooth( prefs.getString("bt_mod", "0"));
		mHandler = new Handler();
		startRepeatingTask();
	}


	/**
	 * Création du module bluetooth sauf si pas de module choisi 
	 * @param unModule
	 */
	public void createBluetooth(String unModule)
	{
		if(unModule != "")
		{	this.module = new Bluetooth(this.getApplicationContext(),unModule);

		}

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


	Runnable mStatusChecker = new Runnable() {
		@Override 
		public void run() {
			if(module != null)
			{
				try {
					if(module != null)
					module.sendData("[HB]\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mHandler.postDelayed(mStatusChecker, mInterval);
		}
	};

	void startRepeatingTask() {
		mStatusChecker.run(); 
	}

	void stopRepeatingTask() {
		mHandler.removeCallbacks(mStatusChecker);
	}





}
