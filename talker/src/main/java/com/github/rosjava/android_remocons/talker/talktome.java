package com.github.rosjava.android_remocons.talker;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.app.Activity;
import android.os.Looper;
import android.widget.TextView;

import geometry_msgs.Vector3;

/**
 * Created by sriharish on 12/10/15.
 */

    public class talktome extends AbstractNodeMain  {

        private String topic_name;
        public float tx,ty,tz,Gx,Gy,Gz,xxx,yyy,zzz,pitch,roll,yaw;
    public double lati1,longi1,alti1;


        public talktome() {
            this.topic_name = "chatter";



        }

        public talktome(String topic) {
            this.topic_name = topic;

        }

        public GraphName getDefaultNodeName() {
            return GraphName.of("rosjava_tutorial_pubsub/talker");
        }

        public void onStart(ConnectedNode connectedNode) {

            Looper.prepare();
            final Publisher publisher = connectedNode.newPublisher("accelerometer", "geometry_msgs/Vector3");
            final Publisher publisher1 = connectedNode.newPublisher("gyroscope","geometry_msgs/Vector3");
            final Publisher publisher2 = connectedNode.newPublisher("magnetometer","geometry_msgs/Vector3");
            final Publisher publisher3 = connectedNode.newPublisher("Orientation","geometry_msgs/Vector3");
            final Publisher publisher4 = connectedNode.newPublisher("GPS","geometry_msgs/Vector3");

            connectedNode.executeCancellableLoop(new CancellableLoop() {

                protected void setup() {


                }

                protected void loop() throws InterruptedException {

                    geometry_msgs.Vector3 vect = (Vector3)publisher.newMessage();
                    geometry_msgs.Vector3 vect1 = (Vector3)publisher1.newMessage();
                    geometry_msgs.Vector3 vect2 = (Vector3)publisher2.newMessage();
                    geometry_msgs.Vector3 vect3 = (Vector3)publisher3.newMessage();
                    geometry_msgs.Vector3 vect4 = (Vector3)publisher4.newMessage();




                    vect.setX((double) Gx);
                    vect.setY((double) Gy);
                    vect.setZ((double) Gz);

                    vect1.setX((double) xxx);
                    vect1.setY((double) yyy);
                    vect1.setZ((double) zzz);

                    vect2.setX((double)tx);
                    vect2.setY((double)ty);
                    vect2.setZ((double)tz);

                    vect3.setX((double)pitch);
                    vect3.setY((double)roll);
                    vect3.setZ((double)yaw);

                    vect4.setX(lati1);
                    vect4.setY(longi1);
                    vect4.setZ(alti1);

                    publisher.publish(vect);
                    publisher1.publish(vect1);
                    publisher2.publish(vect2);
                    publisher3.publish(vect3);
                    publisher4.publish(vect4);

                    Thread.sleep(200L);
                }
            });
        }
        public void fun(float x,float y ,float z){
        this.tx=x;
        this.ty=y;
        this.tz=z;

    }
    public void fun1(float x,float y ,float z){
        this.Gx=x;
        this.Gy=y;
        this.Gz=z;

    }
    public void fun2(double lati , double longi,double alti){
        this.lati1=lati;
        this.longi1=longi;
        this.alti1=alti;

    }
    public void fun3(float x,float y ,float z){
        this.xxx=x;
        this.yyy=y;
        this.zzz=z;

    }
    public void fun4(float x,float y ,float z){
        this.pitch =x;
        this.roll =y;
        this.yaw=z;

    }
}
