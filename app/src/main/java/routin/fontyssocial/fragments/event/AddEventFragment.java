package routin.fontyssocial.fragments.event;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import routin.fontyssocial.R;
import routin.fontyssocial.main.MainActivity;
import routin.fontyssocial.model.Event;
import routin.fontyssocial.model.User;

public class AddEventFragment extends Fragment {
    private EditText text_name;
    private EditText text_desc;
    private EditText text_address;
    private EditText text_startDate;
    private EditText text_endDate;
    private EditText text_startTime;
    private EditText text_endTime;
    private FloatingActionButton fab_addevent;
    private FloatingActionButton fab_closeevent;
    private Calendar mStartDate = Calendar.getInstance();
    private Calendar mEndDate = Calendar.getInstance();
    private Calendar mStartTime = Calendar.getInstance();
    private Calendar mEndTime = Calendar.getInstance();

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference events = database.getReference("events");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_add_event, container, false);

        text_name = (EditText) myView.findViewById(R.id.et_name);
        text_desc = (EditText) myView.findViewById(R.id.et_desc);
        text_address = (EditText) myView.findViewById(R.id.et_address);
        text_startDate = (EditText) myView.findViewById(R.id.et_startdate);
        text_endDate = (EditText) myView.findViewById(R.id.et_enddate);
        text_startTime = (EditText) myView.findViewById(R.id.et_starttime);
        text_endTime = (EditText) myView.findViewById(R.id.et_endtime);
        fab_addevent = (FloatingActionButton) myView.findViewById(R.id.fab_addevent);
        fab_closeevent = (FloatingActionButton) myView.findViewById(R.id.fab_closeevent);

        text_startDate.setTextIsSelectable(true);
        text_endDate.setTextIsSelectable(true);
        text_startTime.setTextIsSelectable(true);
        text_endTime.setTextIsSelectable(true);

        this.initializeStartDatePicker();
        this.initializeEndDatePicker();
        this.initializeStartTimePicker();
        this.initializeEndTimePicker();
        this.initializeButtons();

        return myView;
    }

    private void alertDialog(String title, String content, String validation){
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(content);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, validation,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void initializeStartDatePicker(){
        final DatePickerDialog.OnDateSetListener startdate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mStartDate.set(Calendar.YEAR, year);
                mStartDate.set(Calendar.MONTH, month);
                mStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                text_startDate.setText(sdf.format(mStartDate.getTime()));
            }
        };

        text_startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), startdate, mStartDate
                        .get(Calendar.YEAR), mStartDate.get(Calendar.MONTH),
                        mStartDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void initializeEndDatePicker(){
        final DatePickerDialog.OnDateSetListener enddate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mEndDate.set(Calendar.YEAR, year);
                mEndDate.set(Calendar.MONTH, month);
                mEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                text_endDate.setText(sdf.format(mEndDate.getTime()));
            }
        };

        text_endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), enddate, mEndDate
                        .get(Calendar.YEAR), mEndDate.get(Calendar.MONTH),
                        mEndDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void initializeStartTimePicker(){
        final TimePickerDialog.OnTimeSetListener starttime = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mStartTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mStartTime.set(Calendar.MINUTE, minute);
                String myFormat = "HH:mm";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                text_startTime.setText(sdf.format(mStartTime.getTime()));
            }
        };

        text_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getContext(), starttime, mStartTime
                        .get(Calendar.HOUR_OF_DAY), mStartTime.get(Calendar.MINUTE), true).show();
            }
        });
    }

    private void initializeEndTimePicker(){
        final TimePickerDialog.OnTimeSetListener endtime = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mEndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mEndTime.set(Calendar.MINUTE, minute);
                String myFormat = "HH:mm";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                text_endTime.setText(sdf.format(mEndTime.getTime()));
            }
        };

        text_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getContext(), endtime, mEndTime
                        .get(Calendar.HOUR_OF_DAY), mEndTime.get(Calendar.MINUTE), true).show();
            }
        });
    }

    private void initializeButtons(){
        fab_addevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEvent();
                MainActivity ma = ((MainActivity) getActivity());
                ma.getFragmentManager().beginTransaction().replace(R.id.content_frame, ma.mapEventFragment).commit();
            }
        });

        fab_closeevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity ma = ((MainActivity) getActivity());
                ma.getFragmentManager().beginTransaction().replace(R.id.content_frame, ma.mapEventFragment).commit();
            }
        });
    }

    private void sendEvent(){
        String name = text_name.getText().toString();
        String description = text_desc.getText().toString();
        String owner = User.getInstance().getName();
        String address = text_address.getText().toString();
        String startDate = text_startDate.getText().toString();
        String endDate = text_endDate.getText().toString();
        String startTime = text_startTime.getText().toString();
        String endTime = text_endTime.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        Date startDateValue = new Date();
        Date startTimeValue = new Date();
        Date endDateValue = new Date();
        Date endTimeValue = new Date();;

        try {
            startDateValue = dateFormat.parse(startDate);
            startTimeValue = timeFormat.parse(startTime);
            endDateValue = dateFormat.parse(endDate);
            endTimeValue = timeFormat.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(!name.matches("") && !description.matches("") && !owner.matches("") && !address.matches("") &&
                !startDate.matches("") && !endDate.matches("") && !startTime.matches("") && !endTime.matches("")){
            if(!startDateValue.after(endDateValue) || (startDate.equals(endDate) && !startTimeValue.after(endTimeValue))){
                LatLng position = getLocationFromAddress(getContext(), address);
                if(position != null){
                    SimpleDateFormat idFormat = new SimpleDateFormat("ddMMyyyyHHmmssSS");
                    Date currentDate = new Date();
                    String ID = idFormat.format(currentDate);

                    new Event(ID, name, description, address, owner, position, startDate, endDate, startTime, endTime);

                    events.child(ID).child("info").child("name").setValue(name);
                    events.child(ID).child("info").child("description").setValue(description);
                    events.child(ID).child("info").child("owner").setValue(owner);
                    events.child(ID).child("position").setValue(position);
                    events.child(ID).child("info").child("address").setValue(address);
                    events.child(ID).child("start").child("date").setValue(startDate);
                    events.child(ID).child("start").child("time").setValue(startTime);
                    events.child(ID).child("end").child("date").setValue(endDate);
                    events.child(ID).child("end").child("time").setValue(endTime);
                } else {
                    this.alertDialog(getString(R.string.event_incorrectaddress), getString(R.string.event_incorrectaddressdesc), getString(R.string.event_ok));
                }
            }
        } else {
            this.alertDialog(getString(R.string.event_incorrectfields), getString(R.string.event_incorrectfieldsdesc), getString(R.string.event_ok));
        }
    }

    private LatLng getLocationFromAddress(Context context, String address) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> result;
        LatLng position = null;

        try {
            result = geocoder.getFromLocationName(address, 1);
            if (address == null) {
                position = null;
            }
            if (result.size() > 0){
                Address location = result.get(0);
                location.getLatitude();
                location.getLongitude();
                position = new LatLng(location.getLatitude(), location.getLongitude() );
            } else {
                position = null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return position;
    }
}

