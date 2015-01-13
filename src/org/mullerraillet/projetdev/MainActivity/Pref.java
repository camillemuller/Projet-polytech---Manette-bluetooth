package org.mullerraillet.projetdev.MainActivity;




import java.util.List;

import org.mullerraillet.projetdev.Application.ManetteBluetooth;
import org.projetDev.mullerraillet.manettebluetooth.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Pref extends PreferenceActivity implements
SharedPreferences.OnSharedPreferenceChangeListener {


	private void setEntries(CharSequence pref, CharSequence[] entries,
			CharSequence[] values) {
		ListPreference lp = (ListPreference) findPreference(pref);
		lp.setEntries(entries);
		lp.setEntryValues(values);
	}
	

	public void loadSettings() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String ModuleBluetooth = prefs.getString("moduleBluetooth", "none");


		if (!ModuleBluetooth.equals("none")) {
			
			((ListPreference) findPreference("moduleBluetooth")).setValue(ModuleBluetooth);

		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



	
		/* Inflate from XML */
		this.addPreferencesFromResource(R.xml.preferences);


		/* Loading preferences already existing */
		this.loadSettings();

		
		/* Try to fetch the Bluetooth list */
		new SyncBluetoothList().execute((ManetteBluetooth)this.getApplication());
		
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


		Preference aboutBt = (Preference)findPreference("AboutBt");
		aboutBt.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				affiche();
				return true;
			}
		});
		
		

	}



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
		
			/* Return to previous Activity */
			this.finish();


	}
	
	
	
	private class SyncBluetoothList extends AsyncTask<ManetteBluetooth, Void, Integer>
	{
		
		private List<String> pairedDevices;

		@Override
		protected Integer doInBackground(ManetteBluetooth... params) {
			// TODO Auto-generated method stub
			pairedDevices =  params[0].getModule().getListpairedDevices();
			return null;
		}
		
		protected void onPostExecute(Integer result) {


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

			Pref.this.setEntries("moduleBluetooth", entries, values);
			
			((ListPreference) findPreference("moduleBluetooth")).setEnabled(true);
			}
			
		}
	}
	
	


	
	
}
