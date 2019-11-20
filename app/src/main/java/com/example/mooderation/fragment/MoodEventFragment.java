package com.example.mooderation.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MoodEventFragment extends Fragment {
    private MoodEventViewModel moodEventViewModel;

    private TextView dateTextView;
    private TextView timeTextView;
    private Spinner emotionalStateSpinner;
    private Spinner socialSituationSpinner;
    private EditText reasonEditText;
    private Switch locationSwitch;

    private Button saveButton;

    private FusedLocationProviderClient fusedLocationClient;
    private boolean isToggled = false;

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

        // find and initialize TextViews
        dateTextView = view.findViewById(R.id.date_picker_button);
        timeTextView = view.findViewById(R.id.time_picker_button);

        // find and initialize emotionalStateSpinner
        emotionalStateSpinner = view.findViewById(R.id.emotional_state_spinner);
        emotionalStateSpinner.setAdapter(new MoodConstantAdapter<>(getContext(), EmotionalState.class.getEnumConstants()));

        // find and initialize socialSituationSpinner
        socialSituationSpinner = view.findViewById(R.id.social_situation_spinner);
        socialSituationSpinner.setAdapter(new MoodConstantAdapter<>(getContext(), SocialSituation.class.getEnumConstants()));

        // find reasonEditText
        reasonEditText = view.findViewById(R.id.reason_edit_text);

        // find and initialize locationSwitch
        locationSwitch = view.findViewById(R.id.location_switch);
        locationSwitch.setOnCheckedChangeListener((compoundButton, isToggled) -> {
            if(isToggled) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            }
        });

        moodEventViewModel.getMoodEvent().observe(getViewLifecycleOwner(), moodEvent -> {
            dateTextView.setText(moodEvent.getFormattedDate());
            timeTextView.setText(moodEvent.getFormattedTime());
            emotionalStateSpinner.setSelection(0); // TODO fix -- set to mood events settings
            socialSituationSpinner.setSelection(0);
            reasonEditText.setText(moodEvent.getReason());
        });

        // find and initialize saveButton
        saveButton = view.findViewById(R.id.save_mood_event_button);
        saveButton.setOnClickListener((View v) -> {
            // if location toggle is on
            if(isToggled) {
                // get location
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if(location != null) {
                        // TODO set location field of mood event
                    }
                });
            }

            // TODO better utilize the view model
            MoodEvent moodEvent = moodEventViewModel.getMoodEvent().getValue();
            moodEvent.setEmotionalState((EmotionalState) emotionalStateSpinner.getSelectedItem());
            moodEvent.setSocialSituation((SocialSituation) socialSituationSpinner.getSelectedItem());
            moodEvent.setReason(reasonEditText.getText().toString());
            moodEventViewModel.setMoodEvent(moodEvent);
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
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isToggled = true;
        } else {
            isToggled = false;
            locationSwitch.toggle();
            if(!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                openDialog();
            }
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

