package com.openthos.factorytest.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.openthos.factorytest.MainActivity;
import com.openthos.factorytest.R;

import java.lang.reflect.Method;
import java.util.HashMap;

import static android.os.BatteryManager.BATTERY_STATUS_CHARGING;
import static android.os.BatteryManager.BATTERY_STATUS_DISCHARGING;
import static android.os.BatteryManager.BATTERY_STATUS_FULL;
import static android.os.BatteryManager.BATTERY_STATUS_NOT_CHARGING;
import static android.os.BatteryManager.BATTERY_STATUS_UNKNOWN;

/**
 * Created by root on 5/4/17.
 */

public class BatteryFragment extends Fragment {
    private TextView mType;
    private TextView mLevel;
    private TextView mVoltage;
    private TextView mStatus;
    private BroadcastReceiver mBatteryReceiver;
    private Button mNext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test_battery, container, false);
        mType = (TextView) view.findViewById(R.id.battery_type);
        mLevel = (TextView) view.findViewById(R.id.battery_level);
        mVoltage = (TextView) view.findViewById(R.id.battery_voltage);
        mStatus = (TextView) view.findViewById(R.id.battery_status);
        mNext = (Button) view.findViewById(R.id.bt_next);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).checkNextPage();
            }
        });
        regiester();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mBatteryReceiver);
        mBatteryReceiver = null;
    }

    private void regiester() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mBatteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                String type = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
                int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
                mType.setText(type);
                mLevel.setText(level+ " %");
                mVoltage.setText(voltage + " mv");
                switch (status){
                    case BATTERY_STATUS_UNKNOWN:
                        mStatus.setText("BATTERY_STATUS_UNKNOWN");
                        break;
                    case BATTERY_STATUS_CHARGING:
                        mStatus.setText("BATTERY_STATUS_CHARGING");
                        break;
                    case BATTERY_STATUS_DISCHARGING:
                        mStatus.setText("BATTERY_STATUS_DISCHARGING");
                        break;
                    case BATTERY_STATUS_NOT_CHARGING:
                        mStatus.setText("BATTERY_STATUS_NOT_CHARGING");
                        break;
                    case BATTERY_STATUS_FULL:
                        mStatus.setText("BATTERY_STATUS_FULL");
                        break;

                }
            }
        };
        getActivity().registerReceiver(mBatteryReceiver, filter);
    }
}
