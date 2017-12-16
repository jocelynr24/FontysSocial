package routin.fontyssocial;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.core.UriBuilder;
//
//import org.glassfish.jersey.client.ClientConfig;

import model.User;
import modelGoogle.DistanceGoogleMatrix;

import static android.content.Context.LOCATION_SERVICE;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MapEventFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Marker mLocationMarker = null;
    private Location mLocation = null;
    //    private WebTarget serviceTarget=null;
//    private ClientConfig config = new ClientConfig();
//    private Client client = ClientBuilder.newClient(config);
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("users");
    String apiKeyMapDistance="AIzaSyDeKvB4UAJRjhhGgQg_G5EmcA7OHQQgRMM";
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            mLocation = location;
            mMap.clear();
            ref.child(User.getInstance().getName()).child("latitude").setValue(location.getLatitude());
            ref.child(User.getInstance().getName()).child("longitude").setValue(location.getLongitude());
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

        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Location must be enabled
            this.alertDialog("Location error", "Location authorization is required to work correctly. Please enable it to use this app.", "OK");
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, mLocationListener);
        }

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
                        Map<String,Object> users = (Map<String,Object>) dataSnapshot.getValue();

                        for (Map.Entry<String, Object> entry : users.entrySet()){

                            String name = entry.toString().split("=")[0];

                            if (!name.equals(User.getInstance().getName())){
                                //Get user map
                                Map singleUser = (Map) entry.getValue();
                                //Get phone field and append to list

                                double latitude = (double) singleUser.get("latitude");
                                double longitude = (double) singleUser.get("longitude");
//                                Double distance=getDistance(mLocation.getLatitude(),mLocation.getLongitude(),latitude,longitude);
                                addMarker(latitude, longitude, name,null);
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
        marker = mMap.addMarker(new MarkerOptions().position(position).title(text+"\n"+distance));
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
        Location location = mLocationManager.getLastKnownLocation(provider);
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
    }
    private DistanceGoogleMatrix GetRequest(double latitudeUser,double longitudeUser,double latitude, double longitude) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(new HttpGet("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="
                +latitudeUser+","+longitudeUser+"&destinations="  +latitude+","+longitude+"&key="+apiKeyMapDistance));
        StatusLine statusLine = response.getStatusLine();
        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
            DistanceGoogleMatrix distanceGoogleMatrix= (DistanceGoogleMatrix) response.getEntity();
            return distanceGoogleMatrix;
        } else{
            response.getEntity().getContent().close();
            throw new IOException(statusLine.getReasonPhrase());
        }

    }
//    public Double getDistance(double latitudeUser,double longitudeUser,double latitude, double longitude){
//
//        URI baseURI = UriBuilder.fromUri("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="
//                +latitudeUser+","+longitudeUser+"&destinations="  +latitude+","+longitude+"&key="+apiKeyMapDistance).build();
//
//        serviceTarget = client.target(baseURI);
//
//        Response response = serviceTarget.request().accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML).get();
//        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
//            DistanceGoogleMatrix result = response.readEntity(DistanceGoogleMatrix.class);
//            return result.getRows()[0].elements[0].getDistance().getValue()*0.000621371;
//        }
//        return null;
//    }

}

