package routin.fontyssocial.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import routin.fontyssocial.R;
import routin.fontyssocial.main.MainActivity;
import routin.fontyssocial.model.Event;
import routin.fontyssocial.model.User;
import routin.fontyssocial.modelGoogle.DistanceGoogleMatrix;

import static android.content.Context.LOCATION_SERVICE;

public class MapEventFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private View myView;
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Marker mLocationMarker = null;
    private List<Marker> markers = new ArrayList<>();
    private Location mLocation = null;

    private FirebaseAuth auth;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference("users");
    private DatabaseReference events = database.getReference("events");

    private static DecimalFormat decimalFormat = new DecimalFormat(".##");
    private Gson gson = new Gson();

    private Map<String,Object> users = new HashMap<>();
    private HashMap<Marker, String[]> eventsInfos = new HashMap<Marker, String[]>();
    

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            mLocation = location;

            ref.child(User.getInstance().getName()).child("latitude").setValue(location.getLatitude());
            ref.child(User.getInstance().getName()).child("longitude").setValue(location.getLongitude());

            /*mMap.clear();
            markers.clear();

            for (Map.Entry<String, Object> entry : users.entrySet()) {

                String name = entry.toString().split("=")[0];

                if (!name.equals(User.getInstance().getName())) {
                    //Get user map
                    Map singleUser = (Map) entry.getValue();
                    //Get phone field and append to list

                    Long lat = (Long) singleUser.get("latitude");
                    double latitude = lat.doubleValue();
                    Long longi = (Long) singleUser.get("longitude");
                    double longitude = longi.doubleValue();
                    Double distance = null;
                    try {
                        String [] locations = new String[5];
                        locations[0]=mLocation.getLatitude()+"";
                        locations[1]=mLocation.getLongitude()+"";
                        locations[2]=latitude+"";
                        locations[3]=longitude+"";
                        HttpGetRequest getRequest = new HttpGetRequest(latitude, longitude, name);

                        String[] locations = new String[5];
                        locations[0] = mLocation.getLatitude() + "";
                        locations[1] = mLocation.getLongitude() + "";
                        locations[2] = latitude + "";
                        locations[3] = longitude + "";
                        HttpGetRequest getRequest = new HttpGetRequest();
                        distance = Double.parseDouble(getRequest.execute(locations).get());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    addMarker(latitude, longitude, name, distance);
                }
            }

            }*/
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_map_event, container, false);
        auth = FirebaseAuth.getInstance();

        // The floating action button to add events
        FloatingActionButton fab_createevent = (FloatingActionButton) myView.findViewById(R.id.fab_createevent);
        fab_createevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity ma = ((MainActivity) getActivity());
                ma.getFragmentManager().beginTransaction().replace(R.id.content_frame, ma.addEventFragment).commit();
            }
        });

        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (permissionsGranted()) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, mLocationListener);
        }

        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return myView;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);

        if (permissionsGranted()) {
            mMap.setMyLocationEnabled(true);
            this.zoomToPosition();
        }

        ref.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot

                        Map<String, Object> users = (Map<String, Object>) dataSnapshot.getValue();

                        //mMap.clear();
                        markers.clear();

                        for (Map.Entry<String, Object> entry : users.entrySet()) {

                            String name = entry.toString().split("=")[0];

                            if (!name.equals(User.getInstance().getName())) {
                                //Get user map
                                Map singleUser = (Map) entry.getValue();
                                //Get phone field and append to list

                                double latitude = (double) singleUser.get("latitude");
                                double longitude = (double) singleUser.get("longitude");
                                String[] locations = new String[5];
                                locations[0] = mLocation.getLatitude() + "";
                                locations[1] = mLocation.getLongitude() + "";
                                locations[2] = latitude + "";
                                locations[3] = longitude + "";
                                HttpGetRequest getRequest = new HttpGetRequest(latitude, longitude, name);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        events.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Map<String, Object> events = (Map<String, Object>) dataSnapshot.getValue();

                            for (Map.Entry<String, Object> entry : events.entrySet()) {

                                String ID = entry.toString().split("=")[0];

                                if (!ID.equals(Event.getInstance().getID())) {
                                    Map singleEvent = (Map) entry.getValue();

                                    String name = (String) ((Map) singleEvent.get("info")).get("name");
                                    String description = (String) ((Map) singleEvent.get("info")).get("description");
                                    String address = (String) ((Map) singleEvent.get("info")).get("address");
                                    String owner = (String) ((Map) singleEvent.get("info")).get("owner");
                                    double latitude = (double) ((Map) singleEvent.get("position")).get("latitude");
                                    double longitude = (double) ((Map) singleEvent.get("position")).get("longitude");
                                    String startDate = (String) ((Map) singleEvent.get("start")).get("date");
                                    String startTime = (String) ((Map) singleEvent.get("start")).get("time");
                                    String endDate = (String) ((Map) singleEvent.get("end")).get("date");
                                    String endTime = (String) ((Map) singleEvent.get("end")).get("time");

                                    Marker marker = addEventMarker(latitude, longitude, name, description);
                                    eventsInfos.put(marker, new String[]{ID, name, address, owner, startDate, startTime, endDate, endTime});
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        final String type = marker.getTitle().split(":")[0];
        final String name = eventsInfos.get(marker)[0];

        if (type.equals("Event")) {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle(marker.getTitle());
            alertDialog.setMessage(marker.getSnippet() + "\n\n" + getText(R.string.mapevent_address) + " " + eventsInfos.get(marker)[2] + "\n" + getText(R.string.mapevent_start) + " " + eventsInfos.get(marker)[4] + " " + getText(R.string.mapevent_at) + " " + eventsInfos.get(marker)[5] + "\n" + getText(R.string.mapevent_end) + " " + eventsInfos.get(marker)[6] + " " + getText(R.string.mapevent_at) + " " + eventsInfos.get(marker)[7]);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getText(R.string.mapevent_close),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            if(User.getInstance().getName().equals(eventsInfos.get(marker)[3])){
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getText(R.string.mapevent_remove),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            events.child(name).removeValue();
                            marker.remove();
                        }
                    });
            }
            alertDialog.show();
        }
    }

    private void alertDialog(String title, String content, String validation) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(content);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, validation,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public Marker addMarker(double latitude, double longitude, String text, Double distance) {
        LatLng position;
        Marker marker;

        position = new LatLng(latitude, longitude);

        if(distance>=1) {
            marker = mMap.addMarker(new MarkerOptions().position(position)
                    .title(text)
                    .snippet(decimalFormat.format(distance) + " km"));
        }else{
            marker = mMap.addMarker(new MarkerOptions().position(position)
                    .title(text)
                    .snippet("0"+decimalFormat.format(distance) + " km"));
        }

        return marker;
    }

    public Marker addMarker(LatLng position, String text) {
        Marker marker;

        marker = mMap.addMarker(new MarkerOptions().position(position).title(text));

        return marker;
    }

    public Marker addEventMarker(double latitude, double longitude, String name, String description) {
        LatLng position;
        Marker marker;

        position = new LatLng(latitude, longitude);
        marker = mMap.addMarker(new MarkerOptions().position(position).title("Event: " + name).snippet(description)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        return marker;
    }

    @SuppressLint("MissingPermission")
    public void zoomToPosition() {
        Criteria criteria = new Criteria();
        String provider = mLocationManager.getBestProvider(criteria, false);
        mLocation = getLastKnownLocation();
        LatLng position = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
    }

    public boolean permissionsGranted() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private Location getLastKnownLocation(){
        mLocationManager = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location location = null;
        for (String provider : providers) {
            //get list of location
            @SuppressLint("MissingPermission") Location myLocation = mLocationManager.getLastKnownLocation(provider);

            if (myLocation == null) {
                continue;
            }
            //get the closest location
            if (location==null||myLocation.getAccuracy()<location.getAccuracy()) {
                location = myLocation;
            }
        }
        return location;
    }

    public class HttpGetRequest extends AsyncTask<String, Void, String> {

        private double latitude;
        private double longitude;
        private String name;

        public HttpGetRequest(double latitude,double longitude, String name){
            this.latitude=latitude;
            this.longitude=longitude;
            this.name=name;
        }

        @Override
        protected String doInBackground(String[] params) {
            StringBuffer chainResult = new StringBuffer("");
            try {
                URL url = null;
                String apiKeyMapDistance = "AIzaSyDeKvB4UAJRjhhGgQg_G5EmcA7OHQQgRMM";
                url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="
                        + params[0] + "," + params[1] + "&destinations=" + params[2] + "," + params[3] + "&key=" + apiKeyMapDistance);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                InputStream inputStream = connection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    chainResult.append(line);
                }
                DistanceGoogleMatrix distanceGoogleMatrix =gson.fromJson(chainResult.toString(),DistanceGoogleMatrix.class);
                Double d= distanceGoogleMatrix.getRows()[0].elements[0].getDistance().getValue()/(double)1000;
                return d+"";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Double distance = Double.parseDouble(result);
            addMarker(latitude, longitude, name,distance);
        }
    }
}

