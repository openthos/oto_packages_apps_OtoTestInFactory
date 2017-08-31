package com.openthos.factorytest.fragment;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;

import com.openthos.factorytest.MainActivity;
import com.openthos.factorytest.R;

import static android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED;
import static android.bluetooth.BluetoothAdapter.STATE_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_ON;
import static android.bluetooth.BluetoothAdapter.STATE_TURNING_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_TURNING_ON;

/**
 * Created by root on 5/3/17.
 */

public class BluetoothFragment extends Fragment {
    private TextView mBluetoothstate;
    private Button mChangeState;
    private Button mNext;
    private BluetoothAdapter mBluetooth;
    private int mBluetoothState = -1;
    private BroadcastReceiver mBluetoothReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBluetooth = BluetoothAdapter.getDefaultAdapter();
        View view = inflater.inflate(R.layout.test_bluetooth, container, false);
        mChangeState = (Button) view.findViewById(R.id.but_changestate);
        mNext = (Button) view.findViewById(R.id.bt_next);
        mBluetoothstate = (TextView) view.findViewById(R.id.bluetooth_state);
        mChangeState.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeBluetoothState(mBluetoothState);
                mBluetoothState++;
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).checkNextPage();
            }
        });
        register();
        return view;
    }

    private void register() {
        mBluetoothReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                String info = "";
                switch (state) {
                    case (STATE_TURNING_ON):
                        info = "Bluetooth turning on";
                        break;
                    case (STATE_ON):
                        info = "Bluetooth on";
                        break;
                    case (STATE_TURNING_OFF):
                        info = "Bluetooth turning off";
                        break;

                    case (STATE_OFF):
                        info = "Bluetooth off";
                        break;
                    default:
                        info = "Unknown";
                }
                mBluetoothstate.setText(info);
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_STATE_CHANGED);
        getActivity().registerReceiver(mBluetoothReceiver, intentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBluetooth == null) {
            mBluetoothstate.setText("No Bluetooth Device");
            mBluetoothstate.setTextColor(Color.RED);
        } else if (mBluetooth.isEnabled()) {
            mBluetoothState = 1;
            mChangeState.setText(R.string.but_bluetooth_close);
        } else if (!mBluetooth.isEnabled()) {
            mBluetoothState = 0;
            mChangeState.setText(R.string.but_bluetooth_open);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBluetoothReceiver);
        mBluetoothReceiver = null;
    }

    private void changeBluetoothState(int state) {
        switch (state % 2) {
            case 0:
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                startBluetooth();
                mChangeState.setText(R.string.but_bluetooth_close);
                break;
            case 1:
                stopBluetooth();
                mChangeState.setText(R.string.but_bluetooth_open);
                break;
        }
    }

    private void startBluetooth() {
        mBluetooth.enable();
    }

    private void stopBluetooth() {
        mBluetooth.disable();
    }
}
