package us.petrolog.nexus.events;

/**
 * Created by Vazh on 2/3/2016.
 */
public class StartDetailFragmentEvent {
    int deviceId;

    public StartDetailFragmentEvent(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
}
