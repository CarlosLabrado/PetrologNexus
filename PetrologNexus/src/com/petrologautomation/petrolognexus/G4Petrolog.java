package com.petrologautomation.petrolognexus;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * Created by Cesar on 6/30/13.
 */
public class G4Petrolog {

    final static int TIMEOUT_VALUE = 10;
    final static int _12_BIT_MAX = 4096;

    InputStream Rx  = null;
    OutputStream Tx = null;

    private boolean HeartBeatStopped = false;

    private int Step = 0;
    private int countForDyna = 0;

    private int[] PosLoad = new int[2];
    private boolean stopO = false;

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
            HeartBeatStopped = false;
        } catch (IOException e) {
            e.printStackTrace();
            //Error!!
            return;
        }
    }

    public void Disconnect(){
        if (Tx == null || Rx == null){
            /* Already Disconnected */
        }
        else {
            HeartBeatStopped = true;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Tx.close();
                Rx.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Tx = null;
            Rx = null;
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
        SendCommand("01H");
        SendCommand("01H");
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
        if (HeartBeatStopped){
            return;
        }
        else {
            if (stopO){
                SendCommand("");
                stopO = false;
            }
            /* MB */
            SendCommand("01MB");

            countForDyna++;
            if (countForDyna % 5 == 0) {
            /* One Sec @ 200ms HeartBeat */
                SendCommand("01E");
            }
            if (countForDyna % 26 == 0){
            /* 5.2 Sec @ 200ms HeartBeat */
                switch (Step){
                    case 0:
                /* H */
                        Step = 1;
                        SendCommand("01H");
                        break;
                    case 1:
                /* O */
                        Step = 0;
                        SendCommand("01O");
                        break;
                    default:
                        break;
                }
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
            try {
                // Tx
                if(Rx.available() != 0){
                    byte[] flush = new byte[512];
                    Rx.read(flush);
                }
                Tx.flush();
                Tx.write(command.getBytes());
                Tx.write(0x0D);
                do {
                    // Rx
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
            }
            catch(NullPointerException e) {
                Log.i("PN - Rx","Null Rx Stream");
            }

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
                    try {
                    /* Position */
                        if (Integer.valueOf(MB.substring(59,63),16) <= _12_BIT_MAX &&
                                Integer.valueOf(MB.substring(59,63),16) > 0   ){
                            PosLoad[0] = Integer.valueOf(MB.substring(59,63),16);
                        }
                    /* Load */
                        if (Integer.valueOf(MB.substring(55,59),16) <= _12_BIT_MAX &&
                                Integer.valueOf(MB.substring(55,59),16) > 0   ){
                            PosLoad[1] = Integer.valueOf(MB.substring(55,59),16);
                        }
                    } catch (StringIndexOutOfBoundsException e){
                        PosLoad [0] = -1;
                    } catch (NullPointerException e){
                        PosLoad [0] = -2;
                    } catch (NumberFormatException e){
                        PosLoad [0] = -3;
                    }
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
                    if (Result.substring(12,14).equals(",,")) {
                        O = Result;
                        try {
                        /* Position */
                            if (Integer.valueOf(O.substring(19,23),16) <= _12_BIT_MAX &&
                                    Integer.valueOf(O.substring(19,23),16) > 0){
                                PosLoad[0] = Integer.valueOf(O.substring(19,23),16);
                            }
                        /* Load */
                            if (Integer.valueOf(O.substring(14,18),16) <= _12_BIT_MAX &&
                                    Integer.valueOf(O.substring(14,18),16) > 0){
                                PosLoad[1] = Integer.valueOf(O.substring(14,18),16);
                            }
                            if (Integer.valueOf(O.substring(8,12),16) < 200){
                                stopO = true;
                            }
                        } catch (StringIndexOutOfBoundsException e){
                            Log.i("PN - Rx","O Error 1 = "+Result);
                            PosLoad [0] = -1;
                        } catch (NullPointerException e){
                            Log.i("PN - Rx","O Error 2 = "+Result);
                            PosLoad [0] = -2;
                        } catch (NumberFormatException e){
                            Log.i("PN - Rx","O Error 3 = "+Result);
                            PosLoad [0] = -3;
                        }

                    }
                    else {
                        Log.i("PN - Rx","Bad Response = "+Result);
                    }
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
            return H.substring(3,H.length()-2);
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
            return(Integer.valueOf(O.substring(8,12),16));
        } catch (StringIndexOutOfBoundsException e){
            return -1;
        } catch (NullPointerException e){
            return -2;
        } catch (NumberFormatException e){
            return -3;
        }
    }

    /*
     * This method sets new values to POC parameters.
     * Clock -> String
     * Pump Up -> Int
     * Pump Off -> Int
     * Fillage -> Int
     * TimeOut -> Int
     * Autotimeout -> Boolean
     * Author: CCR, JCC
     *
     * */
    public boolean setSettings (String clock, int pu, int po, int fillage, int to, boolean ato){

        HeartBeatStopped = true;

        SendCommand("01E");

        /* Pump Off */
        SendCommand("01SK"+String.format("%02x", po));
        Log.i("PN - Settings", "PO: " + "01SK" + String.format("%02x", po));

        /* Fillage */
        int toSend = fillage/10;
        if (toSend < 2){
            toSend = 2;
        }
        if (toSend > 9){
            toSend = 9;
        }

        /* Pump Up */
        SendCommand("01SX"+String.format("%02x",pu));
        Log.i("PN - Settings", "PU: " + "01SX" + String.format("%02x", pu));


        SendCommand("01ST"+String.format("%02x", toSend));
        Log.i("PN - Settings", "Fillage: " + "01ST" + String.format("%02x", toSend));

        /* Time Out */
        if (to > 0 && to < 255){
            SendCommand("01SP"+Integer.toHexString(256-to).toUpperCase());
            Log.i("PN - Settings", "TO: " + "01SP" + String.format("%02x", 256 - to).toUpperCase());

        }
        else if (to < 0){
            SendCommand("01SP"+Integer.toHexString(256-1));
            Log.i("PN - Settings", "TO: " + "01SP" + String.format("%02x", 256 - 1));
        }
        else {
            /* error */
            return true;
        }

        /* Auto TimeOut */
        if(ato){
            SendCommand("01SG01");
            Log.i("PN - Settings", "AutoTimeOut: Auto " + "01SG01");
        }
        else{
            SendCommand("01SG00");
            Log.i("PN - Settings", "AutoTimeOut: Fixed " + "01SG00");
        }
        /* Clock */
        String time = null;
        String day = null;
        String month = null;
        String year = null;
        try {
        /* US date format -> Mex date format */
            String date = clock;
            time = date.substring(0,9);
            day = date.substring(12,15);
            month = date.substring(9,12);
            year = date.substring(15);
        } catch (StringIndexOutOfBoundsException e) {
            Log.i("PN - Settings", "Error: Clock Format");
        }


        if((clock.matches("[0-2][0-9].[0-5][0-9].[0-5][0-9].[0-1][0-9].[0-3][0-9].[0-9][0-9]")) &&
                (Integer.valueOf(month.substring(0,2)) <= 12)){
            SendCommand("01SH"+time+day+month+year);
            Log.i("PN - Settings", "Date: " + "01SH" +time+day+month+year);
        }
        else{

            Calendar myDateTime = new GregorianCalendar();
            Log.i("PN - Settings", "Month: "+ myDateTime.get(Calendar.MONTH));

            SendCommand("01SH"+String.format("%02d", myDateTime.get(Calendar.HOUR_OF_DAY))  +" "
                    +String.format("%02d", myDateTime.get(Calendar.MINUTE))       +" "
                    +String.format("%02d", myDateTime.get(Calendar.SECOND))       +" "
                    +String.format("%02d", myDateTime.get(Calendar.DAY_OF_MONTH)) +" "
                    +String.format("%02d", myDateTime.get(Calendar.MONTH)+1)      +" " /* Month starts @ 0 */
                    +String.format("%02d", myDateTime.get(Calendar.YEAR) % 2000)
            );
            Log.i("PN - Settings", "Date: " + "01SH" + String.format("%02d", myDateTime.get(Calendar.HOUR_OF_DAY)) + " "
                    + String.format("%02d", myDateTime.get(Calendar.MINUTE)) + " "
                    + String.format("%02d", myDateTime.get(Calendar.SECOND)) + " "
                    + String.format("%02d", myDateTime.get(Calendar.DAY_OF_MONTH)) + " "
                    + String.format("%02d", myDateTime.get(Calendar.MONTH)+1) + " " /* Month starts @ 0 */
                    + String.format("%02d", myDateTime.get(Calendar.YEAR) % 2000)
            );
        }

        SendCommand("01E");

        SendCommand("01S?1");
        Log.i("PN - Settings","S?1: "+"01S?1");
        SendCommand("01H");
        Log.i("PN - Settings","H: "+"01H");


        HeartBeatStopped = false;


        return false;

    }
}
