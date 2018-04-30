package com.ucf.jsage.blestudent;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static android.provider.Settings.Secure.ANDROID_ID;

public class MainActivity extends AppCompatActivity {
    private Button mAdvertiseButton;
    private TextView infoText;
    private String randID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Permissions must be explicitly granted
        if(android.os.Build.VERSION.SDK_INT >= 23) {
            getPerms();
        }
        setContentView(R.layout.activity_main);

        mAdvertiseButton = (Button) findViewById(R.id.submit_button);
        infoText = (TextView) findViewById(R.id.info_text);

        //check if BLE Advertising is supported by this device
        if (!BluetoothAdapter.getDefaultAdapter().isMultipleAdvertisementSupported()) {
            Toast.makeText(this, "Multiple advertisement not supported", Toast.LENGTH_SHORT).show();
            //mAdvertiseButton.setEnabled( false );
        }

        //Generate the unique user ID for this device
        //This should be wrapped to catch users that don't give telephony permissions and notify them that they are required.
        randID = generateID();
        infoText.setText(randID);

        mAdvertiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                advertise(randID);
            }
        });
    }

    /**
     * Generates a pseudorandom 9-digit string from the device's unique deviceID for identification over BLE advertisement.
     * @return randID
     */
    public String generateID()
    {
        //Generating unique advertisement data
        String dID = "";

        //Device ID is too long as it currently stands.
        // I'm going to use it as a seed for a Random object.
        //I should probably change this from the telephony device id because that requires scary and obnoxious permissions.
        if(android.os.Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                dID = getSystemService(TelephonyManager.class).getDeviceId();
            else
            {
                //Give a message that telephony is a required permission.
            }
        }
        else {
            TelephonyManager tmang = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            dID = tmang.getDeviceId();
        }
        //So now we've got the device id. Time to seed a Random and get some numbers.
        Random random = new Random(Long.parseLong(dID));
        String randID = "";
        for(int i = 0; i < 9; i++)
        {
            randID = randID.concat(Integer.toString(random.nextInt(10)));
        }
        return randID;
    }


    /**
     * Advertises the device name and the given string using BLE.
     * Not all devises are capable of using BLE.
     */
    public void advertise(final String advertisement) {
        Log.d("ADVERTISEMENT", advertisement.getBytes().toString());
        BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
        //customizing the BLE advertiser
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false)
                .build();

        //UUID lets BLE identify each other
        ParcelUuid pUuid = new ParcelUuid(UUID.fromString(getString(R.string.ble_uuid)));

        byte[] messageToSend = advertisement.getBytes();
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(pUuid)
                .addServiceData(pUuid, messageToSend)
                .build();

        //These callbacks let you know if you succeeded in advertising or not
        AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.d("BLE", "Advertising Sucess");
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e("BLE", "Advertising onStartFailure: " + errorCode);
                super.onStartFailure(errorCode);
            }
        };
        //finally, begin advertising
        advertiser.startAdvertising(settings, data, advertisingCallback);
    }

    /**
     * Android M+ requires permissions to be handled like this.
     */
    @TargetApi(23)
    public void getPerms()
    {
        int hasBluetoothAdminPermission = checkSelfPermission( Manifest.permission.BLUETOOTH_ADMIN );
        int hasBluetoothPermission = checkSelfPermission( Manifest.permission.BLUETOOTH );
        int hasCoarseLocationPermission = checkSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION );
        int hasReadPhoneStatePermission = checkSelfPermission( Manifest.permission.READ_PHONE_STATE );
        List<String> permissions = new ArrayList<String>();
        if( hasBluetoothAdminPermission != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.BLUETOOTH_ADMIN );
        }

        if( hasBluetoothPermission != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.BLUETOOTH );
        }

        if( hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.ACCESS_COARSE_LOCATION );
        }

        if(hasReadPhoneStatePermission != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.READ_PHONE_STATE);
        }

        if( !permissions.isEmpty() ) {
            requestPermissions( permissions.toArray( new String[permissions.size()] ), 0 );
        }
    }
}
