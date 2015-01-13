package org.mullerraillet.projetdev.Application;

import org.mullerraillet.projetdev.Bluetooth.Bluetooth;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ManetteBluetooth extends Application {
	private Bluetooth module;
	

	@Override
	public void onCreate() {
		super.onCreate();
		/* Create and setup the model with saved values */
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		createBluetooth( prefs.getString("moduleBluetooth", "0"));
	}

	
	public void createBluetooth(String unModule)
	{
		if(unModule != "")
		this.module = new Bluetooth(this.getApplicationContext(),unModule);
	}

	public Bluetooth getModule() {
		return module;
	}


	public void setModule(Bluetooth module) {
		this.module = module;
	}
}
