package us.petrolog.nexus.misc;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import us.petrolog.nexus.rest.model.Device;

/**
 * Created by Vazh on 10/3/2016.
 */
public class ClusterMarkerLocation implements ClusterItem {

    private LatLng position;
    private Device mDevice;

    public ClusterMarkerLocation(LatLng position, Device device) {
        this.position = position;
        mDevice = device;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public Device getDevice() {
        return mDevice;
    }

    public void setDevice(Device device) {
        mDevice = device;
    }
}