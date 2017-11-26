package routin.fontyssocial;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

public class MapappFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LocationManager mLocationManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapapp, container, false);

        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(getView(), "Localisation authorization required", Snackbar.LENGTH_INDEFINITE).setAction("LocReq", null).show();
        } else {
            mMap.setMyLocationEnabled(true);
        }

        // Test of our custom method to add a marker
        this.addMarker(0.0, 0.0, "Test");
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