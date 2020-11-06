package com.example.basicweatherapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {

    private View mFragmentView;
    private RadioGroup mRadioGroupUnit;
    private RadioGroup mRadioGroupTime;
    private RadioButton mRadioButtonUnits;
    private RadioButton mRadioButtonTime;
    private Button mChooseCityButton;
    private SharedPreferences mSharedPrefs;
    private String mUnitPreferenceKey;
    private String mTimePreferenceKey;
    public static final String PREFERENCE_FILE = "WeatherAppPrefs";

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            mSharedPrefs = getActivity().getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        }

        Places.initialize(Objects.requireNonNull(getContext()), BuildConfig.placesApiKey);
        PlacesClient placesClient = Places.createClient(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_settings, container, false);
        return mFragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnitPreferenceKey = getString(R.string.unit_preference_key);
        mTimePreferenceKey = getString(R.string.time_preference_key);
        mRadioGroupUnit = mFragmentView.findViewById(R.id.toggle_switch_units);
        mRadioGroupTime = mFragmentView.findViewById(R.id.toggle_switch_time);
        mChooseCityButton = mFragmentView.findViewById(R.id.choose_city_btn);
        setUpCheckedButtonState();

        mChooseCityButton.setOnClickListener(v -> startAutoCompleteActivity());

        mRadioGroupUnit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.metric:
                        writeToPreferenceFile(mUnitPreferenceKey, true);
                        break;
                    case R.id.imperial:
                        writeToPreferenceFile(mUnitPreferenceKey,false);
                        break;
                }
            }
        });

        mRadioGroupTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.twelve_hour:
                        writeToPreferenceFile(mTimePreferenceKey, true);
                        break;
                    case R.id.twenty_four_hour:
                        writeToPreferenceFile(mTimePreferenceKey,false);
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                TextView cityTv = mFragmentView.findViewById(R.id.city_zip_code);
                cityTv.setText(place.getName());
                SharedPreferences.Editor editor = mSharedPrefs.edit();
                editor.putString(getString(R.string.city_name), place.getName());
                editor.putString(getString(R.string.city_latitude_key), String.valueOf(Objects.requireNonNull(place.getLatLng()).latitude));
                editor.putString(getString(R.string.city_longitude_key), String.valueOf(place.getLatLng().longitude));
                editor.apply();
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {

            }
        }
    }

    public void startAutoCompleteActivity() {
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG))
                .setTypeFilter(TypeFilter.CITIES)
                .build(getContext());
        startActivityForResult(intent, 2);
    }

    private boolean isMetric() {
        return mSharedPrefs.getBoolean(mUnitPreferenceKey, false);
    }

    private boolean isTwelveHoursPreferred() {
        return mSharedPrefs.getBoolean(mTimePreferenceKey, true);
    }

    private void setUpCheckedButtonState() {
        TextView cityTv = mFragmentView.findViewById(R.id.city_zip_code);
        cityTv.setText(mSharedPrefs.getString(getString(R.string.city_name), ""));
        if (isMetric()) {
            mRadioGroupUnit.check(R.id.metric);
        } else {
            mRadioGroupUnit.check(R.id.imperial);
        }

        if (isTwelveHoursPreferred()) {
            mRadioGroupTime.check(R.id.twelve_hour);
        } else {
            mRadioGroupTime.check(R.id.twenty_four_hour);
        }
    }

    private void writeToPreferenceFile(final String string, final boolean isMetric) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putBoolean(string, isMetric);
        editor.apply();
    }
}