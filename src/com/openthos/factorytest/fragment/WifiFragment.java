package com.openthos.factorytest.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import java.io.IOException;

import com.openthos.factorytest.MainActivity;
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
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, Bundle savedInstanceState) {
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
                try {
                Runtime.getRuntime().exec(
                        new String[]{"su","-c", "rm /data/misc/wifi/*.conf"});
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                ((MainActivity) getActivity()).checkNextPage();
            }
        });
        mChangeState.setText("open wifi");
        return view;
    }

    private void changeWifiState() {
        new Thread() {
            public void run() {
                mWifiManager.setWifiEnabled(true);
            }
        }.start();
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
