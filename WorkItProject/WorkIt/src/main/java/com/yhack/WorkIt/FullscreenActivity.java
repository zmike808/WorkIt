package com.yhack.WorkIt;



import com.yhack.WorkIt.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;


    private EditText timeTextbox;
    private EditText caloriesTextbox;


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

        setContentView(R.layout.activity_fullscreen);
        StrictMode.enableDefaults();

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        View.OnTouchListener buttonCallback = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
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
                else if (btThreadLeft == null)
                {
                    connectBt();
                }

                //ImageView graph = (ImageView)findViewById(R.id.imageView);
                //r.Id.time;
                //r.Id.calories;
                //manual switching between cal and min for now
                String mode="boxing";
                int val=0;
                String given="";
                if(timeTextbox.getText().length() == 0){
                    val=Integer.parseInt(caloriesTextbox.getText().toString());
                    given="cal";}
                else{
                    val=Integer.parseInt(timeTextbox.getText().toString());
                    given="time";}
                //String given="min";
                String qry = mode+" "+val+given;
                Log.d("myapp", qry);
                WAQueryGen q = new WAQueryGen(qry);

                TextView tv = (TextView)findViewById(R.id.wolfram_output);

                //ArrayList<WAImage> images = q.getAllImages();
                ArrayList<String> strings = q.getAllText();
                String x="";
                String result="";
                if (given.equals("cal")) {x="time"; tv.setText("If you want to burn "+val+" Cal while "+mode+":\n");}
                else {x="energy expenditure"; tv.setText("If you work out for "+val+" minutes while "+mode+":\n");}

                for(String s : strings)
                {
                    String[] parts = s.split("\\|");
                    String key = parts[0].trim();
                    String value = parts[1].substring(1,parts[1].length()-1).split("fat")[0].split("  ")[0];
                    if(key.equals(x))
                    {
                        result=value;
                        if(x=="time")
                        {
                            tv.setText(tv.getText() + "You should work out for "+result);
                        }
                        else
                        {
                            tv.setText(tv.getText() + "You will burn "+result);
                        }
                        break;
                    }
                }


                return true;
            }
        };

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.start_button).setOnTouchListener(buttonCallback);
        timeTextbox = (EditText)findViewById(R.id.time);
        caloriesTextbox = (EditText)findViewById(R.id.calories);

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
    }

    private void updateTextLeft(String s)
    {
        TextView tv = (TextView)findViewById(R.id.punch_count);

        if (s.equals("P"))
        {
            numPunchesLeft++;
        }

        tv.setText("Punches: " + (numPunchesLeft + numPunchesRight));
    }

    private void updateTextRight(String s)
    {
        TextView tv = (TextView)findViewById(R.id.punch_count);

        if (s.equals("P"))
        {
            numPunchesRight++;
        }

        tv.setText("Punches: " + (numPunchesLeft + numPunchesRight));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_ENABLE_BT)
        {
            Toast.makeText(context, "Bluetooth enabled!", Toast.LENGTH_SHORT).show();
            connectBt();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };


    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
