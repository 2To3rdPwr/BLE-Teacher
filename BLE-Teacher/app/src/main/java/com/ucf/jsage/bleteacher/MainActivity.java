package com.ucf.jsage.bleteacher;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button mDiscoverButton;
    private TextView mText;
    private String user;
    private String pw;
    private String classPD;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler = new Handler();
    private ArrayList iDList = new ArrayList<>();

    //This callback retrieves discovered data
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result == null)
                return;
            else {
                ScanRecord record = result.getScanRecord();
                String student = "";


                //this gets the data from the advertisement broadcast in bytes
                byte[] dataByte = record.getBytes();
                //Here we convert these bytes into their ascii equivalent.
                //we start at index 22 because that is where I found the beginning of my data to be.
                for(int i = 22; i < 31; i++)
                {
                    String thisByte = Byte.toString(dataByte[i]);
                    int byteInterpret = Integer.parseInt(thisByte) - 48;
                    //byte interpret contains the integer value of the current byte.

                    //add this byte's digit to student
                    student = student.concat(byteInterpret + "");
                }
                if(!iDList.contains(student) && !student.contains("-"))
                {
                    iDList.add(student);
                    Log.d("StudentID: ", student);

                    //DEBUG: Update text field
                    String text = "";
                    for(int i = 0; i < iDList.size(); i++)
                    {
                        text.concat(iDList.get(i).toString() + "\n");
                    }
                    mText.setText(mText.getText() + student + "\n");
                }
                for(int i = 0; i < iDList.size(); i++)
                {
                    Log.d("List["+i+"]", iDList.get(i).toString());
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("BLE", "Discovery onScanFailed: " + errorCode);
            super.onScanFailed(errorCode);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Permissions must be explicitly granted
        getPerms();
        setContentView(R.layout.activity_main);

        //get login info from login activity
        user = getIntent().getStringExtra("EXTRA_USER");
        pw = getIntent().getStringExtra("EXTRA_PW");
        classPD = getIntent().getStringExtra("EXTRA_CLASS");

        //get view components
        mText = (TextView) findViewById(R.id.debug_text);
        mDiscoverButton = (Button) findViewById(R.id.discover_btn);

        //Start the RequestQueue

        //create the scanner for use in discovery
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();

        //set the button up
        mDiscoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discover();
            }
        });
    }

    /**
     * Discovers nearby BLE advertisements by this app.
     * This should work regardless of BLE advertising capabilities
     */
    public void discover() {
        //set filters so we only look for advertisements with the same UUID
        List<ScanFilter> filters = new List<ScanFilter>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<ScanFilter> iterator() {
                return null;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(@NonNull T[] ts) {
                return null;
            }

            @Override
            public boolean add(ScanFilter scanFilter) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NonNull Collection<?> collection) {
                return false;
            }

            @Override
            public boolean addAll(@NonNull Collection<? extends ScanFilter> collection) {
                return false;
            }

            @Override
            public boolean addAll(int i, @NonNull Collection<? extends ScanFilter> collection) {
                return false;
            }

            @Override
            public boolean removeAll(@NonNull Collection<?> collection) {
                return false;
            }

            @Override
            public boolean retainAll(@NonNull Collection<?> collection) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public ScanFilter get(int i) {
                return null;
            }

            @Override
            public ScanFilter set(int i, ScanFilter scanFilter) {
                return null;
            }

            @Override
            public void add(int i, ScanFilter scanFilter) {

            }

            @Override
            public ScanFilter remove(int i) {
                return null;
            }

            @Override
            public int indexOf(Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(Object o) {
                return 0;
            }

            @NonNull
            @Override
            public ListIterator<ScanFilter> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<ScanFilter> listIterator(int i) {
                return null;
            }

            @NonNull
            @Override
            public List<ScanFilter> subList(int i, int i1) {
                return null;
            }
        };
        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(UUID.fromString(getString(R.string.ble_uuid))))
                .build();
        Log.d("BLE", "Filter Built");
        filters.add(filter);
        Log.d("BLE", "Filter Added");

        //set settings for discovery
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        Log.d("BLE", "Scansettings Built");
        //start scanning
        mBluetoothLeScanner.startScan(mScanCallback);
        Log.d("BLE", "Scanner Started");
        //Callback to shut down the scanner and push to database once scanning is complete.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothLeScanner.stopScan(mScanCallback);
                SimpleDateFormat sdf = new SimpleDateFormat("MM dd yyyy");
                String classTime = sdf.format(Calendar.getInstance().getTime());

                //user, pw, classPD, classTime, iDList
                String url = "http://www.cop4331-attendance.com/zelda.php";

                Map<String, String> params = new HashMap<String, String>();
                params.put("email", "HeyDad");

                //request a string response from the url
                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST,//request method
                        url,//url
                        new JSONObject(params),//JSON Object
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.d("Database", response.getString("echo"));
                                } catch(JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {Log.d("Database", error.toString());}
                        });
                    // Add the request to the RequestQueue.
                    RequestQueue queue;
                    queue = Volley.newRequestQueue(MainActivity.this);
                    queue.add(request);
                }
            }, 10000);
    }

    /**
     * Android M+ requires permissions to be handled like this
     */
    @TargetApi(23)
    public void getPerms() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int hasBluetoothAdminPermission = checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN);
            int hasBluetoothPermission = checkSelfPermission(Manifest.permission.BLUETOOTH);
            int hasCoarseLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            int hasInternetPermission = checkSelfPermission(Manifest.permission.INTERNET);
            List<String> permissions = new ArrayList<String>();
            if (hasBluetoothAdminPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH_ADMIN);
            }
            if (hasBluetoothPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH);
            }
            if (hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (hasInternetPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.INTERNET);
            }
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 0);
            }
        }
    }
}