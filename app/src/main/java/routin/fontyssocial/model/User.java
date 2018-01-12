package routin.fontyssocial.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by conte on 04/12/2017.
 */

public class User {
    private static User INSTANCE = null;
    String name;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("users");

    public User(String username) {
        INSTANCE = this;
        this.name = username;

        if (ref.child(username) == null){
            ref.child(username).child("latitude").setValue(51.441642);
            ref.child(username).child("longitude").setValue(5.3697225);
        }
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


