package com.github.rosjava.android_remocons.talker;

import android.content.Context;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rosjava.android_remocons.common_tools.apps.RosAppActivity;

import org.ros.android.MessageCallable;
import org.ros.android.view.RosTextView;
import org.ros.android.view.camera.RosCameraPreviewView;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
public class Talker extends RosAppActivity implements ConnectionCallbacks, OnConnectionFailedListener,LocationListener
{
    private int cameraId = 0;
    private RosCameraPreviewView rosCameraPreviewView;
    private Handler handy = new Handler();
    Camera.Size mSize;
    List<String> camsize = new ArrayList<String>();
    HashMap<String, Camera.Size> cameraSizes = new HashMap<String, Camera.Size>();
    String[] camerasize;
    private Toast lastToast;
    private ConnectedNode node;
    private RosTextView<std_msgs.String> rosTextView;
    private SensorManager sensorManager;
    private Sensor sensor, sensor1, sensor215,sensor4;
    private float x, y, z, zz, xx, yy, timestamp, zzz, xxx, yyy, pitch, roll, yaw;
    private double longi, lati, alti;
    com.github.rosjava.android_remocons.talker.talktome talker = new com.github.rosjava.android_remocons.talker.talktome();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 0; // 10 sec
    private static int FATEST_INTERVAL = 0; // 5 sec
    private static int DISPLACEMENT = 1; // 10 meters
    private static final String TAG = "TaG";

    Camera camera = Camera.open(cameraId);
    Camera.Parameters parameters = camera.getParameters();
    //com.github.rosjava.android_remocons.talker.talktome talker = new com.github.rosjava.android_remocons.talker.talktome();

    public Talker() {
        super("Talker", "Talker");
    }

    Runnable sizeCheckRunnable = new Runnable() {
        @Override
        public void run() {
            if (rosCameraPreviewView.getHeight() == -1 || rosCameraPreviewView.getWidth() == -1) {
                handy.postDelayed(this, 100);
            } else {
/*
                List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
                for (Camera.Size size : sizes) {
                    Log.i("TaG", "Available resolution: " + size.width + " " + size.height);

                    mSize = size;
                    String camsize1 = Integer.toString(mSize.width) + "x" + Integer.toString(mSize.height);
                    camsize.add(camsize1);
                    cameraSizes.put(camsize1, mSize);

                }


                Log.i("TaG", "Chosen resolution: " + mSize.width + " " + mSize.height);
                mSize.width=640;
                mSize.height=480;
                parameters.setPictureSize(mSize.width, mSize.height);
                //camera.setDisplayOrientation(90);
                camera.setParameters(parameters);
*/
                rosCameraPreviewView.setCamera(camera);
            }
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setDefaultMasterName(getString(R.string.default_robot));
        setDashboardResource(R.id.top_bar);
        setMainWindowResource(R.layout.main);
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
      //  sensor = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0);
      //  sensor1 = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
       // sensor215 = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensor4 = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION).get(0);
        camerasize = new String[camsize.size()];
        camsize.toArray(camerasize);
        rosCameraPreviewView = (RosCameraPreviewView) findViewById(R.id.camview);
        camera.setDisplayOrientation(90);


        if (checkPlayServices())
        {
            buildGoogleApiClient();
            createLocationRequest();
        }


    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }



    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        String chatterTopic = remaps.get(getString(R.string.chatter_topic));
        super.init(nodeMainExecutor);


        try {
            // Really horrible hack till I work out exactly the root cause and fix for
            // https://github.com/rosjava/android_remocons/issues/47
            Thread.sleep(1000);
            java.net.Socket socket = new java.net.Socket(getMasterUri().getHost(), getMasterUri().getPort());
            java.net.InetAddress local_network_address = socket.getLocalAddress();
            socket.close();
            NodeConfiguration nodeConfiguration =
                    NodeConfiguration.newPublic(local_network_address.getHostAddress(), getMasterUri());
            Log.e("Talker", "master uri [" + getMasterUri() + "]");
            // org.ros.rosjava_tutorial_pubsub.Talker talker = new org.ros.rosjava_tutorial_pubsub.Talker(getMasterNameSpace().resolve(chatterTopic).toString());
            com.github.rosjava.android_remocons.talker.talktome talker1 = new com.github.rosjava.android_remocons.talker.talktome(getMasterNameSpace().resolve("chatter").toString());
            talker = talker1;
            nodeMainExecutor.execute(talker1, nodeConfiguration);
            nodeMainExecutor.execute(rosCameraPreviewView, nodeConfiguration);
            handy.post(sizeCheckRunnable);
            // nodeMainExecutor.execute(rosTextView, nodeConfiguration);

        } catch (InterruptedException e) {
            // Thread interruption
            Log.e("Talker", "sleep interrupted");
        } catch (IOException e) {
            // Socket problem
            Log.e("Talker", "socket error trying to get networking information from the master uri");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, getString(R.string.stop_app));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case 0:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
       sensorManager.registerListener(accelerationListener, sensor4, SensorManager.SENSOR_DELAY_GAME);

        checkPlayServices();
        if (mGoogleApiClient.isConnected() )
        {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
       // sensorManager.unregisterListener(accelerationListener);
       // if (mGoogleApiClient.isConnected())
        //{
        //    mGoogleApiClient.disconnect();
       // }
        super.onStop();
    }
    @Override
    protected void onDestroy(){
        sensorManager.unregisterListener(accelerationListener);
    if (mGoogleApiClient.isConnected())
    {
        mGoogleApiClient.disconnect();
     }
        super.onDestroy();
    }

    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    private SensorEventListener accelerationListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor sensor3 = event.sensor;

                if (sensor3.getType() == Sensor.TYPE_ORIENTATION) {
                    //else {

                    yaw = event.values[0];
                    pitch = event.values[1];
                    roll = event.values[2];

                    refreshDisplay3();

                }
            }
        };
    /*
private SensorEventListener sensorEventListener = new SensorEventListener()
{
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //TODO: get values
        }else if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
            //TODO: get values
        }
    }
};*/



        private void refreshDisplay3() {
            if (true) {
                String output2 = String.format("X:%3.2f  |  Y:%3.2f  |   Z:%3.2f  ", pitch, roll, yaw);

             //   TextView text2 = (TextView) findViewById(R.id.textView7);
                //text2.setText(output2);
                talker.fun4(pitch, roll, yaw);
                displayLocation();

            }
        }

    private void displayLocation() {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

      //  TextView myLocationText = (TextView) findViewById(R.id.textView9);

        if (mLastLocation != null) {
            double lat = mLastLocation.getLatitude();
            double lng = mLastLocation.getLongitude();
            double alt = mLastLocation.getSpeed();
            String latLongString = "Lat:" + lat + "\nLong:" + lng;
           // myLocationText.setText(latLongString);
            talker.fun2(lat,lng,alt);

        } else {

           // myLocationText.setText("(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }




    @Override
    public void onConnected(Bundle bundle) {

        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }
}






