package com.petrologautomation.petrolognexus;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class MainActivity extends Activity {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mBluetoothSocket;
    public static final int REQUEST_ENABLE_BT = 1;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.connect:
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    // Device does not support Bluetooth
                    Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_SHORT)
                            .show();
                }
                else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                    else
                        ConnectWithPetrolog();
                }
                break;

            case R.id.disconnect:
                try {
                    mBluetoothSocket.close();
                    Thread.sleep(200);
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult (int request, int result, Intent data ) {
        if (request == REQUEST_ENABLE_BT )
            if (result == RESULT_OK){
                ConnectWithPetrolog();
            }
            else
                Toast.makeText(this,"Bluetooth activation failed",Toast.LENGTH_SHORT).show();
    }

    private void ConnectWithPetrolog () {

        Toast.makeText(this,"Bluetooth active",Toast.LENGTH_SHORT).show();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().contains("Petrolog")){
                    try {
                        mBluetoothSocket =
                                device.createInsecureRfcommSocketToServiceRecord
                                        (device.getUuids()[0].getUuid());
                        /* Blocking !!!*/
                        mBluetoothSocket.connect();
                        /* Release Block!*/
                        ActionBar bar = getActionBar();
                        bar.setSubtitle(device.getName());
                        Toast.makeText(this,"Connected!!",Toast.LENGTH_SHORT)
                                .show();
                    } catch (IOException e) {
                        Toast.makeText(this,"Error while connecting",Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        }
        else
            Toast.makeText(this,"No paired devices found",Toast.LENGTH_SHORT).show();
    }

}
