package com.petrologautomation.petrolognexus;

import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
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

public class MainActivity extends Activity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    ActionBar bar;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mBluetoothSocket;
    private GoogleMap WellLocation = null;
    boolean Conectado = false;
    public static final int REQUEST_ENABLE_BT = 1;
    Menu MyMenu;
    LocationClient CurrentLocation;
    G4_Petrolog PetrologSerialCom;

    TextView wellStatus;
    TextView PumpOffStatus;

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

        wellStatus = (TextView)findViewById(R.id.Current);

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        //Location client
        CurrentLocation = new LocationClient(this,this,this);
        CurrentLocation.connect();

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


        Timer UIUpdate = new Timer();
        SerialComHeartBeat.schedule(new TimerTask() {
            @Override
            public void run() {
                // Serial
                if (Conectado) {
                    wellStatus.post(new Runnable() {
                        @Override
                        public void run() {
                            /* Format Well Status */
                            String temp = PetrologSerialCom.getWellStatus();
                            SpannableString ws = new SpannableString("Well Status: "+temp);
                            if (temp.contains("On")){
                                ws.setSpan(new ForegroundColorSpan(Color.BLUE),13,15,0);
                                /* Changes the size of the text in proportion to its original size */
                                //ws.setSpan(new RelativeSizeSpan(1f),13,15,0);
                               // wellStatus.setText(ws);
                            }
                            else if (temp.contains("Off")){
                                ws.setSpan(new ForegroundColorSpan(Color.RED),13,16,0);
                                /* Changes the size of the text in proportion to its original size */
                                //ws.setSpan(new RelativeSizeSpan(1.5f),13,16,0);
                            }
                           wellStatus.setText(ws);
                           wellStatus.append("\n");
                            /* Format Pump Off */
                            temp = PetrologSerialCom.getPumpOffStatus();
                            SpannableString po = new SpannableString("Pump Off: "+temp);
                            if (temp.contains("Normal")){
                                po.setSpan(new ForegroundColorSpan(Color.BLUE),10,16,0);
                                /* Changes the size of the text in proportion to its original size */
                              //  po.setSpan(new RelativeSizeSpan(2f),10,16,0);
                            }
                            else if (temp.contains("Pump Off")){
                                po.setSpan(new ForegroundColorSpan(Color.RED),10,18,0);
                                /* Changes the size of the text in proportion to its original size */
                                //po.setSpan(new RelativeSizeSpan(2f),10,18,0);
                            }
                            wellStatus.append(po);
                            wellStatus.append("\n");
                        }
                    });
                }
            }
        }, 0, 600);
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
                /* Init G4 Com */
                PetrologSerialCom = new G4_Petrolog(mBluetoothSocket);
                /* Action bar title (Well Name) */
                ActionBar bar = getActionBar();
                bar.setTitle(getString(R.string.app_title) + " - " + Device.getName());
                mBluetoothAdapter.cancelDiscovery();
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
                Toast.makeText(MainActivity.this, "Connect", Toast.LENGTH_SHORT)
                        .show();

            }
            else {
                MyMenu.getItem(1).setVisible(true); //Connect
                MyMenu.getItem(2).setVisible(false); //Disconnect
                Toast.makeText(MainActivity.this, "Connect Error", Toast.LENGTH_SHORT)
                        .show();
            }

            Wait.setVisibility(View.INVISIBLE);


        }

    }


}




