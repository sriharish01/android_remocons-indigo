package com.github.rosjava.android_remocons.listener;

import android.util.Log;

import com.github.rosjava.android_remocons.common_tools.zeroconf.Logger;

import java.io.Serializable;

/**
 * Created by sriharish on 1/4/16.
 */
public class SerialClass implements Serializable{
    static String Latlng;
    String log;

   public String returnlistener()
    {
        return Latlng;
    }
  public void setLatlng(String latlng)
  {
      this.Latlng =latlng;
  }


}
