package lasting.wifiplayerdemo;

/**
 * 本APP用于缓冲媒体文件，只需要文件输入所在的地址，就能够播放，
 * 局域网内的地址也OK，可搭配WiFiMusicDemo在局域网内使用，
 * 在CMCC-EDU下测试（未登录状态），可以播放WiFiMusicDemo所在地
 * 址内的媒体文件（浏览器中直接访问地址可能由于安全问题会失败）
 */

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    private static final String TAG = "VideoViewDemo";

    private VideoView mVideoView;
    private EditText mPath;
    private ImageButton mPlay;
    private ImageButton mPause;
    private ImageButton mReset;
    private ImageButton mStop;

    private static final int READY_BUFF = 1500 * 1024;
    private static final int CACHE_BUFF = 500 * 1024;

    private File downloadTempFile;
    private TextView playedTextView = null;
    private TextView downloadedTextView = null;

    private boolean isPaused = false;
    private boolean isStoped = false;
    private boolean isReady = false;
    private boolean isError = false;

    private int VideoDuration = 1;
    private int curPosition;
    private int mediaLength = 1;
    private int totalKbRead = 0;
    private SeekBar seekbar = null;
    private double downloadedPer = 0.00;
    private double playedPer = 0.00;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        findView();
        init();
    }

    /**
     * 初始化各控件
     */

    private void findView() {
        mVideoView = (VideoView) findViewById(R.id.videoView);
        mPath = (EditText) findViewById(R.id.path);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        playedTextView = (TextView) findViewById(R.id.played);
        downloadedTextView = (TextView) findViewById(R.id.downloadedTextView);
        mPlay = (ImageButton) findViewById(R.id.play);
        mPause = (ImageButton) findViewById(R.id.pause);
        mReset = (ImageButton) findViewById(R.id.reset);
        mStop = (ImageButton) findViewById(R.id.stop);
    }

    private void init() {
        seekbar.setMax(100);

        //媒体文件的路径
        mPath.setText("http://192.168.1.101:6666/sdcard/01.mp3");

        mVideoView.setOnPreparedListener(preparedListener);
        mVideoView.setOnCompletionListener(CompletionListener);
        mVideoView.setOnErrorListener(ErrorListener);

        //主界面与播放界面的跳转按钮点击事件
        mPlay.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoPlayer.class);
                intent.putExtra("url", mPath.getText().toString());
                intent.putExtra("cache",
                        Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/VideoCache/" + System.currentTimeMillis() + ".mp4");
                startActivity(intent);
            }
        });

        mPause.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (mVideoView.isPlaying() && mVideoView.canPause()) {
                    mVideoView.pause();
                    isPaused = true;
                }
            }
        });

        mReset.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (mVideoView != null) {
                    mVideoView.stopPlayback();
                    downloadTempFile.delete();
                }
            }
        });

        mStop.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (mVideoView.isPlaying()) {
                    mVideoView.stopPlayback();
                    isStoped = true;
                }
            }
        });

    }

    //实现MediaPlayer类中的OnPreparedListener接口
    private MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            Log.d(TAG, "OnPreparedListener");
            myHandler.sendEmptyMessage(VIDEO_STATE_UPDATE);
            mVideoView.seekTo(curPosition);
            mp.start();
        }
    };

    //实现MediaPlayer类中的OnCompletionListener接口
    private MediaPlayer.OnCompletionListener CompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer arg0) {
            Log.d(TAG, "OnCompletionListener");
            curPosition = 0;
            mVideoView.stopPlayback();
        }
    };

    //实现MediaPlayer类中的OnErrorListener接口
    private MediaPlayer.OnErrorListener ErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.d(TAG, "OnErrorListener");
            isError = true;
            mVideoView.stopPlayback();
            return true;
        }
    };

    //4个状态
    private final static int VIDEO_STATE_UPDATE = 0;
    private final static int CACHE_VIDEO_READY = 1;
    private final static int CACHE_VIDEO_UPDATE = 2;
    private final static int CACHE_VIDEO_END = 3;

    //创建handler对象用于传递信息
    Handler myHandler = new Handler() {
        //重写Handler类中的可选方法handleMessage
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VIDEO_STATE_UPDATE:
                    Log.d(TAG, "VIDEO_STATE_UPDATE");
                    //下载百分比
                    downloadedPer = totalKbRead * 100.00 / mediaLength * 1.0;
                    //设置第二进度条
                    seekbar.setSecondaryProgress((int) downloadedPer);
                    //已下载百分比显示
                    downloadedTextView.setText(String.format("[%.4f]", downloadedPer));

                    if (mVideoView.isPlaying()) {
                        //获取媒体文件持续时间
                        VideoDuration = mVideoView.getDuration() + 1;
                        //获取当前播放位置
                        curPosition = mVideoView.getCurrentPosition();
                        //正在播放百分比
                        playedPer = curPosition * 100.00 / VideoDuration * 1.0;
                        //设置主进度条
                        seekbar.setProgress((int) playedPer);
                        //i默认单位为毫秒，以下是单位转换
                        int i = curPosition;
                        i = i / 1000;
                        //经下列转换，格式为XX时XX分XX秒
                        int minute = i / 60;
                        int hour = minute / 60;
                        int second = i % 60;
                        minute = minute % 60;
                        playedTextView.setText(String.format(
                                "%02d:%02d:%02d[%.4f][%.4f]", hour, minute, second,
                                playedPer, downloadedPer));
                    }
                    //延时发送消息
                    myHandler.sendEmptyMessageDelayed(VIDEO_STATE_UPDATE, 1000);
                    break;

                case CACHE_VIDEO_READY:
                    Log.d(TAG, "CACHE_VIDEO_READY");
                    isReady = true;
                    mVideoView.setVideoPath(downloadTempFile.getAbsolutePath());
                    mVideoView.start();
                    break;

                case CACHE_VIDEO_UPDATE:
                    Log.d(TAG, "CACHE_VIDEO_UPDATE");
                    //缓存文件状态更新
                    if (isError) {
                        mVideoView.setVideoPath(downloadTempFile.getAbsolutePath());
                        mVideoView.start();
                        isError = false;
                    }
                    break;

                case CACHE_VIDEO_END:
                    Log.d(TAG, "CACHE_VIDEO_END");
                    //缓存文件结束时捕获错误
                    if (isError) {
                        mVideoView.setVideoPath(downloadTempFile.getAbsolutePath());
                        mVideoView.start();
                        isError = false;
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public long getFileSize(String urlString) {
        int fileLength = -1;
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            httpURLConnection.setRequestProperty("User-Agent", "NetFox");
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode >= 400) {
                //-2代表error
                return -2;
            }
            String header;
            for (int i = 1; ; i++) {
                header = httpURLConnection.getHeaderFieldKey(i);
                if (header != null) {
                    if (header.equals("content-length")) {
                        fileLength = Integer.parseInt(httpURLConnection
                                .getHeaderField(header));
                        break;
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileLength;
    }

    private void playFromNet(String mediaUrl) {
        FileOutputStream fos = null;
        InputStream is = null;

        try {
            URL url = new URL(mediaUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();

            downloadTempFile = new File(
                    android.os.Environment.getExternalStorageDirectory() + "/"
                            + "carey.mp4");
            //若已存在（即上一次播放产生的），则删除
            if (downloadTempFile.exists()) {
                downloadTempFile.delete();
                downloadTempFile.createNewFile();
            }

            fos = new FileOutputStream(downloadTempFile, true);

            FileInputStream fis = new FileInputStream(downloadTempFile);
            totalKbRead = fis.available();

            httpURLConnection.setRequestProperty("User-Agent", "NetFox");
            String property = "bytes=" + totalKbRead + "-";
            httpURLConnection.setRequestProperty("RANGE", property);

            is = httpURLConnection.getInputStream();
            if (is == null) {
                return;
            }

            mediaLength = httpURLConnection.getContentLength();

            byte buffer[] = new byte[4 * 1024];
            int readLength = 0;
            int oldLength = 0;

            myHandler.sendEmptyMessage(VIDEO_STATE_UPDATE);

            while ((readLength = is.read(buffer)) != -1 && !isStoped) {
                try {
                    fos.write(buffer, 0, readLength);
                    totalKbRead = totalKbRead + readLength;
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

                if (!isReady) {
                    if ((totalKbRead - oldLength) > READY_BUFF) {
                        oldLength = totalKbRead;
                        myHandler.sendEmptyMessage(CACHE_VIDEO_READY);
                    }
                } else {
                    if ((totalKbRead - oldLength) > CACHE_BUFF) {
                        oldLength = totalKbRead;
                        myHandler.sendEmptyMessage(CACHE_VIDEO_UPDATE);
                    }
                }

                if (totalKbRead == mediaLength) {
                    myHandler.sendEmptyMessage(CACHE_VIDEO_END);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }

            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private class PlayThread extends Thread {
        private String url;

        PlayThread(String url) {
            this.url = url;
        }

        public void run() {
            if (URLUtil.isNetworkUrl(url)) {
                playFromNet(url);
            }
        }
    }
}
