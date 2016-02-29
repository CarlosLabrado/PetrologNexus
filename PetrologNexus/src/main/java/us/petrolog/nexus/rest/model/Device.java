package us.petrolog.nexus.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Generated;

@Generated("org.jsonschema2pojo")
public class Device {

    @SerializedName("RemoteDeviceId")
    @Expose
    private Integer RemoteDeviceId;
    @SerializedName("Name")
    @Expose
    private String Name;
    @SerializedName("Location")
    @Expose
    private String Location;
    @SerializedName("Latitude")
    @Expose
    private Double Latitude;
    @SerializedName("Longitude")
    @Expose
    private Double Longitude;
    @SerializedName("NoLoad")
    @Expose
    private Boolean NoLoad;
    @SerializedName("RemoteDeviceStatus")
    @Expose
    private Integer RemoteDeviceStatus;

    /**
     * @return The RemoteDeviceId
     */
    public Integer getRemoteDeviceId() {
        return RemoteDeviceId;
    }

    /**
     * @param RemoteDeviceId The RemoteDeviceId
     */
    public void setRemoteDeviceId(Integer RemoteDeviceId) {
        this.RemoteDeviceId = RemoteDeviceId;
    }

    /**
     * @return The Name
     */
    public String getName() {
        return Name;
    }

    /**
     * @param Name The Name
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * @return The Location
     */
    public String getLocation() {
        return Location;
    }

    /**
     * @param Location The Location
     */
    public void setLocation(String Location) {
        this.Location = Location;
    }

    /**
     * @return The Latitude
     */
    public Double getLatitude() {
        return Latitude;
    }

    /**
     * @param Latitude The Latitude
     */
    public void setLatitude(Double Latitude) {
        this.Latitude = Latitude;
    }

    /**
     * @return The Longitude
     */
    public Double getLongitude() {
        return Longitude;
    }

    /**
     * @param Longitude The Longitude
     */
    public void setLongitude(Double Longitude) {
        this.Longitude = Longitude;
    }

    /**
     * @return The NoLoad
     */
    public Boolean getNoLoad() {
        return NoLoad;
    }

    /**
     * @param NoLoad The NoLoad
     */
    public void setNoLoad(Boolean NoLoad) {
        this.NoLoad = NoLoad;
    }

    /**
     * @return The RemoteDeviceStatus
     */
    public Integer getRemoteDeviceStatus() {
        return RemoteDeviceStatus;
    }

    /**
     * @param RemoteDeviceStatus The RemoteDeviceStatus
     */
    public void setRemoteDeviceStatus(Integer RemoteDeviceStatus) {
        this.RemoteDeviceStatus = RemoteDeviceStatus;
    }

}