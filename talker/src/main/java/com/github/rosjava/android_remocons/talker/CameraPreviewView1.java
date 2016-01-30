package com.github.rosjava.android_remocons.talker;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;

//import org.ros.android.view.camera.RawImageListener;
import com.github.rosjava.android_remocons.talker.RawImageListener1;
import org.ros.exception.RosRuntimeException;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sriharish on 31/1/16.
 */
public class CameraPreviewView1 extends ViewGroup {
    private static final double ASPECT_TOLERANCE = 0.1D;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Camera.Size previewSize;
    private byte[] previewBuffer;
    private RawImageListener1 rawImageListener;
    private CameraPreviewView1.BufferingPreviewCallback bufferingPreviewCallback;

    private void init(Context context) {
        SurfaceView surfaceView = new SurfaceView(context);
        this.addView(surfaceView);
        this.surfaceHolder = surfaceView.getHolder();
        this.surfaceHolder.addCallback(new CameraPreviewView1.SurfaceHolderCallback());
        this.surfaceHolder.setType(3);
        this.bufferingPreviewCallback = new CameraPreviewView1.BufferingPreviewCallback();
    }

    public CameraPreviewView1(Context context) {
        super(context);
        this.init(context);
    }

    public CameraPreviewView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public CameraPreviewView1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init(context);
    }

    public void releaseCamera() {
        if(this.camera != null) {
            this.camera.setPreviewCallbackWithBuffer((Camera.PreviewCallback)null);
            this.camera.stopPreview();
            this.camera.release();
            this.camera = null;
        }
    }

    public void setRawImageListener(RawImageListener1 rawImageListener) {
        this.rawImageListener = rawImageListener;
    }

    public Camera.Size getPreviewSize() {
        return this.previewSize;
    }

    public void setCamera(Camera camera) {
        Preconditions.checkNotNull(camera);
        this.camera = camera;
        this.setupCameraParameters();
        this.setupBufferingPreviewCallback();
        camera.startPreview();

        try {
            camera.setPreviewDisplay(this.surfaceHolder);
        } catch (IOException var3) {
            throw new RosRuntimeException(var3);
        }
    }

    private void setupCameraParameters() {
        Camera.Parameters parameters = this.camera.getParameters();
        List supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        this.previewSize = this.getOptimalPreviewSize(supportedPreviewSizes, this.getWidth(), this.getHeight());
        parameters.setPreviewSize(this.previewSize.width, this.previewSize.height);
        parameters.setPreviewFormat(17);
        this.camera.setParameters(parameters);
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        Preconditions.checkNotNull(sizes);
        double targetRatio = (double)width / (double)height;
        double minimumDifference = 1.7976931348623157E308D;
        Camera.Size optimalSize = null;
        Iterator i$ = sizes.iterator();

        Camera.Size size;
        while(i$.hasNext()) {
            size = (Camera.Size)i$.next();
            double ratio = (double)size.width / (double)size.height;
            if(Math.abs(ratio - targetRatio) <= 0.1D && (double)Math.abs(size.height - height) < minimumDifference) {
                optimalSize = size;
                minimumDifference = (double)Math.abs(size.height - height);
            }
        }

        if(optimalSize == null) {
            minimumDifference = 1.7976931348623157E308D;
            i$ = sizes.iterator();

            while(i$.hasNext()) {
                size = (Camera.Size)i$.next();
                if((double)Math.abs(size.height - height) < minimumDifference) {
                    optimalSize = size;
                    minimumDifference = (double)Math.abs(size.height - height);
                }
            }
        }

        Preconditions.checkNotNull(optimalSize);
        return optimalSize;
    }

    private void setupBufferingPreviewCallback() {
        int format = this.camera.getParameters().getPreviewFormat();
        int bits_per_pixel = ImageFormat.getBitsPerPixel(format);
        this.previewBuffer = new byte[this.previewSize.height * this.previewSize.width * bits_per_pixel / 8];
        this.camera.addCallbackBuffer(this.previewBuffer);
        this.camera.setPreviewCallbackWithBuffer(this.bufferingPreviewCallback);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed && this.getChildCount() > 0) {
            View child = this.getChildAt(0);
            int width = r - l;
            int height = b - t;
            int previewWidth = width;
            int previewHeight = height;
            if(this.previewSize != null) {
                previewWidth = this.previewSize.width;
                previewHeight = this.previewSize.height;
            }

            int scaledChildHeight;
            if(width * previewHeight > height * previewWidth) {
                scaledChildHeight = previewWidth * height / previewHeight;
                child.layout((width - scaledChildHeight) / 2, 0, (width + scaledChildHeight) / 2, height);
            } else {
                scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2, width, (height + scaledChildHeight) / 2);
            }
        }

    }

    private final class SurfaceHolderCallback implements SurfaceHolder.Callback {
        private SurfaceHolderCallback() {
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        public void surfaceCreated(SurfaceHolder holder) {
            try {
                if(CameraPreviewView1.this.camera != null) {
                    CameraPreviewView1.this.camera.setPreviewDisplay(holder);
                }

            } catch (IOException var3) {
                throw new RosRuntimeException(var3);
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            CameraPreviewView1.this.releaseCamera();
        }
    }

    private final class BufferingPreviewCallback implements Camera.PreviewCallback {
        private BufferingPreviewCallback() {
        }

        public void onPreviewFrame(byte[] data, Camera camera) {
            Preconditions.checkArgument(camera == CameraPreviewView1.this.camera);
            Preconditions.checkArgument(data == CameraPreviewView1.this.previewBuffer);
            if(CameraPreviewView1.this.rawImageListener != null) {
                CameraPreviewView1.this.rawImageListener.onNewRawImage(data, CameraPreviewView1.this.previewSize);
            }

            camera.addCallbackBuffer(CameraPreviewView1.this.previewBuffer);
        }
    }
}

