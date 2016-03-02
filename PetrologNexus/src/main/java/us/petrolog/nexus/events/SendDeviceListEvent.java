package us.petrolog.nexus.events;

import java.util.List;

import us.petrolog.nexus.rest.model.Device;

/**
 * Created by Vazh on 1/3/2016.
 */
public class SendDeviceListEvent {

    private List<Device> devices;
    private boolean error;

    public SendDeviceListEvent(List<Device> devices, boolean error) {
        this.devices = devices;
        this.error = error;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
