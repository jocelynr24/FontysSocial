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

import java.util.Locale;

import routin.fontyssocial.R;

public class ProfileFragment extends Fragment {
    private View myView;
    private String[] arraySpinner;
    private String spinner_lang;
    private EditText text_firstname;
    private EditText text_lastname;
    private RadioButton rb_shareyes;
    private RadioButton rb_shareno;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_profile, container, false);

        text_firstname = (EditText) myView.findViewById(R.id.et_firstname);
        text_lastname = (EditText) myView.findViewById(R.id.et_lastname);
        rb_shareyes = (RadioButton) myView.findViewById(R.id.rb_shareyes);
        rb_shareno = (RadioButton) myView.findViewById(R.id.rb_shareno);

        fillTheFields(); // to be coded

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

                // Profile part
                // todo

                // Language part
                spinner_lang = mySpinner.getSelectedItem().toString();
                changeLanguage(getResources(), spinner_lang);
            }
        });

        final FloatingActionButton fab_close = myView.findViewById(R.id.fab_close);
        fab_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When user cancel changes
                fillTheFields(); // to be coded
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
        // todo: fill the options fields at startup or when cancelled, must be coded
        text_firstname.setText("Firstname");
        text_lastname.setText("Lastname");
        rb_shareyes.setChecked(true);
        rb_shareno.setChecked(false);
    }
}