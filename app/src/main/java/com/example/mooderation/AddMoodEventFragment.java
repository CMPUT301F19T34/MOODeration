package com.example.mooderation;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

public class AddMoodEventFragment extends Fragment {
    private MoodHistoryViewModel moodHistoryViewModel;

    private TextView dateTextView;
    private TextView timeTextView;
    private Spinner emotionalStateSpinner;
    private Spinner socialSituationSpinner;
    private EditText reasonEditText;
    private Button saveButton;
    private Switch locationSwitch;
    private FusedLocationProviderClient fusedLocationClient;

    private Calendar dateTime;
    private static Location location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_mood_event_layout,
                container, false);

        // ViewModel for tracking MoodHistory
        moodHistoryViewModel = ViewModelProviders.of(getActivity()).get(MoodHistoryViewModel.class);

        // get an Calendar with the current date and time
        dateTime = Calendar.getInstance();

        // find and initialize dateTextView and setup DatePicker
        dateTextView = view.findViewById(R.id.date_picker_button);
        dateTextView.setText(MoodEvent.dateFormat.format(dateTime.getTime()));
//        dateTextView.setOnClickListener((View v) ->
//            new DatePickerDialog(getActivity(), new DateSetListener(),
//                    dateTime.get(Calendar.YEAR),
//                    dateTime.get(Calendar.MONTH),
//                    dateTime.get(Calendar.DAY_OF_MONTH)).show());

        // find and initialize timeTextView and setup TimePicker
        timeTextView = view.findViewById(R.id.time_picker_button);
        timeTextView.setText(MoodEvent.timeFormat.format(dateTime.getTime()));
//        timeTextView.setOnClickListener((View v) ->
//            new TimePickerDialog(getActivity(), new TimeSetListener(),
//                    dateTime.get(Calendar.HOUR_OF_DAY),
//                    dateTime.get(Calendar.MINUTE),
//                    false).show());

        // find and initialize emotionalStateSpinner
        emotionalStateSpinner = view.findViewById(R.id.emotional_state_spinner);
        emotionalStateSpinner.setAdapter(createAdapter(EmotionalState.class));

        // find and initialize socialSituationSpinner
        socialSituationSpinner = view.findViewById(R.id.social_situation_spinner);
        socialSituationSpinner.setAdapter(createAdapter(SocialSituation.class));

        // find reasonEditText
        reasonEditText = view.findViewById(R.id.reason_edit_text);

        // find and initialize locationSwitch
        locationSwitch = view.findViewById(R.id.location_switch);
        locationSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if(isChecked) {
                // check that the device is running API level 23 or higher (minimum for permission dialogue box support)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                    // if permission has not been granted, request permission
                    checkPermission();
                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

                        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                            if(location != null) {
                                AddMoodEventFragment.location = location;
                            }
                        });
                    } else {
                        locationSwitch.toggle();
                    }
                }
            }
        });

        // find and initialize saveButton
        saveButton = view.findViewById(R.id.save_mood_event_button);
        saveButton.setOnClickListener((View v) -> {
            MoodEvent moodEvent = new MoodEvent(
                    dateTime,
                    (EmotionalState) emotionalStateSpinner.getSelectedItem(),
                    (SocialSituation) socialSituationSpinner.getSelectedItem(),
                    reasonEditText.getText().toString(),
                    location);
            moodHistoryViewModel.addMoodEvent(moodEvent);

            // Close the current fragment
            Navigation.findNavController(v).popBackStack();
        });

        return view;
    }

    /**
     * Create a spinner adapter from an enum type
     * Probably should be done a different way in the future
     * @param enumType Class of the enum to create an Adapter for
     * @param <E> The type of the Enum passed to createAdapter
     * @return A spinner adapter populated using Enum values
     */
    private <E extends Enum<E>> ArrayAdapter<E> createAdapter(Class<E> enumType) {
        // see StackOverFlow https://stackoverflow.com/questions/5469629
        ArrayAdapter<E> adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                enumType.getEnumConstants());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    /**
     * Listens for the date to be set by a DatePickerDialog
     */
    private class DateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dateTime.set(year, month, dayOfMonth);
            dateTextView.setText(MoodEvent.dateFormat.format(dateTime.getTime()));
        }
    }

    /**
     * If location permission is not granted, request permission
     */

    public void checkPermission() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    /**
     * Listens for the time to be set by a TimePickerDialog
     */
    private class TimeSetListener implements TimePickerDialog.OnTimeSetListener {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTime.set(Calendar.MINUTE, minute);
            timeTextView.setText(MoodEvent.dateFormat.format(dateTime.getTime()));
        }
    }
}
