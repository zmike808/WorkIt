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

import java.util.ArrayList;

import java.util.Set;

public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_IS_PAIRED = 2;

    private static boolean hasBluetooth = true;

    private static BluetoothAdapter bluetoothAdapter;

    private BluetoothThread btThread;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.enableDefaults();

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

    public void connectBt()
    {
        if (!hasBluetooth)
            return;

        String targetName = "Band1";
        Set<BluetoothDevice> devs = bluetoothAdapter.getBondedDevices();
        BluetoothDevice dev = null;
        if (devs != null)
        {
            for (BluetoothDevice d : devs)
            {
                if (d != null && d.getName().equals(targetName))
                {
                    dev = d;
                    break;
                }
            }
        }

        if (dev != null)
        {
            btThread = new BluetoothThread(dev);
            btThread.start();
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
            WAQueryGen q = new WAQueryGen("pi");
            ArrayList<WAImage> images = q.getAllImages();
           

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
