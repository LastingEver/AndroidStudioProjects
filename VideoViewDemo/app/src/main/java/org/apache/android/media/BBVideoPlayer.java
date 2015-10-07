package org.apache.android.media;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

public class BBVideoPlayer extends Activity {
	private static final int READY_BUFF = 200 * 1024;
	private static final int DISSMISS_CTLPANEL_TIM = 5 * 1000;

	private LinearLayout llplaycontrol, llloading;
	private ImageButton playbtn;
	private SurfaceView surfaceview;
	private TextView tvcurtime, tvtotaltime;
	private SeekBar videoseekbar;

	private String remoteUrl;
	private String localUrl;
	private VideoDownloder vdl;

	private MediaPlayer mediaPlayer;

	private boolean isready = false;
	private long videoTotalSize = 0;
	private long videoCacheSize = 0;
	private int seekPosition = 0;
	private boolean isloading = false;
	private boolean userpaused = false;

	final Runnable hidectlpanel = new Runnable() {

		@Override
		public void run() {
			llplaycontrol.setVisibility(View.INVISIBLE);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bbvideoplayer);

		findViews();
		init();
		playvideo();
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (isready) {
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

	private void findViews() {
		this.surfaceview = (SurfaceView) findViewById(R.id.surfaceview);
		this.tvcurtime = (TextView) findViewById(R.id.tvcurtime);
		this.tvtotaltime = (TextView) findViewById(R.id.tvtotaltime);
		this.videoseekbar = (SeekBar) findViewById(R.id.videoseekbar);
		this.llloading = (LinearLayout) findViewById(R.id.llloading);
		this.playbtn = (ImageButton) findViewById(R.id.playbtn);
		this.llplaycontrol = (LinearLayout) findViewById(R.id.llplaycontrol);
	}

	private void initParams() {
		Intent intent = getIntent();

		this.remoteUrl = intent.getStringExtra("url");
		System.out.println("remoteUrl: " + remoteUrl);

		if (this.remoteUrl == null) {
			finish();
			return;
		}

		this.localUrl = intent.getStringExtra("cache");
		if (this.localUrl == null) {
			this.localUrl = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/VideoCache/"
					+ System.currentTimeMillis() + ".mp4";
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
				if (!isready) {
					isready = true;
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
				mHandler.removeCallbacks(hidectlpanel);
				if (!llplaycontrol.isShown()) {
					llplaycontrol.setVisibility(View.VISIBLE);
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

		SurfaceHolder surfaceHolder = surfaceview.getHolder();
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

		this.videoseekbar.setProgress(0);
		this.videoseekbar.setMax(100);
		this.videoseekbar
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekbar) {
						showLoading();
						mediaPlayer.seekTo(seekPosition);
						vdl.seekLoadVideo(seekPosition / 1000);
						userpaused = false;
						mHandler.postDelayed(hidectlpanel,
								DISSMISS_CTLPANEL_TIM);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekbar) {
						mediaPlayer.pause();
						userpaused = true;
						mHandler.removeCallbacks(hidectlpanel);
					}

					@Override
					public void onProgressChanged(SeekBar seekbar, int i,
							boolean flag) {
						if (userpaused) {
							seekPosition = i * mediaPlayer.getDuration()
									/ seekbar.getMax();
							tvcurtime.setText(transtimetostr(seekPosition));
						}
					}
				});

		surfaceview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (!llplaycontrol.isShown()) {
						llplaycontrol.setVisibility(View.VISIBLE);
					}

					mHandler.removeCallbacks(hidectlpanel);

					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					mHandler.postDelayed(hidectlpanel, DISSMISS_CTLPANEL_TIM);

					return true;
				}

				return false;
			}
		});

		llplaycontrol.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent motionevent) {
				if (motionevent.getAction() == MotionEvent.ACTION_DOWN) {
					mHandler.removeCallbacks(hidectlpanel);

					return true;
				} else if (motionevent.getAction() == MotionEvent.ACTION_UP) {
					mHandler.postDelayed(hidectlpanel, DISSMISS_CTLPANEL_TIM);

					return true;
				}

				return false;
			}
		});

		playbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isready || isloading) {
					return;
				}

				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
					userpaused = true;
				} else {
					mediaPlayer.start();
					userpaused = false;
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
		isloading = true;

		mHandler.post(new Runnable() {

			@Override
			public void run() {
				llloading.setVisibility(View.VISIBLE);
			}
		});
	}

	private void hideLoading() {
		isloading = false;

		mHandler.post(new Runnable() {

			@Override
			public void run() {
				llloading.setVisibility(View.INVISIBLE);
			}
		});
	}

	/**
	 * 下载和缓冲mp4文件头部数据
	 * */
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
		while ((size = is.read(buf)) != -1 && (!isready)) {
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

		if (!isready) {
			mHandler.sendEmptyMessage(CACHE_VIDEO_READY);
		}

		is.close();
		raf.close();
	}

	private void playvideo() {
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
				videoseekbar.setSecondaryProgress(cachepercent);

				int positon = mediaPlayer.getCurrentPosition();
				int duration = mediaPlayer.getDuration();
				int playpercent = positon * 100
						/ (duration == 0 ? 1 : duration);

				if (mediaPlayer.isPlaying()) {
					playbtn.setImageResource(R.drawable.player_pad_button_pause_normal);

					tvcurtime.setText(transtimetostr(positon));
					tvtotaltime.setText(transtimetostr(duration));
					videoseekbar.setProgress(playpercent);

					int next2sec = positon + 2 * 1000;
					if (next2sec > duration) {
						next2sec = duration;
					}

					if (!vdl.checkIsBuffered(next2sec / 1000)) {
						mediaPlayer.pause();
						showLoading();
					}
				} else {
					playbtn.setImageResource(R.drawable.player_pad_button_play_normal);

					if (!userpaused && isloading) {
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

			case VideoDownloder.MSG_DOWNLOADFINISH:
				videoCacheSize = videoTotalSize;
				break;

			case VideoDownloder.MSG_DOWNLOADUPDATE:
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
	private boolean isinitok = false;
	private int downloadvideoindex = 0;

	private final ArrayList<VideoInfo> vilists = new ArrayList<VideoDownloder.VideoInfo>();
	private final ExecutorService executorService = Executors
			.newFixedThreadPool(5);

	/* 以5s为分割点进行视频分段 */
	private static final int SEP_SECOND = 5;

	public static final int MSG_DOWNLOADUPDATE = 101;
	public static final int MSG_DOWNLOADFINISH = 102;

	public VideoDownloder(Handler handler, String url, String localFilePath) {
		this.handler = handler;
		this.url = url;
		this.localFilePath = localFilePath;
	}

	public void initVideoDownloder(final long startoffset, final long totalsize) {
		if (isinitok) {
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
				cmp4p.printinfo();

				vilists.clear();

				VideoInfo vi = null;
				for (int i = 0; i < cmp4p.syncSamples.length; i++) {
					if (vi == null) {
						vi = new VideoInfo();
						vi.timestart = cmp4p.timeOfSyncSamples[i];
						vi.offsetstart = cmp4p.syncSamplesOffset[i];
					}

					if (cmp4p.timeOfSyncSamples[i] < (vilists.size() + 1)
							* SEP_SECOND) {
						continue;
					}

					vi.offsetend = cmp4p.syncSamplesOffset[i];
					vilists.add(vi);
					vi = null;
					i--;
				}

				if (vi != null) {
					vi.offsetend = totalsize;
					vilists.add(vi);
					vi = null;
				}

				isinitok = true;

				downloadvideo(startoffset);
			}
		});
	}

	public void cancelDownload() {
		this.executorService.shutdown();
	}

	/**
	 * 加载第 time 秒开始的数据流
	 * */
	public synchronized void seekLoadVideo(long time) {
		int index = -1;

		for (VideoDownloder.VideoInfo tvi : vilists) {
			if (tvi.timestart > time) {
				break;
			}
			index++;
		}

		if (index < 0 || index >= this.vilists.size()) {
			return;
		}

		final VideoInfo vi = this.vilists.get(index);

		if (vi.status == DownloadStatus.NOTSTART) {
			executorService.submit(new Runnable() {

				@Override
				public void run() {
					try {
						vi.status = DownloadStatus.DOWNLOADING;
						downloadbyvideoinfo(vi);
						vi.status = DownloadStatus.FINISH;
					} catch (IOException e) {
						vi.status = DownloadStatus.NOTSTART;
						e.printStackTrace();
					}
				}
			});
		}

		downloadvideoindex = index;
	}

	/**
	 * 检测指定时间的视频是否已经缓存好了
	 * */
	public boolean checkIsBuffered(long time) {
		int index = -1;

		for (VideoDownloder.VideoInfo tvi : vilists) {
			if (tvi.timestart > time) {
				break;
			}

			index++;
		}

		if (index < 0 || index >= this.vilists.size()) {
			return true;
		}

		final VideoInfo vi = this.vilists.get(index);

		if (vi.status == DownloadStatus.FINISH) {
			return true;
		} else if (vi.status == DownloadStatus.NOTSTART) {
			return false;
		} else if (vi.status == DownloadStatus.DOWNLOADING) {
			return (vi.downloadsize * 100 / (vi.offsetend - vi.offsetstart)) > ((time - vi.timestart) * 100 / SEP_SECOND);
		}

		return true;
	}

	/**
	 * 检测是否全部视频模块都已下载完毕
	 * */
	private boolean isallfinished() {
		for (VideoDownloder.VideoInfo vi : vilists) {
			if (vi.status != DownloadStatus.FINISH) {
				return false;
			}
		}

		return true;
	}

	private void downloadvideo(long startoffset) {
		this.downloadvideoindex = 0;

		for (VideoDownloder.VideoInfo tvi : vilists) {
			if (tvi.offsetstart > startoffset) {
				break;
			}
			// 标记已下载
			tvi.status = DownloadStatus.FINISH;
			this.downloadvideoindex++;
		}

		while (!isallfinished()) {
			VideoInfo vi = this.vilists
					.get(this.downloadvideoindex %= this.vilists.size());

			if (vi.status == DownloadStatus.NOTSTART) {
				try {
					vi.status = DownloadStatus.DOWNLOADING;
					downloadbyvideoinfo(vi);
					vi.status = DownloadStatus.FINISH;
				} catch (IOException e) {
					e.printStackTrace();
					vi.status = DownloadStatus.NOTSTART;
				}
			}

			this.downloadvideoindex++;
		}

		handler.sendEmptyMessage(MSG_DOWNLOADFINISH);
	}

	/**
	 * 下载一段视频
	 * */
	private void downloadbyvideoinfo(VideoInfo vi) throws IOException {
		System.out.println("download -> " + vi.toString());

		URL url = new URL(this.url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(3000);
		conn.setRequestProperty("Range", "bytes=" + vi.offsetstart + "-"
				+ vi.offsetend);

		RandomAccessFile raf = new RandomAccessFile(new File(localFilePath),
				"rws");
		raf.seek(vi.offsetstart);

		InputStream in = conn.getInputStream();

		byte[] buf = new byte[1024 * 10];
		int len;
		vi.downloadsize = 0;
		while ((len = in.read(buf)) != -1) {
			raf.write(buf, 0, len);
			vi.downloadsize += len;
			Message msg = handler.obtainMessage();
			msg.what = MSG_DOWNLOADUPDATE;
			msg.obj = vi.offsetstart + vi.downloadsize;
			handler.sendMessage(msg);
		}

		in.close();
		raf.close();
	}

	enum DownloadStatus {
		NOTSTART, DOWNLOADING, FINISH,
	}

	class VideoInfo {
		double timestart;
		long offsetstart;
		long offsetend;
		long downloadsize;
		DownloadStatus status;

		public VideoInfo() {
			status = DownloadStatus.NOTSTART;
		}

		@Override
		public String toString() {
			String s = "beginTime: <" + timestart + ">, fileoffset("
					+ offsetstart + " -> " + offsetend + "), isfinish: "
					+ status;

			return s;
		}
	}
}
