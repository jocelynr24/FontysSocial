package routin.fontyssocial.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Jocelyn on 12/12/2017.
 */

public class Event {
    private static Event INSTANCE = null;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("events");
    private String ID;
    private String name;
    private String description;
    private LatLng position;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;

    public Event(String ID){
        INSTANCE = this;
        this.ID = ID;

        if (ref.child(ID) == null){
            ref.child(ID).child("info").child("name").setValue("");
            ref.child(ID).child("info").child("description").setValue("");
            ref.child(ID).child("info").child("address").setValue("");
            ref.child(ID).child("position").setValue(new LatLng(0.0, 0.0));
            ref.child(ID).child("start").child("date").setValue("01/01/1970");
            ref.child(ID).child("start").child("time").setValue("00:00");
            ref.child(ID).child("end").child("date").setValue("01/01/1970");
            ref.child(ID).child("end").child("time").setValue("00:00");
        }
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Event{" +
                "ID='" + ID + '\'' +
                '}';
    }

    public static Event getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Event("");
        }
        return(INSTANCE);
    }
}
