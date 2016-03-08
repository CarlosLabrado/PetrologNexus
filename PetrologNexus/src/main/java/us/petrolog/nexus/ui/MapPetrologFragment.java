package us.petrolog.nexus.ui;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;
import us.petrolog.nexus.R;
import us.petrolog.nexus.events.MapLoadedEvent;
import us.petrolog.nexus.events.SendDeviceListEvent;
import us.petrolog.nexus.events.StartDetailFragmentEvent;
import us.petrolog.nexus.rest.model.Device;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapPetrologFragment extends Fragment {

    private static final String TAG = MapPetrologFragment.class.getSimpleName();
    private static View mView;
    private LatLng mLatLng;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private AlertDialog mMarkerDialog = null;

    private HashMap<String, Device> mEventMarkerMap;

    LatLngBounds mBounds;

    public static Bus mBus;

    public MapPetrologFragment() {
        // Required empty public constructor
    }

    @OnClick(R.id.buttonFocusOnDevices)
    public void focusOnDevicesClicked() {
        if (mBounds != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mBounds, 20));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBus = new Bus();
        mBus.register(this);

        // Inflate the layout for this fragment
        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null)
                parent.removeView(mView);
        }
        try {
            mView = inflater.inflate(R.layout.fragment_map_petrolog, container, false);
            ButterKnife.bind(this, mView);

        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        setUpMapIfNeeded();

        setHasOptionsMenu(true);

        return mView;
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment. (THIS TOOK 2 HOURS)
            mMap = ((SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Device device = mEventMarkerMap.get(marker.getId());
                MainActivity.mBus.post(new StartDetailFragmentEvent(device.getRemoteDeviceId(), device.getName(), device.getLocation()));
            }
        });

        // Initiate loadings

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                MainActivity.mBus.post(new MapLoadedEvent());
            }
        });
    }

    @Subscribe
    public void handleLocation(LatLng latLng) {
        mLatLng = latLng;
        CameraUpdate cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(cameraUpdateFactory);
    }

    @Subscribe
    public void getDeviceList(SendDeviceListEvent event) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        mEventMarkerMap = new HashMap<>();

        if (event != null) {
            for (Device device : event.getDevices()) {
                int deviceStatus = device.getRemoteDeviceStatus();
                float markerColor = 0;
                switch (deviceStatus) {
                    case 0: // No data
                        markerColor = BitmapDescriptorFactory.HUE_CYAN;
                        break;
                    case 1: // active
                        markerColor = BitmapDescriptorFactory.HUE_GREEN;
                        break;
                    case 2: // Inactive
                        markerColor = BitmapDescriptorFactory.HUE_YELLOW;
                        break;
                    case 3: // Offline
                        markerColor = BitmapDescriptorFactory.HUE_RED;
                        break;
                    case 4: // Alert
                        markerColor = BitmapDescriptorFactory.HUE_RED;
                        break;
                }
                LatLng latLng = new LatLng(device.getLatitude(), device.getLongitude());
                builder.include(latLng);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(device.getName() + " - " + device.getLocation())
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));

                mEventMarkerMap.put(marker.getId(), device);
            }
            // Animates the camera to show all the current markers
            mBounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mBounds, 20));

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setActionBarTitle("Map", null, false);
        setUpMapIfNeeded();
        if (mMap != null) {
            //populateTheMap(mLocations);
            if (mLatLng != null && mLatLng.latitude != 0.0 && mLatLng.longitude != 0.0) {
                CameraUpdate cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(mLatLng, 17);
                mMap.animateCamera(cameraUpdateFactory);
            }
        }
    }

}
