package routin.fontyssocial;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;



/**
 * Created by conte on 17/12/2017.
 */

public class SearchFriendAdapterList extends RecyclerView.Adapter<SearchFriendAdapterList.MyViewHolder> {

    private Map<Integer,String> users=new HashMap<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public Button add;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.textViewName);
            add = view.findViewById(R.id.button);
        }
    }

    public SearchFriendAdapterList(Map<Integer,String> users) {
        this.users = users;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        String name = users.get(position);
        holder.name.setText(name);
//        Button button = holde
//        holder.picture.setBackgroundColor(Color.parseColor(contact.getColor()));
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(),
//                        "Position : " + position + "  ListItem : " + holder.name.getText(), Toast.LENGTH_LONG)
//                        .show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


}
