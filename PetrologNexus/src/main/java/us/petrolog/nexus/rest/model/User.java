package us.petrolog.nexus.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Generated;

@Generated("org.jsonschema2pojo")
public class User {

    @SerializedName("UserId")
    @Expose
    private Integer UserId;
    @SerializedName("FirstName")
    @Expose
    private String FirstName;
    @SerializedName("LastName")
    @Expose
    private String LastName;
    @SerializedName("Email")
    @Expose
    private String Email;
    @SerializedName("Password")
    @Expose
    private String Password;
    @SerializedName("CultureInfo")
    @Expose
    private String CultureInfo;
    @SerializedName("Active")
    @Expose
    private Boolean Active;
    @SerializedName("TimeZoneId")
    @Expose
    private String TimeZoneId;
    @SerializedName("UseRemoteDeviceTimeZone")
    @Expose
    private Boolean UseRemoteDeviceTimeZone;
    @SerializedName("Permissions")
    @Expose
    private String Permissions;
    @SerializedName("Flags")
    @Expose
    private String Flags;

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
     * @return The FirstName
     */
    public String getFirstName() {
        return FirstName;
    }

    /**
     * @param FirstName The FirstName
     */
    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    /**
     * @return The LastName
     */
    public String getLastName() {
        return LastName;
    }

    /**
     * @param LastName The LastName
     */
    public void setLastName(String LastName) {
        this.LastName = LastName;
    }

    /**
     * @return The Email
     */
    public String getEmail() {
        return Email;
    }

    /**
     * @param Email The Email
     */
    public void setEmail(String Email) {
        this.Email = Email;
    }

    /**
     * @return The Password
     */
    public String getPassword() {
        return Password;
    }

    /**
     * @param Password The Password
     */
    public void setPassword(String Password) {
        this.Password = Password;
    }

    /**
     * @return The CultureInfo
     */
    public String getCultureInfo() {
        return CultureInfo;
    }

    /**
     * @param CultureInfo The CultureInfo
     */
    public void setCultureInfo(String CultureInfo) {
        this.CultureInfo = CultureInfo;
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
     * @return The UseRemoteDeviceTimeZone
     */
    public Boolean getUseRemoteDeviceTimeZone() {
        return UseRemoteDeviceTimeZone;
    }

    /**
     * @param UseRemoteDeviceTimeZone The UseRemoteDeviceTimeZone
     */
    public void setUseRemoteDeviceTimeZone(Boolean UseRemoteDeviceTimeZone) {
        this.UseRemoteDeviceTimeZone = UseRemoteDeviceTimeZone;
    }

    /**
     * @return The Permissions
     */
    public String getPermissions() {
        return Permissions;
    }

    /**
     * @param Permissions The Permissions
     */
    public void setPermissions(String Permissions) {
        this.Permissions = Permissions;
    }

    /**
     * @return The Flags
     */
    public String getFlags() {
        return Flags;
    }

    /**
     * @param Flags The Flags
     */
    public void setFlags(String Flags) {
        this.Flags = Flags;
    }

}