package org.mullerraillet.projetdev.Activity;




import java.util.List;


import org.mullerraillet.projetdev.Application.ManetteBluetooth;
import org.projetDev.mullerraillet.manettebluetooth.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Pref extends PreferenceActivity implements
SharedPreferences.OnSharedPreferenceChangeListener {


	@SuppressWarnings("deprecation")
	private void setEntries(CharSequence pref, CharSequence[] entries,
			CharSequence[] values) {
		ListPreference lp = (ListPreference) findPreference(pref);
		lp.setEntries(entries);
		lp.setEntryValues(values);
	}


	@SuppressWarnings("deprecation")
	public void loadSettings() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String ModuleBluetooth = prefs.getString("moduleBluetooth", "none");


		if (!ModuleBluetooth.equals("none")) {
			((ListPreference) findPreference("bt_mod")).setValue(ModuleBluetooth);

		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



		/* Inflate from XML */
		this.addPreferencesFromResource(R.xml.preferences);


		/* Loading preferences already existing */
		//this.loadSettings();


		/* Try to fetch the Bluetooth list */
		List<String> pairedDevices= ((ManetteBluetooth)this.getApplication()).getModule().getListpairedDevices();






		/* Preference change listener */
		PreferenceManager.getDefaultSharedPreferences(this)
		.registerOnSharedPreferenceChangeListener(this);


		Preference button = (Preference)findPreference("button");
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				finish();
				return true;
			}
		});



		if(pairedDevices.size() > 0 )
		{
			String[] entries = new String[pairedDevices.size()];
			String[] values = new String[pairedDevices.size()];

			int i = 0;
			for (String pairedDevice : pairedDevices) {
				entries[i] = pairedDevice;
				values[i] = pairedDevice;
				i++;
			}


			Pref.this.setEntries("bt_mod", entries, values);
			((ListPreference) findPreference("bt_mod")).setEnabled(true);

		}



		Preference aboutBt = (Preference)findPreference("AboutBt");
		aboutBt.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				affiche();
				return true;
			}
		});

		/* Loading preferences already existing */
		this.loadSettings();

	}



	@SuppressWarnings("deprecation")
	public void affiche()
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("A propos");
		alertDialog.setMessage("Application soutenue par Muller Camille" 
				+"Contact : muller_camille@icloud.com");
		alertDialog.setButton("Fermer", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// here you can add functions
			}
		});
		alertDialog.show();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {


		if(key.contains("bt_mod"))
		{

			((ManetteBluetooth)this.getApplication()).createBluetooth(sp.getString("bt_mod", "0"));
		}
		/* Return to previous Activity */
		this.finish();


	}




}





