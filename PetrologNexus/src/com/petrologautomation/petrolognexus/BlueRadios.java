package com.petrologautomation.petrolognexus;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by CCR & JC on 11/22/13.
 */
public class BlueRadios {

    InputStream Rx  = null;
    OutputStream Tx = null;
    private String Result;

    private String commandMode;
    private String store;
    private String read;
    private String dataMode;

    final static int DONE = 2;


    public BlueRadios(BluetoothSocket socket){
        try {
            Tx = socket.getOutputStream();
            Rx = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            //Error!!
            return;
        }
        /* Dummy Waaa??? */
        SendCommand("+++");
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
        int waitingForMore = 0;

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
                        waitingForMore = 0;
                    }
                    else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        waitingForMore++;
                    }
                } while (waitingForMore < DONE);
            }
            catch(NullPointerException e) {
                Log.i("PN - Rx", "Null Rx Stream");
            }

            // Process Result
            switch (command.charAt(2)){
                case '+':
                    commandMode = Result;
                    break;
                case 'S':
                    store = Result;
                    break;
                case 'R':
                    read = Result;
                    break;
                case 'M':
                    dataMode = Result;
                    break;
                default:
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
     * This method commands the BlueRadios to Command mode.
     * Author: CCR, JCC
     *
     * */
    public boolean CommandMode (){
        SendCommand("+++");
        Log.i("PN - BlueRadios","CommandMode Rx ="+commandMode);
        if (commandMode.contains("OK")){
            return true;
        }
        return false;
    }
    /*
    * This method write name to BlueRadius.
    * Author: CCR, JCC
    *
    * */
    public boolean Store (int index, String name){
        SendCommand("ATSTORE,"+index+","+name);
        Log.i("PN - BlueRadios","store Rx ="+store);
        if (store.contains("OK")){
            return true;
        }
        return false;
    }
    /*
    * This method read name from BlueRadius.
    * Author: CCR, JCC
    *
    * */
    public String Read (int index){
        SendCommand("ATREAD,"+index);
        Log.i("PN - BlueRadios","Read Rx ="+read);
        if (read.contains("OK")){
            return read.substring(7);
        }
        return "Error";
    }
    /*
    * This method return to dataMode.
    * Author: CCR, JCC
    *
    * */
    public boolean DataMode (){
        SendCommand("ATMD");
        Log.i("PN - BlueRadios","DataMode Rx ="+dataMode);
        if (dataMode.contains("OK")){
            return true;
        }
        return false;
    }

}
