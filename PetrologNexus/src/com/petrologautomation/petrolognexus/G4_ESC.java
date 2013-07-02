package com.petrologautomation.petrolognexus;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Cesar on 6/30/13.
 */
public class G4_ESC {

    InputStream Rx; //= null;
    OutputStream Tx; //= null;

    final static int TIMEOUT_VALUE = 10;

    String result;

    public G4_ESC (BluetoothSocket socket){
        try {
            Tx = socket.getOutputStream();
            Rx = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            //Error!!
            return;
        }
        EnviaAlgo();
    }

    private void EnviaAlgo(){

        final int BUFFER_SIZE = 512;
        int[] buffer = new int[BUFFER_SIZE];
        int i = 1;


        result = "";
        int timeout = 0;

        try {
            // Tx
            Tx.flush();
            Tx.write("01S?1".getBytes());
            Tx.write(0x0D);
            Log.i("PN - Envio","01S?1<CR>" );

            // Rx
            do {
                if ( Rx.available() != 0 ) {
                    /* Blocking until byte Rx */
                    buffer[i] = Rx.read();
                    result = result+(char) buffer[i];
                    i++;
                }
                else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    timeout++;
                    if (timeout >= TIMEOUT_VALUE){
                        result = "Time Out!";
                        break;
                    }

                }
            } while (buffer[i-1] != 0x0D);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
