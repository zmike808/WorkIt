package com.yhack.WorkIt;

import com.wolfram.alpha.*;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.*;
import android.app.*;
import android.util.*;
import android.graphics.*;
import android.view.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.bluetooth.BluetoothAdapter;
import android.widget.Toast;
import android.widget.*;

import java.io.StringBufferInputStream;
import java.util.ArrayList;

import java.util.Set;

public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    private static boolean hasBluetooth = true;

    private static BluetoothAdapter bluetoothAdapter;

    private BluetoothThread btThreadLeft;
    private Handler btHandlerLeft;

    private BluetoothThread btThreadRight;
    private Handler btHandlerRight;

    private Context context;

    private int numPunchesLeft;
    private int numPunchesRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.enableDefaults();

        btHandlerLeft = new Handler()
        {
          public void handleMessage(Message m)
          {
            updateTextLeft((String)m.obj);
          }
        };

        btHandlerRight = new Handler()
        {
            public void handleMessage(Message m)
            {
                updateTextRight((String)m.obj);
            }
        };

        context = getApplicationContext();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
        {
            Toast.makeText(context, "Bluetooth is not available on this Device", Toast.LENGTH_LONG).show();
            hasBluetooth = false;
        }
        else if (!bluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
        }
        else
        {
            connectBt();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_ENABLE_BT)
        {
            Toast.makeText(context, "Bluetooth enabled!", Toast.LENGTH_SHORT).show();
            connectBt();
        }
    }

    private void updateTextLeft(String s)
    {
        TextView tv = (TextView)findViewById(R.id.textView);

        if (s.equals("P"))
        {
            numPunchesLeft++;
        }

        tv.setText("NUMBER OF PUNCHES: " + numPunchesLeft);

        if (numPunchesLeft == 10 || numPunchesLeft == 11)
            btThreadLeft.vibe();
    }

    private void updateTextRight(String s)
    {
        TextView tv = (TextView)findViewById(R.id.textView);

        if (s.equals("P"))
        {
            numPunchesRight++;
        }

        tv.setText("NUMBER OF PUNCHES: " + numPunchesRight);

        if (numPunchesRight == 10 || numPunchesRight == 11)
            btThreadRight.vibe();
    }

    public void connectBt()
    {
        if (!hasBluetooth)
            return;

        String targetNameLeft = "HC-06";
        String targetNameRight = "Band1";
        Set<BluetoothDevice> devs = bluetoothAdapter.getBondedDevices();
        BluetoothDevice devLeft = null, devRight = null;
        if (devs != null)
        {
            for (BluetoothDevice d : devs)
            {
                if (d != null && d.getName().equals(targetNameLeft))
                {
                    devLeft = d;
                }
                if (d != null && d.getName().equals(targetNameRight))
                {
                    devRight = d;
                }
            }
        }

        //Log.d("myapp", "TEST");

        if (devLeft != null)
        {
            btThreadLeft = new BluetoothThread(devLeft, btHandlerLeft);
            btThreadLeft.start();
        }
        if (devRight != null)
        {
            btThreadRight = new BluetoothThread(devRight, btHandlerRight);
            btThreadRight.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {

            ImageView graph = (ImageView)findViewById(R.id.imageView);

            //manual switching between cal and min for now
            String mode="boxing";
            int val=1000;
            String given="min";
            String qry = mode+val+given;
            WAQueryGen q = new WAQueryGen(qry);
            //WAQueryGen q = new WAQueryGen("boxing 1000min");

            ArrayList<WAImage> images = q.getAllImages();
            ArrayList<String> strings = q.getAllText();
            String x="";
            String result="";
            if (given.equals("cal")) {x="time"; Log.d("myapp","If you want to burn "+val+" Cal while "+mode+":\n");}
            else {x="energy expenditure"; Log.d("myapp","If you work out for "+val+" minutes while "+mode+":\n");}

            for(String s : strings)
            {
                String[] parts = s.split("\\|");
                String key = parts[0].trim();
                String value = parts[1].substring(1,parts[1].length()-1).split("fat")[0].split("  ")[0];
                //Log.d("myapp",s+"\n");
                if(key.equals(x))
                {
                    result=value;
                    if(x=="time")
                        Log.d("myapp","You should work out for: "+result);
                    else
                        Log.d("myapp","You will burn: "+result);
                    //Log.d("myapp","parsed: "+ result.split("fat")[0]);
                    break;
                }
            }

        }


    }

}
