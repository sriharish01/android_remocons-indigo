package com.github.rosjava.android_remocons.talker;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rosjava.android_remocons.common_tools.apps.RosAppActivity;

import org.ros.android.MessageCallable;
import org.ros.android.view.RosTextView;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.io.IOException;

public class Talker extends RosAppActivity
{
    private Toast lastToast;
    private ConnectedNode node;
    private RosTextView<std_msgs.String> rosTextView;
    private SensorManager sensorManager;
    private Sensor sensor,sensor1,sensor215;
    private float x,y,z,zz,xx,yy,timestamp,zzz,xxx,yyy;
    private double longi,lati,alti;
    com.github.rosjava.android_remocons.talker.talktome talker =new com.github.rosjava.android_remocons.talker.talktome();
    public Talker()
    {
        super("Talker", "Talker");
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setDefaultMasterName(getString(R.string.default_robot));
        setDashboardResource(R.id.top_bar);
        setMainWindowResource(R.layout.main);
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0);
        sensor1 = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        sensor215 =sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(sensor215==null) {
            TextView text2 = (TextView) findViewById(R.id.textView2);
            text2.setText("Gyrometer not avaiable");
        }
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        String chatterTopic = remaps.get(getString(R.string.chatter_topic));
        super.init(nodeMainExecutor);

        rosTextView = (RosTextView<std_msgs.String>) findViewById(R.id.text);
        rosTextView.setTopicName(getMasterNameSpace().resolve(chatterTopic).toString());
        rosTextView.setMessageType(std_msgs.String._TYPE);
        rosTextView.setMessageToStringCallable(new MessageCallable<String, std_msgs.String>() {
            @Override
            public java.lang.String call(std_msgs.String message) {
                Log.i("Talker", "received closed loop message [" + message.getData() + "]");
                return message.getData();
            }
        });

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
            com.github.rosjava.android_remocons.talker.talktome talker1 = new  com.github.rosjava.android_remocons.talker.talktome(getMasterNameSpace().resolve("chatter").toString());
            talker=talker1;
            nodeMainExecutor.execute(talker1, nodeConfiguration);
           // nodeMainExecutor.execute(rosTextView, nodeConfiguration);

        } catch(InterruptedException e) {
            // Thread interruption
            Log.e("Talker", "sleep interrupted");
        } catch (IOException e) {
            // Socket problem
            Log.e("Talker", "socket error trying to get networking information from the master uri");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(0, 0, 0, getString(R.string.stop_app));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case 0:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerationListener, sensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(accelerationListener, sensor1, SensorManager.SENSOR_DELAY_GAME);
       sensorManager.registerListener(accelerationListener, sensor215,SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(accelerationListener);
        super.onStop();
    }
    private SensorEventListener accelerationListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }
        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor sensor3 = event.sensor;
            if (sensor3.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                refreshDisplay();
            } else if (sensor3.getType() == Sensor.TYPE_ACCELEROMETER) {
                xx = event.values[0];
                yy = event.values[1];
                zz = event.values[2];

                refreshDisplay1();

            } else if (sensor3.getType() == Sensor.TYPE_GYROSCOPE) {
               //else {

                xxx = event.values[0];
                yyy = event.values[1];
                zzz = event.values[2];

                refreshDisplay2();

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

    private void refreshDisplay() {
        if (true) {
            String output = String.format("X:%3.2f u tesla  |  Y:%3.2f u tesla  |   Z:%3.2f u tesla ", x, y, z);
            //String output1 = String.format("Latitude:%3.2f   |  Longitude:%3.2f   |   Altitude:%3.2f  ", lati, longi, alti);
            TextView text =(TextView) findViewById(R.id.textview1);
            //TextView text1 =(TextView) findViewById(R.id.textView2);
            text.setText(output);
            //text1.setText(output1);

            talker.fun(x, y, z);



        }
    }
    private void refreshDisplay1() {
        if (true) {
            String output1 = String.format("X:%3.2f  m/s^2|  Y:%3.2f m/s^2  |   Z:%3.2f m/s^2 ", xx, yy, zz);

            TextView text1 = (TextView) findViewById(R.id.textView);
            text1.setText(output1);
            talker.fun1(xx, yy, zz);


        }
    }
    private void refreshDisplay2() {
        if (true) {
            String output2 = String.format("X:%3.2f  rad/s|  Y:%3.2f rad/s  |   Z:%3.2f rad/s ", xxx, yyy, zzz);

            TextView text2 = (TextView) findViewById(R.id.textView2);
            text2.setText(output2);
            talker.fun3(xxx, yyy, zzz);


        }
    }

    }



