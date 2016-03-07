package us.petrolog.nexus.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Generated;

@Generated("org.jsonschema2pojo")
public class UserBody {

    @SerializedName("Email")
    @Expose
    private String Email;
    @SerializedName("Password")
    @Expose
    private String Password;

    /**
     * No args constructor for use in serialization
     */
    public UserBody() {
    }

    /**
     * @param Email
     * @param Password
     */
    public UserBody(String Email, String Password) {
        this.Email = Email;
        this.Password = Password;
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

}