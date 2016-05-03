package us.petrolog.nexus.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import us.petrolog.nexus.R;
import us.petrolog.nexus.events.MapLoadedEvent;
import us.petrolog.nexus.events.SendDeviceListEvent;
import us.petrolog.nexus.events.StartDetailFragmentEvent;
import us.petrolog.nexus.misc.ClusterMarkerLocation;
import us.petrolog.nexus.rest.model.Device;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapPetrologFragment extends Fragment {

    @Bind(R.id.buttonSwitchToSatellite)
    public FloatingActionButton mButtonSwitchToSatellite;
    @Bind(R.id.buttonFocusOnDevices)
    public FloatingActionButton mButtonFocusOnDevices;
    @Bind(R.id.buttonGoToDetail)
    public FloatingActionButton mButtonGoToDetail;

    private static final String TAG = MapPetrologFragment.class.getSimpleName();
    private static View mView;
    private LatLng mLatLng;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private AlertDialog mMarkerDialog = null;

    private HashMap<String, Device> mEventMarkerMap;

    LatLngBounds mBounds;

    private boolean isFocusOnDevicesOn = false;
    public static Bus mBus;

    private ShowcaseView mShowcaseView;
    private int mShowCaseCounter = 0;

    boolean isSatView = false;

    ClusterManager<ClusterMarkerLocation> mClusterManager;

    private Device mDeviceClicked;

    public MapPetrologFragment() {
        // Required empty public constructor
    }

    @OnClick(R.id.buttonFocusOnDevices)
    public void focusOnDevicesClicked() {
        if (mBounds != null) {
            isFocusOnDevicesOn = true;
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mBounds, 20));
        }
    }

    @OnClick(R.id.buttonSwitchToSatellite)
    public void switchToSatelliteClicked() {
        if (mMap != null) {
            if (!isSatView) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                isSatView = true;
            } else {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                isSatView = false;
            }
        }
    }

    @OnClick(R.id.buttonGoToDetail)
    public void goToDetailClicked() {
        MainActivity.mBus.post(new StartDetailFragmentEvent(mDeviceClicked.getRemoteDeviceId(), mDeviceClicked.getName(), mDeviceClicked.getLocation()));

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

        mButtonGoToDetail.hide();

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

        mClusterManager = new ClusterManager<ClusterMarkerLocation>(getActivity(), mMap);
        mClusterManager.setRenderer(new OwnIconRendered(getContext(), mMap, mClusterManager));
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterMarkerLocation>() {
            @Override
            public boolean onClusterItemClick(ClusterMarkerLocation clusterMarkerLocation) {
                mDeviceClicked = clusterMarkerLocation.getDevice();
                mButtonGoToDetail.show();
                return false;
            }
        });
        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarkerLocation>() {
            @Override
            public void onClusterItemInfoWindowClick(ClusterMarkerLocation clusterMarkerLocation) {
                Device device = clusterMarkerLocation.getDevice();
                MainActivity.mBus.post(new StartDetailFragmentEvent(device.getRemoteDeviceId(), device.getName(), device.getLocation()));

            }
        });
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<ClusterMarkerLocation>() {
            @Override
            public boolean onClusterClick(Cluster<ClusterMarkerLocation> cluster) {
                Log.d(TAG, "cluster click click");
                return false;
            }
        });
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mButtonGoToDetail.hide();
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
        if (!isFocusOnDevicesOn) { // the user pressed the focus on devices, so we keep it that way
            CameraUpdate cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            mMap.animateCamera(cameraUpdateFactory);
        }
    }

    @Subscribe
    public void getDeviceList(SendDeviceListEvent event) {
        if (isFragmentUIActive()) {
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            mEventMarkerMap = new HashMap<>();

            if (event != null) {
                for (Device device : event.getDevices()) {
                    LatLng latLng = new LatLng(device.getLatitude(), device.getLongitude());
                    boundsBuilder.include(latLng);

                    mClusterManager.addItem(new ClusterMarkerLocation(latLng, device));
                }
                // Animates the camera to show all the current markers
                mBounds = boundsBuilder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mBounds, 20));

            }
        }
    }

    /**
     * Is used by the cluster to render custom markers
     */
    class OwnIconRendered extends DefaultClusterRenderer<ClusterMarkerLocation> {

        public OwnIconRendered(Context context, GoogleMap map,
                               ClusterManager<ClusterMarkerLocation> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(ClusterMarkerLocation item, MarkerOptions markerOptions) {
            BitmapDrawable bitmapDraw = null;

            int height = 60;
            int width = 50;
            int deviceStatus = item.getDevice().getRemoteDeviceStatus();

            switch (deviceStatus) {
                case 0: // No data
                    bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.loc_black_pin);
                    break;
                case 1: // active
                    bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.loc_green_pin);
                    break;
                case 2: // Inactive
                    bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.loc_yellow_pin);
                    break;
                case 3: // Offline
                    bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.loc_grey_pin);
                    break;
                case 4: // Alert
                    bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.loc_red_pin);
                    break;
                default:
                    bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.loc_pin);
                    break;
            }
            Bitmap b = bitmapDraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            //markerOptions.snippet(item.getDevice().);
            markerOptions.title(item.getDevice().getName());
            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh_map) {
            callForManualRefresh();
        }
        if (id == R.id.action_help_map) {
            showShowcaseHelp();
        }
        return super.onOptionsItemSelected(item);
    }

    private void callForManualRefresh() {
        MainActivity.mBus.post(new MapLoadedEvent());
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.app_toolbar_title_main), null, false);
        setUpMapIfNeeded();
        if (mMap != null) {
            //populateTheMap(mLocations);
            if (mLatLng != null && mLatLng.latitude != 0.0 && mLatLng.longitude != 0.0 && !isFocusOnDevicesOn) {
                CameraUpdate cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(mLatLng, 17);
                mMap.animateCamera(cameraUpdateFactory);
            }
        }
    }

    private void showShowcaseHelp() {

        // this is to put the button on the left
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        mShowcaseView = new ShowcaseView.Builder(getActivity())
                .setTarget(new ViewTarget(mButtonFocusOnDevices))
                .setContentText(getString(R.string.help_map_focus))
                .setStyle(R.style.CustomShowcaseTheme4)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        switch (mShowCaseCounter) {
                            case 0:
                                mShowcaseView.setShowcase(new ViewTarget(mButtonSwitchToSatellite), false);
                                mShowcaseView.setContentText(getString(R.string.help_map_satellite));
                                break;
                            case 1:
                                mShowcaseView.hide();
                                mShowCaseCounter = -1;
                                break;
                        }
                        mShowCaseCounter++;
                    }
                })
                .build();
        mShowcaseView.setButtonText(getString(R.string.next));
        mShowcaseView.setHideOnTouchOutside(true);

    }

    public boolean isFragmentUIActive() {
        return isAdded() && !isDetached() && !isRemoving();
    }

}
