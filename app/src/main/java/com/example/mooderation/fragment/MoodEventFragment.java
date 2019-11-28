package com.example.mooderation.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import com.example.mooderation.MoodLatLng;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.example.mooderation.EmotionalState;
import com.example.mooderation.LocationDeniedDialog;
import com.example.mooderation.MoodEvent;
import com.example.mooderation.MoodEventConstants;
import com.example.mooderation.R;
import com.example.mooderation.SocialSituation;
import com.example.mooderation.viewmodel.MoodEventViewModel;

public class MoodEventFragment extends Fragment implements AdapterView.OnItemSelectedListener, TextWatcher{
    private MoodEventViewModel moodEventViewModel;

    private TextView dateTextView;
    private TextView timeTextView;
    private Spinner emotionalStateSpinner;
    private Spinner socialSituationSpinner;
    private EditText reasonEditText;
    private FusedLocationProviderClient fusedLocationClient;
    private Switch locationSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moodEventViewModel = ViewModelProviders.of(getActivity()).get(MoodEventViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_mood_event_layout,
                container, false);

        // observe mood event's date and time
        dateTextView = view.findViewById(R.id.date_picker_button);
        timeTextView = view.findViewById(R.id.time_picker_button);

        // observe emotional state
        emotionalStateSpinner = view.findViewById(R.id.emotional_state_spinner);
        emotionalStateSpinner.setAdapter(new MoodConstantAdapter<>(
                getContext(), EmotionalState.class.getEnumConstants()));
        emotionalStateSpinner.setOnItemSelectedListener(this);

        // observe social situation
        socialSituationSpinner = view.findViewById(R.id.social_situation_spinner);
        socialSituationSpinner.setAdapter(new MoodConstantAdapter<>(
                getContext(), SocialSituation.class.getEnumConstants()));
        socialSituationSpinner.setOnItemSelectedListener(this);

        reasonEditText = view.findViewById(R.id.reason_edit_text);
        reasonEditText.addTextChangedListener(this);

        // find locationSwitch
        locationSwitch = view.findViewById(R.id.location_switch);

        // observe the mood event and update UI
        moodEventViewModel.getMoodEvent().observe(getViewLifecycleOwner(), moodEvent -> {
            dateTextView.setText(moodEvent.getFormattedDate());
            timeTextView.setText(moodEvent.getFormattedTime());

            emotionalStateSpinner.setSelection(moodEvent.getEmotionalState().ordinal());
            socialSituationSpinner.setSelection(moodEvent.getSocialSituation().ordinal());

            // prevents infinite loop of text updates
            if (!reasonEditText.getText().toString().equals(moodEvent.getReason())){
                reasonEditText.setText(moodEvent.getReason());
            }

            // set location toggle
            if (moodEventViewModel.getIsEditing().getValue()) {
                locationSwitch.setChecked(true);
                locationSwitch.setEnabled(false);
            } else if(moodEventViewModel.getLocationToggleState().getValue()) {
                locationSwitch.setChecked(true);
            } else {
                locationSwitch.setChecked(false);
            }

        });

        locationSwitch.setOnCheckedChangeListener((compoundButton, isToggled) -> {
            // prevents location from being changed once set
            if(!moodEventViewModel.getIsEditing().getValue()) {
                if(isToggled) {
                    // request permission if permission is not already granted
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    } else {
                        locationSwitch.setChecked(true);
                        moodEventViewModel.setLocationToggleState(true);
                    }
                }
            }else {
                if(moodEventViewModel.getMoodEvent().getValue().getLocation() != null) {
                    locationSwitch.setChecked(true);
                    moodEventViewModel.setLocationToggleState(true);
                } else {
                    locationSwitch.setChecked(false);
                    moodEventViewModel.setLocationToggleState(false);
                }
            }

        });

        // find and initialize saveButton
        Button saveButton = view.findViewById(R.id.save_mood_event_button);
        saveButton.setOnClickListener((View v) -> {
            // store location in mood event if it not already set
            if(locationSwitch.isChecked() && !moodEventViewModel.getIsEditing().getValue()) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if(location != null) {
                        moodEventViewModel.getMoodEvent().getValue().setLocation(new MoodLatLng(location.getLatitude(), location.getLongitude()));
                        moodEventViewModel.saveChanges();
                    }
                });
            }
            // update the database with new changes
            moodEventViewModel.saveChanges();

            // Close keyboard
            InputMethodManager inputManager = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

            // Close the current fragment
            Navigation.findNavController(v).popBackStack();
        });

        return view;
    }

    /**
     * Processes the result of the permission request
     * @param requestCode the request code indicates the permission that was requested
     * @param permissions the permissions that were requested
     * @param grantResults the result of the permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            locationSwitch.setChecked(false);
            moodEventViewModel.setLocationToggleState(false);
            if(!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                openDialog();
            }
        } else {
            locationSwitch.setChecked(true);
            moodEventViewModel.setLocationToggleState(true);
        }
    }

    /**
     * Displays a dialog box instructing the user how to turn on location permission if they
     * selected "deny and never show again"
     */
    public void openDialog() {
        LocationDeniedDialog locationDeniedDialog = new LocationDeniedDialog();
        locationDeniedDialog.show(getFragmentManager(), "Location Denied");
    }

    // for listening updating the mood event when the spinners are updated
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        MoodEvent moodEvent = moodEventViewModel.getMoodEvent().getValue();
        if (moodEvent == null) {
            throw new IllegalStateException("Mood event cannot be null");
        }

        if (parent == emotionalStateSpinner) {
            moodEvent.setEmotionalState((EmotionalState) parent.getItemAtPosition(position));
        }
        else if (parent == socialSituationSpinner) {
            moodEvent.setSocialSituation((SocialSituation) parent.getItemAtPosition(position));
        }

        moodEventViewModel.setMoodEvent(moodEvent);
    }

    // required by OnItemSelectedListener but not used
    @Override
    public void onNothingSelected(AdapterView<?> parent) { /* ignore */ }

    // required by TextWatcher but not used
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    // listens for the reason edit text to be updated
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        MoodEvent moodEvent = moodEventViewModel.getMoodEvent().getValue();
        if (moodEvent == null) {
            throw new IllegalStateException("Mood event cannot be null");
        }
        moodEvent.setReason(s.toString());
        moodEventViewModel.setMoodEvent(moodEvent);
    }

    // required by TextWatcher but not used
    @Override
    public void afterTextChanged(Editable s) {}

    /**
     * Array adapter used to populate the spinners in MoodEventFragment
     * @param <E>
     *      Enum extending MoodEventConstants
     */
    private class MoodConstantAdapter<E extends MoodEventConstants> extends ArrayAdapter {
        private LayoutInflater inflater = getLayoutInflater();
        private E[] enumConstants;

        MoodConstantAdapter(@NonNull Context context, E[] enumConstants) {
            super(context, android.R.layout.simple_spinner_item);
            this.enumConstants = enumConstants;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            return createView(position, convertView, parent);
        }

        @NonNull
        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            return createView(position, convertView, parent);
        }

        private View createView(int position, View convertView, @NonNull ViewGroup parent) {
            if(convertView == null){
                convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            }
            TextView textView = convertView.findViewById(android.R.id.text1);
            textView.setText(getString(enumConstants[position].getStringResource()));
            return convertView;
        }

        @Override
        public int getCount() {
            return enumConstants.length;
        }

        @Override
        public E getItem(int position) {
            return enumConstants[position];
        }
    }
}

