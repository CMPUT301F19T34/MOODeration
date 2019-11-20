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

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.example.mooderation.EmotionalState;
import com.example.mooderation.MoodEvent;
import com.example.mooderation.R;
import com.example.mooderation.SocialSituation;
import com.example.mooderation.viewmodel.MoodHistoryViewModel;

import java.util.ArrayList;
import java.util.List;

public class EditMoodEventFragment extends Fragment {
    private MoodHistoryViewModel moodHistoryViewModel;

    private Spinner emotionalStateSpinner;
    private Spinner socialSituationSpinner;
    private EditText reasonEditText;
    private Button editButton;
    private ArrayList<MoodEvent> moodEventList;
    private int listPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moodHistoryViewModel = ViewModelProviders.of(getActivity()).get(MoodHistoryViewModel.class);
        moodEventList = new ArrayList<>();
        LiveData<List<MoodEvent>> moodHistory = moodHistoryViewModel.getMoodHistory();
            moodEventList.clear();
            moodEventList.addAll(moodHistory.getValue());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.edit_mood_event_layout,
                container, false);
        // Retrieve location of moodEvent from bundle
        Bundle bundle = getArguments();
        listPosition = bundle.getInt("position");
        final MoodEvent moodEvent = moodEventList.get(listPosition);

        // ViewModel for tracking MoodHistory
        moodHistoryViewModel = ViewModelProviders.of(getActivity()).get(MoodHistoryViewModel.class);

        // find and initialize dateTextView
        TextView dateTextView = view.findViewById(R.id.date_text_view);
        dateTextView.setText(moodEvent.getFormattedDate());

        // find and initialize timeTextView
        TextView timeTextView = view.findViewById(R.id.time_text_view);
        timeTextView.setText(moodEvent.getFormattedTime());

        // find and initialize emotionalStateSpinner
        emotionalStateSpinner = view.findViewById(R.id.emotional_state_spinner);
        emotionalStateSpinner.setAdapter(createAdapter(EmotionalState.class));
        emotionalStateSpinner.setSelection(moodEvent.getEmotionalState().ordinal());

        // find and initialize socialSituationSpinner
        socialSituationSpinner = view.findViewById(R.id.social_situation_spinner);
        socialSituationSpinner.setAdapter(createAdapter(SocialSituation.class));
        socialSituationSpinner.setSelection(moodEvent.getSocialSituation().ordinal());

        // find and initialize reasonEditText
        reasonEditText = view.findViewById(R.id.reason_edit_text);
        reasonEditText.setText(moodEventList.get(listPosition).getReason());

        // find and initialize editButton
        editButton = view.findViewById(R.id.edit_mood_event_button);
        editButton.setOnClickListener((View v) -> {
            MoodEvent newMoodEvent = new MoodEvent(
                    moodEvent.getDate(),
                    (EmotionalState) emotionalStateSpinner.getSelectedItem(),
                    (SocialSituation) socialSituationSpinner.getSelectedItem(),
                    reasonEditText.getText().toString());

            moodHistoryViewModel.removeMoodEvent(moodEvent);
            moodHistoryViewModel.addMoodEvent(newMoodEvent);

            // Close keyboard
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
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
