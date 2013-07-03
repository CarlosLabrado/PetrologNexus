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
public class G4_Petrolog {

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
    public G4_Petrolog(BluetoothSocket socket){
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
               return "On";
            }else{
               return "Off";
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
            int OnOff = Integer.valueOf(E.substring(21,22),16);
            if(OnOff%2 == 0){
                return "Normal";
            }else{
                return "Pump Off";
            }
        } catch (StringIndexOutOfBoundsException e){
            return "Empty - String Out of Bounds";
        } catch (NullPointerException e){
            return "Empty - Null Pointer";
        } catch (NumberFormatException e){
            return "Empty - Number Format";
        }
    }
}
