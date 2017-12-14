package routin.fontyssocial;

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
    private String name;
    private LatLng position;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;

    public Event(String name, LatLng position, String startDate, String endDate, String startTime, String endTime){
        INSTANCE = this;
        this.name = name;
        this.position = position;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;

        //if (ref.child(name) == null){
            ref.child(name).child("position").setValue(position);
            ref.child(name).child("startDate").setValue(startDate);
            ref.child(name).child("endDate").setValue(endDate);
            ref.child(name).child("startTime").setValue(startTime);
            ref.child(name).child("endTime").setValue(endTime);
        //}
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
                "name='" + name + '\'' +
                '}';
    }

    public static Event getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Event(null, null, null,null,null, null);
        }
        return(INSTANCE);
    }
}
