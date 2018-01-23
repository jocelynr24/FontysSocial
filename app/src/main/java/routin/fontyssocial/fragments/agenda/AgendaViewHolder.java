package routin.fontyssocial.fragments.agenda;

import android.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import routin.fontyssocial.R;
import routin.fontyssocial.model.Agenda;

/**
 * Created by Jocelyn on 23/01/2018.
 */

public class AgendaViewHolder extends RecyclerView.ViewHolder {

    private final TextView name, location, start, end;

    // Correspond to one cell
    public AgendaViewHolder(final View itemView) {
        super(itemView);

        name = (TextView) itemView.findViewById(R.id.tv_name);
        location = (TextView) itemView.findViewById(R.id.tv_location);
        start = (TextView) itemView.findViewById(R.id.tv_start);
        end = (TextView) itemView.findViewById(R.id.tv_end);
    }

    // Fill the cell depending on the data
    public void display(Agenda agenda, View view) {
        name.setText(agenda.getName());
        location.setText(agenda.getLocation());
        start.setText(agenda.getStartDate() + " " + view.getContext().getString(R.string.agenda_at) + " " + agenda.getStartTime());
        end.setText(agenda.getEndDate() + " " + view.getContext().getString(R.string.agenda_at) + " " + agenda.getEndTime());
    }
}