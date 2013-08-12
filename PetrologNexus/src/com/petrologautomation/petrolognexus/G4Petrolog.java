package com.petrologautomation.petrolognexus;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Switch;

import com.androidplot.xy.SimpleXYSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Cesar on 6/30/13.
 */
public class G4Petrolog {

    final static int TIMEOUT_VALUE = 10;
    final static int _12_BIT_MAX = 4096;

    InputStream Rx  = null;
    OutputStream Tx = null;

    private int Step = 0;
    private int countForDyna = 0;

    private String Result;
    private String S_1;
    private String E;
    private String MB;
    private String H;
    private String F1;
    private String F2;
    private String F3;
    private String F4;
    private String F5;
    private String F6;
    private String F7;
    private String F8;
    private String O;

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
 * This method should be called by the user every time the historical info is needed.
 * Author: CCR, JCC
 *
 * */
    public void requestPetrologHistory (){

        SendCommand("01E");
        SendCommand("01E");
        SendCommand("01F1");
        SendCommand("01F2");
        SendCommand("01F3");
        SendCommand("01F4");
        SendCommand("01F5");
        SendCommand("01F6");
        SendCommand("01F7");
        SendCommand("01F8");
        SendCommand("01S?1");
    }

    /*
     * This method should be called by the user every time the information update is needed.
     * Author: CCR, JCC
     *
     * */
    public void HeartBeat (){
        /* MB */
        SendCommand("01MB");

        countForDyna++;
        if (countForDyna % 5 == 0) {
            /* One Sec @ 200ms HeartBeat */
            Log.i("PN - Rx","Envie E");
            SendCommand("01E");
        }
        if (countForDyna % 26 == 0){
            /* 5.2 Sec @ 200ms HeartBeat */
            switch (Step){
                case 0:
                /* H */
                    Step = 1;
                    Log.i("PN - Rx","Envie H");
                    SendCommand("01H");
                    break;
                case 1:
                /* O */
                    Step = 0;
                    Log.i("PN - Rx","Envie 0");
                    SendCommand("01O");
                    break;
                default:
                    break;
            }

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
            if(Rx.available() != 0){
                byte[] flush = new byte[512];
                Rx.read(flush);
            }
            Tx.flush();
            Tx.write(command.getBytes());
            Tx.write(0x0D);
            if(command.contains("O")){
                try {
                    Thread.sleep(50);
                    Tx.write(0x0D);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


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
            Result.getChars(2, 3, tempCommand, 0);
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
                case 'F':
                    char [] tempF = new char[1];
                    Result.getChars(3,4,tempF,0);
                    switch (tempF[0]){
                        case '1':
                            F1 = Result;
                            break;
                        case '2':
                            F2 = Result;
                            break;
                        case '3':
                            F3 = Result;
                            break;
                        case '4':
                            F4 = Result;
                            break;
                        case '5':
                            F5 = Result;
                            break;
                        case '6':
                            F6 = Result;
                            break;
                        case '7':
                            F7 = Result;
                            break;
                        case '8':
                            F8 = Result;
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    if (Result.contains(",,")) {
                        O = Result;
                    }
                    Log.i("PN - Rx","Bad Response = "+Result);
                    break;
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
            if((OnOff&0x08) == 0){
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
            if((OnOff&0x04) == 0){
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

    /*
     * This method gets the latest Dyna.
     * Author: CCR
     *
     * */
    public int[] getLoadPositionPoint (){
        int[] PosLoad = new int[2];

        try {
            /* Position */
            PosLoad[0] = Integer.valueOf(MB.substring(59,63),16);
            /* Load */
            PosLoad[1] = Integer.valueOf(MB.substring(55,59),16);
        } catch (StringIndexOutOfBoundsException e){
            PosLoad [0] = -1;
        } catch (NullPointerException e){
            PosLoad [0] = -2;
        } catch (NumberFormatException e){
            PosLoad [0] = -3;
        }
        return PosLoad;

    }

    /*
     * This method gets a day (from the last 31) runtime in seconds.
     * Author: CCR
     *
     * */
    public int getHistoricalRuntime (int day){
        final int HEADER = 4;
        final int CHARS_IN_DAY = 16;
        final int OFFSET_IN_DAY = 6;
        final int SEC_LENGHT = 4;
        int HistoricalRuntime;


        if (day < 0 || day > 31){
            /* Parameter Error */
            return -4;
        }
        else{
            if (day<=4){
                int location = HEADER+((day-1)*CHARS_IN_DAY)+OFFSET_IN_DAY;

                try {
                    HistoricalRuntime = Integer.valueOf(F1.substring(location,location+SEC_LENGHT),16);
                } catch (StringIndexOutOfBoundsException e){
                    return -1;
                } catch (NullPointerException e){
                    return -2;
                } catch (NumberFormatException e){
                    return -3;
                }

            }
            else if (day<=8){

                int location = HEADER+(((day-4)-1)*CHARS_IN_DAY)+OFFSET_IN_DAY;
                try {
                    HistoricalRuntime = Integer.valueOf(F2.substring(location,location+SEC_LENGHT),16);
                } catch (StringIndexOutOfBoundsException e){
                    return -1;
                } catch (NullPointerException e){
                    return -2;
                } catch (NumberFormatException e){
                    return -3;
                }

            }
            else if (day<=12){
                int location = HEADER+(((day-8)-1)*CHARS_IN_DAY)+OFFSET_IN_DAY;
                try {
                    HistoricalRuntime = Integer.valueOf(F3.substring(location,location+SEC_LENGHT),16);
                } catch (StringIndexOutOfBoundsException e){
                    return -1;
                } catch (NullPointerException e){
                    return -2;
                } catch (NumberFormatException e){
                    return -3;
                }

            }
            else if (day<=16){
                int location = HEADER+(((day-12)-1)*CHARS_IN_DAY)+OFFSET_IN_DAY;
                try {
                    HistoricalRuntime = Integer.valueOf(F4.substring(location,location+SEC_LENGHT),16);
                } catch (StringIndexOutOfBoundsException e){
                    return -1;
                } catch (NullPointerException e){
                    return -2;
                } catch (NumberFormatException e){
                    return -3;
                }

            }
            else if (day<=20){
                int location = HEADER+(((day-16)-1)*CHARS_IN_DAY)+OFFSET_IN_DAY;
                try {
                    HistoricalRuntime = Integer.valueOf(F5.substring(location,location+SEC_LENGHT),16);
                } catch (StringIndexOutOfBoundsException e){
                    return -1;
                } catch (NullPointerException e){
                    return -2;
                } catch (NumberFormatException e){
                    return -3;
                }

            }
            else if (day<=24){
                int location = HEADER+(((day-20)-1)*CHARS_IN_DAY)+OFFSET_IN_DAY;
                try {
                    HistoricalRuntime = Integer.valueOf(F6.substring(location,location+SEC_LENGHT),16);
                } catch (StringIndexOutOfBoundsException e){
                    return -1;
                } catch (NullPointerException e){
                    return -2;
                } catch (NumberFormatException e){
                    return -3;
                }

            }
            else if (day<=28){
                int location = HEADER+(((day-24)-1)*CHARS_IN_DAY)+OFFSET_IN_DAY;
                try {
                    HistoricalRuntime = Integer.valueOf(F7.substring(location,location+SEC_LENGHT),16);
                } catch (StringIndexOutOfBoundsException e){
                    return -1;
                } catch (NullPointerException e){
                    return -2;
                } catch (NumberFormatException e){
                    return -3;
                }

            }
            else {
                int location = HEADER+(((day-28)-1)*CHARS_IN_DAY)+OFFSET_IN_DAY;
                try {
                    HistoricalRuntime = Integer.valueOf(F8.substring(location,location+SEC_LENGHT),16);
                } catch (StringIndexOutOfBoundsException e){
                    return -1;
                } catch (NullPointerException e){
                    return -2;
                } catch (NumberFormatException e){
                    return -3;
                }

            }
            if (HistoricalRuntime < 43200){
                return HistoricalRuntime*2;
            }
            else {
                return 0;
            }
        }
    }
    /*
     * This method gets the latest running fillage.
     * Author: CCR, JCC
     *
     * */
    public int getCurrentFillage (){
        try {
            return(Integer.valueOf(O.substring(4,8),16));
        } catch (StringIndexOutOfBoundsException e){
            return -1;
        } catch (NullPointerException e){
            return -2;
        } catch (NumberFormatException e){
            return -3;
        }
    }
}
