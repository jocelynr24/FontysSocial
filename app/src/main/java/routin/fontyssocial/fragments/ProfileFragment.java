package routin.fontyssocial.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Map;

import routin.fontyssocial.R;
import routin.fontyssocial.SignUpActivity;
import routin.fontyssocial.main.MainActivity;
import routin.fontyssocial.model.User;

public class ProfileFragment extends Fragment {
    private View myView;
    private String[] arraySpinner;
    private String spinner_lang;
    private EditText text_firstname;
    private EditText text_lastname;
    private RadioButton rb_shareyes;
    private RadioButton rb_shareno;

    private String firstname;
    private String lastname;
    private boolean sharepos;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference users = database.getReference("users");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_profile, container, false);

        text_firstname = (EditText) myView.findViewById(R.id.et_firstname);
        text_lastname = (EditText) myView.findViewById(R.id.et_lastname);
        rb_shareyes = (RadioButton) myView.findViewById(R.id.rb_shareyes);
        rb_shareno = (RadioButton) myView.findViewById(R.id.rb_shareno);

        // Fill the fields according to the data
        fillTheFields();

        this.arraySpinner = new String[] {
                myView.getContext().getString(R.string.profile_en), myView.getContext().getString(R.string.profile_fr)
        };
        final Spinner mySpinner = (Spinner) myView.findViewById(R.id.sp_language);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, arraySpinner);
        mySpinner.setAdapter(adapter);

        final FloatingActionButton fab_check = myView.findViewById(R.id.fab_check);
        fab_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When user apply changes

                // Profile part (get the fields)
                firstname = text_firstname.getText().toString();
                lastname = text_lastname.getText().toString();
                if(rb_shareyes.isChecked()){
                    sharepos = true;
                } else {
                    sharepos = false;
                }

                // Profile part (send to the database)
                users.child(User.getInstance().getName()).child("settings").child("firstname").setValue(firstname);
                users.child(User.getInstance().getName()).child("settings").child("lastname").setValue(lastname);
                users.child(User.getInstance().getName()).child("settings").child("share").setValue(sharepos);

                // Language part
                spinner_lang = mySpinner.getSelectedItem().toString();
                changeLanguage(getResources(), spinner_lang);

                Toast.makeText(getContext(), getString(R.string.settings_saved), Toast.LENGTH_SHORT).show();
            }
        });

        final FloatingActionButton fab_close = myView.findViewById(R.id.fab_close);
        fab_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity ma = ((MainActivity) getActivity());
                ma.getFragmentManager().beginTransaction().replace(R.id.content_frame, ma.mapEventFragment).commit();

                Toast.makeText(getContext(), getString(R.string.settings_cancelled), Toast.LENGTH_SHORT).show();
            }
        });

        return myView;
    }

    private void changeLanguage(Resources res, String lang) {
        Configuration config;
        config = new Configuration(res.getConfiguration());

        switch(lang){
            case "English":
                config.locale = new Locale("en");
                break;
            case "Anglais":
                config.locale = new Locale("en");
                break;
            case "French":
                config.locale = new Locale("fr");
                break;
            case "Français":
                config.locale = new Locale("fr");
                break;
            default:
                config.locale = new Locale("en");
                break;
        }

        getResources().updateConfiguration(config, res.getDisplayMetrics());
        Intent i = myView.getContext().getPackageManager().getLaunchIntentForPackage( myView.getContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void fillTheFields(){
        users.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Map<String, Object> users = (Map<String, Object>) dataSnapshot.getValue();

                            for (Map.Entry<String, Object> entry : users.entrySet()) {

                                String name = entry.toString().split("=")[0];

                                if (name.equals(User.getInstance().getName())) {
                                    Map user = (Map) entry.getValue();

                                    firstname = (String) ((Map) user.get("settings")).get("firstname");
                                    lastname = (String) ((Map) user.get("settings")).get("lastname");
                                    sharepos = (boolean) ((Map) user.get("settings")).get("share");

                                    text_firstname.setText(firstname);
                                    text_lastname.setText(lastname);
                                    if(sharepos){
                                        rb_shareyes.setChecked(true);
                                        rb_shareno.setChecked(false);
                                    }
                                    if(!sharepos){
                                        rb_shareyes.setChecked(false);
                                        rb_shareno.setChecked(true);
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
}