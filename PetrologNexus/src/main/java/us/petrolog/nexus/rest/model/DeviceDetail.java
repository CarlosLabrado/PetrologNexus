package us.petrolog.nexus.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Generated;

@Generated("org.jsonschema2pojo")
public class DeviceDetail {

    @SerializedName("RemoteDeviceId")
    @Expose
    private Integer RemoteDeviceId;
    @SerializedName("Name")
    @Expose
    private String Name;
    @SerializedName("GroupName")
    @Expose
    private String GroupName;
    @SerializedName("Status")
    @Expose
    private Integer Status;
    @SerializedName("Latitude")
    @Expose
    private Double Latitude;
    @SerializedName("Longitude")
    @Expose
    private Double Longitude;
    @SerializedName("GroupId")
    @Expose
    private Integer GroupId;
    @SerializedName("Active")
    @Expose
    private Boolean Active;
    @SerializedName("Location")
    @Expose
    private String Location;
    @SerializedName("TimeZoneId")
    @Expose
    private String TimeZoneId;
    @SerializedName("CreationDate")
    @Expose
    private String CreationDate;
    @SerializedName("BillingDate")
    @Expose
    private Object BillingDate;
    @SerializedName("CancellationDate")
    @Expose
    private Object CancellationDate;
    @SerializedName("Volume")
    @Expose
    private Integer Volume;
    @SerializedName("UserId")
    @Expose
    private Integer UserId;
    @SerializedName("State")
    @Expose
    private us.petrolog.nexus.rest.model.State State;

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
     * @return The GroupName
     */
    public String getGroupName() {
        return GroupName;
    }

    /**
     * @param GroupName The GroupName
     */
    public void setGroupName(String GroupName) {
        this.GroupName = GroupName;
    }

    /**
     * @return The Status
     */
    public Integer getStatus() {
        return Status;
    }

    /**
     * @param Status The Status
     */
    public void setStatus(Integer Status) {
        this.Status = Status;
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
     * @return The GroupId
     */
    public Integer getGroupId() {
        return GroupId;
    }

    /**
     * @param GroupId The GroupId
     */
    public void setGroupId(Integer GroupId) {
        this.GroupId = GroupId;
    }

    /**
     * @return The Active
     */
    public Boolean getActive() {
        return Active;
    }

    /**
     * @param Active The Active
     */
    public void setActive(Boolean Active) {
        this.Active = Active;
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
     * @return The TimeZoneId
     */
    public String getTimeZoneId() {
        return TimeZoneId;
    }

    /**
     * @param TimeZoneId The TimeZoneId
     */
    public void setTimeZoneId(String TimeZoneId) {
        this.TimeZoneId = TimeZoneId;
    }

    /**
     * @return The CreationDate
     */
    public String getCreationDate() {
        return CreationDate;
    }

    /**
     * @param CreationDate The CreationDate
     */
    public void setCreationDate(String CreationDate) {
        this.CreationDate = CreationDate;
    }

    /**
     * @return The BillingDate
     */
    public Object getBillingDate() {
        return BillingDate;
    }

    /**
     * @param BillingDate The BillingDate
     */
    public void setBillingDate(Object BillingDate) {
        this.BillingDate = BillingDate;
    }

    /**
     * @return The CancellationDate
     */
    public Object getCancellationDate() {
        return CancellationDate;
    }

    /**
     * @param CancellationDate The CancellationDate
     */
    public void setCancellationDate(Object CancellationDate) {
        this.CancellationDate = CancellationDate;
    }

    /**
     * @return The Volume
     */
    public Integer getVolume() {
        return Volume;
    }

    /**
     * @param Volume The Volume
     */
    public void setVolume(Integer Volume) {
        this.Volume = Volume;
    }

    /**
     * @return The UserId
     */
    public Integer getUserId() {
        return UserId;
    }

    /**
     * @param UserId The UserId
     */
    public void setUserId(Integer UserId) {
        this.UserId = UserId;
    }

    /**
     * @return The State
     */
    public us.petrolog.nexus.rest.model.State getState() {
        return State;
    }

    /**
     * @param State The State
     */
    public void setState(us.petrolog.nexus.rest.model.State State) {
        this.State = State;
    }

}