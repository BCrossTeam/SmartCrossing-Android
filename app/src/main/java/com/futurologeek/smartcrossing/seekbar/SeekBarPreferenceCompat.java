package com.futurologeek.smartcrossing.seekbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.futurologeek.smartcrossing.R;

/**
 * Created by Pavel Sikun on 22.05.16.
 */

public class SeekBarPreferenceCompat extends Preference implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private static final int DEFAULT_CURRENT_VALUE = 50;
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_INTERVAL = 1;
    private static final boolean DEFAULT_DIALOG_ENABLED = true;

    private static final int DEFAULT_DIALOG_STYLE = R.style.MSB_Dialog_Default;

    private int maxValue;
    private int minValue;
    private int interval;
    private int currentValue;
    private String measurementUnit;
    private boolean dialogEnabled;

    private int dialogStyle;

    private TextView valueView;
    private SeekBar seekBarView;
    private TextView measurementView;
    private LinearLayout valueHolderView;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SeekBarPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public SeekBarPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public SeekBarPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SeekBarPreferenceCompat(Context context) {
        super(context);
        init(null);
    }
    private void init(AttributeSet attrs) {
        setLayoutResource(R.layout.seekbar_view_layout);
        loadValuesFromXml(attrs);
    }

    private void loadValuesFromXml(AttributeSet attrs) {
        if(attrs == null) {
            currentValue = DEFAULT_CURRENT_VALUE;
            minValue = DEFAULT_MIN_VALUE;
            maxValue = DEFAULT_MAX_VALUE;
            interval = DEFAULT_INTERVAL;
            dialogEnabled = DEFAULT_DIALOG_ENABLED;
        }
        else {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SeekBarPreference);
            try {
                minValue = a.getInt(R.styleable.SeekBarPreference_msbp_minValue, DEFAULT_MIN_VALUE);
                maxValue = a.getInt(R.styleable.SeekBarPreference_msbp_maxValue, DEFAULT_MAX_VALUE);
                interval = a.getInt(R.styleable.SeekBarPreference_msbp_interval, DEFAULT_INTERVAL);
                dialogEnabled = a.getBoolean(R.styleable.SeekBarPreference_msbp_dialogEnabled, DEFAULT_DIALOG_ENABLED);

                measurementUnit = a.getString(R.styleable.SeekBarPreference_msbp_measurementUnit);
                currentValue = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "defaultValue", DEFAULT_CURRENT_VALUE);

//                TODO make it work:
//                dialogStyle = a.getInt(R.styleable.SeekBarPreference_msbp_interval, DEFAULT_DIALOG_STYLE);

                dialogStyle = DEFAULT_DIALOG_STYLE;
            }
            finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);
        currentValue = getPersistedInt(currentValue);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);

        //needed cuz on prefs-v7 views are clickable by default!
        view.itemView.setClickable(false);

        seekBarView = (SeekBar) view.findViewById(R.id.seekbar);
        measurementView = (TextView) view.findViewById(R.id.measurement_unit);
        valueView = (TextView) view.findViewById(R.id.seekbar_value);

        setMaxValue(maxValue);
        seekBarView.setOnSeekBarChangeListener(this);

        measurementView.setText(measurementUnit);

        setCurrentValue(currentValue);
        valueView.setText(String.valueOf(currentValue));

        valueHolderView = (LinearLayout) view.findViewById(R.id.value_holder);
        valueHolderView.setOnClickListener(dialogEnabled ? this : null);

        if (!isEnabled()) {
            Log.d(TAG, "view is disabled!");
            seekBarView.setEnabled(false);
            valueView.setEnabled(false);
            valueHolderView.setOnClickListener(null);
        }
    }

    @Override
    public void onAttached() {
        super.onAttached();
        currentValue = getPersistedInt(currentValue);
    }

    @Override
    protected boolean persistInt(int value) {
        if(value < minValue) value = minValue;
        if(value > maxValue) value = maxValue;

        currentValue = value;
        return super.persistInt(currentValue);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int newValue = progress + minValue;

        if (newValue > maxValue) {
            newValue = maxValue;
        }
        else if (newValue < minValue) {
            newValue = minValue;
        }
        else if (interval != 1 && newValue % interval != 0) {
            newValue = Math.round(((float) newValue) / interval) * interval;
        }

        if (!callChangeListener(newValue)) {
            seekBar.setProgress(currentValue - minValue);
            return;
        }

        currentValue = newValue;
        valueView.setText(String.valueOf(newValue));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        notifyChanged();
        setCurrentValue(currentValue);
    }

    @Override
    public void onClick(final View v) {
        new CustomValueDialog(getContext(), dialogStyle, minValue, maxValue, currentValue)
                .setOnValueSelectedListener(new OnValueSelectedListener() {
                    @Override
                    public void onValueSelected(int value) {
                        setCurrentValue(value);
                        seekBarView.setOnSeekBarChangeListener(null);
                        seekBarView.setProgress(currentValue - minValue);
                        seekBarView.setOnSeekBarChangeListener(SeekBarPreferenceCompat.this);

                        valueView.setText(String.valueOf(currentValue));
                    }
                })
                .show();
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;

        if (seekBarView != null) {
            if (minValue <= 0 && maxValue >= 0) {
                seekBarView.setMax(maxValue - minValue);
            }
            else {
                seekBarView.setMax(maxValue);
            }

            seekBarView.setProgress(currentValue - minValue);
        }
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
        setMaxValue(maxValue);
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
        persistInt(currentValue);
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
        if(measurementView != null) {
            measurementView.setText(measurementUnit);
        }
    }

    public boolean isDialogEnabled() {
        return dialogEnabled;
    }

    public void setDialogEnabled(boolean dialogEnabled) {
        this.dialogEnabled = dialogEnabled;

        valueHolderView.setOnClickListener(dialogEnabled ? this : null);
    }

    public void setDialogStyle(int dialogStyle) {
        this.dialogStyle = dialogStyle;
    }
}
