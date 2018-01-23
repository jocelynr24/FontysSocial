package routin.fontyssocial.fragments.agenda;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import routin.fontyssocial.R;
import routin.fontyssocial.model.Agenda;

/**
 * Created by Jocelyn on 22/01/2018.
 */

public class AgendaAdapter extends RecyclerView.Adapter<AgendaViewHolder> {
    private List<Agenda> agendaList;
    private View view;

    public AgendaAdapter(List<Agenda> list, View view) {
        this.agendaList = list;
        this.view = view;
    }

    // Create view holder and the view to inflate
    @Override
    public AgendaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_agenda_row, parent, false);
        return new AgendaViewHolder(view);
    }

    // Fill the cells with text
    @Override
    public void onBindViewHolder(AgendaViewHolder agendaViewHolder, int position) {
        Agenda agenda = agendaList.get(position);
        agendaViewHolder.display(agenda, view);
    }

    @Override
    public int getItemCount() {
        return agendaList.size();
    }
}


