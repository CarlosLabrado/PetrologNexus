package us.petrolog.nexus;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.androidplot.xy.SimpleXYSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * Created by Cesar on 6/30/13.
 */
public class G4Petrolog {

    final static int TIMEOUT_VALUE = 4;
    final static int _12_BIT_MAX = 4096;

    final static int L_LENGHT = 256;
    final static int E_LENGHT = 30;
    final static int MB_LENGHT = 66;
    final static int H_LENGHT = 23;
    final static int S_1_LENGHT = 56;
    final static int SX_LENGHT = 9;
    final static int ST_LENGHT = 9;
    final static int SK_LENGHT = 9;
    final static int SP_LENGHT = 9;
    final static int SG_LENGHT = 9;
    final static int SH_LENGHT = 24;
    final static int A21_LENGHT = 8;
    final static int A31_LENGHT = 8;
    final static int F__LENGHT = 71;


    InputStream Rx = null;
    OutputStream Tx = null;


    private boolean HeartBeatStopped = false;

    private int Step = 0;

    private SimpleXYSeries Dynagraph;

    private String S_1;
    private String SX;
    private String ST;
    private String SK;
    private String SP;
    private String SG;
    private String SH;
    private String A21;
    private String A31;
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

    /*
     * Constructor
     * Author: CCR, JCC
     *
     * */
    public G4Petrolog(BluetoothSocket socket) {
        try {
            Tx = socket.getOutputStream();
            Rx = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HeartBeatStopped = false;
    }

    public void Disconnect() {
        if (Tx == null || Rx == null) {
            /* Already Disconnected */
        } else {
            HeartBeatStopped = true;
            try {
                /* Reset all variables */
                S_1 = "";
                E = "";
                MB = "";
                H = "";
                F1 = "";
                F2 = "";
                F3 = "";
                F4 = "";
                F5 = "";
                F6 = "";
                F7 = "";
                F8 = "";
                cleanUp();
                Rx.close();
                Rx = null;
                Tx.close();
                Tx = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * This method should be called by the user every time the historical info is needed.
     * Author: CCR, JCC
     *
     * */
    public void requestPetrologHistory() {

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
    public void HeartBeat() {
        if (HeartBeatStopped) {
            Log.w("PN - HeartBeat", "HeartBeat stopped");
            return;
        } else {
            SendCommand("01MB");
            switch (Step) {
                case 0:
                    Step = 1;
                    SendCommand("01MB");
                    break;
                case 1:
                    Step = 2;
                    SendCommand("01E");
                    break;
                case 2:
                    Step = 3;
                    SendCommand("01H");
                    break;
                case 3:
                    Step = 4;
                    SendCommand("01L");
                    break;
                case 4:
                    Step = 0;
                    SendCommand("01S?1");
                    break;
                default:
                    Step = 0;
                    break;
            }
        }
    }

    /*
     * Sends and Receives serial G4 command
     * Author: CCR, JCC
     *
     * */
    private void SendCommand(String command) {
        // Tx
        try {
            Tx.flush();
            Tx.write(command.getBytes());
            Tx.write(0x0D);
        } catch (IOException e) {
            Log.e("PN - Tx", "Error! - " + e);
        } catch (NullPointerException e) {
            Log.e("PN - cleanUp", "Error! - " + e);
        }

        // Rx
        char[] commandChars;
        commandChars = command.toCharArray();
        switch (commandChars[2]) {
            case 'A':
                switch (commandChars[3]) {
                    case '2':
                        A21 = readAsciiResponse(A21_LENGHT);
                        Log.w("PN Tx", "A21 = " + A21);
                        break;
                    case '3':
                        A31 = readAsciiResponse(A31_LENGHT);
                        Log.w("PN Tx", "A31 = " + A31);
                        break;
                }
                break;
            case 'S':
                switch (commandChars[3]) {
                    case '?':
                        S_1 = readAsciiResponse(S_1_LENGHT);
                        Log.i("PN - Tx", "S?1 = " + S_1);
                        break;
                    case 'X':
                        SX = readAsciiResponse(SX_LENGHT);
                        Log.w("PN - Tx", "SX = " + SX);
                        break;
                    case 'T':
                        ST = readAsciiResponse(ST_LENGHT);
                        Log.w("PN - Tx", "ST = " + ST);
                        break;
                    case 'K':
                        SK = readAsciiResponse(SK_LENGHT);
                        Log.w("PN - Tx", "SK = " + SK);
                        break;
                    case 'P':
                        SP = readAsciiResponse(SP_LENGHT);
                        Log.w("PN - Tx", "SP = " + SP);
                        break;
                    case 'G':
                        SG = readAsciiResponse(SG_LENGHT);
                        Log.w("PN - Tx", "SG = " + SG);
                        break;
                    case 'H':
                        SH = readAsciiResponse(SH_LENGHT);
                        Log.w("PN - Tx", "SH = " + SH);
                        break;
                    default:
                        break;
                }
                break;
            case 'E':
                E = readAsciiResponse(E_LENGHT);
                Log.d("PN - Tx", "E   = " + E);
                break;
            case 'M':
                MB = readAsciiResponse(MB_LENGHT);
                Log.d("PN - Tx", "MB  = " + MB);
                break;
            case 'H':
                H = readAsciiResponse(H_LENGHT);
                Log.d("PN - Tx", "H   = " + H);
                break;
            case 'L':
                Dynagraph = processL(readBinaryResponse(L_LENGHT));
                Log.d("PN - Tx", "L   = " + Dynagraph);
                break;
            case 'F':
                switch (commandChars[3]) {
                    case '1':
                        F1 = readAsciiResponse(F__LENGHT);
                        Log.d("PN - Tx", "F1  = " + F1);
                        break;
                    case '2':
                        F2 = readAsciiResponse(F__LENGHT);
                        Log.d("PN - Tx", "F2  = " + F2);
                        break;
                    case '3':
                        F3 = readAsciiResponse(F__LENGHT);
                        Log.d("PN - Tx", "F3  = " + F3);
                        break;
                    case '4':
                        F4 = readAsciiResponse(F__LENGHT);
                        Log.d("PN - Tx", "F4  = " + F4);
                        break;
                    case '5':
                        F5 = readAsciiResponse(F__LENGHT);
                        Log.d("PN - Tx", "F5  = " + F5);
                        break;
                    case '6':
                        F6 = readAsciiResponse(F__LENGHT);
                        Log.d("PN - Tx", "F6  = " + F6);
                        break;
                    case '7':
                        F7 = readAsciiResponse(F__LENGHT);
                        Log.d("PN - Tx", "F7  = " + F7);
                        break;
                    case '8':
                        F8 = readAsciiResponse(F__LENGHT);
                        Log.d("PN - Tx", "F8  = " + F8);
                        break;
                    default:
                        break;
                }
                break;
            default:
                Log.e("PN - Tx", "Bad Command");
                break;
        }

    }

    /*
     * Clears left over data on Rx stream. Stops messages until it is done.
     * Author: CCR
     *
     * */
    private void cleanUp() {
        Boolean HeartBeatStopped_previous = HeartBeatStopped;
        HeartBeatStopped = true;
        try {
            while (true) {
                if (Rx.available() != 0) {
                    if (Rx.read() == 0x1A) {
                        Log.e("PN - cleanUp", "Data left over!!! - cleanUp Done!");
                        break;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            Log.e("PN - cleanUp", "Error! - " + e);
        } catch (NullPointerException e) {
            Log.e("PN - cleanUp", "Error! - " + e);
        }
        HeartBeatStopped = HeartBeatStopped_previous;
    }


    /*
    * Clears left over data on Rx stream. Stops messages until it is done.
    * Author: CCR
    *
    * */
    private void cleanUpBinary() {
        Boolean HeartBeatStopped_previous = HeartBeatStopped;
        HeartBeatStopped = true;
        try {
            if (Rx.available() != 0) {
                byte[] trash = new byte[512];
                Thread.sleep(500);
                Rx.read(trash);
                Log.e("PN - cleanUpBinary", "Data left over!!! - cleanUpBinary Done!");
            }
        } catch (InterruptedException e) {
            Log.e("PN - cleanUp", "Error! - " + e);
        } catch (IOException e) {
            Log.e("PN - cleanUp", "Error! - " + e);
        } catch (NullPointerException e) {
            Log.e("PN - cleanUp", "Error! - " + e);
        }
        HeartBeatStopped = HeartBeatStopped_previous;
    }

    /*
     * This reads ASCII responses from input stream.
     * Author: CCR
     *
     * */
    private String readAsciiResponse(int bytesToRead) {

        int[] buffer = new int[bytesToRead];
        int bytesRead = 0;
        int timeout = 0;

        try {
            while (bytesRead < bytesToRead) {
                if (Rx.available() != 0) {
                    buffer[bytesRead] = Rx.read();
                    bytesRead++;
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    timeout++;
                    if (timeout >= TIMEOUT_VALUE) {
                        Log.e("PN - Rx (ASCII)", "Time Out!");
                        cleanUp();
                        return "";
                    }
                }
            }

            String temp = "";
            for (int i = 0; i < buffer.length; i++) {
                temp = temp + (char) buffer[i];
            }
            cleanUp();
            return temp;

        } catch (IOException e) {
            Log.e("PN - Rx (ASCII)", "Error! - " + e);
        } catch (NullPointerException e) {
            Log.e("PN - Rx (ASCII)", "Error! - " + e);
        }

        return "";

    }

    /*
     * This reads Binary responses from input stream.
     * Author: CCR
     *
     * */
    private int[] readBinaryResponse(int bytesToRead) {

        int[] buffer = new int[bytesToRead];
        int bytesRead = 0;
        int timeout = 0;

        try {
            while (bytesRead < bytesToRead) {
                if (Rx.available() != 0) {
                    buffer[bytesRead] = Rx.read();
                    bytesRead++;
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    timeout++;
                    if (timeout >= TIMEOUT_VALUE) {
                        Log.e("PN - Rx (Binary)", "Time Out!");
                        break;
                    }
                }
            }
            cleanUpBinary();
            return buffer;

        } catch (IOException e) {
            Log.e("PN - Rx (Binary)", "Error! - " + e);
        } catch (NullPointerException e) {
            Log.e("PN - Rx (ASCII)", "Error! - " + e);
        }

        return new int[bytesRead];

    }

    /*
     * This reads BinaryL array and creates a SimpleXYSeries
     * Author: CCR
     *
     * */
    private SimpleXYSeries processL(int[] binaryL) {

        SimpleXYSeries temp = new SimpleXYSeries("Dyna");

        int[] wordL = new int[L_LENGHT / 2];

        for (int i = 0, j = 0; i < binaryL.length; i += 2, j++) {
            try {
                if ((binaryL[i] == 0xFF) && (binaryL[i + 1] == 0xFF)) {
                    break;
                } else if (binaryL[i] > 0x0F) {
                    Log.v("PN - processL", "Value out of range");
                } else {
                    wordL[j] = binaryL[i] * 256 + binaryL[i + 1];
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e("PN - processL", "Error! - " + e);
            }
        }
        for (int i = 0; i < wordL.length; i += 2) {
            if ((wordL[i] == 0) || (wordL[i + 1] == 0)) {
                Log.v("PN - processL", "Empty point");
            } else {
                temp.addLast(wordL[i + 1], wordL[i]);
                Log.v("PN - processL", " L[" + i + "] = " + wordL[i] + "," + wordL[i + 1]);
            }
        }


        return temp;
    }

    /*
     * This method gets Well Status.
     * Author: CCR, JCC
     *
     * */
    public String getWellStatus() {
        try {
            byte temp = Byte.valueOf(E.substring(24, 25), 16);
            bitState bit = new bitState();
            if (bit.getBitState(temp, 3)) {
                return "Stopped";
            } else {
                return "Running";
            }
        } catch (StringIndexOutOfBoundsException e) {
            return "Empty - String Out of Bounds";
        } catch (NullPointerException e) {
            return "Empty - Null Pointer";
        } catch (NumberFormatException e) {
            return "Empty - Number Format";
        }
    }

    /*
     * This method gets Pump Off Flag.
     * Author: CCR, JCC
     *
     * */
    public String getPumpOffStatus() {
        try {
            byte temp = Byte.valueOf(E.substring(16, 17), 16);
            bitState bit = new bitState();
            if (bit.getBitState(temp, 2)) {
                return "Yes";
            } else {
                return "No";
            }
        } catch (StringIndexOutOfBoundsException e) {
            return "Empty - String Out of Bounds";
        } catch (NullPointerException e) {
            return "Empty - Null Pointer";
        } catch (NumberFormatException e) {
            return "Empty - Number Format";
        }
    }

    /*
     * This method gets Strokes this Cycle.
     * Author: CCR, JCC
     *
     * */
    public int getStrokesThis() {
        try {
            return Integer.valueOf(MB.substring(25, 29), 16);
        } catch (StringIndexOutOfBoundsException e) {
            return -1;
        } catch (NullPointerException e) {
            return -2;
        } catch (NumberFormatException e) {
            return -3;
        }
    }

    /*
     * This method gets Strokes Last Cycle.
     * Author: CCR, JCC
     *
     * */
    public int getStrokesLast() {
        try {
            return Integer.valueOf(MB.substring(42, 46), 16);
        } catch (StringIndexOutOfBoundsException e) {
            return -1;
        } catch (NullPointerException e) {
            return -2;
        } catch (NumberFormatException e) {
            return -3;
        }
    }

    /*
     * This method gets Minutes to Next Start.
     * Author: CCR, JCC
     *
     * */
    public int getMinNextStart() {
        try {
            return 255 - Integer.valueOf(MB.substring(50, 52), 16);
        } catch (StringIndexOutOfBoundsException e) {
            return -1;
        } catch (NullPointerException e) {
            return -2;
        } catch (NumberFormatException e) {
            return -3;
        }
    }

    /*
     * This method gets Minutes to Next Start.
     * Author: CCR, JCC
     *
     * */
    public int getSecNextStart() {
        try {
            return 255 - Integer.valueOf(MB.substring(52, 54), 16);
        } catch (StringIndexOutOfBoundsException e) {
            return -1;
        } catch (NullPointerException e) {
            return -2;
        } catch (NumberFormatException e) {
            return -3;
        }
    }

    /*
     * This method gets Pump Off Strokes (Setting).
     * Author: CCR, JCC
     *
     * */
    public int getPumpOffStrokesSetting() {
        try {
            int PumpOffStrokes = Integer.valueOf(S_1.substring(11, 13), 16);
            return PumpOffStrokes;
        } catch (StringIndexOutOfBoundsException e) {
            return -1;
        } catch (NullPointerException e) {
            return -2;
        } catch (NumberFormatException e) {
            return -3;
        }
    }

    /*
     * This method gets Fillage (Setting).
     * Author: CCR, JCC
     *
     * */
    public int getFillageSetting() {
        try {
            int Fillage = (Integer.valueOf(S_1.substring(33, 35), 16)) * 10;
            return Fillage;
        } catch (StringIndexOutOfBoundsException e) {
            return -1;
        } catch (NullPointerException e) {
            return -2;
        } catch (NumberFormatException e) {
            return -3;
        }
    }

    /*
     * This method gets Pump Up Strokes (Setting).
     * Author: CCR
     *
     * */
    public int getPumpUpSetting() {
        try {
            int PumpUpStrokes = Integer.valueOf(S_1.substring(9, 11), 16);
            return PumpUpStrokes;
        } catch (StringIndexOutOfBoundsException e) {
            return -1;
        } catch (NullPointerException e) {
            return -2;
        } catch (NumberFormatException e) {
            return -3;
        }
    }

    /*
     * This method gets Current Timeout (Setting).
     * Author: CCR
     *
     * */
    public int getCurrentTimeoutSetting() {
        try {
            int CurrentTimeout = 256 - Integer.valueOf(S_1.substring(29, 31), 16);
            return CurrentTimeout;
        } catch (StringIndexOutOfBoundsException e) {
            return -1;
        } catch (NullPointerException e) {
            return -2;
        } catch (NumberFormatException e) {
            return -3;
        }
    }

    /*
     * This method gets AutomaticTO (Setting).
     * Author: CCR
     *
     * */
    public String getAutomaticTOSetting() {
        try {
            int AutomaticTO = Integer.valueOf(S_1.substring(37, 39), 16);
            if (AutomaticTO != 0) {
                return "Yes";
            } else {
                return "No";
            }
        } catch (StringIndexOutOfBoundsException e) {
            return "StringOutOfBounds";
        } catch (NullPointerException e) {
            return "NullPointer";
        } catch (NumberFormatException e) {
            return "NumberFormat";
        }
    }

    /*
     * This method gets Yesterday's Runtime in seconds.
     * Author: CCR
     *
     * */
    public int getYesterdayRuntime() {
        try {
            int YesterdayRuntime = Integer.valueOf(S_1.substring(45, 49), 16);
            return YesterdayRuntime * 2;
        } catch (StringIndexOutOfBoundsException e) {
            return -1;
        } catch (NullPointerException e) {
            return -2;
        } catch (NumberFormatException e) {
            return -3;
        }
    }

    /*
     * This method gets Today's Runtime in seconds.
     * Author: CCR
     *
     * */
    public int getTodayRuntime() {
        try {
            int TodayRuntime = Integer.valueOf(MB.substring(38, 42), 16);
            if (getOverflowHrsToday()) {
                return TodayRuntime + 65535;
            } else {
                return TodayRuntime;
            }

        } catch (StringIndexOutOfBoundsException e) {
            return -1;
        } catch (NullPointerException e) {
            return -2;
        } catch (NumberFormatException e) {
            return -3;
        }
    }

    /*
     * This method gets Hours Today Overflow bit.
     * Author: CCR
     *
     * */
    public boolean getOverflowHrsToday() {
        try {
            byte temp = Byte.valueOf(E.substring(16, 17), 16);
            bitState bit = new bitState();
            if (bit.getBitState(temp, 1)) {
                return true;
            } else {
                return false;
            }
        } catch (StringIndexOutOfBoundsException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /*
     * This method gets Petrolog Clock.
     * Author: CCR
     *
     * */
    public String getPetrologClock() {
        int foo = 0;
        try {
            /* Validate Usage */
            int totalSecToday = Integer.valueOf(H.substring(3).substring(0, 2)) * 3600 +
                    Integer.valueOf(H.substring(3).substring(3, 5)) * 60 +
                    Integer.valueOf(H.substring(3).substring(6, 8));
            foo = totalSecToday;
            return H.substring(3, H.length() - 2);
        } catch (StringIndexOutOfBoundsException e) {
            return "Empty - String Out of Bounds - " + foo;
        } catch (NullPointerException e) {
            return "Empty - Null Pointer - " + foo;
        } catch (NumberFormatException e) {
            return "Empty - Number Format - " + foo;
        }
    }

    /*
     * This method gets the latest Dyna from L command.
     * Author: CCR
     *
     * */
    public SimpleXYSeries getDynagraph() {
        return Dynagraph;
    }

    /*
     * This method gets a day (from the last 31) runtime in seconds.
     * Author: CCR
     *
     * */
    public int getHistoricalRuntime(int day) {
        final int HEADER = 4;
        final int CHARS_IN_DAY = 16;
        final int OFFSET_IN_DAY = 6;
        final int SEC_LENGHT = 4;
        int HistoricalRuntime;


        if (day < 0 || day > 31) {
            /* Parameter Error */
            return -4;
        } else {
            if (day <= 4) {
                int location = HEADER + ((day - 1) * CHARS_IN_DAY) + OFFSET_IN_DAY;

                try {
                    HistoricalRuntime = Integer.valueOf(F1.substring(location, location + SEC_LENGHT), 16);
                } catch (StringIndexOutOfBoundsException e) {
                    return -1;
                } catch (NullPointerException e) {
                    return -2;
                } catch (NumberFormatException e) {
                    return -3;
                }

            } else if (day <= 8) {

                int location = HEADER + (((day - 4) - 1) * CHARS_IN_DAY) + OFFSET_IN_DAY;
                try {
                    HistoricalRuntime = Integer.valueOf(F2.substring(location, location + SEC_LENGHT), 16);
                } catch (StringIndexOutOfBoundsException e) {
                    return -1;
                } catch (NullPointerException e) {
                    return -2;
                } catch (NumberFormatException e) {
                    return -3;
                }

            } else if (day <= 12) {
                int location = HEADER + (((day - 8) - 1) * CHARS_IN_DAY) + OFFSET_IN_DAY;
                try {
                    HistoricalRuntime = Integer.valueOf(F3.substring(location, location + SEC_LENGHT), 16);
                } catch (StringIndexOutOfBoundsException e) {
                    return -1;
                } catch (NullPointerException e) {
                    return -2;
                } catch (NumberFormatException e) {
                    return -3;
                }

            } else if (day <= 16) {
                int location = HEADER + (((day - 12) - 1) * CHARS_IN_DAY) + OFFSET_IN_DAY;
                try {
                    HistoricalRuntime = Integer.valueOf(F4.substring(location, location + SEC_LENGHT), 16);
                } catch (StringIndexOutOfBoundsException e) {
                    return -1;
                } catch (NullPointerException e) {
                    return -2;
                } catch (NumberFormatException e) {
                    return -3;
                }

            } else if (day <= 20) {
                int location = HEADER + (((day - 16) - 1) * CHARS_IN_DAY) + OFFSET_IN_DAY;
                try {
                    HistoricalRuntime = Integer.valueOf(F5.substring(location, location + SEC_LENGHT), 16);
                } catch (StringIndexOutOfBoundsException e) {
                    return -1;
                } catch (NullPointerException e) {
                    return -2;
                } catch (NumberFormatException e) {
                    return -3;
                }

            } else if (day <= 24) {
                int location = HEADER + (((day - 20) - 1) * CHARS_IN_DAY) + OFFSET_IN_DAY;
                try {
                    HistoricalRuntime = Integer.valueOf(F6.substring(location, location + SEC_LENGHT), 16);
                } catch (StringIndexOutOfBoundsException e) {
                    return -1;
                } catch (NullPointerException e) {
                    return -2;
                } catch (NumberFormatException e) {
                    return -3;
                }

            } else if (day <= 28) {
                int location = HEADER + (((day - 24) - 1) * CHARS_IN_DAY) + OFFSET_IN_DAY;
                try {
                    HistoricalRuntime = Integer.valueOf(F7.substring(location, location + SEC_LENGHT), 16);
                } catch (StringIndexOutOfBoundsException e) {
                    return -1;
                } catch (NullPointerException e) {
                    return -2;
                } catch (NumberFormatException e) {
                    return -3;
                }

            } else {
                int location = HEADER + (((day - 28) - 1) * CHARS_IN_DAY) + OFFSET_IN_DAY;
                try {
                    HistoricalRuntime = Integer.valueOf(F8.substring(location, location + SEC_LENGHT), 16);
                } catch (StringIndexOutOfBoundsException e) {
                    return -1;
                } catch (NullPointerException e) {
                    return -2;
                } catch (NumberFormatException e) {
                    return -3;
                }

            }
            if (HistoricalRuntime < 43200) {
                return HistoricalRuntime * 2;
            } else {
                return 0;
            }
        }
    }

    /*
     * This method gets the latest running fillage.
     * Author: CCR, JCC
     *
     * */
    public int getCurrentFillage() {
        try {
            return (Integer.valueOf(E.substring(6, 8), 16));
        } catch (StringIndexOutOfBoundsException e) {
            return -1;
        } catch (NullPointerException e) {
            return -2;
        } catch (NumberFormatException e) {
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
    public boolean setSettings(String clock, int pu, int po, int fillage, int to, boolean ato) {

        HeartBeatStopped = true;
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SendCommand("01E");

        /* Pump Off */
        SendCommand("01SK" + String.format("%02x", po));
        Log.i("PN - Settings", "PO: " + "01SK" + String.format("%02x", po));

        /* Fillage */
        int toSend = fillage / 10;
        if (toSend < 2) {
            toSend = 2;
        }
        if (toSend > 9) {
            toSend = 9;
        }

        /* Pump Up */
        SendCommand("01SX" + String.format("%02x", pu));
        Log.i("PN - Settings", "PU: " + "01SX" + String.format("%02x", pu));


        SendCommand("01ST" + String.format("%02x", toSend));
        Log.i("PN - Settings", "Fillage: " + "01ST" + String.format("%02x", toSend));

        /* Time Out */
        if (to > 0 && to < 255) {
            SendCommand("01SP" + Integer.toHexString(256 - to).toUpperCase());
            Log.i("PN - Settings", "TO: " + "01SP" + String.format("%02x", 256 - to).toUpperCase());

        } else if (to < 0) {
            Log.e("PN - Settings", "Error: TimeOut < 0");
        } else {
            Log.e("PN - Settings", "Error: TimeOut > 255");
        }

        /* Auto TimeOut */
        if (ato) {
            SendCommand("01SG01");
            Log.i("PN - Settings", "AutoTimeOut: Auto " + "01SG01");
        } else {
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
            time = date.substring(0, 9);
            day = date.substring(12, 15);
            month = date.substring(9, 12);
            year = date.substring(15);
        } catch (StringIndexOutOfBoundsException e) {
            Log.i("PN - Settings", "Error: Clock Format");
        }


        if ((clock.matches("[0-2][0-9].[0-5][0-9].[0-5][0-9].[0-1][0-9].[0-3][0-9].[0-9][0-9]")) &&
                (Integer.valueOf(month.substring(0, 2)) <= 12)) {
            SendCommand("01SH" + time + day + month + year);
            Log.i("PN - Settings", "Date: " + "01SH" + time + day + month + year);
        } else {

            Calendar myDateTime = new GregorianCalendar();
            Log.i("PN - Settings", "Month: " + myDateTime.get(Calendar.MONTH));

            SendCommand("01SH" + String.format("%02d", myDateTime.get(Calendar.HOUR_OF_DAY)) + " "
                    + String.format("%02d", myDateTime.get(Calendar.MINUTE)) + " "
                    + String.format("%02d", myDateTime.get(Calendar.SECOND)) + " "
                    + String.format("%02d", myDateTime.get(Calendar.DAY_OF_MONTH)) + " "
                    + String.format("%02d", myDateTime.get(Calendar.MONTH) + 1) + " " /* Month starts @ 0 */
                    + String.format("%02d", myDateTime.get(Calendar.YEAR) % 2000)
            );
            Log.i("PN - Settings", "Date: " + "01SH" + String.format("%02d", myDateTime.get(Calendar.HOUR_OF_DAY)) + " "
                    + String.format("%02d", myDateTime.get(Calendar.MINUTE)) + " "
                    + String.format("%02d", myDateTime.get(Calendar.SECOND)) + " "
                    + String.format("%02d", myDateTime.get(Calendar.DAY_OF_MONTH)) + " "
                    + String.format("%02d", myDateTime.get(Calendar.MONTH) + 1) + " " /* Month starts @ 0 */
                    + String.format("%02d", myDateTime.get(Calendar.YEAR) % 2000)
            );
        }

        SendCommand("01E");

        SendCommand("01S?1");
        Log.i("PN - Settings", "S?1: " + "01S?1");
        SendCommand("01H");
        Log.i("PN - Settings", "H: " + "01H");


        HeartBeatStopped = false;


        return false;

    }

    /*
     * This method sets the POC to manual and back to auto to start the well.
     * Author: CCR, JCC
     *
     * */
    public void start() {
        try {
            HeartBeatStopped = true;
            SendCommand("01E");
            SendCommand("01A31");
            Thread.sleep(200);
            SendCommand("01A21");

            HeartBeatStopped = false;
        } catch (InterruptedException e) {
            //TODO
        }
    }

}
