package org.apache.android.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

public class VideoViewDemo extends Activity {
	private static final String TAG = "VideoViewDemo";

	private VideoView mVideoView;
	private EditText mPath;
	private ImageButton mPlay;
	private ImageButton mPause;
	private ImageButton mReset;
	private ImageButton mStop;

	private static final int READY_BUFF = 1500 * 1024;
	private static final int CACHE_BUFF = 500 * 1024;

	private File DLTempFile;
	private TextView playedTextView = null;
	private TextView downtext = null;

	private boolean ispaused = false;
	private boolean isstoped = false;
	private boolean isready = false;
	private boolean iserror = false;

	private int VideoDuraton = 1;
	private int curPosition;
	private int mediaLength = 1;
	private int totalKbRead = 0;
	private SeekBar seekbar = null;
	private double downper = 0.00;
	private double playper = 0.00;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);

		findViews();
		init();
	}

	private void findViews() {
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		mPath = (EditText) findViewById(R.id.path);
		seekbar = (SeekBar) findViewById(R.id.seekbar);
		playedTextView = (TextView) findViewById(R.id.has_played);
		downtext = (TextView) findViewById(R.id.downtext);
		mPlay = (ImageButton) findViewById(R.id.play);
		mPause = (ImageButton) findViewById(R.id.pause);
		mReset = (ImageButton) findViewById(R.id.reset);
		mStop = (ImageButton) findViewById(R.id.stop);
	}

	private void init() {
		seekbar.setMax(100);

		mPath.setText("http://192.168.1.101:6666/sdcard/01.mp3");

		mVideoView.setOnPreparedListener(prepareListener);
		mVideoView.setOnCompletionListener(CompletionListener);
		mVideoView.setOnErrorListener(ErrorListener);

		mPlay.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {

				/*
				 * if (mVideoView.isPlaying()) { Log.d(TAG,
				 * "mVideoView isPlaying"); return; }
				 * 
				 * if (ispaused) { Log.d(TAG, "mVideoView isPaused");
				 * 
				 * ispaused = false; isstoped = false;
				 * 
				 * mVideoView.start(); return; }
				 * 
				 * ispaused = false; isstoped = false;
				 * 
				 * String path = mPath.getText().toString(); Log.v(TAG,
				 * "playing: " + path); new PlayThread(path).start();
				 */
				
				// ///for test
				Intent intent = new Intent();
				intent.setClass(VideoViewDemo.this, BBVideoPlayer.class);
				intent.putExtra("url", mPath.getText().toString());
				intent.putExtra("cache",
						Environment.getExternalStorageDirectory().getAbsolutePath()
								+ "/VideoCache/" + System.currentTimeMillis() + ".mp4");
				startActivity(intent);

				// ///
				//Intent intent = new Intent();
				//intent.setAction(Intent.ACTION_VIEW);
				//intent.setDataAndType(Uri.parse("http://bbfile.b0.upaiyun.com/data/videos/2/testxie22.mp4"), "video/*");

				//intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/BBshow/prog.m3u8")), "video/*");
				//startActivity(intent);
			}
		});

		mPause.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (mVideoView.isPlaying() && mVideoView.canPause()) {
					mVideoView.pause();
					ispaused = true;
				}
			}
		});

		mReset.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (mVideoView != null) {
					mVideoView.stopPlayback();
					DLTempFile.delete();
				}
			}
		});

		mStop.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (mVideoView.isPlaying()) {
					mVideoView.stopPlayback();
					isstoped = true;
				}
			}
		});

	}

	private MediaPlayer.OnPreparedListener prepareListener = new MediaPlayer.OnPreparedListener() {
		public void onPrepared(MediaPlayer mp) {
			Log.d(TAG, "OnPreparedListener");
			myHandler.sendEmptyMessage(VIDEO_STATE_UPDATE);
			mVideoView.seekTo(curPosition);
			mp.start();
		}
	};

	private MediaPlayer.OnCompletionListener CompletionListener = new MediaPlayer.OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer arg0) {
			Log.d(TAG, "OnCompletionListener");
			curPosition = 0;
			mVideoView.stopPlayback();
		}
	};

	private MediaPlayer.OnErrorListener ErrorListener = new MediaPlayer.OnErrorListener() {
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			Log.d(TAG, "OnErrorListener");
			iserror = true;
			mVideoView.stopPlayback();
			return true;
		}
	};

	private final static int VIDEO_STATE_UPDATE = 0;
	private final static int CACHE_VIDEO_READY = 1;
	private final static int CACHE_VIDEO_UPDATE = 2;
	private final static int CACHE_VIDEO_END = 3;

	Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case VIDEO_STATE_UPDATE:
				Log.d(TAG, "VIDEO_STATE_UPDATE");

				downper = totalKbRead * 100.00 / mediaLength * 1.0;
				seekbar.setSecondaryProgress((int) downper);
				downtext.setText(String.format("[%.4f]", downper));

				if (mVideoView.isPlaying()) {
					VideoDuraton = mVideoView.getDuration() + 1;
					curPosition = mVideoView.getCurrentPosition();

					playper = curPosition * 100.00 / VideoDuraton * 1.0;
					seekbar.setProgress((int) playper);

					int i = curPosition;
					i /= 1000;
					int minute = i / 60;
					int hour = minute / 60;
					int second = i % 60;
					minute %= 60;
					playedTextView.setText(String.format(
							"%02d:%02d:%02d[%.4f][%.4f]", hour, minute, second,
							playper, downper));
				}

				myHandler.sendEmptyMessageDelayed(VIDEO_STATE_UPDATE, 1000);
				break;

			case CACHE_VIDEO_READY:
				Log.d(TAG, "CACHE_VIDEO_READY");
				isready = true;
				mVideoView.setVideoPath(DLTempFile.getAbsolutePath());
				mVideoView.start();
				break;

			case CACHE_VIDEO_UPDATE:
				Log.d(TAG, "CACHE_VIDEO_UPDATE");

				if (iserror) {
					mVideoView.setVideoPath(DLTempFile.getAbsolutePath());
					mVideoView.start();
					iserror = false;
				}
				break;

			case CACHE_VIDEO_END:
				Log.d(TAG, "CACHE_VIDEO_END");

				if (iserror) {
					mVideoView.setVideoPath(DLTempFile.getAbsolutePath());
					mVideoView.start();
					iserror = false;
				}
				break;
			}

			super.handleMessage(msg);
		}
	};

	public long getFileSize(String urlString) {
		int nFileLength = -1;
		try {
			URL url = new URL(urlString);
			HttpURLConnection httpConnection = (HttpURLConnection) url
					.openConnection();
			httpConnection.setRequestProperty("User-Agent", "NetFox");
			int responseCode = httpConnection.getResponseCode();
			if (responseCode >= 400) {
				return -2; // -2 represent access is error
			}
			String sHeader;
			for (int i = 1;; i++) {
				sHeader = httpConnection.getHeaderFieldKey(i);
				if (sHeader != null) {
					if (sHeader.equals("content-length")) {
						nFileLength = Integer.parseInt(httpConnection
								.getHeaderField(sHeader));
						break;
					}
				} else
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nFileLength;
	}

	private void playFromNet(String mediaUrl, int start) {
		FileOutputStream out = null;
		InputStream is = null;

		try {
			URL url = new URL(mediaUrl);
			HttpURLConnection httpConnection = (HttpURLConnection) url
					.openConnection();

			DLTempFile = new File(
					android.os.Environment.getExternalStorageDirectory() + "/"
							+ "carey.mp4");

			if (DLTempFile.exists()) {
				DLTempFile.delete();
				DLTempFile.createNewFile();
			}

			out = new FileOutputStream(DLTempFile, true);

			FileInputStream fis = new FileInputStream(DLTempFile);
			totalKbRead = fis.available();

			httpConnection.setRequestProperty("User-Agent", "NetFox");
			String sProperty = "bytes=" + totalKbRead + "-";
			httpConnection.setRequestProperty("RANGE", sProperty);

			is = httpConnection.getInputStream();
			if (is == null) {
				return;
			}

			mediaLength = httpConnection.getContentLength();

			byte buf[] = new byte[4 * 1024];
			int readLength = 0;
			int oldlength = 0;

			myHandler.sendEmptyMessage(VIDEO_STATE_UPDATE);

			while ((readLength = is.read(buf)) != -1 && !isstoped) {
				try {
					out.write(buf, 0, readLength);
					totalKbRead += readLength;
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}

				if (!isready) {
					if ((totalKbRead - oldlength) > READY_BUFF) {
						oldlength = totalKbRead;
						myHandler.sendEmptyMessage(CACHE_VIDEO_READY);
					}
				} else {
					if ((totalKbRead - oldlength) > CACHE_BUFF) {
						oldlength = totalKbRead;
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
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					//
				}
			}

			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					//
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
				playFromNet(url, 0);
			} // else {
				// runOnUiThread(new Runnable() {
				// public void run() {
				// mVideoView.setVideoPath(url);
				// mVideoView.start();
				// }
				// });

			// }
		}
	}
}
