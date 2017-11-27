package routin.fontyssocial;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.Context.LOCATION_SERVICE;

public class MapappFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Marker mLocationMarker = null;
    private Location mLocation = null;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
//            if (mLocationMarker != null) {
//                mLocationMarker.remove();
//            }
//            mLocationMarker = addMarker(location.getLatitude(), location.getLongitude(), "My location");
//            mLocation = location;
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
        View view = inflater.inflate(R.layout.fragment_mapapp, container, false);

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
        }

        // Test of our custom method to add a marker
        this.addMarker(51.441642, 5.4697225, "Eindhoven");
    }

    private void alertDialog(String title, String content, String validation){
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

    public Marker addMarker(double latitude, double longitude, String name){
        LatLng position;
        Marker marker;

        position = new LatLng(latitude, longitude);
        marker = mMap.addMarker(new MarkerOptions().position(position).title(name));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        return marker;
    }
}