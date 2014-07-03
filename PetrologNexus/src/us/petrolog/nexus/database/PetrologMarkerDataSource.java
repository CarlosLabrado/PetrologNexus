package us.petrolog.nexus.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carlos Labrado on 8/28/13.
 */
public class PetrologMarkerDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_SERIAL,
            MySQLiteHelper.COLUMN_COMMENT,
            MySQLiteHelper.COLUMN_LAT,
            MySQLiteHelper.COLUMN_LNG,
            MySQLiteHelper.COLUMN_BLUETOOTH,
            MySQLiteHelper.COLUMN_WIFIADDRESS,
            MySQLiteHelper.COLUMN_WIFIPASSWORD,
    };

    public PetrologMarkerDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * creates just one PetrologMarker with a serial, comment, latitude, longitude, a bluetooth address, wifi address and its password .
     *
     * @param serial
     * @param comment
     * @param lat
     * @param lng
     * @param bluetooth
     * @param wifiAddress
     * @param wifiPassword
     * @return
     */
    public PetrologMarker createPetrologMarker(int serial, String comment, double lat, double lng, String bluetooth, String wifiAddress, String wifiPassword){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_SERIAL, serial);
        values.put(MySQLiteHelper.COLUMN_COMMENT, comment);
        values.put(MySQLiteHelper.COLUMN_LAT, lat);
        values.put(MySQLiteHelper.COLUMN_LNG, lng);
        values.put(MySQLiteHelper.COLUMN_BLUETOOTH, bluetooth);
        values.put(MySQLiteHelper.COLUMN_WIFIADDRESS, wifiAddress);
        values.put(MySQLiteHelper.COLUMN_WIFIPASSWORD, wifiPassword);



        long insertId = database.insert(MySQLiteHelper.TABLE_MARKERS, null, values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_MARKERS, allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);

        cursor.moveToFirst();
        PetrologMarker mark = cursorToMarker(cursor);
        cursor.close();

        return mark;

    }


    /**
     * deletes a PetrologMarker from the DB
     * @param mark
     */
    public void deleteMarker(PetrologMarker mark){
        long id = mark.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_MARKERS, MySQLiteHelper.COLUMN_ID + " = " + id, null);
    }


    public void deleteMarkerById(int id) {
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_MARKERS, MySQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * Gets all the PetrologMarkers in the DB
     * @return list of markers
     */
    public List<PetrologMarker> getAllPetrologs(){
        List<PetrologMarker> markList = new ArrayList<PetrologMarker>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_MARKERS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            PetrologMarker mark = cursorToMarker(cursor);
            markList.add(mark);
            cursor.moveToNext();
        }

        //close the cursor
        cursor.close();

        return markList;
    }


    /**
     * used to transform a cursor to a PetrologMarker object
     * @param cursor
     * @return
     */
    private PetrologMarker cursorToMarker(Cursor cursor) {
        PetrologMarker mark = new PetrologMarker();
        mark.setId(cursor.getLong(0));
        mark.setSerial(cursor.getInt(1));
        mark.setComment(cursor.getString(2));
        mark.setLat(cursor.getDouble(3));
        mark.setLng(cursor.getDouble(4));
        mark.setBluetooth(cursor.getString(5));
        mark.setWifiAddress(cursor.getString(6));
        mark.setWifiPassword(cursor.getString(7));

        return mark;
    }

}
