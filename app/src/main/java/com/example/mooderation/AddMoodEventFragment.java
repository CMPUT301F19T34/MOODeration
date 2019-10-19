package com.example.mooderation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddMoodEventFragment extends DialogFragment {
    private OnFragmentInteractionListener listener;

    private Spinner emotionalStateSpinner;
    private Spinner socialSituationSpinner;

    public interface OnFragmentInteractionListener {
        void onPositiveClick(MoodEvent moodEvent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentListener");
        }
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.add_mood_event_fragment, null);

        emotionalStateSpinner = view.findViewById(R.id.emotional_state_spinner);
        emotionalStateSpinner.setAdapter(getSpinnerAdapter(R.array.emotional_state_array));

        socialSituationSpinner = view.findViewById(R.id.social_situation_spinner);
        socialSituationSpinner.setAdapter(getSpinnerAdapter(R.array.social_situation_array));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setTitle(R.string.add_mood_event_title);
        builder.setNegativeButton(R.string.add_mood_event_negative, null);
        builder.setPositiveButton(R.string.add_mood_event_positive, null);

        return builder.create();
    }

    private ArrayAdapter<CharSequence> getSpinnerAdapter(int resource) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(), resource, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }
}
