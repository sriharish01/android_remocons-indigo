package com.github.rosjava.android_remocons.talker;

import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;

import com.google.common.base.Preconditions;

import org.jboss.netty.buffer.ChannelBufferOutputStream;
//import org.ros.android.view.camera.RawImageListener;
import org.ros.internal.message.MessageBuffers;
import org.ros.message.Time;
import org.ros.namespace.NameResolver;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import sensor_msgs.CameraInfo;
import sensor_msgs.CompressedImage;

/**
 * Created by sriharish on 31/1/16.
 */
public class CompressedImagePublisher1 implements RawImageListener1 {
    private final ConnectedNode connectedNode;
    private final Publisher<CompressedImage> imagePublisher;
    private final Publisher<CameraInfo> cameraInfoPublisher;
    private byte[] rawImageBuffer;
    private Camera.Size rawImageSize;
    private YuvImage yuvImage;
    private Rect rect;
    private ChannelBufferOutputStream stream;

    public CompressedImagePublisher1(ConnectedNode connectedNode) {
        this.connectedNode = connectedNode;
        NameResolver resolver = connectedNode.getResolver().newChild("camera");
        this.imagePublisher = connectedNode.newPublisher(resolver.resolve("image/compressed"), "sensor_msgs/CompressedImage");
        this.cameraInfoPublisher = connectedNode.newPublisher(resolver.resolve("camera_info"), "sensor_msgs/CameraInfo");
        this.stream = new ChannelBufferOutputStream(MessageBuffers.dynamicBuffer());
    }

    public void onNewRawImage(byte[] data, Camera.Size size) {
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(size);
        if(data != this.rawImageBuffer || !size.equals(this.rawImageSize)) {
            this.rawImageBuffer = data;
            this.rawImageSize = size;
            this.yuvImage = new YuvImage(this.rawImageBuffer, 17, size.width, size.height, (int[])null);
            this.rect = new Rect(0, 0, size.width, size.height);
        }

        Time currentTime = this.connectedNode.getCurrentTime();
        String frameId = "camera";
        CompressedImage image = (CompressedImage)this.imagePublisher.newMessage();
        image.setFormat("jpeg");
        image.getHeader().setStamp(currentTime);
        image.getHeader().setFrameId(frameId);
        Preconditions.checkState(this.yuvImage.compressToJpeg(this.rect, 50, this.stream));
        image.setData(this.stream.buffer().copy());
        this.stream.buffer().clear();
        this.imagePublisher.publish(image);
        CameraInfo cameraInfo = (CameraInfo)this.cameraInfoPublisher.newMessage();
        cameraInfo.getHeader().setStamp(currentTime);
        cameraInfo.getHeader().setFrameId(frameId);
        cameraInfo.setWidth(size.width);
        cameraInfo.setHeight(size.height);
        this.cameraInfoPublisher.publish(cameraInfo);
    }
}


