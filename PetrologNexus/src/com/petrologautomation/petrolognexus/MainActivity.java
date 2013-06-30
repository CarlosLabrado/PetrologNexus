package com.petrologautomation.petrolognexus;

import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;

public class MainActivity extends Activity {
    ActionBar bar;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mBluetoothSocket;

    public static final int REQUEST_ENABLE_BT = 1;
    Menu MyMenu;

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName().contains("Petrolog")) {
                    new AsyncBluetoothConnect().execute(device);
                }
            }
        }
    };

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mainmenu, menu);
        menu.getItem(2).setVisible(false);
        //Feo
        SearchView tempsearch = (SearchView) menu.findItem(R.id.search).getActionView();
        tempsearch.setQueryHint(getText(R.string.action_search));
        MyMenu = menu;
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
                    else {
                        Toast.makeText(this,"Bluetooth active",Toast.LENGTH_SHORT).show();
                        mBluetoothAdapter.startDiscovery();
                    }
                }
                break;

            case R.id.disconnect:
                try {
                    mBluetoothSocket.close();
                    Thread.sleep(200);
                    MyMenu.getItem(1).setVisible(true);    //Connect
                    MyMenu.getItem(2).setVisible(false);  //Disconnect
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
            if (result == RESULT_OK) {
                mBluetoothAdapter.startDiscovery();
            }
            else
                Toast.makeText(this,"Bluetooth activation failed",Toast.LENGTH_SHORT).show();
    }

    public class AsyncBluetoothConnect extends AsyncTask<BluetoothDevice, Void, Boolean> {
        BluetoothDevice Device;
        FrameLayout Wait = (FrameLayout) findViewById(R.id.waiting_bt_cx);

        protected void onPreExecute() {
            Wait.setVisibility(View.VISIBLE);
        }

        protected Boolean doInBackground(BluetoothDevice... device) {
            Device = device[0];
            try {
                mBluetoothSocket = device[0].createInsecureRfcommSocketToServiceRecord
                        (device[0].getUuids()[0].getUuid());
            /* Blocking !!!*/
                mBluetoothSocket.connect();
            /* Release Block!*/
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        protected void onPostExecute(Boolean ok) {
            if (ok) {
                /* BT Menu icon */
                MyMenu.getItem(1).setVisible(false); //Connect
                MyMenu.getItem(2).setVisible(true); //Disconnect
                /* Action bar title (Well Name) */
                ActionBar bar = getActionBar();
                bar.setTitle(getString(R.string.app_title) + " - " + Device.getName());
                mBluetoothAdapter.cancelDiscovery();
            }
            else {
                MyMenu.getItem(1).setVisible(true); //Connect
                MyMenu.getItem(2).setVisible(false); //Disconnect
            }
            Wait.setVisibility(View.INVISIBLE);
        }

    }

}




