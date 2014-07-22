package us.petrolog.nexus;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import us.petrolog.nexus.database.PetrologMarkerDataSource;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class MainActivity extends Activity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    public static G4Petrolog PetrologSerialCom;

    private static FrameLayout All;

    private Timer UIUpdate;
    private Timer StaticDynaUpdate;
    private Timer SerialComHeartBeat;

    private static wellStatus_post wellStatusPost;
    private static wellRuntime_post wellRuntimePost;
    private static wellHistoricalRuntime_post wellHistoricalRuntimePost;
    public static wellDynagraph_post wellDynagraphPost;
    private static wellSettings_post wellSettingsPost;
    private static wellFillage_post wellFillagePost;

    private static wellSettings_edit wellSettingsEdit;

    public static help Help;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mBluetoothSocket;
    public static boolean Connected = false;
    public static final int REQUEST_ENABLE_BT = 1;
    public static final String UUID_BLUE_RADIOS = "00001101-0000-1000-8000-00805F9B34FB";
    private String wellName;

    public static Menu MyMenu;
    private LocationClient CurrentLocation;

    private Boolean petrologFound;

    private NfcAdapter mNfcAdapter;
    private IntentFilter[] mNdefExchangeFilters;
    private PendingIntent mNfcPendingIntent;
    private final String NFC_PETROLOG_IDENTIFIER = "petr0l0g";

    private PetrologMarkerDataSource dataSource;

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("PN - BT","BroadcastReceiver Action = "+action);

            /* Bluetooth disconnect */
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
                Toast.makeText(MainActivity.this, "Petrolog disconnected", Toast.LENGTH_SHORT).show();
                disconnect();
            }

            /* Bluetooth Discovery Started */
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                petrologFound = false;
                removeAllMenuItems();
                setProgressBarIndeterminateVisibility(true);
            }

            /* Bluetooth Discovery finds a device */
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try {
                    if (device.getName().contains("Petrolog")) {
                        device.fetchUuidsWithSdp();
                        Toast.makeText(MainActivity.this, "Petrolog found: Connecting", Toast.LENGTH_SHORT).show();
                        petrologFound = true;
                        mBluetoothAdapter.cancelDiscovery();
                        new AsyncBluetoothConnect().execute(device);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error, try again", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            /* Bluetooth Discovery Finished */
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                if (!petrologFound){
                    setMenuIconsDisconnected();
                    /* Enable BT connect menu button */
                    MyMenu.getItem(4).setEnabled(true);

                    Toast.makeText(MainActivity.this, "Discovery finished: Petrolog not found, try again", Toast.LENGTH_SHORT)
                            .show();
                }
                setProgressBarIndeterminateVisibility(false);
            }
        }
    };

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /* Progress spinner on menu */
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_main);

        Help = new help(this);
        /* NFC */
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);

        IntentFilter discovery = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mNdefExchangeFilters = new IntentFilter[] { discovery };


    }

    @Override
    protected void onPause (){
        super.onPause();
        Log.i ("PN - onPause", "Called!!");
        prepareForExit();
    }

    @Override
    protected void onResume (){
        super.onResume();
        myInit();
        nfcCheck();
    }

    /**
     * we check to see if the NFC adapter is enabled and we execute enableForegroundDispatch(), passing in our pending intent and filters.
     */
    private void nfcCheck() {
        if(mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
                    mNdefExchangeFilters, null);
            if (!mNfcAdapter.isEnabled()){
                Toast.makeText(this, "Please enable NFC Adapter and go back to PetrologNexus",
                        Toast.LENGTH_LONG).show();
                Intent setnfc = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(setnfc);
            }
        } else {
            Toast.makeText(this, "Sorry, No NFC Adapter found.", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * This is where we get the intent once we've tapped the tag.
     * Then we can use 'getParceableExtra()' to get the tag data and build an NDEF message array.
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            NdefMessage[] messages = null;
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                messages = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    messages[i] = (NdefMessage) rawMsgs[i];
                }
            }
            if (messages != null) {
                if (messages[0] != null) {
                    String result = "";
                    byte[] payload = messages[0].getRecords()[0].getPayload();
                    // this assumes that we get back am SOH followed by host/code
                    for (int b = 1; b < payload.length; b++) { // skip SOH
                        result += (char) payload[b];
                    }
                    Toast.makeText(this, "TAG found", Toast.LENGTH_SHORT).show();
                    separateNFCMessage(result);
                }
            } else {
                Toast.makeText(this, "The NFC tag appears to be empty", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Since the nfc message is a Comma Separated Value type, we need to format it and save it to
     * the DB
     * @param nfcMessage
     */
    public void separateNFCMessage(String nfcMessage) {
        String[] csv = nfcMessage.split(",");

        // 8 is the number of fields that our NFC tags have
        // And it must start with our Identifier
        if (csv.length == 8 && csv[0].equals(NFC_PETROLOG_IDENTIFIER)) {
            int serial = Integer.parseInt(csv[1]);
            String comment = csv[2];
            Double lat = Double.parseDouble(csv[3]);
            Double lng = Double.parseDouble(csv[4]);
            String bluetooth = csv[5];
            String wifiAddress = csv[6];
            String wifiPass = csv[7];

            dataSource = new PetrologMarkerDataSource(this);
            dataSource.open();
            dataSource.createPetrologMarker(serial, comment, lat, lng, bluetooth, wifiAddress, wifiPass);
            dataSource.close();

            initiateBluetoothConnection();
        } else {
            Toast.makeText(this, "Sorry this NFC tag is not a Petrolog tag", Toast.LENGTH_SHORT).show();
        }
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
        LocationRequest myLocationRequest = LocationRequest.create();
        myLocationRequest.setFastestInterval(0);
        myLocationRequest.setInterval(0).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        CurrentLocation.requestLocationUpdates(myLocationRequest, myLocationListener);

    }

    private LocationListener myLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LatLng coordinate;
            try{
                coordinate = new LatLng(CurrentLocation.getLastLocation().getLatitude(),
                        CurrentLocation.getLastLocation().getLongitude());

            }
            catch (NullPointerException e){
                Toast.makeText(MainActivity.this, "Error Getting Location", Toast.LENGTH_SHORT).show();
            }
            catch (IllegalStateException e){
                Toast.makeText(MainActivity.this, "Not Connected to Location Services", Toast.LENGTH_SHORT).show();
            }
        }
    };
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
        super.onDestroy();
        Log.i("PN - onDestroy", "Called!!");
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

        if (mBluetoothSocket == null){
            /* Menu icons */
            setMenuIconsDisconnected();
            /* help */
            Help.setDisconnected();
        }
        else {
            /* Menu icons */
            setMenuIconsConnected();
            /* help */
            Help.setConnectedStopped();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.connect:
                initiateBluetoothConnection();
                break;

            case R.id.disconnect:
                disconnect();
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
                    try {
                        String version = this.getApplicationContext().getPackageManager()
                                .getPackageInfo(this.getApplicationContext().getPackageName(), 0)
                                .versionName;
                        Toast.makeText(MainActivity.this, "PetrologNexus version: "+version,
                                Toast.LENGTH_SHORT).show();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }


                break;

            case R.id.settings:
                wellSettingsEdit.popup();
                break;

            case R.id.start_well:
                PetrologSerialCom.start();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initiateBluetoothConnection() {
                /* Disable BT connect menu button */
        MyMenu.getItem(4).setEnabled(false);

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

                /* get bluetooth name */
                BlueRadios bluetooth = new BlueRadios(mBluetoothSocket);
                if(bluetooth.CommandMode()){
                    wellName = bluetooth.Read(0);
                    if(wellName.equals("Error")){
                        wellName = Device.getName();
                    }
                }
                else{
                    wellName = Device.getName();
                }
                if(!bluetooth.DataMode()){
                    Log.e("PN - BT","Change to data mode failed");
                    return false;
                }
                /* Release!*/
                /* Init G4 Com */
                PetrologSerialCom = new G4Petrolog(mBluetoothSocket);
                /* Ask Petrolog last 30 days of history */
                PetrologSerialCom.requestPetrologHistory();
                return true;
            } catch (IOException e) {
                Log.e("PN - BT","IO Exception - Android error?");
                return false;
            } catch (NullPointerException e) {
                Log.e("PN - BT","Null Pointer - No device found?");
                return false;
            }

        }

        protected void onPostExecute(Boolean ok) {
            /* Enable BT connect menu button */
            MyMenu.getItem(4).setEnabled(true); //TODO

            if (ok) {
                /* Menu icons */
                setMenuIconsConnected();
                /* help */
                Help.setConnectedStopped();
                /* Post Petrolog last 30 days of history */
                wellHistoricalRuntimePost.post();
                /* Action bar title (Well Name) */
                ActionBar bar = getActionBar();
                bar.setTitle(getString(R.string.app_title) + " - " + wellName);
                /* Run Serial Heart Beat only if BT connection established */
                Connected = true;
                Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(MainActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                disconnect();
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
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mReceiver, filter);

        /* Timer to update info from Petrolog */
        if (SerialComHeartBeat == null){
            SerialComHeartBeat = new Timer();
        }
        SerialComHeartBeat.schedule(new TimerTask() {
            @Override
            public void run() {
            // Serial
            if (Connected) {
                PetrologSerialCom.HeartBeat();
            }
            }
        }, 0, 500);

        /* Settings PopUp */
        wellSettingsEdit = new wellSettings_edit(this);

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

        /* Timer to Update Dyna */
        if (StaticDynaUpdate == null){
            StaticDynaUpdate = new Timer();
        }
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
        }, 0, 10000);


    }

    private void prepareForExit (){

        try {
            /* Close Cx */
            PetrologSerialCom.Disconnect();
            Connected = false;

            /* Close BT */
            mBluetoothSocket.close();
            mBluetoothSocket = null;
            unregisterReceiver(mReceiver);

            /* Stop Timers */
            UIUpdate.cancel();
            UIUpdate.purge();
            UIUpdate = null;
            StaticDynaUpdate.cancel();
            StaticDynaUpdate.purge();
            StaticDynaUpdate = null;
            SerialComHeartBeat.cancel();
            SerialComHeartBeat.purge();
            SerialComHeartBeat = null;

            /* Clean UI */
            cleanUI();
            /* Prepare Menu */
            setMenuIconsDisconnected();

            /* Help */
            Help.setDisconnected();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void disconnect (){

        try {

            if (PetrologSerialCom != null) {
                /* Close Cx */
                PetrologSerialCom.Disconnect();
                Connected = false;
            }

            /* Close BT */
            mBluetoothSocket.close();
            mBluetoothSocket = null;

            /* Clean UI */
            cleanUI();
            /* Prepare Menu */
            setMenuIconsDisconnected();
            /* Help */
            Help.setDisconnected();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void setMenuIconsConnected(){
        MyMenu.getItem(4).setVisible(false); //Connect
        MyMenu.getItem(5).setVisible(true); //Disconnect
        MyMenu.getItem(1).setVisible(true); //Settings
        MyMenu.getItem(2).setVisible(true); //Clean
        MyMenu.getItem(0).setVisible(false); //Run
        MyMenu.getItem(3).setVisible(true); //Help

    }

    private void setMenuIconsDisconnected(){
        MyMenu.getItem(4).setVisible(true);  //Connect
        MyMenu.getItem(5).setVisible(false); //Disconnect
        MyMenu.getItem(1).setVisible(false); //Settings
        MyMenu.getItem(2).setVisible(false); //Clean
        MyMenu.getItem(0).setVisible(false); //Run
        MyMenu.getItem(3).setVisible(true); //Help

    }

    private void removeAllMenuItems(){
        MyMenu.getItem(4).setVisible(false); //Connect
        MyMenu.getItem(5).setVisible(false); //Disconnect
        MyMenu.getItem(1).setVisible(false); //Settings
        MyMenu.getItem(2).setVisible(false); //Clean
        MyMenu.getItem(0).setVisible(false); //Run
        MyMenu.getItem(3).setVisible(false); //Help

    }

    private void cleanUI(){

        wellDynagraphPost.clean();
        wellHistoricalRuntimePost.clean();

        ActionBar bar = getActionBar();
        bar.setTitle(getString(R.string.app_title));


        /* Last Update to display N/A (all variables are cleared on Disconnect@G4Petrolog) */
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


} /* Class */




