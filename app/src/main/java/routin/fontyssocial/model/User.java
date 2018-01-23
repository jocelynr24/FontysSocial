package routin.fontyssocial.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import routin.fontyssocial.fragments.FriendsList;

public class User {
    private static User INSTANCE = null;
    private String name;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("users");

    public User(final String mail){
        this.name = null;

        INSTANCE = this;

        ref.orderByKey().addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        Map<String,Object> users = (Map<String, Object>) dataSnapshot.getValue();

                        if (users != null && mail != null) {
                            for (Map.Entry<String, Object> entry : users.entrySet()) {

                                Map singleUser = (Map) entry.getValue();

                                if (singleUser.get("mail").equals(mail)){
                                    User.this.name = entry.toString().split("=")[0];

                                    FriendsList.initializeFriendList(User.this.name);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public User(String username, String mail) {
        INSTANCE = this;
        this.name = username;

        ref.child(username).child("mail").setValue(mail);
        ref.child(username).child("latitude").setValue(50.111111111111111);
        ref.child(username).child("longitude").setValue(50.222222222222222);
        ref.child(username).child("settings").child("firstname").setValue(username);
        ref.child(username).child("settings").child("lastname").setValue(username);
        ref.child(username).child("settings").child("share").setValue(false);

        FriendsList.initializeFriendList(this.name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }

    public static User getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new User("");
        }
        return(INSTANCE);
    }
}


