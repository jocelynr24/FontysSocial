package routin.fontyssocial.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Jocelyn on 12/12/2017.
 */

public class Event {
    private static Event INSTANCE = null;
    private String ID;
    private String name;
    private String description;
    private String address;
    private String owner;
    private LatLng position;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;

    public Event(String ID){
        INSTANCE = this;
        this.ID = ID;
    }

    public Event(String ID, String name, String description, String address, String owner, LatLng position, String startDate, String endDate, String startTime, String endTime){
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.address = address;
        this.position = position;

        if(startDate.matches("^(0[1-9]|1[0-9]|2[0-9]|3[0-1])\\/(0[1-9]|1[0-2])\\/([0-9]{2})$") && endDate.matches("^(0[1-9]|1[0-9]|2[0-9]|3[0-1])\\/(0[1-9]|1[0-2])\\/([0-9]{2})$")
                && startTime.matches("^(0[0-9]|1[0-9]|2[0-3]):(0[0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9])$") && endTime.matches("^(0[0-9]|1[0-9]|2[0-3]):(0[0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9])$")){
            this.startDate = startDate;
            this.endDate = endDate;
            this.startTime = startTime;
            this.endTime = endTime;
        } else {
            throw new IllegalArgumentException("Wrong date or time format.");
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
