package routin.fontyssocial;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import routin.fontyssocial.model.Event;

import static org.junit.Assert.*;

public class EventTest {
    private Event eventWithId, eventComplete;

    @Before
    public void setup(){
        eventWithId = new Event("1234");
        eventComplete = new Event("1234", "Party in Eindhoven", "Night party in Eindhoven for the end of the semester.", "Eindhoven",  new LatLng(50.0, 50.0), "19/01/18", "20/01/18", "22:00", "02:00");
    }

    @Test
    public void eventWithIdShouldReturnTheIdOfTheEvent(){
        String eventID = eventWithId.getID();
        assertEquals("1234", eventID);
    }

    @Test
    public void eventCompleteShouldReturnTheRightId(){
        assertEquals("1234", eventComplete.getID());
    }

    @Test
    public void eventCompleteShouldReturnTheRightName(){
        assertEquals("Party in Eindhoven", eventComplete.getName());
    }

    @Test
    public void eventCompleteShouldReturnTheRightDescription(){
        assertEquals("Night party in Eindhoven for the end of the semester.", eventComplete.getDescription());
    }

    @Test
    public void eventCompleteShouldReturnTheRightAddress(){
        assertEquals("Eindhoven", eventComplete.getAddress());
    }

    @Test
    public void eventCompleteShouldReturnTheRightPosition(){
        LatLng pos = new LatLng(50.0, 50.0);
        assertEquals(pos, eventComplete.getPosition());
    }

    @Test
    public void eventCompleteShouldReturnTheRightStartDate(){
        assertEquals("19/01/18", eventComplete.getStartDate());
    }

    @Test
    public void eventCompleteShouldReturnTheRightEndDate(){
        assertEquals("20/01/18", eventComplete.getEndDate());
    }

    @Test
    public void eventCompleteShouldReturnTheRightStartTime(){
        assertEquals("22:00", eventComplete.getStartTime());
    }

    @Test
    public void eventCompleteShouldReturnTheRightEndTime(){
        assertEquals("02:00", eventComplete.getEndTime());
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidDateFormatShouldThrowAnException(){
        new Event("1234", "Party in Eindhoven", "Night party in Eindhoven for the end of the semester.", "Eindhoven",  new LatLng(50.0, 50.0), "1234", "1234", "22:00", "02:00");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidTimeFormatShouldThrowAnException(){
        new Event("1234", "Party in Eindhoven", "Night party in Eindhoven for the end of the semester.", "Eindhoven",  new LatLng(50.0, 50.0), "19/01/18", "20/01/18", "1234", "1234");
    }
}