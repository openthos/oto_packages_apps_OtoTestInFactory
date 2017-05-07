package com.openthos.factorytest.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.hardware.Camera;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.renderscript.RenderScript;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.openthos.factorytest.R;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by root on 5/2/17.
 */

public class WifiFragment extends Fragment {
    private Button mChangeState;
    private Button mNext;
    private WifiManager mWifiManager;
    private int mWifiState = -1;

    @SuppressLint("WifiManagerLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mWifiManager = (WifiManager) getActivity().getSystemService(WIFI_SERVICE);
        View view = inflater.inflate(R.layout.test_wifi, container, false);
        mChangeState = (Button) view.findViewById(R.id.but_changestate);
        mNext = (Button) view.findViewById(R.id.bt_next);
        mChangeState.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeWifiState();
                mWifiState++;
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = Camera.getNumberOfCameras();
                if (i > 0) {
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment, new CameraFragment()).commit();
                } else {
                    Toast.makeText(getActivity(), "no carema", Toast.LENGTH_SHORT).show();
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment, new BluetoothFragment()).commit();
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            mWifiState = 1;
            mChangeState.setText("off wifi");
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            mWifiState = 0;
            mChangeState.setText("on wifi");
        }
    }

    private void changeWifiState() {
        switch (mWifiState % 2) {
            case 0:
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
                new Thread() {
                    public void run() {
                        mWifiManager.setWifiEnabled(true);
                    }
                }.start();
                break;
            case 1:
                new Thread() {
                    public void run() {
                        mWifiManager.setWifiEnabled(false);
                        mChangeState.post(new Runnable() {
                            @Override
                            public void run() {
                                mChangeState.setText("on wifi");
                            }
                        });
                    }
                }.start();
                break;

        }
    }
}
