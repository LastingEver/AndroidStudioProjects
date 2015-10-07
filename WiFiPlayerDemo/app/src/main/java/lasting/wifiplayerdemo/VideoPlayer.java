package lasting.wifiplayerdemo;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.coremedia.iso.IsoFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoPlayer extends Activity {
    private static final int READY_BUFF = 200 * 1024;
    private static final int DISSMISS_CTLPANEL_TIME = 5 * 1000;

    private LinearLayout llPlayControl, llLoading;
    private ImageButton playButton;
    private SurfaceView surfaceView;
    private TextView curTime, totalTime;
    private SeekBar videoSeekBar;

    private String remoteUrl;
    private String localUrl;
    private VideoDownloder vdl;

    private MediaPlayer mediaPlayer;

    private boolean isReady = false;
    private long videoTotalSize = 0;
    private long videoCacheSize = 0;
    private int seekPosition = 0;
    private boolean isLoading = false;
    private boolean userPaused = false;

    final Runnable hideCtlPanel = new Runnable() {
        @Override
        public void run() {
            llPlayControl.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoplayer);

        findView();
        init();
        playVideo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isReady) {
            mHandler.removeMessages(VIDEO_STATE_UPDATE);
            mHandler.sendEmptyMessageDelayed(VIDEO_STATE_UPDATE, 1000);
        }
    }

    @Override
    protected void onStop() {
        mHandler.removeMessages(VIDEO_STATE_UPDATE);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        super.onStop();
    }

    private void findView() {
        this.surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        this.curTime = (TextView) findViewById(R.id.curTime);
        this.totalTime = (TextView) findViewById(R.id.totalTime);
        this.videoSeekBar = (SeekBar) findViewById(R.id.videoSeekBar);
        this.llLoading = (LinearLayout) findViewById(R.id.llLoading);
        this.playButton = (ImageButton) findViewById(R.id.playButton);
        this.llPlayControl = (LinearLayout) findViewById(R.id.llPlayControl);
    }

    private void initParams() {
        //获取intent连接状态
        Intent intent = getIntent();
        //通过key取到前一个activity传递过来的value
        this.remoteUrl = intent.getStringExtra("url");
        System.out.println("remoteUrl: " + remoteUrl);

        if (this.remoteUrl == null) {
            finish();
            return;
        }

        this.localUrl = intent.getStringExtra("cache");
        if (this.localUrl == null) {
            this.localUrl = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/VideoCache/" + System.currentTimeMillis() + ".mp4";
        }
        System.out.println("localUrl: " + this.localUrl);
    }

    private void createMediaPlayer(SurfaceHolder holder) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        mediaPlayer.reset();
        mediaPlayer.setDisplay(holder);

        mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                if (!isReady) {
                    isReady = true;
                    vdl.initVideoDownloder(videoCacheSize, videoTotalSize);
                    mHandler.sendEmptyMessage(VIDEO_STATE_UPDATE);
                }

                hideLoading();
                mp.start();
            }
        });

        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.pause();
                mHandler.removeCallbacks(hideCtlPanel);
                if (!llPlayControl.isShown()) {
                    llPlayControl.setVisibility(View.VISIBLE);
                }
            }
        });

        mediaPlayer.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mp.reset();
                showLoading();

                return true;
            }
        });
    }

    private void destroyMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.setDisplay(null);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void init() {
        initParams();

        this.vdl = new VideoDownloder(mHandler, remoteUrl, localUrl);

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(new Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                destroyMediaPlayer();
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                createMediaPlayer(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                mediaPlayer.setDisplay(holder);
            }
        });

        this.videoSeekBar.setProgress(0);
        this.videoSeekBar.setMax(100);
        this.videoSeekBar
                .setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekbar) {
                        showLoading();
                        mediaPlayer.seekTo(seekPosition);
                        vdl.seekLoadVideo(seekPosition / 1000);
                        userPaused = false;
                        mHandler.postDelayed(hideCtlPanel,
                                DISSMISS_CTLPANEL_TIME);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekbar) {
                        mediaPlayer.pause();
                        userPaused = true;
                        mHandler.removeCallbacks(hideCtlPanel);
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekbar, int i,
                                                  boolean flag) {
                        if (userPaused) {
                            seekPosition = i * mediaPlayer.getDuration()
                                    / seekbar.getMax();
                            curTime.setText(transtimetostr(seekPosition));
                        }
                    }
                });

        surfaceView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!llPlayControl.isShown()) {
                        llPlayControl.setVisibility(View.VISIBLE);
                    }

                    mHandler.removeCallbacks(hideCtlPanel);

                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mHandler.postDelayed(hideCtlPanel, DISSMISS_CTLPANEL_TIME);

                    return true;
                }

                return false;
            }
        });

        llPlayControl.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionevent) {
                if (motionevent.getAction() == MotionEvent.ACTION_DOWN) {
                    mHandler.removeCallbacks(hideCtlPanel);

                    return true;
                } else if (motionevent.getAction() == MotionEvent.ACTION_UP) {
                    mHandler.postDelayed(hideCtlPanel, DISSMISS_CTLPANEL_TIME);

                    return true;
                }

                return false;
            }
        });

        playButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isReady || isLoading) {
                    return;
                }

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    userPaused = true;
                } else {
                    mediaPlayer.start();
                    userPaused = false;
                }
            }
        });
    }

    private String transtimetostr(int time) {
        int i = time / 1000;
        int hour = i / (60 * 60);
        int minute = i / 60 % 60;
        int second = i % 60;

        StringBuilder sb = new StringBuilder();
        sb.append(hour >= 10 ? hour : "0" + hour);
        sb.append(":");
        sb.append(minute >= 10 ? minute : "0" + minute);
        sb.append(":");
        sb.append(second >= 10 ? second : "0" + second);

        return sb.toString();
    }

    private void showLoading() {
        isLoading = true;

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                llLoading.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideLoading() {
        isLoading = false;

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                llLoading.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * 下载和缓冲mp4文件头部数据，MP4文件的信息都存在头部
     */
    private void prepareVideo() throws IOException {
        URL url = new URL(remoteUrl);
        HttpURLConnection httpConnection = (HttpURLConnection) url
                .openConnection();
        httpConnection.setConnectTimeout(3000);
        httpConnection.setRequestProperty("RANGE", "bytes=" + 0 + "-");

        InputStream is = httpConnection.getInputStream();

        videoTotalSize = httpConnection.getContentLength();
        if (videoTotalSize == -1) {
            return;
        }

        File cacheFile = new File(localUrl);

        if (!cacheFile.exists()) {
            cacheFile.getParentFile().mkdirs();
            cacheFile.createNewFile();
        }

        RandomAccessFile raf = new RandomAccessFile(cacheFile, "rws");
        raf.setLength(videoTotalSize);
        raf.seek(0);

        byte buf[] = new byte[10 * 1024];
        int size = 0;

        videoCacheSize = 0;
        int buffercnt = 0;
        while ((size = is.read(buf)) != -1 && (!isReady)) {
            try {
                raf.write(buf, 0, size);
                videoCacheSize += size;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (videoCacheSize > READY_BUFF && (buffercnt++ % 20 == 0)) {
                mHandler.sendEmptyMessage(CACHE_VIDEO_READY);
            }
        }

        if (!isReady) {
            mHandler.sendEmptyMessage(CACHE_VIDEO_READY);
        }

        is.close();
        raf.close();
    }

    private void playVideo() {
        if (!URLUtil.isNetworkUrl(this.remoteUrl)) {
            try {
                mediaPlayer.setDataSource(this.remoteUrl);
                mediaPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }

        showLoading();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    prepareVideo();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        finishVideoPlay();
    }

    private void finishVideoPlay() {
        if (URLUtil.isNetworkUrl(this.remoteUrl)
                && (videoCacheSize == videoTotalSize)) {
            System.out.println("add cache: " + this.remoteUrl + " --> "
                    + this.localUrl);
        }

        vdl.cancelDownload();
        finish();
    }

    private final static int VIDEO_STATE_UPDATE = 0;
    private final static int CACHE_VIDEO_READY = 1;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VIDEO_STATE_UPDATE:
                    int cachepercent = (int) (videoCacheSize * 100 / (videoTotalSize == 0 ? 1
                            : videoTotalSize));
                    videoSeekBar.setSecondaryProgress(cachepercent);

                    int positon = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();
                    int playpercent = positon * 100
                            / (duration == 0 ? 1 : duration);

                    if (mediaPlayer.isPlaying()) {
                        playButton.setImageResource(R.drawable.player_pad_button_pause_normal);

                        curTime.setText(transtimetostr(positon));
                        totalTime.setText(transtimetostr(duration));
                        videoSeekBar.setProgress(playpercent);

                        int next2sec = positon + 2 * 1000;
                        if (next2sec > duration) {
                            next2sec = duration;
                        }

                        if (!vdl.checkIsBuffered(next2sec / 1000)) {
                            mediaPlayer.pause();
                            showLoading();
                        }
                    } else {
                        playButton.setImageResource(R.drawable.player_pad_button_play_normal);

                        if (!userPaused && isLoading) {
                            int next5sec = positon + 5 * 1000;
                            if (next5sec > duration) {
                                next5sec = duration;
                            }

                            if (vdl.checkIsBuffered(next5sec / 1000)) {
                                mediaPlayer.start();
                                hideLoading();
                            }
                        }
                    }

                    mHandler.sendEmptyMessageDelayed(VIDEO_STATE_UPDATE, 1000);
                    break;

                case CACHE_VIDEO_READY:
                    try {
                        mediaPlayer.setDataSource(localUrl);
                        mediaPlayer.prepareAsync();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case VideoDownloder.MSG_DOWNLOAD_FINISH:
                    videoCacheSize = videoTotalSize;
                    break;

                case VideoDownloder.MSG_DOWNLOAD_UPDATE:
                    if ((Long) msg.obj > videoCacheSize) {
                        videoCacheSize = (Long) msg.obj;
                    }
                    break;
            }

            super.handleMessage(msg);
        }
    };
}

class VideoDownloder {
    private String url, localFilePath;
    private Handler handler;
    private boolean isInitOK = false;
    private int downloadVideoIndex = 0;

    private final ArrayList<VideoInfo> videoLists = new ArrayList<VideoDownloder.VideoInfo>();
    private final ExecutorService executorService = Executors
            .newFixedThreadPool(5);

    /* 以5s为分割点进行视频分段 */
    private static final int SEP_SECOND = 5;

    public static final int MSG_DOWNLOAD_UPDATE = 101;
    public static final int MSG_DOWNLOAD_FINISH = 102;

    public VideoDownloder(Handler handler, String url, String localFilePath) {
        this.handler = handler;
        this.url = url;
        this.localFilePath = localFilePath;
    }

    public void initVideoDownloder(final long startOffset, final long totalSize) {
        if (isInitOK) {
            return;
        }

        this.executorService.submit(new Runnable() {

            @Override
            public void run() {
                IsoFile isoFile = null;
                try {
                    isoFile = new IsoFile(new RandomAccessFile(localFilePath,
                            "r").getChannel());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (isoFile == null) {
                    return;
                }

                CareyMp4Parser cmp4p = new CareyMp4Parser(isoFile);
                cmp4p.printInfo();

                videoLists.clear();

                VideoInfo vi = null;
                for (int i = 0; i < cmp4p.syncSamples.length; i++) {
                    if (vi == null) {
                        vi = new VideoInfo();
                        vi.timeStart = cmp4p.timeOfSyncSamples[i];
                        vi.offsetStart = cmp4p.syncSamplesOffset[i];
                    }

                    if (cmp4p.timeOfSyncSamples[i] < (videoLists.size() + 1)
                            * SEP_SECOND) {
                        continue;
                    }

                    vi.offsetEnd = cmp4p.syncSamplesOffset[i];
                    videoLists.add(vi);
                    vi = null;
                    i--;
                }

                if (vi != null) {
                    vi.offsetEnd = totalSize;
                    videoLists.add(vi);
                    vi = null;
                }

                isInitOK = true;

                downloadVideo(startOffset);
            }
        });
    }

    public void cancelDownload() {
        this.executorService.shutdown();
    }

    /**
     * 加载第 time 秒开始的数据流
     */
    public synchronized void seekLoadVideo(long time) {
        int index = -1;

        for (VideoDownloder.VideoInfo temp : videoLists) {
            if (temp.timeStart > time) {
                break;
            }
            index++;
        }

        if (index < 0 || index >= this.videoLists.size()) {
            return;
        }

        final VideoInfo vi = this.videoLists.get(index);

        if (vi.status == DownloadStatus.ORIGINAL) {
            executorService.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        vi.status = DownloadStatus.DOWNLOADING;
                        downloadByVideoInfo(vi);
                        vi.status = DownloadStatus.FINISHED;
                    } catch (IOException e) {
                        vi.status = DownloadStatus.ORIGINAL;
                        e.printStackTrace();
                    }
                }
            });
        }

        downloadVideoIndex = index;
    }

    /**
     * 检测指定时间的视频是否已经缓存好了
     */
    public boolean checkIsBuffered(long time) {
        int index = -1;

        for (VideoDownloder.VideoInfo temp : videoLists) {
            if (temp.timeStart > time) {
                break;
            }

            index++;
        }

        if (index < 0 || index >= this.videoLists.size()) {
            return true;
        }

        final VideoInfo vi = this.videoLists.get(index);

        if (vi.status == DownloadStatus.FINISHED) {
            return true;
        } else if (vi.status == DownloadStatus.ORIGINAL) {
            return false;
        } else if (vi.status == DownloadStatus.DOWNLOADING) {
            return (vi.downloadSize * 100 / (vi.offsetEnd - vi.offsetStart)) > ((time - vi.timeStart) * 100 / SEP_SECOND);
        }

        return true;
    }

    /**
     * 检测是否全部视频模块都已下载完毕
     */
    private boolean isAllFinished() {
        for (VideoDownloder.VideoInfo vi : videoLists) {
            if (vi.status != DownloadStatus.FINISHED) {
                return false;
            }
        }

        return true;
    }

    private void downloadVideo(long startOffset) {
        this.downloadVideoIndex = 0;

        for (VideoDownloder.VideoInfo temp : videoLists) {
            if (temp.offsetStart > startOffset) {
                break;
            }
            // 标记已下载
            temp.status = DownloadStatus.FINISHED;
            this.downloadVideoIndex++;
        }

        while (!isAllFinished()) {
            VideoInfo vi = this.videoLists
                    .get(this.downloadVideoIndex %= this.videoLists.size());

            if (vi.status == DownloadStatus.ORIGINAL) {
                try {
                    vi.status = DownloadStatus.DOWNLOADING;
                    downloadByVideoInfo(vi);
                    vi.status = DownloadStatus.FINISHED;
                } catch (IOException e) {
                    e.printStackTrace();
                    vi.status = DownloadStatus.ORIGINAL;
                }
            }

            this.downloadVideoIndex++;
        }

        handler.sendEmptyMessage(MSG_DOWNLOAD_FINISH);
    }

    /**
     * 下载一段视频
     */
    private void downloadByVideoInfo(VideoInfo vi) throws IOException {
        System.out.println("download -> " + vi.toString());

        URL url = new URL(this.url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(3000);
        httpURLConnection.setRequestProperty("Range", "bytes=" + vi.offsetStart + "-"
                + vi.offsetEnd);

        RandomAccessFile raf = new RandomAccessFile(new File(localFilePath),
                "rws");
        raf.seek(vi.offsetStart);

        InputStream is = httpURLConnection.getInputStream();

        byte[] buffer = new byte[1024 * 10];
        int length;
        vi.downloadSize = 0;
        while ((length = is.read(buffer)) != -1) {
            raf.write(buffer, 0, length);
            vi.downloadSize = vi.downloadSize + length;
            Message msg = handler.obtainMessage();
            msg.what = MSG_DOWNLOAD_UPDATE;
            msg.obj = vi.offsetStart + vi.downloadSize;
            handler.sendMessage(msg);
        }

        is.close();
        raf.close();
    }

    enum DownloadStatus {
        ORIGINAL, DOWNLOADING, FINISHED,
    }

    class VideoInfo {
        double timeStart;
        long offsetStart;
        long offsetEnd;
        long downloadSize;
        DownloadStatus status;

        public VideoInfo() {
            status = DownloadStatus.ORIGINAL;
        }

        @Override
        public String toString() {
            String string = "beginTime: <" + timeStart + ">, fileoffset("
                    + offsetStart + " -> " + offsetEnd + "), isfinish: "
                    + status;

            return string;
        }
    }
}
