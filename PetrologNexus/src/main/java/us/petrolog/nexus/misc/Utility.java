package us.petrolog.nexus.misc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Vazh on 1/3/2016.
 */
public class Utility {

    /**
     * Returns true if the network is available or about to become available
     *
     * @param context used to get the ConnectivityManager
     * @return .
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    /**
     * Returns a date from a string
     *
     * @param stringDate the raw string
     * @return formatted date
     */
    public static Calendar getFormattedDate(String stringDate) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Calendar calendar = new GregorianCalendar();
            Date date = formatter.parse(stringDate);
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Calculates the greatest common denominator between 2 numbers, we normally use this for the
     * step in the graphs
     *
     * @param a first number
     * @param b second number
     * @return the GCD
     */
    public static int GCD(int a, int b) {
        if (b == 0) return a;
        return GCD(b, a % b);
    }
}
