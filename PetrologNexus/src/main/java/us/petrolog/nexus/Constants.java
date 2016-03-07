package us.petrolog.nexus;

/**
 * Created by carlos on 2/29/16.
 */
public class Constants {

    public static final String API_KEY = "ApiKey=Q2VzYXJBbmRyb2lkQXBw";
    public static final String BASE_URL = "http://petrolog2.azurewebsites.net";


    // Don't use these
    public static final String AUTH_HEADER = "Authorization: Basic aXNhYWMub2plZGFAaW50ZWxlY3RpeC5jb206MTIzNDU2";
    public static final String TYPE_HEADER = "Content-Type: application/json";

    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static final String ARG_DEVICE_ID = "arg_device_id";
    public static final String ARG_DEVICE_NAME = "arg_device_name";
    public static final String ARG_DEVICE_LOCATION_NAME = "arg_device_location_name";

    // Shared Preferences
    public static final String SP_IS_USER_LOGGED = "sp_is_user_logged";
    public static final String SP_USER_EMAIL = "sp_user_email_";
    public static final String SP_USER_PASSWORD = "sp_user_password";
    public static final String SP_USER_NAME = "sp_user_name";

    // Extras
    public static final String EXTRA_USER_NAME = "extra_user_name";
    public static final String EXTRA_USER_EMAIL = "extra_user_email";
}
