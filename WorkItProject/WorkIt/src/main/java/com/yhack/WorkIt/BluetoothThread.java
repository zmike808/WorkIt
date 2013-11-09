package com.yhack.WorkIt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import android.os.Handler;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Message;
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
    private OutputStream btOutStream;

    private Handler mHandler;

    private boolean vibe;

    public BluetoothThread(BluetoothDevice device, Handler handler)
    {
        btDevice = device;
        mHandler = handler;

        BluetoothSocket tempSocket = null;
        try
        {
            tempSocket = device.createRfcommSocketToServiceRecord(THIS_UUID);
        }
        catch (IOException e)
        {
            Log.d("nop", "Error creating the bluetooth stream");
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
            Log.d("nop", "Error connecting to the bluetooth stream");
            try
            {
                btSocket.close();
            }
            catch (IOException ee)
            {
                Log.d("nop", "Error closing the socket");
            }
            return;
        }

        InputStream inStream = null;
        OutputStream outStream = null;

        try
        {
            inStream = btSocket.getInputStream();
            outStream = btSocket.getOutputStream();
        }
        catch (IOException e)
        {
            Log.d("nop", "Error opening input stream");
        }

        btInStream = inStream;
        btOutStream = outStream;

        int errorCount = 0;

        byte nextByte;
       // ArrayList<Byte> packet = new ArrayList<Byte>();
        //byte[] buffer;

        while (true)
        {
            try
            {
                nextByte = (byte)btInStream.read();
                if (nextByte == 'P')
                {
                    Message m = Message.obtain();
                    m.obj = "P";
                    mHandler.sendMessage(m);
                    /*buffer = new byte[packet.size()];
                    for (int i = 0; i < buffer.length; i++)
                        buffer[i] = packet.get(i);

                    Message m = Message.obtain();
                    m.obj = new String(buffer);
                    mHandler.sendMessage(m);
                    packet.clear();*/
                }
                else
                {
                    //packet.add(nextByte);
                }

                errorCount = 0;

                if (vibe)
                {
                    Log.d("nop", "VIBE1");
                    btOutStream.write(new byte[] { 'v' });
                    vibe = false;
                }
            }
            catch (Exception e)
            {
                Log.e("nop", "Something went wrong reading data!");
                errorCount++;

                if (errorCount > 100)
                {
                    Log.e("nop", "Stopping bluetooth thread");
                    break;
                }
            }
        }
    }

    public void vibe()
    {
        this.vibe = true;
        Log.d("nop", "VIBE1");
    }

    public void cancel()
    {
        try
        {
            btSocket.close();
        }
        catch (IOException e) { }
    }
}
