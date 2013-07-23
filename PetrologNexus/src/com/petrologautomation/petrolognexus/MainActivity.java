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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends Activity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    public static G4Petrolog PetrologSerialCom;

    private static FrameLayout All;
    private static wellStatus_post wellStatusPost;
    private static wellRuntime_post wellRuntimePost;
    private static wellHistoricalRuntime_post wellHistoricalRuntimePost;
    private static wellDynagraph_post wellDynagraphPost;
    private static wellSettings_post wellSettingsPost;
    private static wellFillage_post wellFillagePost;

    ActionBar bar;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mBluetoothSocket;
    private GoogleMap WellLocation = null;
    boolean Conectado = false;
    public static final int REQUEST_ENABLE_BT = 1;
    public static final String UUID_BLUE_RADIOS = "00001101-0000-1000-8000-00805F9B34FB";
    Menu MyMenu;
    LocationClient CurrentLocation;

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName().contains("Petrolog")) {
                    device.fetchUuidsWithSdp();
                    Log.i("PN - BT","Start UUID Discovery");
                    mBluetoothAdapter.cancelDiscovery();
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

        //Location client
        CurrentLocation = new LocationClient(this,this,this);
        CurrentLocation.connect();

        /* Timer to update info from Petrolog */
        Timer SerialComHeartBeat = new Timer();
        SerialComHeartBeat.schedule(new TimerTask() {
            @Override
            public void run() {
                // Serial
                if (Conectado) {
                    PetrologSerialCom.HeartBeat();
                }
            }
        }, 0, 200);

        /* Timer to Update UI */
        All = (FrameLayout)findViewById(R.id.Main);

        wellStatusPost = new wellStatus_post(findViewById(R.id.CurrentTV));
        wellRuntimePost = new wellRuntime_post(findViewById(R.id.RuntimeTV));
        wellHistoricalRuntimePost = new wellHistoricalRuntime_post(findViewById(R.id.RuntimeTrendIV));
        wellDynagraphPost = new wellDynagraph_post(findViewById(R.id.DynaIV));
        wellSettingsPost = new wellSettings_post(findViewById(R.id.SettingsTV));
        wellFillagePost = new wellFillage_post(findViewById(R.id.SettingsTV));

        Timer UIUpdate = new Timer();
        UIUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                // Serial
                if (Conectado) {
                    All.post(new Runnable() {
                        @Override
                        public void run() {
                            wellStatusPost.post();
                            wellRuntimePost.post();
                            wellHistoricalRuntimePost.post();
                            wellDynagraphPost.post();
                            wellSettingsPost.post();
                            wellFillagePost.post();
                        }
                    });
                }
            }
        }, 0, 400);
    }
    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Toast.makeText(this, "Connected to Location Services", Toast.LENGTH_SHORT).show();
    }
    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected from Location Services", Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
                    Conectado = false;
                    mBluetoothSocket.close();
                    if(WellLocation != null){
                        WellLocation.clear();
                    }
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
                Log.i("PN - BT",""+device[0].getName());
                mBluetoothSocket = device[0].createInsecureRfcommSocketToServiceRecord
                        (UUID.fromString(UUID_BLUE_RADIOS));

                /* Blocking !!!*/
                mBluetoothSocket.connect();
                /* Release Block!*/
                return true;
            } catch (IOException e) {
                return false;
            } catch (NullPointerException e) {
                Log.i("PN - BT","No device found?");
                return false;
            }

        }

        protected void onPostExecute(Boolean ok) {
            if (ok) {
                /* BT Menu icon */
                MyMenu.getItem(1).setVisible(false); //Connect
                MyMenu.getItem(2).setVisible(true); //Disconnect
                /* Init G4 Com */
                PetrologSerialCom = new G4Petrolog(mBluetoothSocket);
                /* Action bar title (Well Name) */
                ActionBar bar = getActionBar();
                bar.setTitle(getString(R.string.app_title) + " - " + Device.getName());
                //Map
                LatLng coordinate = new LatLng(CurrentLocation.getLastLocation().getLatitude(),
                                               CurrentLocation.getLastLocation().getLongitude());
                if (WellLocation == null) {
                    WellLocation = ((MapFragment) getFragmentManager().findFragmentById(R.id.MapFragment))
                            .getMap();
                    // Check if we were successful in obtaining the map.
                    if (WellLocation != null) {
                        // The Map is verified. It is now safe to manipulate the map.
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinate,17);
                        WellLocation.animateCamera(cameraUpdate);
                        WellLocation.addMarker(new MarkerOptions()
                                .position(coordinate)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.oilpumpjack)));
                    }
                }
                /* Run Serial Heart Beat only if BT connection established */
                Conectado = true;
                Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT)
                        .show();

            }
            else {
                MyMenu.getItem(1).setVisible(true); //Connect
                MyMenu.getItem(2).setVisible(false); //Disconnect
                Toast.makeText(MainActivity.this, "Connection Error", Toast.LENGTH_SHORT)
                        .show();
            }

            Wait.setVisibility(View.INVISIBLE);


        }

    }


}




