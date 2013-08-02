package com.petrologautomation.petrolognexus;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Switch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Cesar on 6/30/13.
 */
public class G4Petrolog {

    final static int TIMEOUT_VALUE = 10;

    InputStream Rx  = null;
    OutputStream Tx = null;

    private int Step = 0;
    private String Result;
    private String S_1;
    private String E;
    private String MB;
    private String H;

    /*
     * Constructor
     * Author: CCR, JCC
     *
     * */
    public G4Petrolog(BluetoothSocket socket){
        try {
            Tx = socket.getOutputStream();
            Rx = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            //Error!!
            return;
        }
    }

    /*
     * This method should be called by the user every time the information update is needed.
     * Author: CCR, JCC
     *
     * */
    public void HeartBeat (){
        switch (Step){
            case 0:
                /* S?1 */
                Step = 1;
                SendCommand("01S?1");
                break;
            case 1:
                /* E */
                Step = 2;
                SendCommand("01E");
                break;
            case 2:
                /* MB */
                Step = 3;
                SendCommand("01MB");
                break;
            case 3:
                /* H */
                Step = 0;
                SendCommand("01H");
                break;
            default:
                break;
        }
    }

    /*
     * Sends and Receives serial G4 command
     * Author: CCR, JCC
     *
     * */
    private void SendCommand(String command){

        final int BUFFER_SIZE = 512;
        char[] buffer = new char[BUFFER_SIZE];
        int i = 1;

        Result = "";
        int timeout = 0;

        try {
            // Tx
            Tx.flush();
            Tx.write(command.getBytes());
            Tx.write(0x0D);

            // Rx
            do {
                if ( Rx.available() != 0 ) {
                    /* Blocking until byte Rx */
                    i++;
                    buffer[i] = (char) Rx.read();
                    Result = Result+ buffer[i];
                    timeout = 0;
                }
                else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    timeout++;
                    if (timeout >= TIMEOUT_VALUE){
                        Result = "Time Out!";
                        break;
                    }
                }
            } while (buffer[i] != 0x1A);

            // Process Result
            char [] tempCommand = new char [1];
            Result.getChars(2,3,tempCommand,0);
            switch (tempCommand[0]){
                case 'S':
                    S_1 = Result;
                    break;
                case 'E':
                    E = Result;
                    break;
                case 'M':
                    MB = Result;
                    break;
                case 'H':
                    H = Result;
                    break;
                default:
                    Log.i("PN - Rx","Bad Response = "+Result);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            Log.i("PN - Rx","Bad Response = "+Result);
        }
    }
    /*
     * This method gets Well Status.
     * Author: CCR, JCC
     *
     * */
    public String getWellStatus (){
        try {
            int OnOff = Integer.valueOf(E.substring(24,25),16);
            if(OnOff%3 == 0){
               return "Running";
            }else{
               return "Stopped";
            }
        } catch (StringIndexOutOfBoundsException e){
            return "Empty - String Out of Bounds";
        } catch (NullPointerException e){
            return "Empty - Null Pointer";
        } catch (NumberFormatException e){
            return "Empty - Number Format";
        }
    }

    /*
     * This method gets Pump Off Flag.
     * Author: CCR, JCC
     *
     * */
    public String getPumpOffStatus (){
        try {
            int OnOff = Integer.valueOf(E.substring(16,17),16);
            if(OnOff%3 == 0){
                return "No";
            }else{
                return "Yes";
            }
        } catch (StringIndexOutOfBoundsException e){
            return "Empty - String Out of Bounds";
        } catch (NullPointerException e){
            return "Empty - Null Pointer";
        } catch (NumberFormatException e){
            return "Empty - Number Format";
        }
    }

    /*
     * This method gets Strokes this Cycle.
     * Author: CCR, JCC
     *
     * */
    public int getStrokesThis (){
        try {
            return Integer.valueOf(MB.substring(25,29),16);
        } catch (StringIndexOutOfBoundsException e){
            return -1;
        } catch (NullPointerException e){
            return -2;
        } catch (NumberFormatException e){
            return -3;
        }
    }

    /*
     * This method gets Strokes Last Cycle.
     * Author: CCR, JCC
     *
     * */
    public int getStrokesLast (){
        try {
            return Integer.valueOf(MB.substring(42,46),16);
        } catch (StringIndexOutOfBoundsException e){
            return -1;
        } catch (NullPointerException e){
            return -2;
        } catch (NumberFormatException e){
            return -3;
        }
    }

    /*
     * This method gets Minutes to Next Start.
     * Author: CCR, JCC
     *
     * */
    public int getMinNextStart (){
        try {
            return 255-Integer.valueOf(MB.substring(50,52),16);
        } catch (StringIndexOutOfBoundsException e){
            return -1;
        } catch (NullPointerException e){
            return -2;
        } catch (NumberFormatException e){
            return -3;
        }
    }

