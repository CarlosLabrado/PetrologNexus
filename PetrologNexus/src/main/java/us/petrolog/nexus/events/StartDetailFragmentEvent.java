package us.petrolog.nexus.events;

/**
 * Created by Vazh on 2/3/2016.
 */
public class StartDetailFragmentEvent {
    int deviceId;
    String name;
    String locationName;

    public StartDetailFragmentEvent(int deviceId, String name, String locationName) {
        this.deviceId = deviceId;
        this.name = name;
        this.locationName = locationName;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
