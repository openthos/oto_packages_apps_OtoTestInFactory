package com.openthos.factorytest.fragment;

import android.app.Fragment;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.openthos.factorytest.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by root on 5/3/17.
 */

public class InfoFragment extends Fragment {
    private TextView mTimeInfo;
    private TextView mCpuInfo;
    private TextView mMemeryInfo;
    private TextView mGpuInfo;
    private FrameLayout mGpuVIew;
    private Button mRefrsh;
    private Button mNext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test_info, container, false);
        mTimeInfo = (TextView) view.findViewById(R.id.time_info);
        mCpuInfo = (TextView) view.findViewById(R.id.cpu_info);
        mMemeryInfo = (TextView) view.findViewById(R.id.memery_info);
        mGpuInfo = (TextView) view.findViewById(R.id.gpu_info);
        mGpuVIew = (FrameLayout) view.findViewById(R.id.gpu_view);
        mRefrsh = (Button) view.findViewById(R.id.bt_refresh);
        mNext = (Button) view.findViewById(R.id.bt_next);
        initComsTime();
        initCpu();
        initMemery();
        initGpu();
        mRefrsh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initComsTime();
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment, new BatteryFragment()).commit();
            }
        });
        return view;
    }

    private void initComsTime() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                execTime(new String[]{"su", "-c", "hwclock"}, mTimeInfo);

            }
        }.start();
    }

    private void initCpu() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                execCpu(new String[]{"cat", "/proc/cpuinfo"}, mCpuInfo);

            }
        }.start();
    }

    private void initMemery() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                execMemery(new String[]{"cat", "/proc/meminfo"}, mMemeryInfo);

            }
        }.start();
    }

    private void initGpu() {
        final GpuGLSurfaceView gpuGLSurfaceView = new GpuGLSurfaceView(getActivity());
        mGpuVIew.addView(gpuGLSurfaceView);
        mGpuVIew.setVisibility(View.INVISIBLE);
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (gpuGLSurfaceView.getGpuInfo() == null) {
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mGpuInfo.setText(gpuGLSurfaceView.getGpuInfo());
                    }
                });
            }
        }.start();
    }

    private void execTime(String[] commands, final TextView view) {
        String result = "";
        BufferedReader in = null;
        try {
            Process pro = Runtime.getRuntime().exec(commands);
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result = line;
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
        final String temp = result.replace("0.000000 seconds", "");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setText(temp);
            }
        });
    }

    private void execCpu(String[] commands, final TextView view) {
        String result = "";
        BufferedReader in = null;
        boolean isGetedInfo = false;
        int count = 0;
        try {
            Process pro = Runtime.getRuntime().exec(commands);
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("model name")) {
                    if (!isGetedInfo) {
                        result = line.substring(13);
                    }
                    count++;
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
        final String temp = result + " x " + count;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setText(temp);
            }
        });
    }

    private void execMemery(String[] commands, final TextView view) {
        String result = "";
        BufferedReader in = null;
        try {
            Process pro = Runtime.getRuntime().exec(commands);
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("MemTotal")) {
                    result = line.split("\\s+")[1];
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
        DecimalFormat df = new DecimalFormat("#.00");
        final String temp = df.format(Double.parseDouble(result.trim()) / 1024 / 1024) + " GB";
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setText(temp);
            }
        });
    }

    private class GpuRender implements GLSurfaceView.Renderer {
        private String mGpuInfo;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mGpuInfo = gl.glGetString(GL10.GL_RENDERER);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
        }

        @Override
        public void onDrawFrame(GL10 gl) {
        }

        private String getGpuInfo() {
            return mGpuInfo;
        }
    }

    private class GpuGLSurfaceView extends GLSurfaceView {
        private GpuRender mGpuRender;

        public GpuGLSurfaceView(Context context) {
            super(context);
            setEGLConfigChooser(8, 8, 8, 8, 8, 0);
            mGpuRender = new GpuRender();
            setRenderer(mGpuRender);
        }

        private String getGpuInfo() {
            return mGpuRender.getGpuInfo();
        }
    }
}
