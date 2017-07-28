package com.openthos.factorytest.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.io.IOException;

import com.openthos.factorytest.R;

/**
 * Created by root on 5/3/17.
 */

public class CameraFragment extends Fragment {
    private Button mOpenCamera;
    private Button mNext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test_camera, container, false);
        mOpenCamera = (Button) view.findViewById(R.id.but_changecamera);
        mNext = (Button) view.findViewById(R.id.bt_next);
        mOpenCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment, new BluetoothFragment()).commit();

            }
        });
        return view;
    }
}
