package com.example.basicweatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsFragment extends Fragment {

    private View mFragmentView;
    private RadioGroup mRadioGroupUnit;
    private RadioGroup mRadioGroupTime;
    private RadioButton mRadioButtonUnits;
    private RadioButton mRadioButtonTime;
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
        setUpCheckedButtonState();

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

    private boolean isMetric() {
        return mSharedPrefs.getBoolean(mUnitPreferenceKey, false);
    }

    private boolean isTwelveHoursPreferred() {
        return mSharedPrefs.getBoolean(mTimePreferenceKey, true);
    }

    private void setUpCheckedButtonState() {
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