package routin.fontyssocial;

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
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import model.User;
import modelGoogle.DistanceGoogleMatrix;

import static android.content.Context.LOCATION_SERVICE;

public class MapEventFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Marker mLocationMarker = null;
    private Location mLocation = null;
    private FirebaseAuth auth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference("users");
    private Map<String,Object> users = new HashMap<>();
    private String apiKeyMapDistance="AIzaSyDeKvB4UAJRjhhGgQg_G5EmcA7OHQQgRMM";
    private static DecimalFormat decimalFormat = new DecimalFormat(".##");
    private Gson gson = new Gson();
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            mLocation = location;
            mMap.clear();
            ref.child(User.getInstance().getName()).child("latitude").setValue(location.getLatitude());
            ref.child(User.getInstance().getName()).child("longitude").setValue(location.getLongitude());

            for (Map.Entry<String, Object> entry : users.entrySet()){

                String name = entry.toString().split("=")[0];

                if (!name.equals(User.getInstance().getName())){
                    //Get user map
                    Map singleUser = (Map) entry.getValue();
                    //Get phone field and append to list

                    double latitude = (double) singleUser.get("latitude");
                    double longitude = (double) singleUser.get("longitude");
                    Double distance= null;
                    try {
                        String [] locations = new String[5];
                        locations[0]=mLocation.getLatitude()+"";
                        locations[1]=mLocation.getLongitude()+"";
                        locations[2]=latitude+"";
                        locations[3]=longitude+"";
                        HttpGetRequest getRequest = new HttpGetRequest(latitude, longitude, name);
                        distance = Double.parseDouble(getRequest.execute(locations).get());
                    } catch ( InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    addMarker(latitude, longitude, name,distance);
                }
            }


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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_event, container, false);
        auth = FirebaseAuth.getInstance();
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Location must be enabled
            this.alertDialog("Location error", "Location authorization is required to work correctly. Please enable it to use this app.", "OK");
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, mLocationListener);
        }
        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Location must be enabled
        } else {
            mMap.setMyLocationEnabled(true);
            this.zoomToPosition();
        }

        ref.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        users = (Map<String,Object>) dataSnapshot.getValue();

                        for (Map.Entry<String, Object> entry : users.entrySet()){

                            String name = entry.toString().split("=")[0];

                            if (!name.equals(User.getInstance().getName())){
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

        // Test of our custom method to add a marker
        //this.addMarker(51.441642, 5.4697225, "Eindhoven");
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        return marker;
    }

    public Marker addMarker(LatLng position, String text) {
        Marker marker;

        marker = mMap.addMarker(new MarkerOptions().position(position).title(text));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        return marker;
    }

    @SuppressLint("MissingPermission") // Permission check is not needed here since this method is accessed after a permission check
    public void zoomToPosition() {
        Criteria criteria = new Criteria();
        String provider = mLocationManager.getBestProvider(criteria, false);
        mLocation = getLastKnownLocation();
        LatLng position = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
    }

    private Location getLastKnownLocation() {
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

