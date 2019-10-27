package com.example.mooderation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class AddMoodEventFragment extends Fragment {

    private TextView dateTextView;
    private TextView timeTextView;
    private Spinner emotionalStateSpinner;
    private Spinner socialSituationSpinner;
    private EditText reasonEditText;

    private Calendar dateTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_mood_event_fragment,
                container, false);

        dateTextView = view.findViewById(R.id.date_text_view);
        timeTextView = view.findViewById(R.id.time_text_view);
        emotionalStateSpinner = view.findViewById(R.id.emotional_state_spinner);
        socialSituationSpinner = view.findViewById(R.id.social_situation_spinner);
        reasonEditText = view.findViewById(R.id.reason_edit_text);

        initializeUserInterface();

        return view;
    }

    private void initializeUserInterface() {
        dateTime = Calendar.getInstance();

        dateTextView.setText(MoodEvent.dateFormat.format(dateTime.getTime()));
        dateTextView.setOnClickListener((View v) -> {
            new DatePickerDialog(getContext(), new DateSetListener(),
                    dateTime.get(Calendar.YEAR),
                    dateTime.get(Calendar.MONTH),
                    dateTime.get(Calendar.DAY_OF_MONTH)).show();
        });

        timeTextView.setText(MoodEvent.timeFormat.format(dateTime.getTime()));
        timeTextView.setOnClickListener((View v) -> {
            new TimePickerDialog(getContext(), new TimeSetListener(),
                    dateTime.get(Calendar.HOUR_OF_DAY),
                    dateTime.get(Calendar.MINUTE),
                    false).show();
        });

        emotionalStateSpinner.setAdapter(getAdapter(EmotionalState.class));
        socialSituationSpinner.setAdapter(getAdapter(SocialSituation.class));
    }

    private <E extends Enum<E>> ArrayAdapter<E> getAdapter(Class<E> enumType) {
        // see this stack over flow post for more details
        // https://stackoverflow.com/questions/5469629
        ArrayAdapter<E> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                enumType.getEnumConstants());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    class DateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dateTime.set(year, month, dayOfMonth);
            dateTextView.setText(MoodEvent.dateFormat.format(dateTime.getTime()));
        }
    }

    class TimeSetListener implements TimePickerDialog.OnTimeSetListener {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTime.set(Calendar.MINUTE, minute);
            timeTextView.setText(MoodEvent.dateFormat.format(dateTime.getTime()));
        }
    }
}
