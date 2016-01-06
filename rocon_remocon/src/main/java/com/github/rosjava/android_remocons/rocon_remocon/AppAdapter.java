/*
* Software License Agreement (BSD License)
*
* Copyright (c) 2011, Willow Garage, Inc.
* All rights reserved.
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above
* copyright notice, this list of conditions and the following
* disclaimer in the documentation and/or other materials provided
* with the distribution.
* * Neither the name of Willow Garage, Inc. nor the names of its
* contributors may be used to endorse or promote products derived
* from this software without specific prior written permission.
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

package com.github.rosjava.android_remocons.rocon_remocon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jboss.netty.buffer.ChannelBuffer;

import java.util.ArrayList;

import rocon_interaction_msgs.Interaction;

public class AppAdapter extends BaseAdapter {
  private Context context;
  private ArrayList<Interaction> interactions;

  public AppAdapter(Context c, ArrayList<Interaction> interactions) {
    context = c;
    this.interactions = interactions;
  }

  @Override
  public int getCount() {
    if (interactions == null) {
      return 0;
    }
    return interactions.size();
  }

  @Override
  public Object getItem(int position) {
    return null;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  /**
   * Create a new View for each item referenced by the Adapter.
   */
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    View view = inflater.inflate(R.layout.app_item, null);
    Interaction interaction = interactions.get(position);
    if( interaction.getIcon().getData().array().length > 0 && interaction.getIcon().getFormat() != null &&
        (interaction.getIcon().getFormat().equals("jpeg") || interaction.getIcon().getFormat().equals("png")) ) {
    	ChannelBuffer buffer = interaction.getIcon().getData();
    	Bitmap iconBitmap = BitmapFactory.decodeByteArray( interaction.getIcon().getData().array(), buffer.arrayOffset(), buffer.readableBytes());

      if( iconBitmap != null ) {
        ImageView iv = (ImageView) view.findViewById(R.id.icon);
        iv.setImageBitmap(iconBitmap);
      }
    }
    TextView tv = (TextView) view.findViewById(R.id.name);
    tv.setText(interaction.getDisplayName());

    return view;
  }
}
