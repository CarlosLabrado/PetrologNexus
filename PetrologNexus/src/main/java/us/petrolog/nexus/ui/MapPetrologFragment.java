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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import us.petrolog.nexus.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapPetrologFragment extends Fragment {

    private static View mView;
    private LatLng mLatLng;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private AlertDialog mMarkerDialog = null;
    private LatLng mLatLong;

    public static Bus mBus;

    public MapPetrologFragment() {
        // Required empty public constructor
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
                //goLockyMarker = eventMarkerMap.get(marker.getId());
                //inflateMarkerClickedDialog(goLockyMarker);
            }
        });

        // Initiate loadings

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
//                MainActivity.bus.post(new StartRegisteringUserEvent(StartRegisteringUserEvent.Type.STARTED, 1));
            }
        });
    }

    @Subscribe
    public void handleLocation(LatLng latLng) {
        mLatLong = latLng;
        CameraUpdate cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(cameraUpdateFactory);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (mMap != null) {
            //populateTheMap(mLocations);
            if (mLatLong != null && mLatLong.latitude != 0.0 && mLatLong.longitude != 0.0) {
                CameraUpdate cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(mLatLong, 17);
                mMap.animateCamera(cameraUpdateFactory);
            }
        }
    }

}
