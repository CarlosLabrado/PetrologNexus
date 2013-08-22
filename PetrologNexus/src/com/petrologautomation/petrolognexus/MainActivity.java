package com.petrologautomation.petrolognexus;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.XYPlot;
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

    EditText settings_v0ET;
    EditText settings_v1ET;
    EditText settings_v2ET;
    EditText settings_v3ET;
    EditText settings_v4ET;
    Spinner autoTimeOut;
    ImageView helpConnected;
    ImageView helpDisconnected;


    private Timer UIUpdate;
    private Timer StaticDynaUpdate;
    private Timer SerialComHeartBeat;

    private static XYPlot RuntimeTread;
    private static XYPlot Dynagraph;

    private static wellStatus_post wellStatusPost;
    private static wellRuntime_post wellRuntimePost;
    private static wellHistoricalRuntime_post wellHistoricalRuntimePost;
    private static wellDynagraph_post wellDynagraphPost;
    private static wellSettings_post wellSettingsPost;
    private static wellFillage_post wellFillagePost;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mBluetoothSocket;
    private GoogleMap WellLocation = null;
    boolean Connected = false;
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
                try {
                    if (device.getName().contains("Petrolog")) {
                        device.fetchUuidsWithSdp();
                        Log.i("PN - BT","Start UUID Discovery");
                        mBluetoothAdapter.cancelDiscovery();
                        new AsyncBluetoothConnect().execute(device);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error Finding Petrolog, Try Again", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    };

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        /* Init Help */
        Typeface helpFont = Typeface.createFromAsset(getAssets(),"fonts/gloriahallelujah.ttf");
        TextView current = (TextView)findViewById(R.id.current_h);
        current.setTypeface(helpFont);
        TextView runtime = (TextView)findViewById(R.id.runtime_h);
        runtime.setTypeface(helpFont);
        TextView runtime_trend = (TextView)findViewById(R.id.runtime_trend_h);
        runtime_trend.setTypeface(helpFont);
        TextView map = (TextView)findViewById(R.id.map_h);
        map.setTypeface(helpFont);
        TextView dyna = (TextView)findViewById(R.id.dyna_h);
        dyna.setTypeface(helpFont);
        TextView settings = (TextView)findViewById(R.id.settings_h);
        settings.setTypeface(helpFont);
        TextView fillage = (TextView)findViewById(R.id.fillage_h);
        fillage.setTypeface(helpFont);

        helpConnected = (ImageView)findViewById(R.id.help_connectedIV);
        helpDisconnected = (ImageView)findViewById(R.id.help_disconnectedIV);


    }

    @Override
    protected void onPause (){
        super.onPause();

        UIUpdate.cancel();
        UIUpdate.purge();

        StaticDynaUpdate.cancel();
        StaticDynaUpdate.purge();

        SerialComHeartBeat.cancel();
        SerialComHeartBeat.purge();

        if (PetrologSerialCom != null){
            PetrologSerialCom.Disconnect();
        }

    }

    @Override
    protected void onResume (){
        super.onResume();
        myInit();
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
        MyMenu = menu;

        // Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mainmenu, menu);

        return true;
	}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MyMenu = menu;

        MyMenu.getItem(4).setVisible(false); //Disconnect
        MyMenu.getItem(0).setVisible(false); //Settings
        MyMenu.getItem(1).setVisible(false); //Clean
        //help
        helpDisconnected.setVisibility(View.VISIBLE);
        helpConnected.setVisibility(View.INVISIBLE);


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
                    PetrologSerialCom.Disconnect();
                    Connected = false;
                    mBluetoothSocket.close();
                    mBluetoothSocket = null;
                    wellDynagraphPost.clean();
                    if(WellLocation != null){
                        WellLocation.clear();
                    }
                    Thread.sleep(200);
                    MyMenu.getItem(3).setVisible(true);  //Connect
                    MyMenu.getItem(4).setVisible(false); //Disconnect
                    MyMenu.getItem(0).setVisible(false); //Settings
                    MyMenu.getItem(1).setVisible(false); //Clean
                    //help
                    helpDisconnected.setVisibility(View.VISIBLE);
                    helpConnected.setVisibility(View.INVISIBLE);

                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.clean:
                wellDynagraphPost.clean();
                break;

            case R.id.help:

                LinearLayout help = (LinearLayout)findViewById(R.id.Help);

                if(help.getVisibility()==View.VISIBLE){
                    help.setVisibility(View.INVISIBLE);
                }
                else{
                    help.setVisibility(View.VISIBLE);
                }


                break;


            case R.id.settings:

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                dialog.setTitle("POC Settings");
                dialog.setCancelable(true);
                LayoutInflater inflater = this.getLayoutInflater();

                final View tempView = inflater.inflate(R.layout.settings, null);
                settings_v0ET = (EditText)tempView.findViewById(R.id.settings_v0ET);
                settings_v1ET = (EditText)tempView.findViewById(R.id.settings_v1ET);
                settings_v2ET = (EditText)tempView.findViewById(R.id.settings_v2ET);
                settings_v3ET = (EditText)tempView.findViewById(R.id.settings_v3ET);
                settings_v4ET = (EditText)tempView.findViewById(R.id.settings_v4ET);

                settings_v0ET.setHint(PetrologSerialCom.getPetrologClock());
                settings_v1ET.setHint(String.valueOf(PetrologSerialCom.getPumpUpSetting()));
                settings_v2ET.setHint(String.valueOf(PetrologSerialCom.getPumpOffStrokesSetting()));
                settings_v3ET.setHint(String.valueOf(PetrologSerialCom.getFillageSetting()));
                settings_v4ET.setHint(String.valueOf(PetrologSerialCom.getCurrentTimeoutSetting()));

                autoTimeOut = (Spinner) tempView.findViewById(R.id.automatic_time_out);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                        this,
                        R.array.auto_timeout,
                        R.layout.my_spinner
                );

                autoTimeOut.setAdapter(adapter);

                if (PetrologSerialCom.getAutomaticTOSetting().equals("Yes")){
                    autoTimeOut.setSelection(0);
                }
                else {
                    autoTimeOut.setSelection(1);
                }

                dialog.setView(tempView);
                dialog.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String tempV0;
                        int tempV1;
                        int tempV2;
                        int tempV3;
                        int tempV4;
                        boolean temp;

                        // Remove notification bar
                        getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);


                        /* Clock */
                        if (settings_v0ET.getText().toString().equals("")) {
                            tempV0 = settings_v0ET.getHint().toString();
                        }
                        else {
                            tempV0 = settings_v0ET.getText().toString();
                        }
                        /* Pump Up */
                        if (settings_v1ET.getText().toString().equals("")) {
                            tempV1 = Integer.valueOf(settings_v1ET.getHint().toString());
                        }
                        else {
                            tempV1 = Integer.valueOf(settings_v1ET.getText().toString());
                        }
                        /* Pump Off */
                        if (settings_v2ET.getText().toString().equals("")) {
                            tempV2 = Integer.valueOf(settings_v2ET.getHint().toString());
                        }
                        else {
                            tempV2 = Integer.valueOf(settings_v2ET.getText().toString());
                        }
                        /* Fillage */
                        if (settings_v3ET.getText().toString().equals("")) {
                            tempV3 = Integer.valueOf(settings_v3ET.getHint().toString());
                        }
                        else {
                            tempV3 = Integer.valueOf(settings_v3ET.getText().toString());
                        }
                        /* Time Out */
                        if (settings_v4ET.getText().toString().equals("")) {
                            tempV4 = Integer.valueOf(settings_v4ET.getHint().toString());
                        }
                        else {
                            tempV4 = Integer.valueOf(settings_v4ET.getText().toString());
                        }

                        if (autoTimeOut.getSelectedItemPosition()==0){
                            temp = true;
                        }
                        else {
                            temp = false;
                        }
                        /* Write values to Petrolog */
                        try {
                            PetrologSerialCom.setSettings(
                                    tempV0,
                                    tempV1,
                                    tempV2,
                                    tempV3,
                                    tempV4,
                                    temp
                            );
                        }
                        catch (NumberFormatException e){

                        }
                        wellDynagraphPost.clean();
                    }
                });
                dialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Remove notification bar
                        getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
                    }
                });

                AlertDialog temp = dialog.create();
                temp.show();

                Window myWin = temp.getWindow();
                myWin.setLayout(500,700);


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
        FrameLayout Wait = (FrameLayout) findViewById(R.id.wait);

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
                /* Release!*/
                /* Init G4 Com */
                PetrologSerialCom = new G4Petrolog(mBluetoothSocket);
                /* Ask Petrolog last 30 days of history */
                PetrologSerialCom.requestPetrologHistory();
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
                /* Menu icons */
                MyMenu.getItem(3).setVisible(false); //Connect
                MyMenu.getItem(4).setVisible(true); //Disconnect
                MyMenu.getItem(0).setVisible(true); //Settings
                MyMenu.getItem(1).setVisible(true); //Clean
                //help
                helpDisconnected.setVisibility(View.INVISIBLE);
                helpConnected.setVisibility(View.VISIBLE);
                /* Post Petrolog last 30 days of history */
                wellHistoricalRuntimePost.post();
                /* Action bar title (Well Name) */
                ActionBar bar = getActionBar();
                bar.setTitle(getString(R.string.app_title) + " - " + Device.getName());
                //Map
                LatLng coordinate = null;
                try{
                    coordinate = new LatLng(CurrentLocation.getLastLocation().getLatitude(),
                            CurrentLocation.getLastLocation().getLongitude());
                }
                catch (NullPointerException e){
                    Toast.makeText(MainActivity.this, "Error Getting Location", Toast.LENGTH_SHORT).show();
                    /* Dummy coordinates when location services not available */
                    LatLng foo = new LatLng(31.993518,-102.078835);
                    coordinate = foo;
                }

                if (WellLocation == null) {
                    WellLocation = ((MapFragment) getFragmentManager().findFragmentById(R.id.MapFragment))
                            .getMap();

                }
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinate,17);
                WellLocation.animateCamera(cameraUpdate);
                WellLocation.addMarker(new MarkerOptions()
                        .position(coordinate)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.oilpumpjack)));
                /* Run Serial Heart Beat only if BT connection established */
                Connected = true;
                Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT)
                        .show();

            }
            else {
                MyMenu.getItem(3).setVisible(true); //Connect
                MyMenu.getItem(4).setVisible(false); //Disconnect
                MyMenu.getItem(0).setVisible(false); //Settings
                MyMenu.getItem(1).setVisible(false); //Clean
                //help
                helpDisconnected.setVisibility(View.VISIBLE);
                helpConnected.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Connection Error", Toast.LENGTH_SHORT)
                        .show();
            }

            Wait.setVisibility(View.INVISIBLE);


        }

    }

    private void myInit (){

        // Force call to onPrepareOptionsMenu()
        invalidateOptionsMenu();

        // Remove notification bar
        getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        //Location client
        CurrentLocation = new LocationClient(this,this,this);
        CurrentLocation.connect();

        //Init Graphs
        RuntimeTread = FormatTrend.format((XYPlot)findViewById(R.id.runtimeTrend));
        Dynagraph = FormatGraph.format((XYPlot)findViewById(R.id.dynagraph));

        /* Timer to update info from Petrolog */
        SerialComHeartBeat = new Timer();
        SerialComHeartBeat.schedule(new TimerTask() {
            @Override
            public void run() {
                // Serial
                if (Connected) {
                    PetrologSerialCom.HeartBeat();
                }
            }
        }, 0, 200);

        /* Timer to Update UI */
        All = (FrameLayout)findViewById(R.id.Main);

        wellStatusPost = new wellStatus_post(this);
        wellSettingsPost = new wellSettings_post(this);
        wellRuntimePost = new wellRuntime_post(this);
        wellDynagraphPost = new wellDynagraph_post(this);
        wellHistoricalRuntimePost = new wellHistoricalRuntime_post(this);
        wellFillagePost = new wellFillage_post(this);

        UIUpdate = new Timer();
        UIUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                // Serial
                if (Connected) {
                    All.post(new Runnable() {
                        @Override
                        public void run() {
                            wellStatusPost.post();
                            wellSettingsPost.post();
                            wellRuntimePost.post();
                            wellFillagePost.post();
                        }
                    });
                }
            }
        }, 0, 400);

        StaticDynaUpdate = new Timer();
        StaticDynaUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                // Serial
                if (Connected) {
                    All.post(new Runnable() {
                        @Override
                        public void run() {
                            wellDynagraphPost.post();
                        }
                    });
                }
            }
        }, 0, 200);

    }



}




