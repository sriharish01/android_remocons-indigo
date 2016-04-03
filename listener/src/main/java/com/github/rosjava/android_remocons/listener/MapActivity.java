package com.github.rosjava.android_remocons.listener;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import com.github.rosjava.android_remocons.listener.Listener.MyCallbackInterface;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends FragmentActivity  {
    private GoogleMap map;
    private SupportMapFragment fragment;
    private LatLngBounds latlngBounds;
    private Button bNavigation;
    private Polyline newPolyline;
    private boolean isTravelingToParis = false;
    private int width, height;
    private LatLng sourcelalng;
    private LatLng destlatlng;
    public Intent in;
    public String destlat;
    public String destlng;
  public   Listener  listener1;
    public SerialClass serialClass;
    String latlng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSreenDimanstions();

        fragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        map = fragment.getMap();


         in = getIntent();
        Bundle bundleObject = getIntent().getExtras();
         serialClass = (SerialClass) in.getSerializableExtra("serialClass");
        if(serialClass!=null)
        {
            Log.d("Listener","Success in passing serialclass");
        }
       latlng = serialClass.returnlistener();
        String[] parts = latlng.split(",");
        destlat=parts[0];
        destlng=parts[1];
       // Log.d("Listener",serialClass.log);
        if(listener1!=null)
        {
            Log.d("Listener","Success in passing listener");
        }
        destlat = in.getStringExtra("destlat");
         destlng = in.getStringExtra("destlng");
       // listener1.registerCallback(this);

      destlatlng = new LatLng(Double.parseDouble(destlat),Double.parseDouble(destlng));
//        destlatlng = new LatLng(11.102568 , 76.955109);

        map.setMyLocationEnabled(true);
       // sourcelalng=new LatLng(map.getMyLocation().getLatitude(),map.getMyLocation().getLongitude());
        sourcelalng=new LatLng(11.225362 , 77.119904);

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {
                //
                sourcelalng = new LatLng(arg0.getLatitude(), arg0.getLongitude());
               // com.github.rosjava.android_remocons.listener.Listener listener = new com.github.rosjava.android_remocons.listener.Listener();
                in = getIntent();
                String latlng= serialClass.returnlistener();
                String[] parts = latlng.split(",");
                destlat=parts[0];
                destlng=parts[1];
                destlatlng=new LatLng(Double.parseDouble(destlat),Double.parseDouble(destlng));
                //destlat = in.getStringExtra("destlat");
                //destlng = in.getStringExtra("destlng");
                Log.d("Listener",destlat +","+destlng);
                findDirections(sourcelalng.latitude, sourcelalng.longitude, destlatlng.latitude, destlatlng.longitude, GMapV2Direction.MODE_DRIVING);
                //  map.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
            }
        });
        findDirections(sourcelalng.latitude, sourcelalng.longitude, destlatlng.latitude, destlatlng.longitude, GMapV2Direction.MODE_DRIVING);
    }

    @Override
    protected void onResume() {

        super.onResume();
      //  latlngBounds = createLatLngBoundsObject(AMSTERDAM, PARIS);
        latlngBounds=createLatLngBoundsObject(sourcelalng,destlatlng);
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds, width, height, 20));

    }

    public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
        PolylineOptions rectLine = new PolylineOptions().width(5).color(Color.BLUE);

        for(int i = 0 ; i < directionPoints.size() ; i++)
        {
            rectLine.add(directionPoints.get(i));
        }
        if (newPolyline != null)
        {
            newPolyline.remove();
        }
        newPolyline = map.addPolyline(rectLine);
       // latlngBounds = createLatLngBoundsObject(AMSTERDAM, PARIS);
        latlngBounds=createLatLngBoundsObject(sourcelalng,destlatlng);
      //  map.moveCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds, width, height, 20));


    }

    private void getSreenDimanstions()
    {
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
    }

    private LatLngBounds createLatLngBoundsObject(LatLng firstLocation, LatLng secondLocation)
    {
        if (firstLocation != null && secondLocation != null)
        {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(firstLocation).include(secondLocation);

            return builder.build();
        }
        return null;
    }

    public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

        GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
        asyncTask.execute(map);
    }

  //  @Override
    //public void onRequestCompleted(String latlng) {
      //  String[] parts = latlng.split(",");
       // destlat = parts[0];
        //destlng = parts[1];
        //destlatlng= new LatLng(Double.parseDouble(destlat),Double.parseDouble(destlng));

    //}
}

