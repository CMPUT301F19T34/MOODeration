package com.example.mooderation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.example.mooderation.EmotionalState;
import com.example.mooderation.MoodEvent;
import com.example.mooderation.MoodEventConstants;
import com.example.mooderation.R;
import com.example.mooderation.SocialSituation;
import com.example.mooderation.viewmodel.MoodEventViewModel;
import com.example.mooderation.viewmodel.MoodHistoryViewModel;

public class EditMoodEventFragment extends Fragment {
    private MoodHistoryViewModel moodHistoryViewModel;
    private MoodEventViewModel moodEventViewModel;

    private Spinner emotionalStateSpinner;
    private Spinner socialSituationSpinner;
    private EditText reasonEditText;
    private Button editButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moodHistoryViewModel = ViewModelProviders.of(getActivity()).get(MoodHistoryViewModel.class);
        moodEventViewModel = ViewModelProviders.of(getActivity()).get(MoodEventViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.edit_mood_event_layout, container, false);

        // ViewModel for tracking MoodHistory
        moodHistoryViewModel = ViewModelProviders.of(getActivity()).get(MoodHistoryViewModel.class);

        // find and initialize emotionalStateSpinner
        emotionalStateSpinner = view.findViewById(R.id.emotional_state_spinner);
        emotionalStateSpinner.setAdapter(new MoodConstantAdapter<>(getContext(), EmotionalState.class.getEnumConstants()));

        // find and initialize socialSituationSpinner
        socialSituationSpinner = view.findViewById(R.id.social_situation_spinner);
        socialSituationSpinner.setAdapter(new MoodConstantAdapter<>(getContext(), SocialSituation.class.getEnumConstants()));

        // find reasonEditText
        reasonEditText = view.findViewById(R.id.reason_edit_text);

        // find and initialize editButton
        editButton = view.findViewById(R.id.edit_mood_event_button);
        editButton.setOnClickListener((View v) -> {
            MoodEvent moodEvent = moodEventViewModel.getMoodEvent().getValue();
            MoodEvent newMoodEvent = new MoodEvent(
                    moodEvent.getDate(),
                    (EmotionalState) emotionalStateSpinner.getSelectedItem(),
                    (SocialSituation) socialSituationSpinner.getSelectedItem(),
                    reasonEditText.getText().toString());

            // overwrites the old mood event in the database
            moodHistoryViewModel.addMoodEvent(newMoodEvent);

            InputMethodManager inputManager = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null)
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

            // Close the current fragment
            Navigation.findNavController(v).popBackStack();
        });
        return view;
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
