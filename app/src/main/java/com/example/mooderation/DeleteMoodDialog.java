package com.example.mooderation;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.example.mooderation.viewmodel.MoodHistoryViewModel;

import java.util.ArrayList;
import java.util.List;


public class DeleteMoodDialog extends DialogFragment {
    ArrayList<MoodEvent> moodEventList;
    private MoodHistoryViewModel moodHistoryViewModel;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        moodHistoryViewModel = ViewModelProviders.of(getActivity()).get(MoodHistoryViewModel .class);
        moodEventList = new ArrayList<>();
        LiveData<List<MoodEvent>> moodHistory = moodHistoryViewModel.getMoodHistory();
        moodEventList.clear();
        moodEventList.addAll(moodHistory.getValue());

        Bundle bundle = getArguments();
        final int listPosition = bundle.getInt("position");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirm Delete");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MoodEvent moodEvent = moodEventList.get(listPosition);
                moodHistoryViewModel.removeMoodEvent(moodEvent);
            }
        });
        return builder.create();

}
}
