package routin.fontyssocial.fragments.friends;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import routin.fontyssocial.R;

public class FriendsList extends Fragment {

    public static List<String> friends = new ArrayList<>();

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference ref = database.getReference("users");

    ListView friendList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view=inflater.inflate(R.layout.fragment_friend_list, container, false);

        friendList = view.findViewById(R.id.friend_list);
        if (friends != null) {
            friendList.setAdapter(new FriendListAdapter(this.getActivity(), friends));
        }
        else {
            friendList.setAdapter(new FriendListAdapter(this.getActivity(), new ArrayList<String>()));
        }

        return view;
    }

    public static void initializeFriendList (String username) {
        ref.child(username).child("friends").orderByKey().addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> users = (ArrayList) dataSnapshot.getValue();

                        friends = users;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
}
