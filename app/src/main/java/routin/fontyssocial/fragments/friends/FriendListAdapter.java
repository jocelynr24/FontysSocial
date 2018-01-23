package routin.fontyssocial.fragments.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import routin.fontyssocial.R;

public class FriendListAdapter extends ArrayAdapter<String> {

    public FriendListAdapter(Context context, List<String> friends) {
        super(context, 0, friends);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friend_list_item, parent, false);
        }

        String friend_name = getItem(position);

        TextView tvName = (TextView) convertView.findViewById(R.id.friend_name);

        tvName.setText(friend_name);

        return convertView;
    }
}


