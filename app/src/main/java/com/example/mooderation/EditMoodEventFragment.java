package com.example.mooderation;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
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

public class EditMoodEventFragment extends Fragment {
    private MoodHistoryViewModel moodHistory;

    //private TextView dateTextView;
    //private TextView timeTextView;
    private Spinner emotionalStateSpinner;
    private Spinner socialSituationSpinner;
    private EditText reasonEditText;
    private Button saveButton;
    private Button cancelButton;

    private Calendar dateTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.edit_mood_event_fragment,
                container, false);

        // ViewModel for tracking MoodHistory
        moodHistory = ViewModelProviders.of(this).get(MoodHistoryViewModel.class);

        // get an Calendar with the current date and time
        dateTime = Calendar.getInstance();

        // find and initialize dateTextView and setup DatePicker
        /*dateTextView = view.findViewById(R.id.date_text_view);
        dateTextView.setText(MoodEvent.dateFormat.format(dateTime.getTime()));
        dateTextView.setOnClickListener((View v) -> {
            new DatePickerDialog(getActivity(), new DateSetListener(),
                    dateTime.get(Calendar.YEAR),
                    dateTime.get(Calendar.MONTH),
                    dateTime.get(Calendar.DAY_OF_MONTH)).show();
        });*/

        // find and initialize timeTextView and setup TimePicker
        /*timeTextView = view.findViewById(R.id.time_text_view);
        timeTextView.setText(MoodEvent.timeFormat.format(dateTime.getTime()));
        timeTextView.setOnClickListener((View v) -> {
            new TimePickerDialog(getActivity(), new TimeSetListener(),
                    dateTime.get(Calendar.HOUR_OF_DAY),
                    dateTime.get(Calendar.MINUTE),
                    false).show();
        });*/

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
            moodHistory.addMoodEvent(moodEvent);

            // Close the current fragment
            Navigation.findNavController(v).popBackStack();
        });

        cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener((View v) -> {
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
/*
    private class DateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dateTime.set(year, month, dayOfMonth);
            dateTextView.setText(MoodEvent.dateFormat.format(dateTime.getTime()));
        }
    }

    private class TimeSetListener implements TimePickerDialog.OnTimeSetListener {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTime.set(Calendar.MINUTE, minute);
            timeTextView.setText(MoodEvent.dateFormat.format(dateTime.getTime()));
        }
    }*/
}
