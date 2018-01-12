package routin.fontyssocial;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import routin.fontyssocial.model.User;


/**
 * Created by conte on 31/12/2017.
 */

public class SearchFriendFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference ref = firebaseDatabase.getReference("users");
    private Map<String,Object> users = new HashMap<>();
    private Map<Integer,String> itemsList= new HashMap<>();

    public SearchFriendFragment() {
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ProgressBar mProgressBar;
    private Button mButton;
    ListView listView ;
    View view;
    String searchText;
    private RecyclerView recyclerView;

    public SearchFriendFragment newInstance(String text) {
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, text);
        SearchFriendFragment fragment = new SearchFriendFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view=inflater.inflate(R.layout.fragment_search_friend, container, false);
        this.searchText=getArguments().getString("searchText");
        //listView = (ListView) view.findViewById(R.id.listR);

        ref.orderByKey().startAt(searchText).endAt(searchText+"\uf8ff").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        users = (Map<String,Object>) dataSnapshot.getValue();

                        if (users != null) {
                            for (Map.Entry<String, Object> entry : users.entrySet()){

                                String name = entry.toString().split("=")[0];

                                if (!name.equals(User.getInstance().getName())){
                                    itemsList.put(itemsList.size(),name);
                                }
                            }
                            recyclerView = (RecyclerView) view.findViewById(R.id.listR);
                            SearchFriendAdapterList adapter = new SearchFriendAdapterList(itemsList);

                            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                            manager.setOrientation(LinearLayoutManager.VERTICAL);
                            recyclerView.setLayoutManager(manager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        mButton=view.findViewById(R.id.buttonCancel);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment, new FriendsList());
                ft.commit();
            }
        });
        return view;


    }
}
