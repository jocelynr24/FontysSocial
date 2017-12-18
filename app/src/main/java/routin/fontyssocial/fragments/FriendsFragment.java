package routin.fontyssocial.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import routin.fontyssocial.R;

public class FriendsFragment extends Fragment {
    View myView;

    public FriendsFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_friends, container, false);
        return myView;
    }
}