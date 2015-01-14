package org.mullerraillet.projetdev.MainActivity;

import java.io.IOException;

import org.mullerraillet.projetdev.Application.ManetteBluetooth;
import org.mullerraillet.projetdev.Bluetooth.BluetoothListener;
import org.mullerraillet.projetdev.Joystick.JoystickMovedListener;
import org.mullerraillet.projetdev.Joystick.JoystickView;
import org.projetDev.mullerraillet.manettebluetooth.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	private TextView txtX, txtY;
	private JoystickView joystick;
	private  ManetteBluetooth bt;
	private String vitesse = "1"; // Vitesse normal
	private ProgressBar saProgressBar;
	private ToggleButton SwitchOnOff;
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_search)
			startActivity(new Intent(MainActivity.this, Pref.class));
		return true;
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.joystick);

		saProgressBar = (ProgressBar) findViewById(R.id.chargementBatterie);
		//bt = new Bluetooth(getApplicationContext());
		
		bt = (ManetteBluetooth) this.getApplication();

		SwitchOnOff = (ToggleButton)  findViewById(R.id.ToggleButtun1);
		final RadioGroup RG = (RadioGroup) findViewById(R.id.radioGroup1);
		final Button klaxon = (Button) findViewById(R.id.button1);

		
			
		klaxon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					if(SwitchOnOff.isChecked())
					bt.getModule().sendData("[B]\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}});

		RG.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// find which radio button is selected
				if(checkedId == R.id.radio0) {
					vitesse = "1";
				} else
				{
					vitesse = "0";
				}
			}
		});
		SwitchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if(!b)
				{
					try {
						bt.getModule().closeBT(); 
					}catch (Exception e)
					{
						Toast.makeText(getApplicationContext(), "L'apparail est déjà déconnecté", Toast.LENGTH_LONG).show();
					}
				}else
				{
					try
					{
						if(bt.getModule().findBT())
							bt.getModule().openBT();
						else
							Toast.makeText(getApplicationContext(), "L'apparail non appareillé ", Toast.LENGTH_LONG).show();
					} catch (IOException er)
					{
						SwitchOnOff.setChecked(false);
					}
				}
			}
		});
		txtX = (TextView)findViewById(R.id.TextViewX);
		txtY = (TextView)findViewById(R.id.TextViewY);
		joystick = (JoystickView)findViewById(R.id.joystickView);
		joystick.setOnJostickMovedListener(_listener);
		bt.getModule().setOnBluetoothListener(_ListenerBluetooth);
	}


	/**
	 * Gestion des actions graphiques en cas d'action du bluetooth
	 */
	private BluetoothListener _ListenerBluetooth = new BluetoothListener()
	{
		@Override
		public void onConnect()
		{
			SwitchOnOff.setChecked(true);
		}
		@Override
		public void onDisconnect()
		{
			SwitchOnOff.setChecked(false);
			Toast.makeText(getApplicationContext(), "Connexion perdue", Toast.LENGTH_LONG).show();
		}
		@Override
		public void onReceived(String data) {
			// TODO Auto-generated method stub
			saProgressBar.setProgress(Integer.parseInt( data.substring(data.indexOf(",")+1, data.indexOf("]"))));
		}

	};
 


	/**
	 * Gestion des actions graphiques en cas d'action du Joystick
	 * Gestion des actions d'envoie des coordonnées
	 */
	private JoystickMovedListener _listener = new JoystickMovedListener() {

		@Override

		// Movement of the joystick
		public void OnMoved(int pan, int tilt) {
			txtX.setText(": "+Integer.toString(pan));
			txtY.setText(": "+Integer.toString(tilt));
			try {
				if(SwitchOnOff.isChecked())
				bt.getModule().sendData("[CMD,"+Integer.toString(pan)+","+Integer.toString(tilt)+","+vitesse+"]\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void OnReleased() {
		}

		@Override
		public void OnReturnedToCenter() {
			txtX.setText(": "+Integer.toString(0));
			txtY.setText(": "+Integer.toString(0));
			try {
				if(SwitchOnOff.isChecked())
				bt.getModule().sendData("[CMD,"+Integer.toString(0)+","+Integer.toString(0)+","+vitesse+"]\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
	}; 
}