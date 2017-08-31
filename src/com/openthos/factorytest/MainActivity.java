package com.openthos.factorytest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.openthos.factorytest.fragment.BatteryFragment;
import com.openthos.factorytest.fragment.BluetoothFragment;
import com.openthos.factorytest.fragment.CameraFragment;
import com.openthos.factorytest.fragment.InfoFragment;
import com.openthos.factorytest.fragment.LcdFragment;
import com.openthos.factorytest.fragment.SoundFragment;
import com.openthos.factorytest.fragment.WifiFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by wang on 17-4-27.
 */

public class MainActivity extends Activity {
    private boolean isContainCarema;
    private boolean isContainBluetooth;
    private boolean isContainWifi;
    private static final int INFO_TEST = 0;
    private static final int BATTERY_TEST = 1;
    private static final int LCD_TEST = 2;
    private static final int WIFI_TEST = 3;
    private static final int CAMERA_TEST = 4;
    private static final int BLUETOOTH_TEST = 5;
    private static final int SOUND_TEST = 6;
    private int currentStep = -1;
    private String text = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initEnvironment();
        checkNextPage();
    }

    private void initEnvironment() {
        int i = Camera.getNumberOfCameras();
        if (i > 0) {
            isContainCarema = true;
        } else {
            isContainCarema = false;
        }

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            if (adapter.getName() != null) {
                isContainBluetooth = true;
            } else {
                isContainBluetooth = false;
            }
        } else {
            isContainBluetooth = false;
        }

        BufferedReader in = null;
        isContainWifi = false;
        try {
            Process pro = Runtime.getRuntime().exec(new String[]{"su", "-c", "netcfg"});
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {

                if (line.contains("wlan")) {
                    isContainWifi = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void checkNextPage() {
        currentStep++;
        switch (currentStep) {
            case INFO_TEST:
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment, new InfoFragment()).commit();
                break;
            case BATTERY_TEST:
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment, new BatteryFragment()).commit();
                break;
            case LCD_TEST:
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment, new LcdFragment()).commit();
                break;
            case WIFI_TEST:
                if (isContainWifi) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment, new WifiFragment()).commit();
                } else {
                    text = "No Wlan!";
                    checkNextPage();
                }
                break;
            case CAMERA_TEST:
                if (isContainCarema) {
                    if (!TextUtils.isEmpty(text)) {
                        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                        text = "";
                    }
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment, new CameraFragment()).commit();

                } else {
                    if (!TextUtils.isEmpty(text)) {
                        text += "\n";
                    }
                    text += "No Camera!";
                    checkNextPage();
                }
                break;
            case BLUETOOTH_TEST:
                if (isContainBluetooth) {
                    if (!TextUtils.isEmpty(text)) {
                        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                        text = "";
                    }
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment, new BluetoothFragment()).commit();

                } else {
                    if (!TextUtils.isEmpty(text)) {
                        text += "\n";
                    }
                    text += "No Bluuetooth!";
                    checkNextPage();
                }
                break;
            case SOUND_TEST:
                if (!TextUtils.isEmpty(text)) {
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                    text = "";
                }
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment, new SoundFragment()).commit();

                break;
        }

    }

    @Override
    public void onBackPressed() {
    }

}
