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
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

public class MapEventFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Marker mLocationMarker = null;
    //private Location mLocation = null;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("users");
    DatabaseReference events = database.getReference("events");

    private HashMap<Marker, String[]> eventsInfos = new HashMap<Marker, String[]>();

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //mLocation = location;
            //mMap.clear();
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

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_event, container, false);

        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (permissionsGranted()) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, mLocationListener);
        }

        return view;
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
                        Map<String,Object> users = (Map<String,Object>) dataSnapshot.getValue();

                        for (Map.Entry<String, Object> entry : users.entrySet()){

                            String name = entry.toString().split("=")[0];

                            if (!name.equals(User.getInstance().getName())){
                                //Get user map
                                Map singleUser = (Map) entry.getValue();
                                //Get phone field and append to list

                                double latitude = (double) singleUser.get("latitude");
                                double longitude = (double) singleUser.get("longitude");
                                addMarker(latitude, longitude, name);
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
                        //Get map of users in datasnapshot
                        Map<String,Object> events = (Map<String,Object>) dataSnapshot.getValue();

                        for (Map.Entry<String, Object> entry : events.entrySet()){

                            String name = entry.toString().split("=")[0];

                            if (!name.equals(Event.getInstance().getName())){
                                //Get user map
                                Map singleEvent = (Map) entry.getValue();
                                //Get phone field and append to list

                                String description = (String) singleEvent.get("description");
                                String address = (String) singleEvent.get("address");
                                double latitude = (double) ((Map) singleEvent.get("position")).get("latitude");
                                double longitude = (double) ((Map) singleEvent.get("position")).get("longitude");
                                String startDate = (String) ((Map) singleEvent.get("start")).get("date");
                                String startTime = (String) ((Map) singleEvent.get("start")).get("time");
                                String endDate = (String) ((Map) singleEvent.get("end")).get("date");
                                String endTime = (String) ((Map) singleEvent.get("end")).get("time");

                                Marker marker = addEventMarker(latitude, longitude, name, description);
                                eventsInfos.put(marker, new String[]{name, address, startDate, startTime, endDate, endTime});
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
    public void onInfoWindowClick(Marker marker) {
        final String type = marker.getTitle().split(":")[0];
        final String name = eventsInfos.get(marker)[0];

        if(type.equals("Event")){
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle(marker.getTitle());
            alertDialog.setMessage(marker.getSnippet() + "\n\n" + R.string.mapevent_address + eventsInfos.get(marker)[1] + "\n" + R.string.mapevent_start + eventsInfos.get(marker)[2] + R.string.mapevent_at + eventsInfos.get(marker)[3] + "\n" + R.string.mapevent_end + eventsInfos.get(marker)[4] + R.string.mapevent_at + eventsInfos.get(marker)[5]);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            // todo: the event can be only removed by the owner of the event
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Remove",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            events.child(name).removeValue();
                        }
                    });
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

    public Marker addMarker(double latitude, double longitude, String text) {
        LatLng position;
        Marker marker;

        position = new LatLng(latitude, longitude);
        marker = mMap.addMarker(new MarkerOptions().position(position).title(text));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        return marker;
    }

    public Marker addMarker(LatLng position, String text) {
        Marker marker;

        marker = mMap.addMarker(new MarkerOptions().position(position).title(text));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

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
        Location location = mLocationManager.getLastKnownLocation(provider);
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
    }

    public boolean permissionsGranted(){
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}

