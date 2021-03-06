package routin.fontyssocial.fragments.event;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
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
import routin.fontyssocial.fragments.agenda.AgendaFragment;
import routin.fontyssocial.main.MainActivity;
import routin.fontyssocial.model.Event;
import routin.fontyssocial.model.User;
import routin.fontyssocial.modelGoogle.DistanceGoogleMatrix;

import static android.content.Context.LOCATION_SERVICE;

public class MapEventFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Location mLocation = null;
    private HashMap<String, Marker> markers = new HashMap<>();
    private boolean firstpassage = true;

    private FirebaseAuth auth;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference("users");
    private DatabaseReference events = database.getReference("events");

    private static DecimalFormat decimalFormat = new DecimalFormat(".##");
    private Gson gson = new Gson();

    private Map<String, Object> users = new HashMap<>();
    private HashMap<Marker, String[]> eventsInfos = new HashMap<Marker, String[]>();
    private List<String> elementClosed = new ArrayList<>();

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            mLocation = location;

            if (location != null) {
                DatabaseReference ref = database.getReference("users");
                ref.child(User.getInstance().getName()).child("latitude").setValue(location.getLatitude());
                ref.child(User.getInstance().getName()).child("longitude").setValue(location.getLongitude());
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

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_map_event, container, false);
        auth = FirebaseAuth.getInstance();
        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (permissionsGranted()) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, mLocationListener);
        }

        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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

                        for (Map.Entry<String, Object> entry : users.entrySet()) {

                            String name = entry.toString().split("=")[0];

                            if (!name.equals(User.getInstance().getName())) {
                                if (!firstpassage) {
                                    Marker marker = markers.get(name);
                                    if (marker != null) {
                                        marker.remove();
                                    }
                                }

                                //Get user map
                                Map singleUser = (Map) entry.getValue();
                                //Get phone field and append to list

                                if (singleUser.get("settings").toString().contains("true")) {
                                    double latitude = (double) singleUser.get("latitude");
                                    double longitude = (double) singleUser.get("longitude");
                                    String[] locations = new String[4];
                                    locations[0] = mLocation.getLatitude() + "";
                                    locations[1] = mLocation.getLongitude() + "";
                                    locations[2] = latitude + "";
                                    locations[3] = longitude + "";
                                    HttpGetRequest getRequest = new HttpGetRequest(latitude, longitude, name, "user", null,
                                            null, null, null, null, null, null, null);
                                    getRequest.execute(locations);
                                }
                            }
                        }
                        firstpassage = false;
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
                        if (dataSnapshot.exists()) {
                            Map<String, Object> events = (Map<String, Object>) dataSnapshot.getValue();

                            for (Map.Entry<String, Object> entry : events.entrySet()) {
                                String ID = entry.toString().split("=")[0];

                                if (!ID.equals(Event.getInstance().getID())) {
                                    Map singleEvent = (Map) entry.getValue();

                                    Map info = (Map) singleEvent.get("info");
                                    Map position = (Map) singleEvent.get("position");
                                    Map start = (Map) singleEvent.get("start");
                                    Map end = (Map) singleEvent.get("end");

                                    if ((info != null) && (info.size() == 4) && (position != null) && (position.size() == 2) && (start != null) && (start.size() == 2) && (end != null) && (end.size() == 2)) {
                                        String name = (String) info.get("name");
                                        String description = (String) info.get("description");
                                        String address = (String) info.get("address");
                                        String owner = (String) info.get("owner");
                                        double latitude = (double) position.get("latitude");
                                        double longitude = (double) position.get("longitude");
                                        String startDate = (String) start.get("date");
                                        String startTime = (String) start.get("time");
                                        String endDate = (String) end.get("date");
                                        String endTime = (String) end.get("time");

                                        String[] locations = new String[4];
                                        if (mLocation == null) {
                                            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                        }
                                        locations[0] = mLocation.getLatitude() + "";
                                        locations[1] = mLocation.getLongitude() + "";
                                        locations[2] = latitude + "";
                                        locations[3] = longitude + "";
                                        HttpGetRequest getRequest = new HttpGetRequest(latitude, longitude, name, "event", description, address, startDate, startTime, endDate, endTime, ID, owner);
                                        getRequest.execute(locations);
                                    }
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

        if (type.equals("Event")) {
            final String name = eventsInfos.get(marker)[0];
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle(marker.getTitle());
            alertDialog.setMessage(marker.getSnippet() + "\n\n" + getText(R.string.mapevent_address) + " " + eventsInfos.get(marker)[2] + "\n" + getText(R.string.mapevent_start) + " " + eventsInfos.get(marker)[4] + " " + getText(R.string.mapevent_at) + " " + eventsInfos.get(marker)[5] + "\n" + getText(R.string.mapevent_end) + " " + eventsInfos.get(marker)[6] + " " + getText(R.string.mapevent_at) + " " + eventsInfos.get(marker)[7] + "\n" + getText(R.string.mapevent_distance) + " " + eventsInfos.get(marker)[8]);
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

    public void addMarker(double latitude, double longitude, String name, String type, Double distance,
                          String description, String address, String startDate, String startTime, String endDate, String endTime,
                          String ID, String owner) {
        LatLng position;
        Marker marker;
        String distanceText = null;
        position = new LatLng(latitude, longitude);
        if(distance!=-1.0) {
            if (distance >= 1) {
                distanceText = decimalFormat.format(distance) + " km";
            } else {
                distanceText = "0" + decimalFormat.format(distance) + " km";
            }
        }else{
            distanceText="";
        }
        if(type.equals("user")) {
            marker = mMap.addMarker(new MarkerOptions().position(position)
                    .title(name)
                    .snippet(distanceText));
            markers.put(name, marker);
        } else {
            position = new LatLng(latitude, longitude);
            marker = mMap.addMarker(new MarkerOptions().position(position).title("Event: " + name).snippet(description)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            eventsInfos.put(marker, new String[]{ID, name, address, owner, startDate, startTime, endDate, endTime, distanceText});
        }
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
        return ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location location = null;
        for (String provider : providers) {
            //get list of location
            @SuppressLint("MissingPermission") Location myLocation = mLocationManager.getLastKnownLocation(provider);

            if (myLocation == null) {
                continue;
            }
            //get the closest location
            if (location == null || myLocation.getAccuracy() < location.getAccuracy()) {
                location = myLocation;
            }
        }
        return location;
    }

    public Double initDistanceGoogleMatrix(URL url) throws IOException {
        StringBuffer chainResult = new StringBuffer("");
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
        DistanceGoogleMatrix distanceGoogleMatrix = gson.fromJson(chainResult.toString(), DistanceGoogleMatrix.class);

        if (distanceGoogleMatrix.getStatus().equals("OK")) {
            if(distanceGoogleMatrix.getRows()[0].elements[0].getDistance() != null) {
                return (Double) (double) distanceGoogleMatrix.getRows()[0].elements[0].getDistance().getValue();
            }else{
                return -1000.0;
            }
        } else {
            return -1000.0;
        }
    }

    private final void createNotifications(){
        NotificationCompat.Builder builder = null;

        if (getActivity() != null) {
            if (elementClosed.size() == 1) {
                builder = new NotificationCompat.Builder(getActivity(), "fontys notification")
                        .setSmallIcon(R.drawable.ic_logo_fontys)
                        .setContentTitle(getString(R.string.notif_close))
                        .setColor(101)
                        .setContentText(getString(R.string.notif_one_obj));
            } else {
                builder = new NotificationCompat.Builder(getActivity(), "fontys notification")
                        .setSmallIcon(R.drawable.ic_logo_fontys)
                        .setContentTitle(getString(R.string.notif_close))
                        .setColor(101)
                        .setContentText(getString(R.string.notif_sev_obj));
            }
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

            //LED
            builder.setLights(Color.RED, 1000, 1000);
            Intent intent = new Intent(getActivity(), AgendaFragment.class);
            PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
            // Add as notification
            NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());
        }

    }

    public class HttpGetRequest extends AsyncTask<String, Void, Double> {
        private double latitude;
        private double longitude;
        private String name;
        private String type;
        private String description;
        private String address;
        private String startDate;
        private String startTime;
        private String endDate;
        private String endTime;
        private String ID;
        private String owner;

        HttpGetRequest(double latitude, double longitude, String name, String type,
                       String description, String address, String startDate, String startTime, String endDate, String endTime,
                       String ID, String owner) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.name = name;
            this.type = type;
            this.description = description;
            this.address = address;
            this.startDate = startDate;
            this.startTime = startTime;
            this.endDate = endDate;
            this.endTime = endTime;
            this.ID = ID;
            this.owner = owner;
        }

        @Override
        protected Double doInBackground(String... params) {
            try {
                URL url = null;
                URL url2 = null;
//                String apiKeyMapDistance = "AIzaSyBnTtrn-E0kKiWVJBpAFna1sC9L6Xy9b6A";
                String apiKeyMapDistance="AIzaSyBZEgS-tRchA1Zg_-LPTpGLDJzybGV1amA";
                url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="
                        + params[0] + "," + params[1] + "&destinations=" + params[2] + "," + params[3] + "&mode=driving&key=" + apiKeyMapDistance);

                url2 = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="
                        + params[0] + "," + params[1] + "&destinations=" + params[2] + "," + params[3] + "&mode=walking&key=" + apiKeyMapDistance);
                Double walkingResult = initDistanceGoogleMatrix(url2);
                return walkingResult;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Double result) {
            super.onPostExecute(result);
            if (result < 100&&result !=-1000.0) {
                elementClosed.add(name);
                createNotifications();
            } else {
                if (elementClosed.contains(name)) {
                    elementClosed.remove(name);
                }
            }
            addMarker(latitude, longitude, name, type, result / 1000, description, address, startDate, startTime, endDate, endTime, ID, owner);
        }
    }
}