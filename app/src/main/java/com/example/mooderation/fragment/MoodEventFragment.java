package com.example.mooderation.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.example.mooderation.LocationDisabledDialog;
import com.example.mooderation.MoodLatLng;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.example.mooderation.EmotionalState;
import com.example.mooderation.LocationDeniedDialog;
import com.example.mooderation.MoodEventConstants;
import com.example.mooderation.R;
import com.example.mooderation.SocialSituation;
import com.example.mooderation.viewmodel.MoodEventViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class MoodEventFragment extends Fragment implements AdapterView.OnItemSelectedListener, TextWatcher{
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private MoodEventViewModel moodEventViewModel;

    private TextView dateTextView;
    private TextView timeTextView;
    private Spinner emotionalStateSpinner;
    private Spinner socialSituationSpinner;
    private EditText reasonEditText;
    private FusedLocationProviderClient fusedLocationClient;
    private Switch locationSwitch;

    // views related to taking and displaying images
    private ViewFlipper viewFlipper;
    private ImageView imageView;

    // the local version of the image
    private Uri imageUri;
    private File imageFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moodEventViewModel = ViewModelProviders.of(getActivity()).get(MoodEventViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mood_event_layout,
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

        // for switching between view with photo and without
        viewFlipper = view.findViewById(R.id.photo_view_flipper);
        imageView = view.findViewById(R.id.mood_image_view);

        // add the photo to the mood event
        Button takePhotoButton = view.findViewById(R.id.take_photo_button);
        takePhotoButton.setOnClickListener(v -> {
            dispatchCameraIntent();
        });

        // delete the photo from the mood event
        Button deletePhotoButton = view.findViewById(R.id.delete_photo_button);
        deletePhotoButton.setOnClickListener(v -> {
            moodEventViewModel.deleteImage();
            viewFlipper.setDisplayedChild(0);
        });

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
                locationSwitch.setEnabled(false);
            }
            if(moodEventViewModel.getLocationToggleState().getValue()) {
                locationSwitch.setChecked(true);
                locationSwitch.setText("Location attached");
            } else {
                locationSwitch.setChecked(false);
                locationSwitch.setText("Location not attached");
            }

            if (moodEvent.getImagePath() != null) {
                viewFlipper.setDisplayedChild(1);
                moodEventViewModel.downloadImage().addOnSuccessListener(bytes -> {
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(imageBitmap);
                    viewFlipper.setDisplayedChild(2);
                });
            }
        });

        locationSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            // prevents location from being changed once set
            if(!moodEventViewModel.getIsEditing().getValue()) {
                if(isChecked) {
                    // check if location is turned on
                    LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                    if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        locationSwitch.setChecked(false);
                        locationSwitch.setText("Location not attached");
                        moodEventViewModel.setLocationToggleState(false);
                        openLocationDisabledDialog();
                    } else {
                        // request permission if permission is not already granted
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                        } else {
                            locationSwitch.setChecked(true);
                            locationSwitch.setText("Location attached");
                            moodEventViewModel.setLocationToggleState(true);
                        }
                    }
                } else {
                    locationSwitch.setText("Location not attached");
                    moodEventViewModel.setLocationToggleState(false);
                }
            }else {
                if(moodEventViewModel.getMoodEvent().getValue().getLocation() != null) {
                    locationSwitch.setChecked(true);
                    locationSwitch.setText("Location attached");
                    moodEventViewModel.setLocationToggleState(true);
                } else {
                    locationSwitch.setChecked(false);
                    locationSwitch.setText("Location not attached");
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
                    } else {
                        Toast.makeText(getContext(), "Unable to add location", Toast.LENGTH_SHORT).show();
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

    // allocates a file where an image can be stored.
    // from: https://developer.android.com/training/camera/photobasics
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getCacheDir();
        //File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    private void dispatchCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            imageFile = null;
            try {
                imageFile = createImageFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            if (imageFile != null) {
                imageUri = FileProvider.getUriForFile(
                        getContext(), "com.example.android.fileprovider", imageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // called when returning from camera intent
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            viewFlipper.setDisplayedChild(1);
            moodEventViewModel.uploadImage(imageUri);
        }
        // delete local version of file
        if (imageFile != null) {
            imageFile.delete(); // TODO check return result
        }
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
            locationSwitch.setText("Location not attached");
            moodEventViewModel.setLocationToggleState(false);
            if(!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                openPermissionDialog();
            }
        } else {
            locationSwitch.setChecked(true);
            locationSwitch.setText("Location attached");
            moodEventViewModel.setLocationToggleState(true);
        }
    }

    /**
     * Displays a dialog box instructing the user how to turn on location permission if they
     * selected "deny and never show again"
     */
    public void openPermissionDialog() {
        LocationDeniedDialog locationDeniedDialog = new LocationDeniedDialog();
        locationDeniedDialog.show(getFragmentManager(), "Location Denied");
    }

    /**
     * Displays a dialog box when a user attempts to attach location to a Mood Event but their
     * device's location services are disabled
     */
    public void openLocationDisabledDialog() {
        LocationDisabledDialog locationDisabledDialog = new LocationDisabledDialog();
        locationDisabledDialog.show(getFragmentManager(), "Location Off");
    }

    // for listening updating the mood event when the spinners are updated
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        moodEventViewModel.updateMoodEvent(moodEvent -> {
            if (parent == emotionalStateSpinner) {
                moodEvent.setEmotionalState((EmotionalState) parent.getItemAtPosition(position));
            }
            else if (parent == socialSituationSpinner) {
                moodEvent.setSocialSituation((SocialSituation) parent.getItemAtPosition(position));
            }
            return moodEvent;
        });
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
        moodEventViewModel.updateMoodEvent(moodEvent -> {
            moodEvent.setReason(s.toString());
            return moodEvent;
        });
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

