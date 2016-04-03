package com.github.rosjava.android_remocons.listener;
import org.json.JSONObject;
import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.BitmapFromImage;
import org.ros.android.view.RosImageView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.github.rosjava.android_remocons.common_tools.apps.RosAppActivity;

import org.ros.android.MessageCallable;
import org.ros.android.view.RosTextView;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.io.IOException;
import java.io.Serializable;

public class Listener extends RosAppActivity
{
    private Toast    lastToast;
    private ConnectedNode node;
    private RosTextView<std_msgs.String> rosTextView;
    private RosTextView<std_msgs.String> rosTextView1;
    private RosImageView<sensor_msgs.CompressedImage> rosImageView;
    private String lat;
    private String lng;
    public Listener()
    {
        super("Listener", "Listener");
    }
    public Button btn;
    public MyCallbackInterface mCallback;
    Intent in;
    public Listener listener =this;
    public SerialClass serialClass=new SerialClass();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setDefaultMasterName(getString(R.string.default_robot));
        setDashboardResource(R.id.top_bar);
        setMainWindowResource(R.layout.main);
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor)
    {
       // String chatterTopic = remaps.get(getString(R.string.chatter_topic));
//        String lngTopic = remaps.get("Longitude");
        String latTopic = remaps.get("LatLng");
        String camtopic =remaps.get("camera/image/compressed");

        rosImageView = (RosImageView<sensor_msgs.CompressedImage>)findViewById(R.id.camview);
        rosImageView.setTopicName(camtopic);
        rosImageView.setMessageType("sensor_msgs/CompressedImage");
        rosImageView.setMessageToBitmapCallable(new BitmapFromCompressedImage());

        btn = (Button) findViewById(R.id.button);
        in= new Intent(getApplicationContext(), MapActivity.class );
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Starting single contact activity
                Bundle bundleObject = new Bundle();
                //Intent i = new Intent(this,SecondActivity.class);
                bundleObject.putSerializable("serialClass", serialClass);
                in.putExtras(bundleObject);

                //in.putExtras("Listener",listener);
                Log.d("Listener", "received latitude [" + lat + "," + lng + "]");

                startActivity(in);
                // overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

            }
        });

        rosTextView = (RosTextView<std_msgs.String>) findViewById(R.id.text);
        rosTextView.setTopicName(latTopic);
        rosTextView.setMessageType(std_msgs.String._TYPE);
        rosTextView.setMessageToStringCallable(new MessageCallable<String, std_msgs.String>() {

            public java.lang.String call(std_msgs.String message) {
                Log.d("Listener", "received latlng [" + message.getData() + "]");
                String[] parts = message.getData().split(",");
                lat = parts[0];
                lng = parts[1];
                in.putExtra("destlat", lat);
                serialClass.setLatlng(message.getData());

                in.putExtra("destlng", lng);
                //if(mCallback!=null) {
                 //   Log.e("Listener","Success1");
                   // mCallback.onRequestCompleted(message.getData());
                //}
                return null;

            }
        });

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
            NodeConfiguration nodeConfiguration1 =
                    NodeConfiguration.newPublic(local_network_address.getHostAddress(), getMasterUri());

            nodeMainExecutor.execute(rosImageView,nodeConfiguration);


            nodeMainExecutor.execute(rosTextView, nodeConfiguration);
        } catch(InterruptedException e) {
            // Thread interruption
        } catch (IOException e) {
            // Socket problem
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(0,0,0,R.string.stop_app);
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

    public interface MyCallbackInterface {
        //its supposed to send the JSON object on request completed

        void onRequestCompleted(String latlng);
    }



    public void  registerCallback(MyCallbackInterface callback){
        mCallback = callback;
        if(mCallback!=null)
        {
            Log.d("Listener", "Success");
        }
    }
//    /**
//     * Call Toast on UI thread.
//     * @param message Message to show on toast.
//     */
//    private void showToast(final String message)
//    {
//        runOnUiThread(new Runnable()
//        {
//            @Override
//            public void run() {
//                if (lastToast != null)
//                    lastToast.cancel();
//
//                lastToast = Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG);
//                lastToast.show();
//            }
//        });
//    }

}
