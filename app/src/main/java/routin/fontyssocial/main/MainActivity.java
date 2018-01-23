package routin.fontyssocial.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import routin.fontyssocial.R;
import routin.fontyssocial.fragments.event.AddEventFragment;
import routin.fontyssocial.fragments.friends.FriendsFragment;
import routin.fontyssocial.fragments.event.MapEventFragment;
import routin.fontyssocial.fragments.agenda.AgendaFragment;
import routin.fontyssocial.fragments.profile.ProfileFragment;
import routin.fontyssocial.login.LoginActivity;
import routin.fontyssocial.model.User;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public MapEventFragment mapEventFragment;
    public AddEventFragment addEventFragment;
    public AgendaFragment agendaFragment;
    public FriendsFragment friendsFragment;
    public ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(routin.fontyssocial.R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Permissions check
        checkPermissions();

        // App run for the first time
        if (savedInstanceState == null){
            // We create all the fragments
            mapEventFragment = new MapEventFragment();
            addEventFragment = new AddEventFragment();
            agendaFragment = new AgendaFragment();
            friendsFragment = new FriendsFragment();
            profileFragment = new ProfileFragment();

            // We set the map fragment as default
            MenuItem item =  navigationView.getMenu().getItem(0);
            onNavigationItemSelected(item);
        }

        // Handler used because the User instance is not available immediately
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Add the username to the logout text
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                Menu navMenu = navigationView.getMenu();
                MenuItem logoutItem = navMenu.findItem(R.id.nav_logout);
                logoutItem.setTitle(getString(R.string.action_logout) + " (" + User.getInstance().getName() + ")");
            }
        }, 5000);
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
                break;
            case R.id.nav_agenda:
                getFragmentManager().beginTransaction().replace(R.id.content_frame, agendaFragment).commit();
                break;
            case R.id.nav_friends:
                getFragmentManager().beginTransaction().replace(R.id.content_frame, friendsFragment).commit();
                break;
            case R.id.nav_profile:
                getFragmentManager().beginTransaction().replace(R.id.content_frame, profileFragment).commit();
                break;
            case R.id.nav_logout:
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void alertDialog(String title, String content, String validation){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
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

    public void checkPermissions(){
        if ((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
            this.alertDialog(getString(R.string.permission_locationerror), getString(R.string.permission_locationerrordesc), getString(R.string.permission_locationok));
        }
    }

}