    /*
     * This method gets Minutes to Next Start.
     * Author: CCR, JCC
     *
     * */
    public int getSecNextStart (){
        try {
            return 255-Integer.valueOf(MB.substring(52,54),16);
        } catch (StringIndexOutOfBoundsException e){
            return -1;
        } catch (NullPointerException e){
            return -2;
        } catch (NumberFormatException e){
            return -3;
        }
    }

    /*
     * This method gets Pump Off Strokes (Setting).
     * Author: CCR, JCC
     *
     * */
    public int getPumpOffStrokesSetting (){
        try {
            int PumpOffStrokes = Integer.valueOf(S_1.substring(11,13),16);
            return PumpOffStrokes;
        } catch (StringIndexOutOfBoundsException e){
            return -1;
        } catch (NullPointerException e){
            return -2;
        } catch (NumberFormatException e){
            return -3;
        }
    }

    /*
     * This method gets Fillage (Setting).
     * Author: CCR, JCC
     *
     * */
    public int getFillageSetting (){
        try {
            int Fillage = (Integer.valueOf(S_1.substring(33,35),16))*10;
            return Fillage;
        } catch (StringIndexOutOfBoundsException e){
            return -1;
        } catch (NullPointerException e){
            return -2;
        } catch (NumberFormatException e){
            return -3;
        }
    }

    /*
     * This method gets Pump Up Strokes (Setting).
     * Author: CCR
     *
     * */
    public int getPumpUpSetting (){
        try {
            int PumpUpStrokes = Integer.valueOf(S_1.substring(9,11),16);
            return PumpUpStrokes;
        } catch (StringIndexOutOfBoundsException e){
            return -1;
        } catch (NullPointerException e){
            return -2;
        } catch (NumberFormatException e){
            return -3;
        }
    }

    /*
     * This method gets Current Timeout (Setting).
     * Author: CCR
     *
     * */
    public int getCurrentTimeoutSetting (){
        try {
            int CurrentTimeout = 256-Integer.valueOf(S_1.substring(29,31),16);
            return CurrentTimeout;
        } catch (StringIndexOutOfBoundsException e){
            return -1;
        } catch (NullPointerException e){
            return -2;
        } catch (NumberFormatException e){
            return -3;
        }
    }

    /*
     * This method gets AutomaticTO (Setting).
     * Author: CCR
     *
     * */
    public String getAutomaticTOSetting (){
        try {
            int AutomaticTO = Integer.valueOf(S_1.substring(37,39),16);
            if (AutomaticTO != 0){
                return "Yes";
            }
            else {
                return "No";
            }
        } catch (StringIndexOutOfBoundsException e){
            return "StringOutOfBounds";
        } catch (NullPointerException e){
            return "NullPointer";
        } catch (NumberFormatException e){
            return "NumberFormat";
        }
    }

    /*
     * This method gets Yesterday's Runtime in seconds.
     * Author: CCR
     *
     * */
    public int getYesterdayRuntime (){
        try {
            int YesterdayRuntime = Integer.valueOf(S_1.substring(45,49),16);
            return YesterdayRuntime*2;
        } catch (StringIndexOutOfBoundsException e){
            return -1;
        } catch (NullPointerException e){
            return -2;
        } catch (NumberFormatException e){
            return -3;
        }
    }

    /*
     * This method gets Today's Runtime in seconds.
     * Author: CCR
     *
     * */
    public int getTodayRuntime (){
        try {
            int TodayRuntime = Integer.valueOf(MB.substring(38,42),16);
            if (getOverflowHrsToday()){
                return TodayRuntime+65535;
            }
            else{
                return TodayRuntime;
            }

        } catch (StringIndexOutOfBoundsException e){
            return -1;
        } catch (NullPointerException e){
            return -2;
        } catch (NumberFormatException e){
            return -3;
        }
    }

    /*
     * This method gets Hours Today Overflow bit.
     * Author: CCR
     *
     * */
    public boolean getOverflowHrsToday (){
        try {
            int temp = Integer.valueOf(E.substring(16,17),16);
            if (temp%2 == 0) {
                return false;
            }
            else {
                return true;
            }
        } catch (StringIndexOutOfBoundsException e){
            return false;
        } catch (NullPointerException e){
            return false;
        } catch (NumberFormatException e){
            return false;
        }
    }

    /*
     * This method gets Petrolog Clock.
     * Author: CCR
     *
     * */
    public String getPetrologClock (){
        int foo = 0;
        try {
            /* Validate Usage */
            int totalSecToday = Integer.valueOf(H.substring(3).substring(0,2))*3600 +
                                Integer.valueOf(H.substring(3).substring(3,5))*60   +
                                Integer.valueOf(H.substring(3).substring(6,8));
            foo = totalSecToday;
            return H.substring(3);
        } catch (StringIndexOutOfBoundsException e){
            return "Empty - String Out of Bounds - "+foo;
        } catch (NullPointerException e){
            return "Empty - Null Pointer - "+foo;
        } catch (NumberFormatException e){
            return "Empty - Number Format - "+foo;
        }
    }

}
