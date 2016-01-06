/*
 * Software License Agreement (BSD License)
 *
 * Copyright (c) 2013, Yujin Robot.
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 *  * Neither the name of Willow Garage, Inc. nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *   
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.rosjava.android_remocons.common_tools.master;

import java.util.Map;

/**
 * Mostly a clone of RobotId but generic enough to work also for concert apps.
 *
 * @author jorge@yujinrobot.com (Jorge Santos Simon)
 */
public class MasterId implements java.io.Serializable {
    private static final long serialVersionUID = -1185642483404745956L;

    private String masterUri;
    private String wifi;
    private String wifiEncryption;
    private String wifiPassword;

    public MasterId() {
    }

    public MasterId(String masterUri, String wifi, String wifiEncryption, String wifiPassword) {
        this.masterUri = masterUri;
        this.wifi = wifi;
        this.wifiEncryption = wifiEncryption;
        this.wifiPassword = wifiPassword;
    }

    public MasterId(Map<String, Object> map) {
        if (map.containsKey("URL")) {
            this.masterUri = map.get("URL").toString();
        }
        if (map.containsKey("WIFI")) {
            this.wifi = map.get("WIFI").toString();
        }
        if (map.containsKey("WIFIENC")) {
            this.wifiEncryption = map.get("WIFIENC").toString();
        }
        if (map.containsKey("WIFIPW")) {
            this.wifiPassword = map.get("WIFIPW").toString();
        }
    }

    public MasterId(String masterUri) {
        this.masterUri = masterUri;
    }

    public String getMasterUri() {
        return masterUri;
    }

    public String getWifi() {
        return wifi;
    }

    public String getWifiEncryption() {
        return wifiEncryption;
    }

    public String getWifiPassword() {
        return wifiPassword;
    }

    @Override
    public String toString() {
        String str = getMasterUri() == null ? "" : getMasterUri();
        if (getWifi() != null) {
            str = str + " On Wifi: " + getWifi();
        }
        return str;
    }

    //TODO: not needed?
    private boolean nullSafeEquals(Object a, Object b) {
        if (a == b) { //Handles case where both are null.
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        //Non-are null
        return a.equals(b);
    }

    @Override
    public boolean equals(Object o) {

        // Return true if the objects are identical.
        // (This is just an optimization, not required for correctness.)
        if (this == o) {
            return true;
        }
        // Return false if the other object has the wrong type.
        // This type may be an interface depending on the interface's specification.
        if (!(o instanceof MasterId)) {
            return false;
        }
        // Cast to the appropriate type.
        // This will succeed because of the instanceof, and lets us access private fields.
        MasterId lhs = (MasterId) o;
        return nullSafeEquals(this.masterUri, lhs.masterUri)
            && nullSafeEquals(this.wifi, lhs.wifi)
            && nullSafeEquals(this.wifiEncryption, lhs.wifiEncryption)
            && nullSafeEquals(this.wifiPassword, lhs.wifiPassword);
    }

    @Override
    public int hashCode() {
        // Start with a non-zero constant.
        int result = 17;
        // Include a hash for each field checked by equals().
        result = 31 * result + (masterUri == null ? 0 : masterUri.hashCode());
        result = 31 * result + (wifi == null ? 0 : wifi.hashCode());
        result = 31 * result + (wifiEncryption == null ? 0 : wifiEncryption.hashCode());
        result = 31 * result + (wifiPassword == null ? 0 : wifiPassword.hashCode());
        return result;
    }
}
