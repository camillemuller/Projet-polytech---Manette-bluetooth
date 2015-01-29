package org.mullerraillet.projetdev.Activity;

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
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


/**
 * Classe principale "La vue" Gerant l'activité de la fenetre principale
 * @author camillemuller
 *
 */
public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	private TextView txtX, txtY;
	private JoystickView joystick;
	private  ManetteBluetooth bt;
	private String vitesse = "1"; // Vitesse normal
	private ProgressBar saProgressBar;
	private ToggleButton SwitchOnOff;
	private  Button unBp;



	/**
	 * Action effectuée lorsque l'on appuis sur le bouton Menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * Action effectuée lorsqu'un item est selectionné dans le menu 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_search)
			startActivity(new Intent(MainActivity.this, Pref.class));
		return true;
	}
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		bt.getModule().setOnBluetoothListener(_ListenerBluetooth);
	}

	/**
	 * Appellé lors de la création de l'activité
	 */
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
		
		unBp = (Button) findViewById(R.id.Connec);

		unBp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					
					
					
					if(!SwitchOnOff.isChecked())
					{						
						try
						{
							unBp.setClickable(false);
							if(bt.getModule().findBT())
							{	
								bt.getModule().openBT();
								
							}
							else
							{
								Toast.makeText(getApplicationContext(), "L'apparail non appareillé ", Toast.LENGTH_LONG).show();
								unBp.setClickable(true);
	
							}
						} catch (IOException er)
						{
							er.printStackTrace();
							unBp.setClickable(true);
						}
					}
					else
					{
						bt.getModule().closeBT();
						unBp.setText("Connecter");
					}
				}catch(Exception e)
				{

				}

			}});

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
			unBp.setText("Déconnecter");
			Toast.makeText(getApplicationContext(), "Connexion activée", Toast.LENGTH_LONG).show();
			unBp.setClickable(true);
		}
		@Override
		public void onDisconnect()
		{
			SwitchOnOff.setChecked(false);
			saProgressBar.setProgress(0);
			unBp.setText("Connecter");
			Toast.makeText(getApplicationContext(), "Connexion terminée", Toast.LENGTH_LONG).show();
			unBp.setClickable(true);

		}
		@Override
		public void onReceived(String data) {
			// TODO Auto-generated method stub
			try
			{
			saProgressBar.setProgress(Integer.parseInt( data.substring(data.indexOf(",")+1, data.indexOf("]"))));
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	};



	/**
	 * Réimplémentation du listener permettant : 
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
		};
	}; 
}