package routin.fontyssocial.fragments.agenda;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import routin.fontyssocial.R;
import routin.fontyssocial.model.Agenda;
import routin.fontyssocial.model.Event;

public class AgendaFragment extends Fragment {
    private View myView;
    private RecyclerView recyclerView;
    private List<Agenda> agendaList = new ArrayList<>();

    private FirebaseAuth auth;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference events = database.getReference("events");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_agenda, container, false);

        recyclerView = (RecyclerView) myView.findViewById(R.id.agenda_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        events.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Map<String, Object> events = (Map<String, Object>) dataSnapshot.getValue();

                            for (Map.Entry<String, Object> entry : events.entrySet()) {
                                String ID = entry.toString().split("=")[0];

                                if (!ID.equals(Event.getInstance().getID())) {
                                    Map singleEvent = (Map) entry.getValue();

                                    Map info = (Map) singleEvent.get("info");
                                    Map position = (Map) singleEvent.get("position");
                                    Map start = (Map) singleEvent.get("start");
                                    Map end = (Map) singleEvent.get("end");

                                    if ((info != null) && (info.size() == 4) && (position != null) && (position.size() == 2) && (start != null) && (start.size() == 2) && (end != null) && (end.size() == 2)) {
                                        String name = (String) info.get("name");
                                        String address = (String) info.get("address");
                                        String startDate = (String) start.get("date");
                                        String startTime = (String) start.get("time");
                                        String endDate = (String) end.get("date");
                                        String endTime = (String) end.get("time");

                                        agendaList.add(new Agenda(name, address, startDate, startTime, endDate, endTime));
                                        recyclerView.setAdapter(new AgendaAdapter(agendaList, myView));
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

        return myView;
    }
}