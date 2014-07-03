package us.petrolog.nexus.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Carlos Labrado on 8/28/13.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_MARKERS = "markers";
    public static final String COLUMN_SERIAL = "serial";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";
    public static final String COLUMN_BLUETOOTH = "bluetooth";
    public static final String COLUMN_WIFIADDRESS = "wifiaddress";
    public static final String COLUMN_WIFIPASSWORD = "wifipassword";


    private static final String DATABASE_NAME = "petrolog.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_MARKERS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_SERIAL + " integer not null, " +
            COLUMN_COMMENT + " text not null, " +
            COLUMN_LAT + " double not null, " +
            COLUMN_LNG + " double not null, " +
            COLUMN_BLUETOOTH + " text not null, " +
            COLUMN_WIFIADDRESS + " text not null, " +
            COLUMN_WIFIPASSWORD + " text not null " +
            ");";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKERS);
        onCreate(db);
    }

}