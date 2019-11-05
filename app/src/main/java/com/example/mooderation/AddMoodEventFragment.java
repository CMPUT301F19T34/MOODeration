package com.example.mooderation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import java.util.Calendar;
import java.util.Date;

public class AddMoodEventFragment extends Fragment {
    private MoodHistoryViewModel moodHistoryViewModel;

    private TextView dateTextView;
    private TextView timeTextView;
    private Spinner emotionalStateSpinner;
    private Spinner socialSituationSpinner;
    private EditText reasonEditText;
    private Button saveButton;

    private Date dateTime;

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

        // get a Date with the current date and time
        dateTime = new Date();

        // find and initialize dateTextView
        dateTextView = view.findViewById(R.id.date_picker_button);
        dateTextView.setText(MoodEvent.dateFormat.format(dateTime.getTime()));

        // find and initialize timeTextView
        timeTextView = view.findViewById(R.id.time_picker_button);
        timeTextView.setText(MoodEvent.timeFormat.format(dateTime.getTime()));

        // find and initialize emotionalStateSpinner
        emotionalStateSpinner = view.findViewById(R.id.emotional_state_spinner);
        emotionalStateSpinner.setAdapter(createAdapter(EmotionalState.class));

        // find and initialize socialSituationSpinner
        socialSituationSpinner = view.findViewById(R.id.social_situation_spinner);
        socialSituationSpinner.setAdapter(createAdapter(SocialSituation.class));

        // find reasonEditText
        reasonEditText = view.findViewById(R.id.reason_edit_text);

        // find and initialize saveButton
        saveButton = view.findViewById(R.id.save_mood_event_button);
        saveButton.setOnClickListener((View v) -> {
            MoodEvent moodEvent = new MoodEvent(
                    dateTime,
                    (EmotionalState) emotionalStateSpinner.getSelectedItem(),
                    (SocialSituation) socialSituationSpinner.getSelectedItem(),
                    reasonEditText.getText().toString());
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
}
