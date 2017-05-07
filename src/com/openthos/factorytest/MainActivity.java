package com.openthos.factorytest;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import com.openthos.factorytest.fragment.InfoFragment;

/**
 * Created by wang on 17-4-27.
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getFragmentManager().beginTransaction().add(R.id.fragment, new InfoFragment()).commit();
    }
}
