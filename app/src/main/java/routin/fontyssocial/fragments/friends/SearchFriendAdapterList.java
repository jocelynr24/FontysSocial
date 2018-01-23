package routin.fontyssocial.fragments.friends;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import routin.fontyssocial.R;
import routin.fontyssocial.model.User;


/**
 * Created by conte on 17/12/2017.
 */

public class SearchFriendAdapterList extends RecyclerView.Adapter<SearchFriendAdapterList.MyViewHolder> {

    private Context context;

    private Map<Integer,String> users=new HashMap<>();

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference("users");
    private int numbersOfFriends = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public Button add;

        public MyViewHolder(View view) {
            super(view);
            context = view.getContext();
            name = view.findViewById(R.id.textViewName);
            add = view.findViewById(R.id.button);

            String username = User.getInstance().getName();

            if (ref.child(username).child("friends") != null) {
                ref.child(username).child("friends").orderByKey().addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                List<String> users = (ArrayList) dataSnapshot.getValue();

                                if (users != null) {
                                    numbersOfFriends = users.size();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
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

        if (FriendsList.friends != null){
            if (FriendsList.friends.contains(holder.name.getText())){
                holder.add.setText("Friend");
                holder.add.setBackgroundColor(context.getColor(R.color.colorPrimary));
                holder.add.setTextColor(Color.WHITE);
                holder.add.setEnabled(false);
            }
        }

        /*holder.picture.setBackgroundColor(Color.parseColor(contact.getColor()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),
                        "Position : " + position + "  ListItem : " + holder.name.getText(), Toast.LENGTH_LONG)
                        .show();
            }
        });*/

        Button button = holder.add;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = User.getInstance().getName();
                ref.child(username).child("friends").child(String.valueOf(numbersOfFriends)).setValue(users.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


}
