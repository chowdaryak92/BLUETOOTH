package com.example.bluetooth;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter adapter;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = BluetoothAdapter.getDefaultAdapter();
        button=(Button)findViewById(R.id.b5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.disable();
                Toast.makeText(getApplicationContext(),"Bluetooth Turned OFF", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter statefilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
         registerReceiver(stateReceiver,statefilter);
         IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
         registerReceiver(foundReceiver,foundFilter);
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stateReceiver);
        unregisterReceiver(foundReceiver);
    }

    public void enablebluetooth(View view) {
        if (adapter == null) {
            Toast.makeText(this, "Device not supported bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (!adapter.isEnabled()) {
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(i, 1);
            }

            if (adapter.isEnabled()) {
                Toast.makeText(this, "Blutooth is already enabled", Toast.LENGTH_LONG).show();
            }
        }
}



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==1)
        {
            if(resultCode == RESULT_OK)
            {
                Toast.makeText(this,"Bluetooth turned on",Toast.LENGTH_LONG).show();
            }

            if (requestCode==RESULT_CANCELED)
            {
                Toast.makeText(this,"Bluetooth is not turnde on",Toast.LENGTH_LONG).show();
            }
        }

        else if (requestCode ==3)
        {
          if (resultCode != RESULT_CANCELED)
          {
              Toast.makeText(this,"Device Discoverability Start",Toast.LENGTH_LONG).show();
          }
          else
          {
              Toast.makeText(this,"Device discoverability canceled",Toast.LENGTH_LONG).show();
          }
        }

    }

    BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action =intent.getAction();
            Toast.makeText(context,"Inside state chage receiver",Toast.LENGTH_LONG).show();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))
            {
                int state =intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);

               switch (state)
                {
                    case  BluetoothAdapter.STATE_ON:
                        Toast.makeText(MainActivity.this,"Bluetooth on",Toast.LENGTH_LONG).show();
                        break;

                    case  BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(MainActivity.this,"Bluetooth Turning on",Toast.LENGTH_LONG).show();
                        break;

                    case  BluetoothAdapter.STATE_OFF:
                        Toast.makeText(MainActivity.this,"Bluetooth off",Toast.LENGTH_LONG).show();
                        break;

                    case  BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(MainActivity.this,"Bluetooth Turning off",Toast.LENGTH_LONG).show();
                        break;

                }
            }

        }
        
    };


    public void getpaireddevices(View view) {

        if(adapter!=null)
        {
            Set<BluetoothDevice> pairedDevices= adapter.getBondedDevices();

            if (pairedDevices.size() > 0)
            {
                for ( BluetoothDevice device: pairedDevices)
                {
                    String deviceName =device.getName();
                    String devicehardwreaddress = device.getAddress();
                    Toast.makeText(MainActivity.this,"paired devices are"+deviceName,Toast.LENGTH_LONG).show();
                }
            }
        }


    }

    public void discoverbluetoothdevices(View view) {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);
        if (adapter!=null)
            adapter.startDiscovery();
        Toast.makeText(this,"Start Discovery"+adapter.startDiscovery(),Toast.LENGTH_LONG).show();

    }

    //create a Broadcast receiver for action found
    private  final  BroadcastReceiver foundReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName= device.getName();
                String deviceHardwareAddress =device.getAddress();//MAC ADDRESS
                Toast.makeText(MainActivity.this,deviceName,Toast.LENGTH_LONG).show();
            }
        }
    };


    public void makediscoverable(View view) {
        if (adapter.isEnabled())
        {
            Intent discoverableIntent =new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);

            startActivityForResult(discoverableIntent,3);
        }
    }
}
