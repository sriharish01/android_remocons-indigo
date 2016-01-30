package com.github.rosjava.android_remocons.talker;

import android.content.Context;
import android.util.AttributeSet;

//import org.ros.android.view.camera.CameraPreviewView;
//import org.ros.android.view.camera.CompressedImagePublisher1;

//import org.ros.android.view.camera.CompressedImagePublisher;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import com.github.rosjava.android_remocons.talker.CompressedImagePublisher1;

/**
 * Created by sriharish on 31/1/16.
 */
public class RosCameraPreviewView1 extends CameraPreviewView1 implements NodeMain {
    //com.github.rosjava.android_remocons.talker.CompressedImagePublisher1 compressedImagePublisher1 = new com.github.rosjava.android_remocons.talker.CompressedImagePublisher1();
        public RosCameraPreviewView1(Context context) {
            super(context);
        }

        public RosCameraPreviewView1(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public RosCameraPreviewView1(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public GraphName getDefaultNodeName() {
            return GraphName.of("ros_camera_preview_view");
        }

        public void onStart(ConnectedNode connectedNode) {
            this.setRawImageListener( new CompressedImagePublisher1(connectedNode));
        }

        public void onShutdown(Node node) {
        }

        public void onShutdownComplete(Node node) {
        }

        public void onError(Node node, Throwable throwable) {
        }
    }



