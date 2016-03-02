package us.petrolog.nexus.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.petrolog.nexus.FirstApp;
import us.petrolog.nexus.R;
import us.petrolog.nexus.events.SendDeviceListEvent;
import us.petrolog.nexus.misc.Utility;
import us.petrolog.nexus.rest.model.Device;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.container)
    FrameLayout mContainer;
    @Bind(R.id.nav_view)
    NavigationView mNavView;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private LatLng mLatLng;


    private Stack<Integer> mDrawerStack;

    boolean isFirstRun = true;

    // Navigation Drawer
    private String[] navMenuTitles;

    boolean isConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        /**toolBar **/
        setUpToolBar();

        setUpDrawer();

        isConnected = Utility.isNetworkAvailable(this);

        // This will take care of registering the user and also filling the user image
        if (isConnected) {
            //callForFacebookInfoAsync();
        } else {
            showNotConnectedDialog();
        }

        mDrawerStack = new Stack<>();

        if (savedInstanceState == null) {
            /**
             * This dummy fragment is to prevent the transition error when popping the fragment backStack
             * Error: Attempt to invoke virtual method 'boolean android.support.v4.app.Fragment.getAllowReturnTransitionOverlap()' on a null object reference
             * https://code.google.com/p/android/issues/detail?id=82832
             */
            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .add(R.id.container, new Fragment())
                    .addToBackStack("dummy")
                    .commit();

            // on first time display view for first nav item
            displayView(0);
        }

        checkForLocationServicesEnabled();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


        goToTheBackend();

    }

    /**
     * sets up the top bar
     */
    private void setUpToolBar() {
        setSupportActionBar(mToolbar);
        setActionBarTitle(getString(R.string.app_toolbar_title_main), null, false);
        //getSupportActionBar().setElevation(0f);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /**
     * Gets called from the fragments onResume and its because only the first doesn't have the up
     * button on the actionBar
     *
     * @param title          The title to show on the ActionBar
     * @param subtitle       The subtitle to show on the ActionBar
     * @param showNavigateUp if true, shows the up button
     */
    public void setActionBarTitle(String title, String subtitle, boolean showNavigateUp) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            if (subtitle != null) {
                getSupportActionBar().setSubtitle(subtitle);
            } else {
                getSupportActionBar().setSubtitle(null);
            }
            if (showNavigateUp) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }


    /**
     * Finishes to draw the drawer
     */
    private void setUpDrawer() {
        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavView.setNavigationItemSelectedListener(this);
        setCheckedDrawerItem(0);
    }


    /**
     * Displaying fragment view for selected nav drawer list item
     */
    private void displayView(int position) {
        boolean addToBackStack = true;
        mDrawerStack.push(position);
        FragmentManager fragmentManager = getSupportFragmentManager();
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                /**
                 * All this block prevents the first (Home) fragment to be recreated every time
                 * you click on it, we have to do this, because of the inner nested fragments,
                 * specially the map one, becomes "not stable"
                 */
                Fragment lastFragment;
                if (!isFirstRun) {
                    int backStackCount = fragmentManager.getBackStackEntryCount();
                    lastFragment = fragmentManager.getFragments().get(backStackCount - 1);
                    if (lastFragment instanceof MapPetrologFragment) {
                        addToBackStack = false;
                    } else {
                        fragment = fragmentManager.getFragments().get(1);
                    }
                } else {
                    fragment = new MapPetrologFragment();
                    isFirstRun = false;
                }
                break;
            case 1:
                //new BalanceFragment();
                //fragment = BalanceFragment.newInstance(mUser);
                break;
            case 2:
                //fragment = new ScanQRFragment();
                break;
            case 3:
                //fragment = new RewardsFragment();
                break;
            case 4:
                /*new ProfileFragment();
                if (mUser != null) {
                    fragment = ProfileFragment.newInstance(mUser.getName(), mUser.getEmail(), mFacebookId);
                } else {
                    Toast.makeText(this, R.string.profile_still_loading, Toast.LENGTH_SHORT).show();
                }*/
                break;
            default:
                break;
        }

        if (fragment != null && addToBackStack) { // addToBackStack will only be false if is the Home fragment
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.explode));
                fragment.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.fade));
            }
            String backStateName = fragment.getClass().getName();

            fragmentManager.beginTransaction()
                    .addToBackStack(backStateName)
                    .replace(R.id.container, fragment)
                    .commit();
            Log.d(TAG, "fragment added " + fragment.getTag());

            setTitle(navMenuTitles[position]);
            // update selected item and title, then close the drawer
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            // error in creating fragment
            Log.e("MainActivity", "Fragment not created");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Location services connected.");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            Log.d(TAG, "onConnected requesting Loc");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            Log.d(TAG, "onConnected has last location");
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged location changed");
        handleNewLocation(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * gets the location and then asks Map fragment to update it
     *
     * @param location current loc
     */
    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        mLatLng = new LatLng(currentLatitude, currentLongitude);
        MapPetrologFragment.mBus.post(mLatLng);

        Log.e(TAG, "ACCURACY " + String.valueOf(location.getAccuracy()));

        //getLocationsFromBackend(mLatLng);

    }

    private void showNotConnectedDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.dialog_not_connected_tittle));
        dialog.setMessage(getResources().getString(R.string.dialog_not_connected_message));
        dialog.setNegativeButton(getString(R.string.dialog_not_connected_close), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                finish();
            }
        });
        dialog.show();
    }

    /**
     * checks for the GPS to be enabled
     */
    private void checkForLocationServicesEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled_message));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            if (fragments > 2) {
                mDrawerStack.pop();
                setCheckedDrawerItem(mDrawerStack.peek());
                super.onBackPressed();
            } else {
                finish();
            }
        }
    }

    /*@Subscribe
    public void popDrawerStack(PopDrawerStackEvent event) {
        mDrawerStack.pop();
        setCheckedDrawerItem(mDrawerStack.peek());
    }*/

    private void setCheckedDrawerItem(int item) {
        mNavView.getMenu().getItem(item).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            displayView(0);
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            displayView(1);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void goToTheBackend() {

        final List<Device> devices = new ArrayList<>();

        Call<List<Device>> call = FirstApp.getRestClient().getApiService().getDevices();
        call.enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                if (response.body() != null) {
                    for (int i = 0; i < response.body().size(); i++) {
                        devices.add(response.body().get(i));
                        response.body();
                    }
                    MapPetrologFragment.mBus.post(new SendDeviceListEvent(devices, false));
                    //getDeviceDetail(devices.get(0).getRemoteDeviceId());

                    Log.d(TAG, "Callback successfully returned");
                }

            }

            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {
                Log.e(TAG, "Callback failed");
            }
        });
    }


}
