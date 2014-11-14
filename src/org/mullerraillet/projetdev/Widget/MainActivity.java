package org.mullerraillet.projetdev.Widget;

import java.io.IOException;

import org.mullerraillet.projetdev.Bluetooth.Bluetooth;
import org.mullerraillet.projetdev.Bluetooth.BluetoothListener;
import org.mullerraillet.projetdev.Joystick.JoystickMovedListener;
import org.mullerraillet.projetdev.Joystick.JoystickView;
import org.projetDev.mullerraillet.manettebluetooth.R;

import android.app.Activity;
import android.os.Bundle;
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
	TextView txtX, txtY;
	JoystickView joystick;
	volatile static Bluetooth bt;
	String vitesse = "1"; // Vitesse normal
	ProgressBar saProgressBar;
	ToggleButton swi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.joystick);

		saProgressBar = (ProgressBar) findViewById(R.id.chargementBatterie);
		bt = new Bluetooth(getApplicationContext(),saProgressBar);

		swi = (ToggleButton)  findViewById(R.id.ToggleButtun1);
		final RadioGroup RG = (RadioGroup) findViewById(R.id.radioGroup1);
		final Button klaxon = (Button) findViewById(R.id.button1);
		
		
		klaxon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					bt.sendData("[B]\n");
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



		swi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if(!b)
				{
					try {
						bt.closeBT();
					} catch (IOException e) {

					} catch (Exception e)
					{
						Toast.makeText(getApplicationContext(), "L'apparail est déjà déconnecter ", Toast.LENGTH_LONG).show();

					}
				}else
				{
					try
					{
						bt.findBT();
						bt.openBT();
					} catch (IOException er)
					{
						swi.setChecked(false);
					}
				}
			}
		});

		


		txtX = (TextView)findViewById(R.id.TextViewX);
		txtY = (TextView)findViewById(R.id.TextViewY);
		joystick = (JoystickView)findViewById(R.id.joystickView);
		joystick.setOnJostickMovedListener(_listener);
		bt.setOnBluetoothListener(_ListenerBluetooth);
	}
	
	
	private BluetoothListener _ListenerBluetooth = new BluetoothListener()
	{
		public void onConnect()
		{
		swi.setChecked(true);
		}
		public void onDisconnect()
		{
		swi.setChecked(false);
		Toast.makeText(getApplicationContext(), "Connexion perdue", Toast.LENGTH_LONG).show();
		}

	};

	
	
	
	private JoystickMovedListener _listener = new JoystickMovedListener() {

		@Override

		// Movement of the joystick
		public void OnMoved(int pan, int tilt) {
			txtX.setText(": "+Integer.toString(pan));
			txtY.setText(": "+Integer.toString(tilt));
			
			
			try {
				bt.sendData("[CMD,"+Integer.toString(pan)+","+Integer.toString(tilt)+","+vitesse+"]\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void OnReleased() {

		}

		public void OnReturnedToCenter() {
			txtX.setText(": 0");
			txtY.setText(": 0");
		};
	}; 

}