package com.openthos.factorytest.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.openthos.factorytest.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wang on 17-5-4.
 */

public class SoundFragment extends Fragment {
    private Button mBasicTest;
    private Button mLeftTest;
    private Button mRightTest;
    private Button mLeftAndRightTest;
    private Button mStopTest;
    private Button mNext;
    private PlayThread mBasicThread;
    private PlayThread mSongThread;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test_sound, container, false);
        mBasicTest = (Button) view.findViewById(R.id.bt_basic_test);
        mLeftTest = (Button) view.findViewById(R.id.bt_left_test);
        mRightTest = (Button) view.findViewById(R.id.bt_right_test);
        mLeftAndRightTest = (Button) view.findViewById(R.id.bt_left_and_right_test);
        mStopTest = (Button) view.findViewById(R.id.bt_stop);
        mNext = (Button) view.findViewById(R.id.bt_next);
        mBasicThread = new PlayThread(getActivity(), "basic.wav");
        mBasicThread.setChannel(true, true);

        mSongThread = new PlayThread(getActivity(), "song.wav");

        mBasicTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSongThread.pause();
                if (!mBasicThread.isAlive()) {
                    mBasicThread.start();
                } else {
                    mBasicThread.play();
                }
            }
        });
        mLeftTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBasicThread.pause();
                mSongThread.setChannel(true, false);
                if (!mSongThread.isAlive()) {
                    mSongThread.start();
                } else {
                    mSongThread.play();
                }
            }
        });
        mRightTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBasicThread.pause();
                mSongThread.setChannel(false, true);
                if (!mSongThread.isAlive()) {
                    mSongThread.start();
                } else {
                    mSongThread.play();
                }
            }
        });
        mLeftAndRightTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBasicThread.pause();
                mSongThread.setChannel(true, true);
                mSongThread.play();
                if (!mSongThread.isAlive()) {
                    mSongThread.start();
                }
            }
        });
        mStopTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBasicThread.isAlive()) {
                    mBasicThread.pause();
                    mBasicThread.shut();
                }
                if (mSongThread.isAlive()) {
                    mSongThread.pause();
                    mSongThread.shut();
                }
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 mStopTest.performClick();
                 execDisable();
            }
        });
        return view;
    }

    private void execDisable() {
        try {
            Process pro = Runtime.getRuntime().exec(
                    new String[]{"su","-c","pm disable com.openthos.factorytest"});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class PlayThread extends Thread {
        private int mSampleRateInHz = 44100;
        private int mChannelConfig = AudioFormat.CHANNEL_OUT_MONO;
        // private int mChannelConfig = AudioFormat.CHANNEL_OUT_STEREO;
        private static final String TAG = "PlayThread";
        private Activity mActivity;
        private AudioTrack mAudioTrack;
        private byte[] data;
        private String mFileName;

        public PlayThread(Activity activity, String fileName) {
            mActivity = activity;
            mFileName = fileName;
            int bufferSize = AudioTrack.getMinBufferSize(mSampleRateInHz, mChannelConfig,
                    AudioFormat.ENCODING_PCM_16BIT);
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRateInHz, mChannelConfig,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
        }

        @Override
        public void run() {
            super.run();
            try {
                if (null != mAudioTrack) {
                    mAudioTrack.play();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                InputStream inputStream = mActivity.getResources().getAssets().open(mFileName);

                byte[] buffer = new byte[1024];
                int playIndex = 0;
                boolean isLoaded = false;
                while (null != mAudioTrack
                        && AudioTrack.PLAYSTATE_STOPPED != mAudioTrack.getPlayState()) {
                    int len;
                    if (-1 != (len = inputStream.read(buffer))) {
                        byteArrayOutputStream.write(buffer, 0, len);
                        data = byteArrayOutputStream.toByteArray();
                    } else {
                        isLoaded = true;
                    }

                    if (AudioTrack.PLAYSTATE_PAUSED == mAudioTrack.getPlayState()) {
                    }
                    if (AudioTrack.PLAYSTATE_PLAYING == mAudioTrack.getPlayState()) {
                        playIndex += mAudioTrack.write(data, playIndex, data.length - playIndex);
                        if (isLoaded && playIndex == data.length) {
                            mAudioTrack.stop();
                        }

                        if (playIndex < 0) {
                            mAudioTrack.stop();
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void setBalance(int max, int balance) {
            float b = (float) balance / (float) max;
            if (null != mAudioTrack)
                mAudioTrack.setStereoVolume(1 - b, b);
        }

        public void setChannel(boolean left, boolean right) {
            if (null != mAudioTrack) {
                mAudioTrack.setStereoVolume(left ? 1 : 0, right ? 1 : 0);
                mAudioTrack.play();
            }
        }

        public void pause() {
            if (null != mAudioTrack)
                mAudioTrack.pause();
        }

        public void play() {
            if (null != mAudioTrack) {
                mAudioTrack.play();

            }
        }

        public void shut() {
            releaseAudioTrack();
        }

        private void releaseAudioTrack() {
            if (null != mAudioTrack) {
                mAudioTrack.stop();
                mAudioTrack.release();
                mAudioTrack = null;
            }
        }
    }
}
