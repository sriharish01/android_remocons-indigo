package com.github.rosjava.android_remocons.talker;

import android.hardware.Camera;

/**
 * Created by sriharish on 31/1/16.
 */
interface RawImageListener1 {
    void onNewRawImage(byte[] var1, Camera.Size var2);
}
