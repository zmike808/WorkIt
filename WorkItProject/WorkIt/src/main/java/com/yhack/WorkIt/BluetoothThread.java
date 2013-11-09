package com.yhack.WorkIt;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * Created by Robert Rouhani on 11/9/13.
 */
public class BluetoothThread extends Thread implements Runnable
{
    private BluetoothSocket btSocket;
    private BluetoothDevice btDevice;
    private static final UUID THIS_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private InputStream btInStream;

    private String currentData;

    public BluetoothThread(BluetoothDevice device)
    {
        btDevice = device;

        BluetoothSocket tempSocket = null;
        try
        {
            tempSocket = device.createRfcommSocketToServiceRecord(THIS_UUID);
        }
        catch (IOException e)
        {
            Log.d("myapp", "Error creating the bluetooth stream");
        }

        btSocket = tempSocket;
    }

    @Override
    public void run()
    {
        try
        {
            btSocket.connect();
        }
        catch (IOException e)
        {
            Log.d("myapp", "Error connecting to the bluetooth stream");
            try
            {
                btSocket.close();
            }
            catch (IOException ee)
            {
                Log.d("myapp", "Error closing the socket");
            }
            return;
        }

        InputStream inStream = null;

        try
        {
            inStream = btSocket.getInputStream();
        }
        catch (IOException e)
        {
            Log.d("myapp", "Error opening input stream");
        }

        btInStream = inStream;

        int errorCount = 0;

        byte nextByte;
        ArrayList<Byte> packet = new ArrayList<Byte>();
        byte[] buffer;

        Log.d("myapp", "STARTING");

        while (true)
        {
            try
            {
                nextByte = (byte)btInStream.read();
                if (nextByte == '\n')
                {
                    buffer = new byte[packet.size()];
                    for (int i = 0; i < buffer.length; i++)
                        buffer[i] = packet.get(i);

                    currentData = new String(buffer);
                    Log.d("myapp", currentData);
                    packet.clear();
                }
                packet.add(nextByte);

                errorCount = 0;
            }
            catch (Exception e)
            {
                Log.e("myapp", "Something went wrong reading data!");
                errorCount++;

                if (errorCount > 100)
                {
                    Log.e("myapp", "Stopping bluetooth thread");
                    break;
                }
            }
        }
    }

    public void cancel()
    {
        try
        {
            btSocket.close();
        }
        catch (IOException e) { }
    }

    public String getCurrentData()
    {
        return currentData;
    }
}
