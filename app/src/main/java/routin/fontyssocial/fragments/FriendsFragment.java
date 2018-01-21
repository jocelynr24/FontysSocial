package routin.fontyssocial.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.SearchView;

import routin.fontyssocial.R;

public class FriendsFragment extends Fragment {
    View myView;
    SearchView searchView;
    public FriendsFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_friends, container, false);
        searchView = myView.findViewById(R.id.search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                if(!text.equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("searchText", text);
                    SearchFriendFragment searchFriendFragment = new SearchFriendFragment();
                    searchFriendFragment.setArguments(bundle);
                    loadFragment(searchFriendFragment);
                }else{
                    loadFragment(new FriendsList());
                }
                return false;
            }
        });
        return myView;
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit(); // save the changes
    }

}