package routin.fontyssocial;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public MapEventFragment mapEventFragment;
    AddEventFragment addEventFragment;
    NotificationsFragment notificationsFragment;
    FriendsFragment friendsFragment;
    SettingsFragment settingsFragment;
    ProfileFragment profileFragment;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // The floating action button to add events
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent myIntent = new Intent(MainActivity.this, AddEventActivity.class);
                //MainActivity.this.startActivity(myIntent);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, addEventFragment).commit();
                if(fab.getDrawable().getConstantState() == view.getContext().getResources().getDrawable(R.drawable.ic_event_close, view.getContext().getTheme()).getConstantState()){
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, mapEventFragment).commit();
                    fab.show();
                    fab.setImageResource(R.drawable.ic_event_add);
                } else {
                    fab.setImageResource(R.drawable.ic_event_close);
                }
            }
        });

        // App run for the first time
        if (savedInstanceState == null){
            // We create all the fragments
            mapEventFragment = new MapEventFragment();
            addEventFragment = new AddEventFragment();
            notificationsFragment = new NotificationsFragment();
            friendsFragment = new FriendsFragment();
            settingsFragment = new SettingsFragment();
            profileFragment = new ProfileFragment();

            // We set the map fragment as default
            MenuItem item =  navigationView.getMenu().getItem(0);
            onNavigationItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_map:
                getFragmentManager().beginTransaction().replace(R.id.content_frame, mapEventFragment).commit();
                fab.show();
                fab.setImageResource(R.drawable.ic_event_add);
                break;
            case R.id.nav_notifications:
                getFragmentManager().beginTransaction().replace(R.id.content_frame, notificationsFragment).commit();
                fab.hide();
                break;
            case R.id.nav_friends:
                getFragmentManager().beginTransaction().replace(R.id.content_frame, friendsFragment).commit();
                fab.hide();
                break;
            case R.id.nav_settings:
                getFragmentManager().beginTransaction().replace(R.id.content_frame, settingsFragment).commit();
                fab.hide();
                break;
            case R.id.nav_profile:
                getFragmentManager().beginTransaction().replace(R.id.content_frame, profileFragment).commit();
                fab.hide();
                break;
            case R.id.nav_logout:
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addEvent(String name, String startDate, String startTime, String endDate, String endTime, LatLng position){
        // Restore the map fragment after adding an event
        getFragmentManager().beginTransaction().replace(R.id.content_frame, mapEventFragment).commit();
        fab.setImageResource(R.drawable.ic_event_add);
        // Place the new event on the map
        //mapEventFragment.addMarker(position, name);
        mapEventFragment.addMarker(51.441642, 5.4697225, "Eindhoven");
    }
}
