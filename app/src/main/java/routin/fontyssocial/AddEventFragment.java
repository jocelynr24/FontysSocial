package routin.fontyssocial;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEventFragment extends Fragment {
    View myView;

    EditText text_name;
    EditText text_address;
    EditText text_startDate;
    EditText text_endDate;
    EditText text_startTime;
    EditText text_endTime;
    FloatingActionButton fab_add;

    private Calendar mStartDate = Calendar.getInstance();
    private Calendar mEndDate = Calendar.getInstance();
    private Calendar mStartTime = Calendar.getInstance();
    private Calendar mEndTime = Calendar.getInstance();

    public AddEventFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_add_event, container, false);

        text_name = (EditText) myView.findViewById(R.id.et_name);
        text_address = (EditText) myView.findViewById(R.id.et_address);
        text_startDate = (EditText) myView.findViewById(R.id.et_startdate);
        text_endDate = (EditText) myView.findViewById(R.id.et_enddate);
        text_startTime = (EditText) myView.findViewById(R.id.et_starttime);
        text_endTime = (EditText) myView.findViewById(R.id.et_endtime);
        fab_add = (FloatingActionButton) myView.findViewById(R.id.fab_add);

        text_startDate.setTextIsSelectable(true);
        text_endDate.setTextIsSelectable(true);
        text_startTime.setTextIsSelectable(true);
        text_endTime.setTextIsSelectable(true);

        this.initializeStartDatePicker();
        this.initializeEndDatePicker();
        this.initializeStartTimePicker();
        this.initializeEndTimePicker();
        this.initializeButton();

        return myView;
    }

    public void alertDialog(String title, String content, String validation){
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

    public void initializeStartDatePicker(){
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

    public void initializeEndDatePicker(){
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

    public void initializeStartTimePicker(){
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

    public void initializeEndTimePicker(){
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

    public void initializeButton(){
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEvent();
            }
        });
    }

    public void sendEvent(){
        String name = text_name.getText().toString();
        String address = text_address.getText().toString();
        String startDate = text_startDate.getText().toString();
        String endDate = text_endDate.getText().toString();
        String startTime = text_startTime.getText().toString();
        String endTime = text_endTime.getText().toString();

        LatLng position = getLocationFromAddress(getContext(), address);

        if(position != null){
        //    ((MainActivity) getActivity()).addEvent(name, startDate, startTime, endDate, endTime, position);
            // todo: add the event to the database
        } else {
            this.alertDialog(getString(R.string.event_incorrectaddress), getString(R.string.event_incorrectaddressdesc), getString(R.string.event_ok));
        }

    }

    public LatLng getLocationFromAddress(Context context, String address) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> result;
        LatLng position = null;

        try {
            result = geocoder.getFromLocationName(address, 1);
            if (address == null) {
                return null;
            }
            Address location = result.get(0);
            location.getLatitude();
            location.getLongitude();

            position = new LatLng(location.getLatitude(), location.getLongitude() );
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return position;
    }

}

